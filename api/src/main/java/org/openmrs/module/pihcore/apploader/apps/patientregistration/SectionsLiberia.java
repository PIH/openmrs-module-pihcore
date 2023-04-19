package org.openmrs.module.pihcore.apploader.apps.patientregistration;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.pihcore.config.Components;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.registrationapp.model.DropdownWidget;
import org.openmrs.module.registrationapp.model.Field;
import org.openmrs.module.registrationapp.model.Question;
import org.openmrs.module.registrationapp.model.RegistrationAppConfig;
import org.openmrs.module.registrationapp.model.Section;

public class SectionsLiberia extends SectionsDefault {

    private Config config;

    public SectionsLiberia(Config config) {
        super(config);
        this.config = config;
    }

    @Override
    public void addSections(RegistrationAppConfig c) {

        if (config.isComponentEnabled(Components.BIOMETRICS_FINGERPRINTS)) {
            c.addSection(getBiometricsSection());
        }

        c.addSection(getDemographicsSection());
        c.addSection(getContactInfoSection());
        c.addSection(getSocialSection());
        c.addSection(getPatientSupportSection());
        c.addSection(getIdentifierSection());

        if (config.isComponentEnabled(Components.ID_CARD_PRINTING)) {
            c.addSection(getIdCardPrintSection());
        }
    }

    @Override
    public Section getSocialSection() {
        Section s = new Section();
        s.setId("social");
        s.setLabel("zl.registration.patient.social.label");
        s.addQuestion(getBirthplaceQuestion());
        s.addQuestion(getOccupationQuestion());
        return s;
    }

    @Override
    public Question getOccupationQuestion() {
        Question q = new Question();
        q.setId("occupationLabel");
        q.setLegend("zl.registration.patient.occupation.label");
        q.setHeader("zl.registration.patient.occupation.question");

        Field f = new Field();
        f.setFormFieldName("obs.PIH:Occupation");
        f.setType("obs");

        DropdownWidget w = new DropdownWidget();

        w.getConfig().addOption("PIH:STUDENT", "zl.registration.patient.occupation.student.label");
        w.getConfig().addOption("PIH:RETIRED", "zl.registration.patient.occupation.retired.label");
        w.getConfig().addOption("CIEL:159674", "zl.registration.patient.occupation.fisherman.label");
        w.getConfig().addOption("PIH:FARMER", "zl.registration.patient.occupation.farmer.label");
        w.getConfig().addOption("PIH:DRIVER", "zl.registration.patient.occupation.driver.label");
        w.getConfig().addOption("PIH:1404", "zl.registration.patient.occupation.housework.label");
        w.getConfig().addOption("PIH:PROFESSIONAL", "zl.registration.patient.occupation.professional.label");
        w.getConfig().addOption("CIEL:163714", "pih.registration.patient.occupation.smallChild.label");
        w.getConfig().addOption("CIEL:165494", "pih.registration.patient.occupation.childNotInSchool.label");
        w.getConfig().addOption("CIEL:161382", "pih.registration.patient.occupation.selfEmployed.label");
        w.getConfig().addOption("CIEL:164832", "pih.registration.patient.occupation.cleaner.label");
        w.getConfig().addOption("PIH:HOUSEWORK/FIELDWORK", "zl.registration.patient.occupation.plantationWorker.label");
        w.getConfig().addOption("PIH:SHOP OWNER", "zl.registration.patient.occupation.shopOwner.label");
        w.getConfig().addOption("PIH:OTHER NON-CODED", "zl.registration.patient.occupation.other.label");

        w.getConfig().setExpanded(true);
        f.setWidget(toObjectNode(w));
        q.addField(f);

        return q;
    }

    private Section getPatientSupportSection() {
        Section s = new Section();
        s.setId("patientSupport");
        s.setLabel("pih.registration.patientSupport.section.label");
        s.setQuestions(getPatientSupportQuestions());
        return s;
    }

    private List<Question> getPatientSupportQuestions() {

        List<Question> questions = new ArrayList<>();

        Question q1 = new Question();
        q1.setId("patientReferredOrEscortedQuestion");
        q1.setLegend("pih.registration.patientSupport.refOrEsc");
        {
            Field f = new Field();
            f.setFormFieldName("obsgroup.PIH:14493.obs.PIH:6547");
            f.setLabel("pih.registration.patientSupport.typeOfSupport");
            f.setType("obsgroup");
            f.setWidget(getTextFieldWidget(50));
            DropdownWidget w = new DropdownWidget();
            w.getConfig().addOption("PIH:11521", "pih.referred");
            w.getConfig().addOption("PIH:14646", "pih.escorted");
            w.getConfig().setExpanded(true);
            f.setWidget(toObjectNode(w));
            q1.addField(f);
        }
        questions.add(q1);

        Question q2 = new Question();
        q2.setId("patientTypeOfSupportQuestion");
        q2.setLegend("pih.registration.patientSupport.roleType");
        {
            Field f = new Field();
            f.setFormFieldName("obsgroup.PIH:14493.obs.PIH:14494");
            f.setLabel("pih.registration.patientSupport.type.label");
            f.setType("obsgroup");
            DropdownWidget w = new DropdownWidget();
            w.getConfig().addOption("PIH:14496", "pih.registration.following.type.cha");
            w.getConfig().addOption("PIH:14495", "pih.registration.following.type.chp");
            w.getConfig().addOption("PIH:14645", "pih.registration.following.type.chv");
            w.getConfig().addOption("PIH:13489", "pih.registration.following.type.tba");
            w.getConfig().addOption("PIH:14502", "pih.registration.following.type.pssa");
            w.getConfig().addOption("PIH:14649", "pih.registration.selfOrFamily");
            w.getConfig().setExpanded(true);
            f.setWidget(toObjectNode(w));
            q2.addField(f);
        }
        questions.add(q2);


        Question q3 = new Question();
        q3.setId("patientSupportLocationQuestion");
        q3.setLegend("pihcore.encounterList.location");
        {
            Field f = new Field();
            f.setFormFieldName("obsgroup.PIH:14493.obs.PIH:6402");
            f.setLabel("pih.registration.patientSupport.location.label");
            f.setType("obsgroup");
            f.setWidget(getTextFieldWidget(50));
            q3.addField(f);
        }
        questions.add(q3);


        Question q4 = new Question();
        q4.setId("patientSupportNameQuestion");
        q4.setLegend("pih.registration.patientSupport.name.label");
        {
            Field f = new Field();
            f.setFormFieldName("obsgroup.PIH:14493.obs.CIEL:164141");
            f.setLabel("pih.registration.patientSupport.name.label");
            f.setType("obsgroup");
            f.setWidget(getTextFieldWidget(50));
            q4.addField(f);
        }
        questions.add(q4);

        Question q5 = new Question();
        q5.setId("patientSupportPhoneQuestion");
        q5.setLegend("pih.registration.patientSupport.phone.label");
        {
            Field f = new Field();
            f.setFormFieldName("obsgroup.PIH:14493.obs.PIH:2614");
            f.setLabel("pih.registration.patientSupport.phone.label");
            f.setType("obsgroup");
            f.setWidget(getTextFieldWidget(50));
            q5.addField(f);
        }
        questions.add(q5);

        return questions;
    }
}
