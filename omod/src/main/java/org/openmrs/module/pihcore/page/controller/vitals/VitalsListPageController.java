package org.openmrs.module.pihcore.page.controller.vitals;

import org.joda.time.DateMidnight;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PersonService;
import org.openmrs.module.haiticore.metadata.HaitiPersonAttributeTypes;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.pihcore.PihCoreUtil;
import org.openmrs.module.pihcore.ZlConfigConstants;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.metadata.core.EncounterTypes;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.resource.ResourceFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VitalsListPageController {

    public String get(PageModel model, UiUtils ui,
                      @SpringBean("encounterService") EncounterService encounterService,
                      @SpringBean("personService") PersonService personService,
                      @SpringBean Config config) throws IOException {

        // TODO restrict by location at some point if necessary

        ResourceFactory resourceFactory = ResourceFactory.getInstance();

        Map<Patient, Encounter> patientsWithCheckInEncounter = new LinkedHashMap<Patient, Encounter>();

        // all patients that have a check-in encounter today, but no vitals encounter
        List<Encounter> checkInEncounters = encounterService.getEncounters(null, null, new DateMidnight().toDate(), null,
                null, Collections.singletonList(encounterService.getEncounterTypeByUuid(EncounterTypes.CHECK_IN.uuid())),
                null, null, null, false);

        List<Encounter> vitalsEncounters = encounterService.getEncounters(null, null, new DateMidnight().toDate(), null,
                null, Collections.singletonList(encounterService.getEncounterTypeByUuid(EncounterTypes.VITALS.uuid())),
                null, null, null, false);

        for (Encounter encounter : checkInEncounters) {
            // sanity check on visit
            if (encounter.getVisit() != null) {
                // we check that the patient isn't already in the list instead of just inserting again because we want to keep the earliest date
                if (!patientsWithCheckInEncounter.containsKey(encounter.getPatient())) {
                    patientsWithCheckInEncounter.put(encounter.getPatient(), encounter);
                }
            }
        }

        for (Encounter encounter : vitalsEncounters) {
            if (patientsWithCheckInEncounter.containsKey(encounter.getPatient())) {
                patientsWithCheckInEncounter.remove(encounter.getPatient());
            }
        }

        String formPath = PihCoreUtil.getFormResource("vitals.xml");

        SimpleObject vitalsListBreadcrumb = SimpleObject.create("label", ui.message("pihcore.vitalsList.title"), "link", ui.pageLink("pihcore", "vitals/vitalsList"));

        PatientIdentifierType dossierNumberType = MetadataUtils.existing(PatientIdentifierType.class, ZlConfigConstants.PATIENTIDENTIFIERTYPE_DOSSIERNUMBER_UUID);

        // used to determine whether or not we display a link to the patient in the results list
        model.addAttribute("patientWithCheckInEncounter", patientsWithCheckInEncounter);
        model.addAttribute("mothersFirstName", personService.getPersonAttributeTypeByUuid(HaitiPersonAttributeTypes.MOTHERS_FIRST_NAME.uuid()));
        model.addAttribute("dossierIdentifierName", dossierNumberType.getName());
        model.addAttribute("breadcrumbOverride", ui.toJson(Arrays.asList(vitalsListBreadcrumb)));
        model.addAttribute("formPath", formPath);

        return null;
    }

}
