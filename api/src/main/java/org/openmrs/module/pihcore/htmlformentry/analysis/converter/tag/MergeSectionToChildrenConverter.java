package org.openmrs.module.pihcore.htmlformentry.analysis.converter.tag;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.pihcore.htmlformentry.analysis.HtmlFormUtils;

import java.util.HashMap;
import java.util.Map;

public class MergeSectionToChildrenConverter extends MergeParentToChildrenConverter {

    public MergeSectionToChildrenConverter() {
        super("section", " > ");
    }

    @Override
    public Map<String, String> computeAttributesToMerge(Map<String, String> inputAttributes) {
        String section = inputAttributes.get("headerCode");
        if (StringUtils.isBlank(section)) {
            section = HtmlFormUtils.mapToString(inputAttributes, "=", ",");
        }
        Map<String, String> ret = new HashMap<>();
        ret.put("section", section);
        return ret;
    }
}
