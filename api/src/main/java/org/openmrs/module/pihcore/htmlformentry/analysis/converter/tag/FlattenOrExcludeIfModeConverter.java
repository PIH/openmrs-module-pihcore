package org.openmrs.module.pihcore.htmlformentry.analysis.converter.tag;

import lombok.Data;
import org.openmrs.module.pihcore.htmlformentry.analysis.HtmlFormTag;
import org.openmrs.module.pihcore.htmlformentry.analysis.converter.Converter;

@Data
public class FlattenOrExcludeIfModeConverter implements Converter<HtmlFormTag, HtmlFormTag> {

    @Override
    public HtmlFormTag convert(HtmlFormTag input) throws Exception {
        HtmlFormTag outputTag = input.cloneWithoutChildren();
        for (HtmlFormTag childTag : input.getChildTags()) {
            childTag = convert(childTag);
            if (childTag.getName().equalsIgnoreCase("ifMode")) {
                boolean viewMode = childTag.hasAttribute("mode", "view");
                boolean include = !childTag.hasAttribute("include", "false");
                boolean viewOnlyNode = (viewMode && include) || (!viewMode && !include);
                if (!viewOnlyNode) {
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
