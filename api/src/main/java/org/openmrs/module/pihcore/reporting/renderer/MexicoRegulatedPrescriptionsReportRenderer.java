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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.renderer.RenderingException;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.renderer.ReportTemplateRenderer;
import org.openmrs.util.OpenmrsUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.ibm.icu.text.PluralRules.Operand.i;

/**
 * Renderer for the Mexico regulated prescriptions report
 */
@Handler
public class MexicoRegulatedPrescriptionsReportRenderer extends ReportTemplateRenderer {

    private Log log = LogFactory.getLog(this.getClass());

    File getTemplateFile() {
        return new File(OpenmrsUtil.getApplicationDataDirectory(), "configuration/pih/images/recetas-template.pdf");
    }

    @Override
    public void render(ReportData reportData, String argument, OutputStream out) throws IOException, RenderingException {
        log.debug("Rendering Mexico Regulated Prescriptions Report...");
        try (PDDocument pdf = new PDDocument()) {
            try (PDDocument template = Loader.loadPDF(getTemplateFile())) {
                SimpleDataSet encounterDataSet = (SimpleDataSet) reportData.getDataSets().get("encounters");
                Map<String, Map<String, Object>> encounterData = new LinkedHashMap<>();
                for (DataSetRow row : encounterDataSet.getRows()) {
                    String encounterId = row.getColumnValue("encounter_id").toString();
                    encounterData.put(encounterId, row.getColumnValuesByKey());
                    encounterData.get(encounterId).put("prescriptions", new ArrayList<>());
                }
                SimpleDataSet prescriptionDataSet = (SimpleDataSet) reportData.getDataSets().get("prescriptions");
                for (DataSetRow row : prescriptionDataSet.getRows()) {
                    String encounterId = row.getColumnValue("encounter_id").toString();
                    List<Map<String, Object>> prescriptions = (List<Map<String, Object>>)encounterData.get(encounterId).get("prescriptions");
                    prescriptions.add(row.getColumnValuesByKey());
                }
                for (String encounterId : encounterData.keySet()) {
                    addPrescription(pdf, template, encounterData.get(encounterId), 1);
                }
                pdf.save(out);
            }
        }
    }

    void addPrescription(PDDocument pdf, PDDocument template, Map<String, Object> encounterData, int pageNum) throws IOException {
        PDAcroForm pdfTemplateForm = template.getDocumentCatalog().getAcroForm();
        PDAcroForm pdfForm = new PDAcroForm(pdf);
        pdfForm.setDefaultResources(pdfTemplateForm.getDefaultResources());
        pdf.getDocumentCatalog().setAcroForm(pdfForm);

        PDPage templatePage = template.getPage(0);
        COSDictionary newDict = new COSDictionary(templatePage.getCOSObject());
        newDict.removeItem(COSName.ANNOTS);
        PDPage newPage = new PDPage(newDict);
        pdf.addPage(newPage);

        // Populate the form fields where they are present
        List<PDField> fields = new ArrayList<>();
        populateFormField(newPage, pdfForm, pdfTemplateForm, fields, "date", encounterData.get("encounter_date"));
        populateFormField(newPage, pdfForm, pdfTemplateForm, fields, "patientName", encounterData.get("patient_name"));
        populateFormField(newPage, pdfForm, pdfTemplateForm, fields, "age", encounterData.get("age"));
        populateFormField(newPage, pdfForm, pdfTemplateForm, fields, "diagnosis", encounterData.get("diagnoses"));
        populateFormField(newPage, pdfForm, pdfTemplateForm, fields, "allergies", encounterData.get("allergies"));
        pdfForm.setFields(fields);
        pdfForm.flatten(fields, true);

        // Populate the prescriptions
        int drugNameIndex = 30;
        int doseIndex = 225;
        int morningIndex = 280;
        int noonIndex = 345;
        int afternoonIndex = 430;
        int eveningIndex = 495;
        int durationIndex = 555;
        int instructionsIndex = 640;

        int yIndex = 395;
        List<Map<String, Object>> prescriptions = (List<Map<String, Object>>)encounterData.get("prescriptions");
        try (PDPageContentStream content = new PDPageContentStream(pdf, newPage, PDPageContentStream.AppendMode.APPEND,true,true)) {
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
            for (Iterator<Map<String, Object>> i = prescriptions.iterator(); i.hasNext();) {
                Map<String, Object> m = i.next();
                List<Integer> medLines = new ArrayList<>();
                medLines.add(addAndWrapText(content, yIndex + ": " + m.get("name"), drugNameIndex, yIndex, 40));
                List<Integer> dose1Lines = new ArrayList<>();
                dose1Lines.add(addAndWrapText(content, formatQtyAndUnits(m.get("dose_1"), m.get("dose_1_units")), doseIndex, yIndex, 12));
                dose1Lines.add(addAndWrapText(content, m.get("dose_1_morning"), morningIndex, yIndex, 12));
                dose1Lines.add(addAndWrapText(content, m.get("dose_1_noon"), noonIndex, yIndex, 12));
                dose1Lines.add(addAndWrapText(content, m.get("dose_1_afternoon"), afternoonIndex, yIndex, 12));
                dose1Lines.add(addAndWrapText(content, m.get("dose_1_evening"), eveningIndex, yIndex, 12));
                int dose1LinesUsed = Collections.max(dose1Lines);
                yIndex = yIndex - 10*dose1LinesUsed;
                List<Integer> dose2Lines = new ArrayList<>();
                dose2Lines.add(addAndWrapText(content, formatQtyAndUnits(m.get("dose_2"), m.get("dose_2_units")), doseIndex, yIndex, 12));
                dose2Lines.add(addAndWrapText(content, m.get("dose_2_morning"), morningIndex, yIndex, 12));
                dose2Lines.add(addAndWrapText(content, m.get("dose_2_noon"), noonIndex, yIndex, 12));
                dose2Lines.add(addAndWrapText(content, m.get("dose_2_afternoon"), afternoonIndex, yIndex, 12));
                dose2Lines.add(addAndWrapText(content, m.get("dose_2_evening"), eveningIndex, yIndex, 12));
                int dose2LinesUsed = Collections.max(dose2Lines);
                yIndex = yIndex + 10*dose1LinesUsed;
                medLines.add(dose1LinesUsed + dose2LinesUsed);
                medLines.add(addAndWrapText(content, formatQtyAndUnits(m.get("duration"), m.get("duration_units")), durationIndex, yIndex, 12));
                medLines.add(addAndWrapText(content, m.get("instructions"), instructionsIndex, yIndex, 40));
                int linesUsed = Collections.max(medLines);

                yIndex = yIndex - 10*linesUsed;

                if (i.hasNext()) {
                    content.setStrokingColor(1.0f, 0.59608f, 0.0f);
                    content.moveTo(30, yIndex);
                    content.lineTo(755, yIndex);
                    content.stroke();
                    yIndex -= 20;
                }
            }
        }
    }

    int addAndWrapText(PDPageContentStream content, Object value, int x, int y, int maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        String formattedValue = ObjectUtil.format(value);
        if (StringUtils.isNotBlank(formattedValue)) {
            StringBuilder currentLine = new StringBuilder();
            for (String word : formattedValue.split(" ")) {
                if (currentLine.length() + word.length() > maxWidth) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder();
                }
                currentLine.append(word).append(" ");
            }
            if (currentLine.length() > 0) {
                lines.add(currentLine.toString());
            }

            for (Iterator<String> i = lines.iterator(); i.hasNext(); ) {
                content.beginText();
                content.newLineAtOffset(x, y);
                content.showText(i.next());
                content.endText();
                if (i.hasNext()) {
                    y = y - 10;
                }
            }
        }
        return lines.size();
    }

    void addText(PDPageContentStream content, String value, int x, int y) throws IOException {
        content.beginText();
        content.newLineAtOffset(x, y);
        content.showText(value);
        content.endText();
    }

    String formatQtyAndUnits(Object dose, Object doseUnits) {
        if (ObjectUtil.isNull(dose)) {
            return "";
        }
        if (ObjectUtil.isNull(doseUnits)) {
            return ObjectUtil.format(dose);
        }
        return ObjectUtil.format(dose) + " " + ObjectUtil.format(doseUnits).toLowerCase();
    }

    void populateFormField(PDPage page, PDAcroForm pdfForm, PDAcroForm pdfTemplateForm, List<PDField> fields, String pdfFieldName, Object value) throws IOException {
        PDTextField templateField = (PDTextField) pdfTemplateForm.getField(pdfFieldName);
        PDTextField newField = new PDTextField(pdfForm);
        newField.setPartialName(templateField.getPartialName());
        newField.setDefaultAppearance(templateField.getDefaultAppearance());
        newField.setValue(ObjectUtil.format(value));
        List<PDAnnotationWidget> newWidgets = new ArrayList<>();
        for (PDAnnotationWidget originalWidget : templateField.getWidgets()) {
            PDAnnotationWidget newWidget = new PDAnnotationWidget();
            newWidget.getCOSObject().setString(COSName.DA, originalWidget.getCOSObject().getString(COSName.DA));
            newWidget.setRectangle(originalWidget.getRectangle());
            newWidgets.add(newWidget);
        }
        newField.setWidgets(newWidgets);
        fields.add(newField);
        page.getAnnotations().addAll(newWidgets);
    }

    /**
     * @see ReportRenderer#getFilename(ReportRequest)
     */
    @Override
    public String getFilename(ReportRequest request) {
        return getFilenameBase(request) + ".pdf";
    }
}
