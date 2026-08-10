package org.openmrs.module.pihcore.apploader.apps.patientregistration;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.LesothoConfigConstants;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.registrationapp.model.Field;
import org.openmrs.module.registrationapp.model.Question;
import org.openmrs.module.registrationapp.model.RegistrationAppConfig;
import org.openmrs.module.registrationapp.model.Section;

public class SectionsLesotho extends SectionsDefault {

    public SectionsLesotho(Config config) {
        super(config);
    }

    @Override
    public void addSections(RegistrationAppConfig c) {
        c.addSection(getIdentifierSection());
    }

    @Override
    public Section getIdentifierSection() {
        Section s = new Section();
        s.setId("patient-identification-section");
        s.setLabel("registrationapp.patient.identifiers.label");

        s.addQuestion(getNationalId());
        s.addQuestion(getMDRTBlId());
        s.addQuestion(getDSTBId());

        return s;
    }

    private Question getNationalId() {

        PatientIdentifierType nationalId = Context.getPatientService().getPatientIdentifierTypeByUuid(LesothoConfigConstants.PATIENTIDENTIFIERTYPE_NATIONAL_ID_UUID);

        Question q = new Question();
        q.setId("nationalId");
        q.setLegend("National ID");
        q.setHeader("ui.i18n.PatientIdentifierType.name." + LesothoConfigConstants.PATIENTIDENTIFIERTYPE_NATIONAL_ID_UUID);

        Field f = new Field();
        f.setFormFieldName("patientIdentifier" + LesothoConfigConstants.PATIENTIDENTIFIERTYPE_NATIONAL_ID_UUID);
        f.setUuid(LesothoConfigConstants.PATIENTIDENTIFIERTYPE_NATIONAL_ID_UUID);
        f.setType("patientIdentifier");
        if (StringUtils.isNotBlank(nationalId.getFormat())) {
            f.setCssClasses(Arrays.asList("regex"));
            f.setWidget(getTextFieldWidget(16, nationalId.getFormat()));
        } else {
            f.setWidget(getTextFieldWidget(16));
        }

        q.addField(f);
        return q;
    }

    private Question getMDRTBlId() {

        PatientIdentifierType mdrtbId = Context.getPatientService().getPatientIdentifierTypeByUuid(LesothoConfigConstants.PATIENTIDENTIFIERTYPE_MDRTB_ID_UUID);

        Question q = new Question();
        q.setId("mdrtbId");
        q.setLegend("MDRTB ID");
        q.setHeader("ui.i18n.PatientIdentifierType.name." + LesothoConfigConstants.PATIENTIDENTIFIERTYPE_MDRTB_ID_UUID);

        Field f = new Field();
        f.setFormFieldName("patientIdentifier" + LesothoConfigConstants.PATIENTIDENTIFIERTYPE_MDRTB_ID_UUID);
        f.setUuid(LesothoConfigConstants.PATIENTIDENTIFIERTYPE_MDRTB_ID_UUID);
        f.setType("patientIdentifier");
        if (StringUtils.isNotBlank(mdrtbId.getFormat())) {
            f.setCssClasses(Arrays.asList("regex"));
            f.setWidget(getTextFieldWidget(16, mdrtbId.getFormat()));
        } else {
            f.setWidget(getTextFieldWidget(16));
        }

        q.addField(f);
        return q;
    }

    private Question getDSTBId() {

        PatientIdentifierType dstbId = Context.getPatientService().getPatientIdentifierTypeByUuid(LesothoConfigConstants.PATIENTIDENTIFIERTYPE_DSTB_ID_UUID);

        Question q = new Question();
        q.setId("dstbId");
        q.setLegend("DSTB ID");
        q.setHeader("ui.i18n.PatientIdentifierType.name." + LesothoConfigConstants.PATIENTIDENTIFIERTYPE_DSTB_ID_UUID);

        Field f = new Field();
        f.setFormFieldName("patientIdentifier" + LesothoConfigConstants.PATIENTIDENTIFIERTYPE_DSTB_ID_UUID);
        f.setUuid(LesothoConfigConstants.PATIENTIDENTIFIERTYPE_DSTB_ID_UUID);
        f.setType("patientIdentifier");
        if (StringUtils.isNotBlank(dstbId.getFormat())) {
            f.setCssClasses(Arrays.asList("regex"));
            f.setWidget(getTextFieldWidget(16, dstbId.getFormat()));
        } else {
            f.setWidget(getTextFieldWidget(16));
        }

        q.addField(f);
        return q;
    }

}
