package org.openmrs.module.pihcore.htmlformentry.analysis;

import org.openmrs.Concept;
import org.openmrs.module.htmlformentry.handler.AttributeDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class HtmlFormTagConverter {

    private final Logger log = LoggerFactory.getLogger(HtmlFormTagConverter.class);

    public HtmlFormTag removeTagsByName(HtmlFormTag tag, String tagName) {
        HtmlFormTag convertedTag = tag.cloneWithoutChildren();
        for (HtmlFormTag childTag : tag.getChildTags()) {
            childTag = removeTagsByName(childTag, tagName);
            if (childTag.getName().equalsIgnoreCase(tagName)) {
                if (!childTag.getChildTags().isEmpty()) {
                    convertedTag.getChildTags().addAll(childTag.getChildTags());
                }
            }
            else {
                convertedTag.getChildTags().add(childTag);
            }
        }
        return convertedTag;
    }

    public HtmlFormTag removeIfModeTags(HtmlFormTag tag) {
        HtmlFormTag convertedTag = tag.cloneWithoutChildren();
        for (HtmlFormTag childTag : tag.getChildTags()) {
            childTag = removeIfModeTags(childTag);
            if (childTag.getName().equalsIgnoreCase("ifMode")) {
                boolean viewMode = childTag.hasAttribute("mode", "view");
                boolean include = !childTag.hasAttribute("include", "false");
                if ((viewMode && include) || (!viewMode && !include)) {
                    log.trace("Excluding content that is configured only for view mode: " +  childTag);
                }
                else {
                    convertedTag.getChildTags().addAll(childTag.getChildTags());
                }
            }
            else {
                convertedTag.getChildTags().add(childTag);
            }
        }
        return convertedTag;
    }

    public HtmlFormTag mergeTagIntoChildren(HtmlFormTag tag, String tagName) {
        HtmlFormTag convertedTag = tag.cloneWithoutChildren();
        for (HtmlFormTag childTag : tag.getChildTags()) {
            if (tag.getName().equalsIgnoreCase(tagName)) {
                for (String attribute : tag.getAttributes().keySet()) {
                    childTag.addAttribute(attribute, tag.getAttributes().get(attribute), true);
                }
            }
            convertedTag.getChildTags().add(mergeTagIntoChildren(childTag, tagName));
        }
        return removeTagsByName(convertedTag, tagName);
    }

    public HtmlFormTag mergeSiblings(HtmlFormTag tag, String tagName) {
        HtmlFormTag convertedTag = tag.cloneWithoutChildren();
        HtmlFormTag matchingTag = null;
        for (HtmlFormTag childTag : tag.getChildTags()) {
            childTag = mergeSiblings(childTag, tagName);
            if (childTag.getName().equalsIgnoreCase(tagName)) {
                if (!childTag.getChildTags().isEmpty()) {
                    throw new IllegalStateException("You cannot merge siblings that have children");
                }
                if (matchingTag == null) {
                    matchingTag = childTag;
                    convertedTag.getChildTags().add(matchingTag);
                }
                else {
                    Set<String> attributes = new LinkedHashSet<>(matchingTag.getAttributes().keySet());
                    attributes.addAll(childTag.getAttributes().keySet());
                    for (String attribute : attributes) {
                        Set<String> values = new HashSet<>();
                        values.add(matchingTag.getAttributes().get(attribute));
                        values.add(childTag.getAttributes().get(attribute));
                        matchingTag.getAttributes().put(attribute, String.join(" || ", values));
                    }
                }
            }
            else {
                convertedTag.getChildTags().add(childTag);
            }
        }
        return convertedTag;
    }

    public HtmlFormTag mergeObsGroupIntoChildren(HtmlFormTag tag) {
        HtmlFormTag convertedTag = tag.cloneWithoutChildren();
        for (HtmlFormTag childTag : tag.getChildTags()) {
            childTag = mergeObsGroupIntoChildren(childTag);
            if (childTag.getName().equalsIgnoreCase("obsGroup")) {
                String hiddenConceptId = childTag.getAttributes().remove("hiddenConceptId");
                String hiddenAnswerConceptId = childTag.getAttributes().remove("hiddenAnswerConceptId");
                if (hiddenConceptId != null || hiddenAnswerConceptId != null) {
                    HtmlFormTag hiddenTag = new HtmlFormTag();
                    hiddenTag.setName("obs");
                    hiddenTag.getAttributes().put("hidden", "true");
                    hiddenTag.getAttributes().put("conceptId", hiddenConceptId);
                    hiddenTag.getAttributes().put("answerConceptId", hiddenAnswerConceptId);
                    childTag.getChildTags().add(hiddenTag);
                }
            }
            convertedTag.getChildTags().add(childTag);
        }
        return mergeTagIntoChildren(convertedTag, "obsGroup");
    }

}
