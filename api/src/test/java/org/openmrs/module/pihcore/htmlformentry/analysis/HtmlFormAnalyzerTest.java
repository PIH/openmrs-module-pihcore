package org.openmrs.module.pihcore.htmlformentry.analysis;

import org.junit.jupiter.api.Test;
import org.openmrs.module.pihcore.htmlformentry.analysis.processor.TagCounter;
import org.openmrs.module.pihcore.htmlformentry.analysis.processor.TagExporter;
import org.openmrs.module.pihcore.htmlformentry.analysis.processor.TagWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Map;

public class HtmlFormAnalyzerTest {

    public File getHtmlFormsDirectory() {
        return new File("/home/mseaton/code/github/pih/openmrs-config-pihsl/target/openmrs-packager-config/configuration/pih/htmlforms");
    }

    @Test
    public void generateCountsOfAllTags() throws Exception {
        HtmlFormAnalyzer analyzer = new HtmlFormAnalyzer();
        DataSet dataSet = analyzer.analyze(getHtmlFormsDirectory(), new TagCounter());
        dataSet.toCsv(new File(getHtmlFormsDirectory(), "tagAnalysis.csv"), ',');
        dataSet.print("\t");
    }

    @Test
    public void generateCsvForForms() throws Exception {
        HtmlFormAnalyzer analyzer = new HtmlFormAnalyzer();
        File htmlForm = new File(getHtmlFormsDirectory(), "mentalHealthFollowup.xml");
        DataSet dataSet = analyzer.analyze(htmlForm, new TagExporter());
        dataSet.print("\t");
    }

    @Test
    public void writeFormData() throws Exception {
        HtmlFormAnalyzer analyzer = new HtmlFormAnalyzer();
        DataSet dataSet = analyzer.analyze(getHtmlFormsDirectory(), new TagWriter());
        try (FileWriter writer = new FileWriter(new File(getHtmlFormsDirectory(), "tagAnalysis.txt"))) {
            for (Map<String, String> rows : dataSet.getRows()) {
                writer.write(rows.get("inputFile") + System.lineSeparator());
                writer.write("=============================" + System.lineSeparator());
                writer.write(rows.get("outputData") + System.lineSeparator());
            }
        }
    }

    @Test
    public void writeMentalHealthForm() throws Exception {
        HtmlFormAnalyzer analyzer = new HtmlFormAnalyzer();
        DataSet dataSet = analyzer.analyze(new File(getHtmlFormsDirectory(), "admissionNote.xml"), new TagWriter());
        try (PrintWriter writer = new PrintWriter(System.out)) {
            for (Map<String, String> rows : dataSet.getRows()) {
                writer.write(rows.get("outputData") + System.lineSeparator());
            }
        }
    }
}
