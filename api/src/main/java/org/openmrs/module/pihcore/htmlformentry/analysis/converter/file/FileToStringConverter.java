package org.openmrs.module.pihcore.htmlformentry.analysis.converter.file;

import org.apache.commons.io.FileUtils;
import org.openmrs.module.pihcore.htmlformentry.analysis.converter.Converter;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class FileToStringConverter implements Converter<File, String> {

    public String convert(File input) throws Exception {
        return FileUtils.readFileToString(input, StandardCharsets.UTF_8);
    }
}
