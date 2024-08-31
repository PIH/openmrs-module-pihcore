package org.openmrs.module.pihcore.htmlformentry.analysis.mapper;

import lombok.Data;
import org.openmrs.module.pihcore.htmlformentry.analysis.HtmlFormTag;

@Data
public class ObsGroupMapper implements TagMapper {

    @Override
    public HtmlFormTag map(HtmlFormTag inputTag) {
        HtmlFormTag outputTag = inputTag.cloneWithoutChildren();
        for (HtmlFormTag childTag : inputTag.getChildTags()) {
            childTag = map(childTag);
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
        ParentToChildMerger parentToChildMerger = new ParentToChildMerger("obsGroup");
        return parentToChildMerger.map(outputTag);
    }
}
