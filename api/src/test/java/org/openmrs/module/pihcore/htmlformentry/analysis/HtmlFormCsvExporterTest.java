package org.openmrs.module.pihcore.htmlformentry.analysis;

import com.opencsv.CSVWriter;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HtmlFormCsvExporterTest {

    private static final Logger log = LoggerFactory.getLogger(HtmlFormNonMemoryTest.class);

    public File getHtmlFormsDirectory() {
        return new File("/home/mseaton/code/github/pih/openmrs-config-pihsl/target/openmrs-packager-config/configuration/pih/htmlforms");
    }

    @Test
    public void execute() throws Exception {
        generateCsvForForm(new File(getHtmlFormsDirectory(), "mentalHealthFollowup.xml"));
    }

    /*
    public void generateTagSummary() throws Exception {
        List<File> htmlFormFiles = HtmlFormUtils.getNestedFilesBySuffix(getHtmlFormsDirectory(), ".xml");
        Map<String, HtmlFormCsvExporter> parsedForms = new TreeMap<>();
        for (File inputFile : htmlFormFiles) {
            try {
                HtmlFormCsvExporter parser = new HtmlFormCsvExporter();
                parser.parseFile(inputFile);
                parsedForms.put(inputFile.getName(), parser);
            }
            catch (Exception e) {
                log.error("Error parsing form: " + inputFile.getName());
            }
        }
        File outputFile = new File(getHtmlFormsDirectory(), "formSummary.csv");
        try (FileWriter fileWriter = new FileWriter(outputFile)) {
            try (CSVWriter csvWriter = new CSVWriter(fileWriter)) {
                String[] csvColumns = {"form", "tag", "comments"};
                csvWriter.writeNext(csvColumns);
                for (String formName : parsedForms.keySet()) {
                    HtmlFormCsvExporter parser = parsedForms.get(formName);
                    for (Map<String, String> row : parser.getRows()) {
                        String[] data = new String[csvColumns.length];
                        data[0] = formName;
                        data[1] = row.get("tagName");
                        data[2] = row.get("additionalConfiguration");
                        csvWriter.writeNext(data);
                    }
                }
            }
        }
    }

     */

    public void generateCsvForAllForms() {
        List<File> htmlFormFiles = HtmlFormUtils.getNestedFilesBySuffix(getHtmlFormsDirectory(), ".xml");
        for (File inputFile : htmlFormFiles) {
            try {
                generateCsvForForm(inputFile);
            }
            catch (Exception e) {
                log.error("Unable to generate CSV for form: " + inputFile.getName());
            }
        }
    }

    public void generateCsvForForm(File inputFile) throws Exception {
        File outputFile = new File(inputFile.getAbsolutePath() + ".csv");
        log.info("Generating schema for " + inputFile.getName());
        log.info("TagRegister loaded for " + inputFile.getName());
        HtmlFormCsvExporter exporter = new HtmlFormCsvExporter();
        exporter.export(inputFile);
        /*
        String[] columns = parser.getColumns().toArray(new String[0]);
        List<Map<String, String>> rows = parser.getRows();
        log.info("TagRegister parsed into " + columns.length + " columns and " + rows.size() + " rows");
        try (FileWriter fileWriter = new FileWriter(outputFile)) {
            try (CSVWriter csvWriter = new CSVWriter(fileWriter)) {
                csvWriter.writeNext(columns); // Header
                for (Map<String, String> row : rows) {
                    String[] rowArray = new String[columns.length];
                    for (int i=0; i<columns.length; i++) {
                        String value = row.get(columns[i]);
                        rowArray[i] = (value == null ? "" : value);
                    }
                    csvWriter.writeNext(rowArray);
                }
            }
        }

         */
        log.info(inputFile.getName() + " processed successfully");
    }
}
