/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.pihcore.reporting.renderer;

import com.opencsv.CSVReader;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.util.OpenmrsClassLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * To run this test, remove the Disabled annotation, and update the getRenderer method to point the appropriate template location
 */
@Disabled
public class MexicoRegulatedPrescriptionsReportRendererTest {

    ReportData createReportData() {
        ReportData reportData = new ReportData();
        reportData.getDataSets().put("encounters", loadCsvIntoDataSet("regulated_prescriptions_encounters.csv"));
        reportData.getDataSets().put("prescriptions", loadCsvIntoDataSet("regulated_prescriptions_meds.csv"));
        return reportData;
    }

    @Test
    public void testRenderer() throws Exception {
        try (FileOutputStream out = new FileOutputStream("/tmp/mexico.pdf")) {
            ReportData reportData = createReportData();
            MexicoRegulatedPrescriptionsReportRenderer renderer = getRenderer();
            renderer.render(reportData, "pdf", out);
        }
    }

    MexicoRegulatedPrescriptionsReportRenderer getRenderer() {
        return new MexicoRegulatedPrescriptionsReportRenderer() {
            @Override
            File getTemplateFile() {
                return new File("/home/mseaton/code/github/pih/openmrs-config-ces/configuration/pih/images/recetas-template.pdf");
            }
        };
    }

    SimpleDataSet loadCsvIntoDataSet(String resourceName) {
        SimpleDataSet dataset = new SimpleDataSet(null, null);
        try (InputStream in = OpenmrsClassLoader.getInstance().getResourceAsStream(resourceName)) {
            try (Reader reader = new InputStreamReader(in)) {
                try (CSVReader csvReader = new CSVReader(reader)) {
                    String[] headerRow = csvReader.readNext();
                    for (String[] line = csvReader.readNext(); line != null; line = csvReader.readNext()) {
                        DataSetRow row = new DataSetRow();
                        for (int i = 0; i < headerRow.length; i++) {
                            String header = headerRow[i];
                            row.addColumnValue(new DataSetColumn(header, header, String.class), line[i]);
                        }
                        dataset.addRow(row);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return dataset;
    }
}
