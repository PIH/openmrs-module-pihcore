package org.openmrs.module.pihcore.htmlformentry.analysis.converter.tag;

import lombok.Data;
import org.openmrs.module.pihcore.htmlformentry.analysis.HtmlFormTag;
import org.openmrs.module.pihcore.htmlformentry.analysis.converter.Converter;

import java.util.HashMap;
import java.util.Map;

@Data
public class MergeParentToChildrenConverter implements Converter<HtmlFormTag, HtmlFormTag> {

    private String tagName;
    private String multiValueSeparator = " AND ";

    public MergeParentToChildrenConverter(String tagName, String multiValueSeparator) {
        this.tagName = tagName;
        this.multiValueSeparator = multiValueSeparator;
    }

    @Override
    public HtmlFormTag convert(HtmlFormTag input) throws Exception {
        HtmlFormTag outputTag = input.cloneWithoutChildren();
        for (HtmlFormTag childTag : input.getChildTags()) {
            if (input.getName().equalsIgnoreCase(tagName)) {
                Map<String, String> attributesToAdd = computeAttributesToMerge(new HashMap<>(input.getAttributes()));
                for (Map.Entry<String, String> attribute : attributesToAdd.entrySet()) {
                    childTag.mergeAttributeRecursively(attribute.getKey(), attribute.getValue(), multiValueSeparator);
                }
            }
            childTag = convert(childTag);
            outputTag.getChildTags().add(childTag);
        }
        outputTag = new RemoveTagByNameConverter(tagName).convert(outputTag);
        return outputTag;
    }

    public Map<String, String> computeAttributesToMerge(Map<String, String> inputAttributes) {
        return inputAttributes;
    }
}
