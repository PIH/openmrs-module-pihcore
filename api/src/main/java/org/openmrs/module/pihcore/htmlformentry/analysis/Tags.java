package org.openmrs.module.pihcore.htmlformentry.analysis;

import lombok.Getter;

public class Tags {

    public enum HFE {
        appointments, causeOfDeathList, code, codedOrFreeTextObs, completeProgram, condition, controls, drugOrder,
        encounterDate, encounterDiagnosesByObs, encounterDisposition, encounterLocation, encounterProvider,
        encounterProviderAndRole, encounterType, encounterVoided, enrollInProgram, excludeIf, exitFromCare,
        familyHistoryRelativeCheckboxes, htmlform, ifMode, immunization, includeIf, lookup, macro, macros,
        markPatientDead, obs, obsgroup, obsreference, option, order, orderProperty, orderTemplate,
        pastMedicalHistoryCheckbox, patient, postSubmissionAction, redirectOnSave, relationship,
        render, repeat, section, submit, template, translations, uiInclude, uimessage, uimessages,
        variant, when, workflowState
    }

    @Getter
    public enum Standard {

        a, b, br, button, center, comment("#comment"), dd, div, dl, dt, field, fieldset, font,
        h1, h2, h3, h4, h5, h6, h7, hr, i, img, input, label, legend, li, p, script, select, sl, small, span,
        strong, style, table, tbody, td, text("#text"), th, thead, tr, u, ul;

        private final String nodeName;

        Standard() {
            nodeName = name();
        }

        Standard(String nodeName) {
            this.nodeName = nodeName;
        }
    }
}
