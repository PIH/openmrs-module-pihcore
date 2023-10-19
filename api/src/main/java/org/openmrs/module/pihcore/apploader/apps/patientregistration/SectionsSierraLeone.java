package org.openmrs.module.pihcore.apploader.apps.patientregistration;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.addresshierarchy.AddressField;
import org.openmrs.module.addresshierarchy.AddressHierarchyLevel;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.openmrs.module.pihcore.SierraLeoneConfigConstants;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.registration.ContactPersonConfigDescriptor;
import org.openmrs.module.pihcore.config.registration.PersonRelationshipConfigDescriptor;
import org.openmrs.module.registrationapp.model.DropdownWidget;
import org.openmrs.module.registrationapp.model.Field;
import org.openmrs.module.registrationapp.model.Question;
import org.openmrs.module.registrationapp.model.RegisterPersonRelationshipWidget;
import org.openmrs.module.registrationapp.model.RegistrationAppConfig;
import org.openmrs.module.registrationapp.model.Section;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SectionsSierraLeone extends SectionsDefault {

    private Config config;

    public SectionsSierraLeone(Config config) {
        super(config);
        this.config = config;
    }

    public void addSections(RegistrationAppConfig c) {

        c.addSection(getDemographicsSection());
        c.addSection(getContactInfoSection());
        c.addSection(getSocialSection());
        c.addSection(getPersonRelationshipsSection());
        c.addSection(getRelationshipsSection());
        c.addSection(getContactsSection());
        c.addSection(getLocalContactSection());
        c.addSection(getIdentifierSection());
        c.addSection(getIdCardPrintSection());
    }

    private Section getPersonRelationshipsSection() {
        Section s = new Section();
        s.setId("registerRelationships");
        s.setLabel("registrationapp.person.relationship");

        List<PersonRelationshipConfigDescriptor> relationships = config.getRegistrationConfig().getRelationships();
        if (relationships != null && relationships.size() > 0) {
            for (PersonRelationshipConfigDescriptor relationship : relationships) {
                relationship.getRelationshipType();
                Question q = new Question();
                q.setId("relationships_" + relationship.getId());
                q.setLegend(relationship.getLabel());
                q.setHeader(relationship.getLabel());

                Field field = new Field();
                field.setFormFieldName(relationship.getId() + "Name");
                field.setType(relationship.getId() + "relationship");
                RegisterPersonRelationshipWidget widget = new RegisterPersonRelationshipWidget();
                widget.getConfig().setRelationshipType(relationship.getRelationshipType());
                widget.getConfig().setRelationshipDirection(relationship.getRelationshipDirection());
                widget.getConfig().setMultipleValues(relationship.getMultipleValues());
                widget.getConfig().setRequired(relationship.getRequired());
                widget.getConfig().setGender(relationship.getGender());

                field.setWidget(toObjectNode(widget));
                q.addField(field);
                s.addQuestion(q);
            }
        }
        return s;
    }

    private Section getRelationshipsSection() {
        Section s = new Section();
        s.setId("relationshipsInfo");
        s.setLabel("registrationapp.person.relationship");

        Question q = new Question();
        q.setId("relationshipsInfoQuestion");
        q.setLegend("registrationapp.person.relationship.label");
        q.setHeader("registrationapp.person.relationship.question");

        Field f = new Field();
        f.setType("personRelationships");

        Map<String, String> m = new HashMap<String, String>();
        m.put("providerName", "registrationapp");
        m.put("fragmentId", "field/personRelationship");
        f.setWidget(toObjectNode(m));

        q.addField(f);
        s.addQuestion(q);
        return s;
    }

    private Section getLocalContactSection() {
        Section s = new Section();
        s.setId("localContactInfo");
        s.setLabel("registration.patient.localContact.label");
        s.addQuestion(getLocalContactName());
        s.addQuestion(getLocalAddressQuestion());
        s.addQuestion(getLocalContactPhoneNumber());
        return s;
    }

    private Question getLocalContactName() {
        Question q = new Question();
        q.setId("contactNameLabel");
        q.setLegend("sl.registration.patient.localContactPerson.name.label");
        q.setHeader("sl.registration.patient.localContactPerson.contactName.question");
        {
            Field f = new Field();
            f.setFormFieldName("obsgroup.PIH:14704.obs.PIH:NAMES AND FIRSTNAMES OF CONTACT");
            f.setLabel("sl.registration.patient.localContactPerson.contactName.question");
            f.setType("obsgroup");
            f.setWidget(getTextFieldWidget(30));
            q.addField(f);
        }

        return q;
    }

    private Question getLocalContactPhoneNumber() {

        Question q = new Question();
        q.setId("contactPhoneNumberQuestionLabel");
        q.setHeader("sl.registration.patient.localContact.phonenumber.question");
        q.setLegend("sl.registration.patient.localContact.phonenumber.label");

        {
            Field f = new Field();
            f.setFormFieldName("obsgroup.PIH:14704.obs.PIH:TELEPHONE NUMBER OF CONTACT");
            f.setLabel("registrationapp.patient.phone.label");
            f.setType("obsgroup");
            ContactPersonConfigDescriptor contactPersonConfig = config.getRegistrationConfig().getContactPerson();
            if (contactPersonConfig != null && contactPersonConfig.getPhoneNumber() != null
                    && StringUtils.isNotBlank(contactPersonConfig.getPhoneNumber().getRegex())) {
                f.setCssClasses(Arrays.asList("regex"));
                f.setWidget(getTextFieldWidget(30, contactPersonConfig.getPhoneNumber().getRegex()));
            } else {
                f.setWidget(getTextFieldWidget(30));
            }
            q.addField(f);
        }

        return q;
    }

    private Question getLocalAddressQuestion() {
        Question q = new Question();
        q.setId("localAddressLabel");
        q.setHeader("sl.registration.patient.localAddress.question");
        q.setLegend("sl.registration.patient.localAddress.label");

        Field f = new Field();
        f.setLabel("sl.registration.patient.localAddress.label");
        f.setType("personAddress");

        // If there are address hierarchy levels configured, use the address hierarchy widget, otherwise use the standard address widget
        List<AddressHierarchyLevel> levels = Context.getService(AddressHierarchyService.class).getAddressHierarchyLevels();
        if (levels != null && levels.size() > 0) {
            q.setDisplayTemplate(getAddressHierarchyDisplayTemplate(levels));
            f.setWidget(getAddressHierarchyWidget(levels, getLocalContactAddressFieldMappings(), true));
        } else {
            Map<String, String> m = new HashMap<String, String>();
            m.put("providerName", "uicommons");
            m.put("fragmentId", "field/personAddress");
            f.setWidget(toObjectNode(m));
        }
        q.addField(f);

        return q;
    }

    @Override
    public Section getIdentifierSection() {
        Section s = new Section();
        s.setId("patient-identification-section");
        s.setLabel("registrationapp.patient.identifiers.label");
        if (config.isKgh()) {
            s.addQuestion(getWellbodyEmrId());
        } else if (config.isWellbody()) {
            s.addQuestion(getKghEmrId());
        }

        return s;
    }

    private Question getWellbodyEmrId() {
        Question q = new Question();
        q.setId("wellbody-emr-id");
        q.setLegend("Wellbody EMR ID");
        q.setHeader("ui.i18n.PatientIdentifierType.name." + SierraLeoneConfigConstants.PATIENTIDENTIFIERTYPE_WELLBODYEMRID_UUID);

        Field f = new Field();
        f.setFormFieldName("patientIdentifier" + SierraLeoneConfigConstants.PATIENTIDENTIFIERTYPE_WELLBODYEMRID_UUID);
        f.setUuid(SierraLeoneConfigConstants.PATIENTIDENTIFIERTYPE_WELLBODYEMRID_UUID);
        f.setType("patientIdentifier");
        f.setWidget(getTextFieldWidget(16));

        q.addField(f);
        return q;
    }

    private Question getKghEmrId() {
        Question q = new Question();
        q.setId("kgh-emr-id");
        q.setLegend("KGH EMR ID");
        q.setHeader("ui.i18n.PatientIdentifierType.name." + SierraLeoneConfigConstants.PATIENTIDENTIFIERTYPE_KGHEMRID_UUID);

        Field f = new Field();
        f.setFormFieldName("patientIdentifier" + SierraLeoneConfigConstants.PATIENTIDENTIFIERTYPE_KGHEMRID_UUID);
        f.setUuid(SierraLeoneConfigConstants.PATIENTIDENTIFIERTYPE_KGHEMRID_UUID);
        f.setType("patientIdentifier");
        f.setWidget(getTextFieldWidget(16));

        q.addField(f);
        return q;
    }

    @Override
    public Section getSocialSection() {
        Section s = new Section();
        s.setId("social");
        s.setLabel("zl.registration.patient.social.label");
        s.addQuestion(getBirthplaceQuestion());
        s.addQuestion(getCivilStatusQuestion());
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

        // ordered alphabetically with Unemployed and Other last
        w.getConfig().addOption("CIEL:162944", "zl.registration.patient.occupation.civilServant.label");
        w.getConfig().addOption("PIH:COMMERCE", "zl.registration.patient.occupation.commerce.label");
        w.getConfig().addOption("PIH:Commercial bike rider", "zl.registration.patient.occupation.motorcycletaxi");
        w.getConfig().addOption("PIH:Cowherd", "zl.registration.patient.occupation.cowherd.label");
        w.getConfig().addOption("PIH:DRIVER", "zl.registration.patient.occupation.driver.label");
        w.getConfig().addOption("PIH:FACTORY WORKER", "zl.registration.patient.occupation.factoryWorker.label");
        w.getConfig().addOption("PIH:FARMER", "zl.registration.patient.occupation.farmer.label");
        w.getConfig().addOption("CIEL:159674", "zl.registration.patient.occupation.fisherman.label");
        w.getConfig().addOption("PIH:FRUIT OR VEGETABLE SELLER", "zl.registration.patient.occupation.fruitOrVegetableVendor.label");
        w.getConfig().addOption("PIH:HEALTH CARE WORKER", "zl.registration.patient.occupation.healthCareWorker.label");
        w.getConfig().addOption("PIH:1404", "zl.registration.patient.occupation.housework.label");
        w.getConfig().addOption("PIH:HOUSEWORK/FIELDWORK", "zl.registration.patient.occupation.houseworkFieldwork.label");
        w.getConfig().addOption("PIH:MANUAL LABORER", "zl.registration.patient.occupation.manualLaborer.label");
        w.getConfig().addOption("CIEL:162945", "zl.registration.patient.occupation.marketVendor.label");
        w.getConfig().addOption("PIH:Military", "zl.registration.patient.occupation.military.label");
        w.getConfig().addOption("PIH:MINER", "zl.registration.patient.occupation.miner.label");
        w.getConfig().addOption("PIH:Police", "zl.registration.patient.occupation.police.label");
        w.getConfig().addOption("PIH:PROFESSIONAL", "zl.registration.patient.occupation.professional.label");
        w.getConfig().addOption("PIH:RETIRED", "zl.registration.patient.occupation.retired.label");
        w.getConfig().addOption("PIH:SHOP OWNER", "zl.registration.patient.occupation.shopOwner.label");
        w.getConfig().addOption("PIH:STUDENT", "zl.registration.patient.occupation.student.label");
        w.getConfig().addOption("PIH:Teacher", "zl.registration.patient.occupation.teacher.label");
        w.getConfig().addOption("PIH:UNEMPLOYED", "zl.registration.patient.occupation.unemployed.label");
        w.getConfig().addOption("PIH:OTHER NON-CODED", "zl.registration.patient.occupation.other.label");

        w.getConfig().setExpanded(true);
        f.setWidget(toObjectNode(w));
        q.addField(f);

        return q;
    }

    private Map<String, String> getLocalContactAddressFieldMappings() {
        // Haiti-specific
        Map<String, String> fieldMappings = new HashMap<String, String>();
        fieldMappings.put(AddressField.COUNTRY.getName(), "obsgroup.PIH:14704.obs.PIH:Country");
        fieldMappings.put(AddressField.COUNTY_DISTRICT.getName(), "obsgroup.PIH:14704.obs.PIH:14784");
        fieldMappings.put(AddressField.STATE_PROVINCE.getName(), "obsgroup.PIH:14704.obs.PIH:State Province");
        fieldMappings.put(AddressField.ADDRESS_1.getName(), "obsgroup.PIH:14704.obs.PIH:Address1");
        fieldMappings.put(AddressField.CITY_VILLAGE.getName(), "obsgroup.PIH:14704.obs.PIH:City Village");
        fieldMappings.put(AddressField.ADDRESS_2.getName(), "obsgroup.PIH:14704.obs.PIH:Address2");
        return fieldMappings;
    }

}
