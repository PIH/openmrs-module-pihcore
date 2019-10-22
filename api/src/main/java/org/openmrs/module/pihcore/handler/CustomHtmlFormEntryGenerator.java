package org.openmrs.module.pihcore.handler;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.htmlformentry.*;
import org.openmrs.module.htmlformentry.handler.IteratingTagHandler;
import org.openmrs.module.htmlformentry.handler.TagHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

public class CustomHtmlFormEntryGenerator extends HtmlFormEntryGenerator {

    private FormEntrySession formEntrySession;

    public CustomHtmlFormEntryGenerator(FormEntrySession formEntrySession) {
        this.formEntrySession = formEntrySession;
    }

    public String generateHtml(String xml) throws Exception {
        xml = super.substituteCharacterCodesWithAsciiCodes(xml);
        xml = super.stripComments(xml);
        xml = super.convertSpecialCharactersWithinLogicAndVelocityTests(xml);
        xml = super.applyRoleRestrictions(xml);
        xml = this.applyMacros(this.formEntrySession, xml);
        xml = this.applyRepeats(xml);
        xml = this.applyTranslations(xml, this.formEntrySession.getContext());
        xml = this.applyTags(this.formEntrySession, xml);

        return xml;
    }

    @Override
    public String applyRepeats(String xml) throws Exception {
        xml = this.applyRepeatTemplateTags(xml);
        xml = this.applyRepeatWithTags(xml);
        return xml;
    }

    @Override
    public String applyTags(FormEntrySession session, String xml) throws Exception {
        Document doc = HtmlFormEntryUtil.stringToDocument(xml);
        Node content = doc.getFirstChild();
        StringWriter out = new StringWriter();
        this.applyTagsHelper(this.formEntrySession, new PrintWriter(out), null, content, null);
        return out.toString();
    }

    @Override
    public String applyTranslations(String xml, FormEntryContext context) throws Exception {
        Document doc = HtmlFormEntryUtil.stringToDocument(xml);
        Node content = doc.getFirstChild();
        Node transNode = HtmlFormEntryUtil.findChild(content, "translations");

        // if there are no translations defined, we just return the original xml unchanged
        if (transNode == null) {
            return xml;
        }

        String defaultLocaleStr = HtmlFormEntryUtil.getNodeAttribute(transNode, "defaultLocale", "en");

        NodeList codeNodeList = transNode.getChildNodes();
        for (int i = 0; i < codeNodeList.getLength(); i++) {
            Node codeNode = codeNodeList.item(i);
            if (codeNode.getNodeName().equalsIgnoreCase("code")) {
                String codeName = HtmlFormEntryUtil.getNodeAttribute(codeNode, "name", null);
                if (codeName == null) {
                    throw new IllegalArgumentException("All translation elements must contain a valid code name");
                }
                NodeList variantNodeList = codeNode.getChildNodes();
                for (int j = 0; j < variantNodeList.getLength(); ++j) {
                    Node variantNode = variantNodeList.item(j);
                    if (variantNode.getNodeName().equalsIgnoreCase("variant")) {
                        String localeStr = HtmlFormEntryUtil.getNodeAttribute(variantNode, "locale", defaultLocaleStr);
                        String valueStr = HtmlFormEntryUtil.getNodeAttribute(variantNode, "value", null);
                        if (valueStr == null) {
                            throw new IllegalArgumentException("All variants must specify a value");
                        }
                        context.getTranslator().addTranslation(localeStr, codeName, valueStr);
                    }
                }
            }
        }

        // now remove the macros node
        content.removeChild(transNode);

        // switch back to String mode from the document so we can use string utilities to substitute
        xml = HtmlFormEntryUtil.documentToString(doc);
        return xml;
    }

    private String applyRepeatWithTags(String xml) throws Exception {

        while (xml.contains("<repeat with=")) {

            int startIndex = xml.indexOf("<repeat with=");
            int endIndex = xml.indexOf("</repeat>", startIndex) + 9;

            String xmlToReplace = xml.substring(startIndex, endIndex);

            int substitutionSetsStartIndex = xmlToReplace.indexOf("with=") + 6;
            int substitutionSetsEndIndex =  xmlToReplace.indexOf("]\">") + 1;
            List<List<String>> substitutionSets = getSubstitutionSets(xmlToReplace.substring(substitutionSetsStartIndex, substitutionSetsEndIndex));

            int templateStartIndex = xmlToReplace.indexOf("]\">") + 3;
            int templateEndIndex = xmlToReplace.indexOf("</repeat>");
            String template = xmlToReplace.substring(templateStartIndex, templateEndIndex);

            StringBuilder sb = new StringBuilder();

            String current = template;
            for (List<String> substitutionSet : substitutionSets) {

                int i = 0;

                for (String substitution : substitutionSet) {
                    current = current.replace("{" + i + "}", substitution);
                    i++;
                }

                sb.append(current);
                current = template;
            }

            xml = xml.substring(0, startIndex) + sb + xml.substring(endIndex);
        }

        return xml;
    }

    private String applyRepeatTemplateTags(String xml) throws Exception {
        Document doc = HtmlFormEntryUtil.stringToDocument(xml);
        Node content = doc.getFirstChild();

        // We are doing this as follows since I can't seem to get the XML node cloning to work right.
        // We can refactor later as needed if we can get it to work properly, or replace the xml library
        // First we need to parse the document to get the node attributes for repeating elements
        List<List<Map<String, String>>> renderMaps = new ArrayList<List<Map<String, String>>>();

        loadRenderElementsForEachRepeatElement(content, renderMaps);

        // Now we are just going to use String replacements to explode the repeat tags properly
        Iterator<List<Map<String, String>>> renderMapIter = renderMaps.iterator();
        int idLabelpairIndex = 0;
        while (xml.contains("<repeat>")) {
            int startIndex = xml.indexOf("<repeat>");
            int endIndex = xml.indexOf("</repeat>", startIndex) + 9;
            String xmlToReplace = xml.substring(startIndex, endIndex);

            String template = xmlToReplace.substring(xmlToReplace.indexOf("<template>") + 10,
                    xmlToReplace.indexOf("</template>"));
            StringBuilder replacement = new StringBuilder();
            for (Map<String, String> replacements : renderMapIter.next()) {
                String curr = template;
                for (String key : replacements.keySet()) {
                    curr = curr.replace("{" + key + "}", replacements.get(key));
                }
                replacement.append(curr);
            }
            xml = xml.substring(0, startIndex) + replacement + xml.substring(endIndex);

        }

        return xml;
    }

    private void loadRenderElementsForEachRepeatElement(Node node, List<List<Map<String, String>>> renderMaps)
            throws Exception {

        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node n = list.item(i);
            if (n.getNodeName().equalsIgnoreCase("repeat") && !n.hasAttributes()) {
                Node templateNode = HtmlFormEntryUtil.findChild(n, "template");
                if (templateNode == null) {
                    throw new IllegalArgumentException("All <repeat> elements must contain a child <template> element.");
                }
                List<Map<String, String>> l = new ArrayList<Map<String, String>>();
                NodeList repeatNodes = n.getChildNodes();
                for (int j = 0; j < repeatNodes.getLength(); j++) {
                    Node renderNode = repeatNodes.item(j);
                    if (renderNode.getNodeName().equalsIgnoreCase("render")) {
                        l.add(HtmlFormEntryUtil.getNodeAttributes(renderNode));
                    }
                }
                renderMaps.add(l);
            } else {
                loadRenderElementsForEachRepeatElement(n, renderMaps);
            }
        }
    }

    @Override
    public String applyMacros(FormEntrySession session, String xml) throws Exception {
        Document doc = HtmlFormEntryUtil.stringToDocument(xml);
        Node content = doc.getFirstChild();
        Node macrosNode = HtmlFormEntryUtil.findChild(content, "macros");

        // if there are no macros defined, we just return the original xml unchanged
        if (macrosNode == null) {
            return xml;
        }

        // One way to define macros is simply as the text content of the macros node.  This is left for backwards compatibility
        Properties macros = new Properties();
        String macrosText = macrosNode.getTextContent();
        if (macrosText != null) {
            macros.load(new ByteArrayInputStream(macrosText.getBytes()));
        }

        // Another way to define macros is as child tags to the macros node.
        NodeList children = macrosNode.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if ("macro".equals(node.getNodeName())) {
                String key = HtmlFormEntryUtil.getNodeAttribute(node, "key", "");
                if (StringUtils.isBlank(key)) {
                    throw new IllegalArgumentException("Macros must define a 'key' attribute");
                }
                String value = HtmlFormEntryUtil.getNodeAttribute(node, "value", "");
                if (StringUtils.isBlank(value)) {
                    String expression = HtmlFormEntryUtil.getNodeAttribute(node, "expression", "");
                    if (StringUtils.isBlank(expression)) {
                        throw new IllegalArgumentException("Macros must define either a 'value' or 'expression' attribute");
                    }
                    if (session != null) {
                        value = session.evaluateVelocityExpression("$!{" + expression + "}");
                    } else {
                        value = expression;
                    }
                }
                macros.put(key, value);
            }
        }

        // now remove the macros node
        content.removeChild(macrosNode);

        // switch back to String mode from the document so we can use string utilities to substitute
        xml = HtmlFormEntryUtil.documentToString(doc);

        // substitute any macros we found
        for (Object temp : macros.keySet()) {
            String key = (String) temp;
            String value = macros.getProperty(key, "");
            xml = xml.replace("$" + key, value);
        }

        return xml;
    }


    private void applyTagsHelper(FormEntrySession session, PrintWriter out, Node parent, Node node,
                                 Map<String, TagHandler> tagHandlerCache) {
        if (tagHandlerCache == null)
            tagHandlerCache = new HashMap<String, TagHandler>();
        TagHandler handler = null;
        // Find the handler for this node
        {
            String name = node.getNodeName();
            if (name != null) {
                if (tagHandlerCache.containsKey(name)) {
                    // we've looked this up before (though it could be null)
                    handler = tagHandlerCache.get(name);
                } else {
                    handler = HtmlFormEntryUtil.getService().getHandlerByTagName(name);
                    tagHandlerCache.put(name, handler);
                }
            }
        }

        if (handler == null)
            handler = this; // do default actions

        try {
            boolean handleContents = handler.doStartTag(session, out, parent, node);

            // Unless the handler told us to skip them, then iterate over any children
            if (handleContents) {
                if (handler != null && handler instanceof IteratingTagHandler) {
                    // recurse as many times as the tag wants
                    IteratingTagHandler iteratingHandler = (IteratingTagHandler) handler;
                    while (iteratingHandler.shouldRunAgain(session, out, parent, node)) {
                        NodeList list = node.getChildNodes();
                        for (int i = 0; i < list.getLength(); ++i) {
                            applyTagsHelper(session, out, node, list.item(i), tagHandlerCache);
                        }
                    }

                } else { // recurse to contents once
                    NodeList list = node.getChildNodes();
                    for (int i = 0; i < list.getLength(); ++i) {
                        applyTagsHelper(session, out, node, list.item(i), tagHandlerCache);
                    }
                }
            }

            handler.doEndTag(session, out, parent, node);
        } catch (BadFormDesignException e) {
            out.print("<div class=\"error\">" + handler + " reported an error in the design of the form. Consult your administrator.<br/><pre>");
            e.printStackTrace(out);
            out.print("</pre></div>");
        }

    }

    private List<List<String>> getSubstitutionSets(String val) {

        List<List<String>> substitutionSet = new ArrayList<List<String>>();

        // first, strip off the leading and trailing brackets
        val = val.replaceFirst("\\s*\\[\\s*", "");
        val = val.replaceFirst("\\s*\\]\\s*$", "");

        // split on " ] , [ "
        for (String subVal : val.split("\\s*\\]\\s*\\,\\s*\\[\\s*")) {

            List<String> set= new ArrayList<String>();

            // trim off the leading quote and trailing quote
            subVal = subVal.replaceFirst("\\s*\\'", "");
            subVal = subVal.replaceFirst("\\s*'\\s*$","");

            // split on " ',' "
            for (String str : subVal.split("\\s*\\'\\s*\\,\\s*\\'\\s*")) {
                set.add(str);
            }

            substitutionSet.add(set);
        }

        return substitutionSet;
    }
}
