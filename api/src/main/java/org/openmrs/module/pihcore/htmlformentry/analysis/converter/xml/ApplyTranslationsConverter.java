package org.openmrs.module.pihcore.htmlformentry.analysis.converter.xml;

import lombok.Data;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.HtmlFormEntryGenerator;
import org.openmrs.module.htmlformentry.Translator;
import org.openmrs.module.pihcore.htmlformentry.analysis.converter.Converter;

@Data
public class ApplyTranslationsConverter implements Converter<String, String> {

    @Override
    public String convert(String input) throws Exception {
        HtmlFormEntryGenerator generator = new HtmlFormEntryGenerator();
        return generator.applyTranslations(input, new FormEntryContext(FormEntryContext.Mode.EDIT) {
            @Override
            public Translator getTranslator() {
                return new Translator() {
                    @Override
                    public String translate(String localeStr, String key) {
                        String ret = getTranslations(localeStr).get(key);
                        return (ret == null ? key : ret);
                    }
                };
            }
        });
    }
}
