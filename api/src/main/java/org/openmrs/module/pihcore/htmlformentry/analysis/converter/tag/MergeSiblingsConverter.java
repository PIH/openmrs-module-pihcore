package org.openmrs.module.pihcore.htmlformentry.analysis.converter.tag;

import lombok.Data;
import org.openmrs.module.pihcore.htmlformentry.analysis.HtmlFormTag;
import org.openmrs.module.pihcore.htmlformentry.analysis.converter.Converter;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class MergeSiblingsConverter implements Converter<HtmlFormTag, HtmlFormTag> {

    private String tagName;

    public MergeSiblingsConverter(String tagName) {
        this.tagName = tagName;
    }

    @Override
    public HtmlFormTag convert(HtmlFormTag input) throws Exception {
        HtmlFormTag outputTag = input.cloneWithoutChildren();
        HtmlFormTag matchingTag = null;
        for (HtmlFormTag childTag : input.getChildTags()) {
            childTag = convert(childTag);
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
