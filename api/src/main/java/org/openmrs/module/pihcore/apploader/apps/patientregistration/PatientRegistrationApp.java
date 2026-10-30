package org.openmrs.module.pihcore.apploader.apps.patientregistration;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.node.ObjectNode;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.pihcore.apploader.CustomAppLoaderConstants;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.registrationapp.model.Field;
import org.openmrs.module.registrationapp.model.PossibleDuplicatesWidget;
import org.openmrs.module.registrationapp.model.Question;
import org.openmrs.module.registrationapp.model.RegistrationAppConfig;
import org.openmrs.module.registrationapp.model.Section;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Helper class to help defining PatientRegistrationApp
 */
@Component
public class PatientRegistrationApp {

    public AppDescriptor getAppDescriptor(Config config) {
        AppDescriptor d = new AppDescriptor();
        d.setId(CustomAppLoaderConstants.Apps.PATIENT_REGISTRATION);
        d.setDescription("registrationapp.registerPatient");
        d.setLabel("registrationapp.app.registerPatient.label");
        d.setIcon("fas fa-fw fa-user");
        d.setUrl("registrationapp/findPatient.page?appId=" + CustomAppLoaderConstants.Apps.PATIENT_REGISTRATION);
        d.setRequiredPrivilege("App: registrationapp.registerPatient");
        d.setConfig(toObjectNode(getRegistrationAppConfig(config)));
        return d;
    }

    private RegistrationAppConfig getRegistrationAppConfig(Config config) {
        RegistrationAppConfig c = new RegistrationAppConfig();
        c.setPatientDashboardLink("registrationapp/registrationSummary.page?appId=" +  CustomAppLoaderConstants.Apps.PATIENT_REGISTRATION);
        c.setRegistrationEncounter(PihEmrConfigConstants.ENCOUNTERTYPE_PATIENT_REGISTRATION_UUID, PihEmrConfigConstants.ENCOUNTERROLE_ADMINISTRATIVECLERK_UUID);
        c.setAllowRetrospectiveEntry(true);
        c.setAllowUnknownPatients(config.getRegistrationConfig().isAllowUnknownPatients());
        c.setAllowManualIdentifier(config.getRegistrationConfig().isAllowManualEntryOfPrimaryIdentifier());
        c.setAfterCreatedUrl(config.getRegistrationConfig().getAfterCreatedUrl());
        c.setAfterCreatedActions(config.getRegistrationConfig().getAfterCreatedActions());
        c.setMatchingPatientsPropertiesToDisplay(config.getRegistrationConfig().getMatchingPatientsPropertiesToDisplay());
        c.setMaxPatientSearchResults(config.getRegistrationConfig().getMaxPatientMatchResults());
        c.setIdentifierTypesToDisplay(config.getRegistrationConfig().getIdentifierTypesToDisplay());
        switch(config.getCountry()) {
            case HAITI:
                new SectionsHaiti(config).addSections(c);
                break;
            case LIBERIA:
                new SectionsLiberia(config).addSections(c);
                break;
            case MEXICO:
                new SectionsMexico(config).addSections(c);
                break;
            case SIERRA_LEONE:
                new SectionsSierraLeone(config).addSections(c);
                break;
            case LESOTHO:
                new SectionsLesotho(config).addSections(c);
                break;
            default:
                new SectionsDefault(config).addSections(c);
        }
        if (hasPossibleDuplicatesQuestion(c)) {
            // the possible duplicates question reviews the matches, so the form only tells that there are some, and doesn't list
            // them again on the confirmation
            c.setSimilarPatientsBanner(RegistrationAppConfig.SIMILAR_PATIENTS_BANNER_NOTE);
            c.setCheckExactMatchesOnConfirmation(false);
        }
        return c;
    }

    private boolean hasPossibleDuplicatesQuestion(RegistrationAppConfig c) {
        if (c.getSections() != null) {
            for (Section section : c.getSections()) {
                if (section.getQuestions() != null) {
                    for (Question question : section.getQuestions()) {
                        for (Field field : question.getFields()) {
                            if (PossibleDuplicatesWidget.FIELD_TYPE.equals(field.getType())) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private ObjectNode toObjectNode(Object o) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        return mapper.convertValue(o, ObjectNode.class);
    }

}
