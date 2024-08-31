package org.openmrs.module.pihcore.htmlformentry.analysis.mapper;

import lombok.Data;
import org.openmrs.module.pihcore.htmlformentry.analysis.HtmlFormTag;

@Data
public class TagRemover implements TagMapper {

    private String tagName;
    private boolean removeChildren;

    public TagRemover(String tagName) {
        this.tagName = tagName;
    }

    @Override
    public HtmlFormTag map(HtmlFormTag inputTag) {
        HtmlFormTag outputTag = inputTag.cloneWithoutChildren();
        for (HtmlFormTag childTag : inputTag.getChildTags()) {
            childTag = map(childTag);
            if (childTag.getName().equalsIgnoreCase(tagName)) {
                if (!removeChildren) {
                    outputTag.getChildTags().addAll(childTag.getChildTags());
                }
            }
            else {
                outputTag.getChildTags().add(childTag);
            }
        }
        return outputTag;
    }
}
