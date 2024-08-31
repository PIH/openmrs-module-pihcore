package org.openmrs.module.pihcore.htmlformentry.analysis.mapper;

import lombok.Data;
import org.openmrs.module.pihcore.htmlformentry.analysis.HtmlFormTag;

@Data
public class IfModeTagRemover implements TagMapper {

    @Override
    public HtmlFormTag map(HtmlFormTag inputTag) {
        HtmlFormTag outputTag = inputTag.cloneWithoutChildren();
        for (HtmlFormTag childTag : inputTag.getChildTags()) {
            childTag = map(childTag);
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
