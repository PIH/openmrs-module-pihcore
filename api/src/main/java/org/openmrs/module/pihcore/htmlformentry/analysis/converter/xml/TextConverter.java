package org.openmrs.module.pihcore.htmlformentry.analysis.converter.xml;

import lombok.Data;
import org.openmrs.module.pihcore.htmlformentry.analysis.converter.Converter;

@Data
public class TextConverter implements Converter<String, String> {

    private String find;
    private String replace;

    public TextConverter(String find, String replace) {
        this.find = find;
        this.replace = replace;
    }

    @Override
    public String convert(String input) throws Exception {
        return input.replace(find, replace);
    }
}
