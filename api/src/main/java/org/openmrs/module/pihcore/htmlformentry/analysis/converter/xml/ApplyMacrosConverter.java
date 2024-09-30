package org.openmrs.module.pihcore.htmlformentry.analysis.converter.xml;

import lombok.Data;
import org.openmrs.module.htmlformentry.HtmlFormEntryGenerator;
import org.openmrs.module.pihcore.htmlformentry.analysis.converter.Converter;

@Data
public class ApplyMacrosConverter implements Converter<String, String> {

    @Override
    public String convert(String input) throws Exception {
        HtmlFormEntryGenerator generator = new HtmlFormEntryGenerator();
        return generator.applyMacros(input);
    }
}
