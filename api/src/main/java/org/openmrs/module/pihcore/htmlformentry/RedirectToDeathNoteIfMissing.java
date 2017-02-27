package org.openmrs.module.pihcore.htmlformentry;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.disposition.DispositionDescriptor;
import org.openmrs.module.emrapi.disposition.DispositionService;
import org.openmrs.module.htmlformentry.CustomFormSubmissionAction;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.pihcore.PihCoreConstants;
import org.openmrs.module.pihcore.config.Components;
import org.openmrs.module.pihcore.config.Config;

import java.util.Arrays;
import java.util.List;

/**
 * When included in a form, upon any save (of a new or edited form) if all the following are true, then the user is
 * redirected to fill out a new death certificate:
 *  * this form's encounter's disposition is Death
 *  * the user has the privilege to enter a Death Certificate
 *  * the patient has no non-voided Death Certificate encounter
 *
 * Usage within an HTML Form:
 * <postSubmissionAction class="org.openmrs.module.pihcore.htmlformentry.RedirectToDeathNoteIfMissing"/>
 */
public class RedirectToDeathNoteIfMissing implements CustomFormSubmissionAction {

    @Override
    public void applyAction(FormEntrySession session) {

        DispositionService dispositionService = Context.getService(DispositionService.class);

        if (!Context.getRegisteredComponent("config", Config.class).isComponentEnabled(Components.DEATH_CERTIFICATE)) {
            return;
        }

        if (!Context.hasPrivilege("Task: mirebalais.enterDeathCertificate")) {
            return;
        }

        if (!dispositionIsDeath(session.getEncounter(), dispositionService)) {
            return;
        }

        if (hasDeathCertificateEncounter(session.getPatient())) {
            return;
        }

        session.setAfterSaveUrlTemplate("htmlformentryui/htmlform/enterHtmlFormWithSimpleUi.page" +
                "?patientId={{patient.id}}" +
                "&definitionUiResource=pihcore:htmlforms/deathCertificate.xml" +
                "&returnUrl=" + session.getReturnUrl());
    }

    private boolean hasDeathCertificateEncounter(Patient patient) {
        EncounterService encounterService = Context.getEncounterService();
        EncounterType deathCertificateEncounterType = Context.getEncounterService().getEncounterTypeByUuid(PihCoreConstants.DEATH_CERTIFICATE_ENCOUNTER_TYPE_UUID);
        List<Encounter> deathCertificateEncounters = encounterService.getEncounters(patient, null, null, null, null, Arrays.asList(deathCertificateEncounterType), null, null, null, false);
        return deathCertificateEncounters.size() > 0;
    }

    private boolean dispositionIsDeath(Encounter encounter, DispositionService dispositionService) {
        DispositionDescriptor dispositionDescriptor = dispositionService.getDispositionDescriptor();
        for (Obs obs : encounter.getObsAtTopLevel(false)) {
            if (dispositionDescriptor.isDisposition(obs)) {
                Obs dispoObs = dispositionDescriptor.getDispositionObs(obs);
                return isDeath(dispoObs);
            }
        }
        return false;
    }

    private boolean isDeath(Obs dispoObs) {
        // ideally we'd actually check against the mapping "PIH:DEATH", but UUID should be safe
        return dispoObs.getValueCoded().getUuid().equals("d96d995a-c4e1-4217-b8e4-e1497f62e12d");
    }

}
