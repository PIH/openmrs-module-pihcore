package org.openmrs.module.pihcore.htmlformentry.analysis.converter.tag;

import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.openmrs.module.pihcore.htmlformentry.analysis.HtmlFormTag;
import org.openmrs.module.pihcore.htmlformentry.analysis.converter.Converter;

@Data
public class ObsConverter implements Converter<HtmlFormTag, HtmlFormTag> {

    @Override
    public HtmlFormTag convert(HtmlFormTag input) throws Exception {
        // Any of these will effectively show the comment field, so update that if needed
        if (!input.hasAttribute("showCommentField", "true")) {
            if (input.hasAttribute("commentFieldLabel") || input.hasAttribute("commentFieldCode")) {
                input.addAttribute("showCommentField", "true");
            }
        }
        return input;
    }
}
