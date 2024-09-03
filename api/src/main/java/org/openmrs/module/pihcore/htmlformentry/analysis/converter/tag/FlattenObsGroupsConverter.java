package org.openmrs.module.pihcore.htmlformentry.analysis.converter.tag;

import lombok.Data;
import org.openmrs.module.pihcore.htmlformentry.analysis.HtmlFormTag;
import org.openmrs.module.pihcore.htmlformentry.analysis.converter.Converter;

@Data
public class FlattenObsGroupsConverter implements Converter<HtmlFormTag, HtmlFormTag> {

    @Override
    public HtmlFormTag convert(HtmlFormTag input) throws Exception {
        HtmlFormTag outputTag = input.cloneWithoutChildren();
        for (HtmlFormTag childTag : input.getChildTags()) {
            childTag = convert(childTag);
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
            outputTag.getChildTags().add(childTag);
        }
        MergeParentToChildrenConverter parentToChildMerger = new MergeParentToChildrenConverter("obsGroup", " > ");
        return parentToChildMerger.convert(outputTag);
    }
}
