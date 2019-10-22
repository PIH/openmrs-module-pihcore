package org.openmrs.module.pihcore.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.htmlformentry.BadFormDesignException;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.handler.AbstractTagHandler;
import org.openmrs.module.htmlformentry.handler.AttributeDescriptor;
import org.openmrs.ui.framework.resource.Resource;
import org.openmrs.ui.framework.resource.ResourceFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HtmlFormIncludeHandler extends AbstractTagHandler {

    protected Log log = LogFactory.getLog(getClass());

    @Override
    protected List<AttributeDescriptor> createAttributeDescriptors() {
        List<AttributeDescriptor> attributeDescriptors = new ArrayList<AttributeDescriptor>();
        attributeDescriptors.add(new AttributeDescriptor("provider", String.class));
        attributeDescriptors.add(new AttributeDescriptor("form", Resource.class));
        return Collections.unmodifiableList(attributeDescriptors);
    }

    @Override
    public boolean doStartTag(FormEntrySession formEntrySession, PrintWriter printWriter, Node parent, Node include) throws BadFormDesignException {
        NamedNodeMap map = include.getAttributes();

        String provider = map.getNamedItem("provider").getNodeValue();
        String formPath = map.getNamedItem("form").getNodeValue();

        ResourceFactory resourceFactory = ResourceFactory.getInstance();

        String xml;
        try {
            xml = resourceFactory.getResourceAsString(provider, formPath);
            printWriter.print(new CustomHtmlFormEntryGenerator(formEntrySession).generateHtml(xml));
        } catch (Exception e) {
            log.error(e);
            printWriter.print("<div class=\"error\">" + " There's an error in the design of the form. Consult your administrator.<br/><pre>");
            e.printStackTrace(printWriter);
            printWriter.print("</pre></div>");
        }

        return true;
    }

    @Override
    public void doEndTag(FormEntrySession formEntrySession, PrintWriter printWriter, Node node, Node node1) throws BadFormDesignException {

    }
}
