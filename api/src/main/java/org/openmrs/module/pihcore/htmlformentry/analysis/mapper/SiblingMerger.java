package org.openmrs.module.pihcore.htmlformentry.analysis.mapper;

import lombok.Data;
import org.openmrs.module.pihcore.htmlformentry.analysis.HtmlFormTag;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class SiblingMerger implements TagMapper {

    private String tagName;

    public SiblingMerger(String tagName) {
        this.tagName = tagName;
    }

    @Override
    public HtmlFormTag map(HtmlFormTag inputTag) {
        HtmlFormTag outputTag = inputTag.cloneWithoutChildren();
        HtmlFormTag matchingTag = null;
        for (HtmlFormTag childTag : inputTag.getChildTags()) {
            childTag = map(childTag);
            if (childTag.getName().equalsIgnoreCase(tagName)) {
                if (!childTag.getChildTags().isEmpty()) {
                    throw new IllegalStateException("You cannot merge siblings that have children");
                }
                if (matchingTag == null) {
                    matchingTag = childTag;
                    outputTag.getChildTags().add(matchingTag);
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
                outputTag.getChildTags().add(childTag);
            }
        }
        return outputTag;
    }
}
