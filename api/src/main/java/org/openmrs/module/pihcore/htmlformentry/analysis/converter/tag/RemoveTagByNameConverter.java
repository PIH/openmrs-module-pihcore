package org.openmrs.module.pihcore.htmlformentry.analysis.converter.tag;

import lombok.Data;
import org.openmrs.module.pihcore.htmlformentry.analysis.HtmlFormTag;
import org.openmrs.module.pihcore.htmlformentry.analysis.converter.Converter;

@Data
public class RemoveTagByNameConverter implements Converter<HtmlFormTag, HtmlFormTag> {

    private String tagName;
    private boolean removeChildren;

    public RemoveTagByNameConverter(String tagName) {
        this.tagName = tagName;
    }

    @Override
    public HtmlFormTag convert(HtmlFormTag input) throws Exception {
        HtmlFormTag outputTag = input.cloneWithoutChildren();
        for (HtmlFormTag childTag : input.getChildTags()) {
            childTag = convert(childTag);
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
