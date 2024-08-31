package org.openmrs.module.pihcore.htmlformentry.analysis;

import org.junit.jupiter.api.Test;

import java.io.File;

public class HtmlFormTagCounterTest {

    public File getHtmlFormsDirectory() {
        return new File("/home/mseaton/code/github/pih/openmrs-config-pihsl/target/openmrs-packager-config/configuration/pih/htmlforms");
    }

    @Test
    public void execute() throws Exception {
        HtmlFormTagCounter analyzer = new HtmlFormTagCounter();
        analyzer.analyze(getHtmlFormsDirectory());
        analyzer.writeToCsv(new File(getHtmlFormsDirectory(), "tagAnalysis.csv"));
    }
}
