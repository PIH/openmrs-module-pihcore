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
        super.addSections(c);
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
        return getIdentifierQuestion("nationalId", "National ID", LesothoConfigConstants.PATIENTIDENTIFIERTYPE_NATIONAL_ID_UUID);
    }

    private Question getMDRTBlId() {
        return getIdentifierQuestion("mdrtbId", "MDRTB ID", LesothoConfigConstants.PATIENTIDENTIFIERTYPE_MDRTB_ID_UUID);
    }

    private Question getDSTBId() {
        return getIdentifierQuestion("dstbId", "DSTB ID", LesothoConfigConstants.PATIENTIDENTIFIERTYPE_DSTB_ID_UUID);
    }

    private Question getIdentifierQuestion(String questionId, String legend, String identifierTypeUuid) {

        PatientIdentifierType identifierType = Context.getPatientService().getPatientIdentifierTypeByUuid(identifierTypeUuid);

        Question q = new Question();
        q.setId(questionId);
        q.setLegend(legend);
        q.setHeader("ui.i18n.PatientIdentifierType.name." + identifierTypeUuid);

        Field f = new Field();
        f.setFormFieldName("patientIdentifier" + identifierTypeUuid);
        f.setUuid(identifierTypeUuid);
        f.setType("patientIdentifier");
        if (StringUtils.isNotBlank(identifierType.getFormat())) {
            f.setCssClasses(Arrays.asList("regex"));
            f.setWidget(getTextFieldWidget(16, identifierType.getFormat()));
        } else {
            f.setWidget(getTextFieldWidget(16));
        }

        q.addField(f);
        return q;
    }

}
