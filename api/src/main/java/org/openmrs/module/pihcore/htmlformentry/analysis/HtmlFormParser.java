package org.openmrs.module.pihcore.htmlformentry.analysis;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.htmlformentry.HtmlFormEntryGenerator;
import org.openmrs.module.htmlformentry.HtmlFormEntryUtil;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Stack;

/**
 * Loads and html form and parses it into tags
 */
public class HtmlFormParser {

    protected final Log log = LogFactory.getLog(getClass());

    public static final String HTMLFORM_TAG_NAME = "htmlform";

    private final HtmlFormEntryGenerator generator = new HtmlFormEntryGenerator();

    protected final Stack<HtmlFormTag> tagStack = new Stack<>();

    public HtmlFormTag parseFile(File formFile) {
        try {
            log.debug("Parsing htmlform file: " + formFile.getAbsolutePath());
            return parseXml(FileUtils.readFileToString(formFile, StandardCharsets.UTF_8));
        }
        catch (IOException e) {
            throw new IllegalArgumentException("Unable to load form from " + formFile.getName(), e);
        }
    }

    public HtmlFormTag parseXml(String formXml) {
        try {
            formXml = preprocessXml(formXml);
            Document document = HtmlFormEntryUtil.stringToDocument(formXml);
            Node htmlFormNode = HtmlFormEntryUtil.findChild(document, HTMLFORM_TAG_NAME);
            HtmlFormTag htmlFormTag = processNode(htmlFormNode);
            if (!tagStack.isEmpty()) {
                throw new IllegalStateException("Tag Stack is not empty after processing.  Found: " + tagStack);
            }
            return htmlFormTag;
        }
        catch (Exception e) {
            throw new IllegalStateException("Unable to parse form xml", e);
        }
    }

    protected String preprocessXml(String xml) throws Exception {
        return xml;
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
                    handleTagAttribute(tag, attr.getNodeName(), attr.getNodeValue());
                }
            }
            if (!tagStack.isEmpty()) {
                tagStack.peek().getChildTags().add(tag);
            }
            handleTagStart(tag);
            tagStack.push(tag);
            NodeList childNodes = node.getChildNodes();
            for (int i=0; i<childNodes.getLength(); i++) {
                Node childNode = childNodes.item(i);
                processNode(childNode);
            }
            tagStack.pop();
            handleTagEnd(tag);
        }
        return tag;
    }

    protected void handleTagStart(HtmlFormTag tag) {
        if (log.isTraceEnabled()) {
            log.trace("startTag: " + tag.getName());
        }
    }

    protected void handleTagEnd(HtmlFormTag tag) {
        if (log.isTraceEnabled()) {
            log.trace("endTag: " + tag.getName());
        }
    }

    protected void handleTagAttribute(HtmlFormTag tag, String attributeName, String attributeValue) {
        if (log.isTraceEnabled()) {
            log.trace("tagAttribute: " + attributeName + " = " + attributeValue);
        }
    }


}
