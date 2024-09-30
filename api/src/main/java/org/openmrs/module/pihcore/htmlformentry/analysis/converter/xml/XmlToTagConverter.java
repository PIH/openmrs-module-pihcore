package org.openmrs.module.pihcore.htmlformentry.analysis.converter.xml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.htmlformentry.HtmlFormEntryUtil;
import org.openmrs.module.pihcore.htmlformentry.analysis.HtmlFormTag;
import org.openmrs.module.pihcore.htmlformentry.analysis.converter.Converter;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Stack;

/**
 * Loads and html form and parses it into tags
 */
public class XmlToTagConverter implements Converter<String, HtmlFormTag> {

    protected final Log log = LogFactory.getLog(getClass());

    public static final String HTMLFORM_TAG_NAME = "htmlform";

    private final Stack<HtmlFormTag> tagStack = new Stack<>();

    public HtmlFormTag convert(String input) throws Exception {
        Document document = HtmlFormEntryUtil.stringToDocument(input);
        Node htmlFormNode = HtmlFormEntryUtil.findChild(document, HTMLFORM_TAG_NAME);
        HtmlFormTag htmlFormTag = processNode(htmlFormNode);
        if (!tagStack.isEmpty()) {
            throw new IllegalStateException("Tag Stack is not empty after processing.  Found: " + tagStack);
        }
        return htmlFormTag;
    }

    protected HtmlFormTag processNode(Node node) {
        HtmlFormTag tag = null;
        if (node != null) {
            tag = new HtmlFormTag();
            tag.setName(node.getNodeName());
            if (node instanceof CharacterData) {
                CharacterData characterDataNode = (CharacterData) node;
                tag.setData(characterDataNode.getData());
            }
            NamedNodeMap attrs = node.getAttributes();
            if (attrs != null) {
                for (int i = 0; i < attrs.getLength(); i++) {
                    Node attr = attrs.item(i);
                    tag.getAttributes().put(attr.getNodeName(), attr.getNodeValue());
                    log.trace("tagAttribute: " + attr.getNodeName() + " = " + attr.getNodeValue());
                }
            }
            if (!tagStack.isEmpty()) {
                tagStack.peek().getChildTags().add(tag);
            }
            log.trace("startTag: " + tag.getName());
            tagStack.push(tag);
            NodeList childNodes = node.getChildNodes();
            for (int i=0; i<childNodes.getLength(); i++) {
                Node childNode = childNodes.item(i);
                processNode(childNode);
            }
            tagStack.pop();
            log.trace("endTag: " + tag.getName());
        }
        return tag;
    }
}
