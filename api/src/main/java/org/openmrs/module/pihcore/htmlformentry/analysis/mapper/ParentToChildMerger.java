package org.openmrs.module.pihcore.htmlformentry.analysis.mapper;

import lombok.Data;
import org.openmrs.module.pihcore.htmlformentry.analysis.HtmlFormTag;

import java.util.HashMap;
import java.util.Map;

@Data
public class ParentToChildMerger implements TagMapper {

    private String tagName;

    public ParentToChildMerger(String tagName) {
        this.tagName = tagName;
    }

    @Override
    public HtmlFormTag map(HtmlFormTag inputTag) {
        HtmlFormTag outputTag = inputTag.cloneWithoutChildren();
        for (HtmlFormTag childTag : inputTag.getChildTags()) {
            if (inputTag.getName().equalsIgnoreCase(tagName)) {
                Map<String, String> attributesToAdd = computeAttributesToMerge(new HashMap<>(inputTag.getAttributes()));
                for (Map.Entry<String, String> attribute : attributesToAdd.entrySet()) {
                    childTag.addAttribute(attribute.getKey(), attribute.getValue(), true);
                }
            }
            childTag = map(childTag);
            outputTag.getChildTags().add(childTag);
        }
        outputTag = new TagRemover(tagName).map(outputTag);
        return outputTag;
    }

    public Map<String, String> computeAttributesToMerge(Map<String, String> inputAttributes) {
        return inputAttributes;
    }
}
