package org.openmrs.module.pihcore.htmlformentry.analysis;

import org.junit.jupiter.api.Test;
import org.openmrs.module.pihcore.htmlformentry.analysis.processor.TagCounter;
import org.openmrs.module.pihcore.htmlformentry.analysis.processor.TagExporter;

import javax.xml.crypto.Data;
import java.io.File;

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
}
