package org.openmrs.module.pihcore.htmlformentry.analysis.processor;

import lombok.Data;
import org.openmrs.module.pihcore.htmlformentry.analysis.DataSet;
import org.openmrs.module.pihcore.htmlformentry.analysis.HtmlFormTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

@Data
public class TagWriter implements TagProcessor {

    private File outputFile;

    private final Logger log = LoggerFactory.getLogger(TagWriter.class);

    public DataSet process(List<HtmlFormTag> tags) throws Exception {
        DataSet ret = new DataSet();
        ret.setHeaderRow("inputFile", "outputData");
        for (HtmlFormTag tag : tags) {
            try (StringWriter writer = new StringWriter()) {
                write(tag, writer, "  ");
                String data = writer.toString();
                ret.addDataRow(tag.getHtmlFormFile().getName(), data);
            }
        }
        return ret;
    }

    public static void write(HtmlFormTag tag, Writer writer, String prefix) throws IOException {
        write(tag, writer, "", prefix);
    }

    private static void write(HtmlFormTag tag, Writer writer, String prefix, String prefixIncrement) throws IOException {
        writer.write(prefix + tag + System.lineSeparator());
        for (HtmlFormTag childTag : tag.getChildTags()) {
            write(childTag, writer, prefix + prefixIncrement, prefix);
        }
    }
}
