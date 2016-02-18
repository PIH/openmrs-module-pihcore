angular.module("visit-templates", ["constants", "encounterTypeConfig"])

    .factory("VisitTemplates", [ "EncounterTypes", "EncounterRoles", "EncounterTypeConfig", function(EncounterTypes, EncounterRoles, EncounterTypeConfig) {

        // if use has only "enterConsultNote", must be an active visit; retroConsultNote or retroConsultNoteThisProviderOnly required for retro visits
        var standardConsultNoteRequire = "(user.hasPrivilege('Task: emr.enterConsultNote') && visit && !visit.stopDatetime) || (user.hasPrivilege('Task: emr.retroConsultNote') || user.hasPrivilege('Task: emr.retroConsultNoteThisProviderOnly'))"


        var visitActions = {
            type: "include",
            include: "templates/visitActions.page"
        };

        var reverseChronologicalEncounters = {
            type: "include",
            include: "templates/reverseChronologicalEncounters.page"
        };


        var checkIn = {
            type: "encounter",
            encounter: {
                encounterType: {
                    uuid: EncounterTypes.checkIn.uuid
                }
            }
        };

        var vitals = {
            type: "encounter",
            encounter: {
                encounterType: {
                    uuid: EncounterTypes.vitals.uuid
                }
            }
        };

        var reviewAllergies = {
            type: "include",
            include: "templates/allergies/reviewAllergies.page"
        };

        var vaccinations = {
            type: "include",
            include: "templates/vaccination/vaccinations.page"
        };

        var primaryCareConsultInfo = {
            type: "consult-section",
            label: "pihcore.visitNote.consultInfo.label",
            icon: "icon-file-alt",
            shortTemplate: "templates/sections/consultInfoSectionShort.page",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{consultEncounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/primary-care-consult-info.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"

        }

        var primaryCareHistory = {
            type: "consult-section",
            label: "pihcore.history.label",
            icon: "icon-file-alt",
            shortTemplate: "templates/sections/defaultSectionShort.page",
            longTemplate: "templates/sections/viewSectionWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{consultEncounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/primary-care-history.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{consultEncounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/primary-care-history.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        };

        var primaryCareExam = {
            type: "consult-section",
            label: "pihcore.exam.label",
            icon: "icon-stethoscope",
            shortTemplate: "templates/sections/defaultSectionShort.page",
            longTemplate: "templates/sections/viewSectionWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{consultEncounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/primary-care-exam.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithSimpleUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{consultEncounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/primary-care-exam.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        };

        var primaryCareDisposition = {
            type: "consult-section",
            label: "pihcore.disposition.label",
            icon: "icon-stethoscope",
            shortTemplate: "templates/sections/defaultSectionShort.page",
            longTemplate: "templates/sections/dispositionLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{consultEncounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/primary-care-disposition.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{consultEncounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/primary-care-disposition.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        };

        var feeding = {
            type: "consult-section",
            label: "pihcore.feeding.history.label",
            icon: "icon-food",
            shortTemplate: "templates/sections/defaultSectionShort.page",
            longTemplate: "templates/sections/viewSectionWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{consultEncounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/primary-care-peds-feeding.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{consultEncounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/primary-care-peds-feeding.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        };

        var supplements = {
            type: "consult-section",
            label: "pihcore.supplements.history.label",
            icon: "icon-asterisk",
            shortTemplate: "templates/sections/defaultSectionShort.page",
            longTemplate: "templates/sections/viewSectionWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{consultEncounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/primary-care-peds-supplements.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{consultEncounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/primary-care-peds-supplements.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        };

        var primaryCareDx = {
            type: "consult-section",
            label: "pihcore.diagnosis.label",
            icon: "icon-list-ul",
            shortTemplate: "templates/sections/defaultSectionShort.page",
            longTemplate: "templates/sections/dxLong.page",
            //templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{consultEncounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/primary-care-dx.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{consultEncounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/primary-care-dx.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        };

        var primaryCarePlan = {
            type: "consult-section",
            label: "pihcore.visitNote.plan",
            icon: "icon-list-ul",
            shortTemplate: "templates/sections/defaultSectionShort.page",
            longTemplate: "templates/sections/viewSectionWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{consultEncounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/primary-care-plan.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{consultEncounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/primary-care-plan.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"

        };

        var allowedForAll = function(visit) {
            return true;
        };

        var ret = {
            timeline: {
                label: "pihcore.visitType.generic",
                encounterTypeConfig: EncounterTypeConfig,
                allowedFor: allowedForAll,
                elements: [
                    //visitActions,
                    reverseChronologicalEncounters
                ]
            },

            adultInitialOutpatient: {
                label: "pihcore.visitType.adultInitialOutpatient",
                allowedFor: allowedForAll,
                consultEncounterType: EncounterTypes.primaryCareVisit,
                encounterTypeConfig: EncounterTypeConfig,
                elements: [
                    checkIn,
                    vitals,
                    primaryCareConsultInfo,
                    reviewAllergies,
                    primaryCareHistory,
                    primaryCareExam,
                    primaryCareDx,
                    primaryCarePlan,
                    primaryCareDisposition
                ]
            },
            adultFollowupOutpatient: {
                label: "pihcore.visitType.adultFollowupOutpatient",
                allowedFor: allowedForAll,
                consultEncounterType: EncounterTypes.primaryCareVisit,
                encounterTypeConfig: EncounterTypeConfig,
                elements: [
                    checkIn,
                    vitals,
                    primaryCareConsultInfo,
                    reviewAllergies,
                    primaryCareHistory,
                    primaryCareExam,
                    primaryCareDx,
                    primaryCarePlan,
                    primaryCareDisposition
                ]
            },
            pedsInitialOutpatient: {
                label: "pihcore.visitType.pedsInitialOutpatient",
                allowedFor: allowedForAll,
                consultEncounterType: EncounterTypes.primaryCareVisit,
                encounterTypeConfig: EncounterTypeConfig,
                elements: [
                    checkIn,
                    vitals,
                    primaryCareConsultInfo,
                    vaccinations,
                    supplements,
                    reviewAllergies,
                    primaryCareHistory,
                    feeding,
                    primaryCareExam,
                    primaryCareDx,
                    primaryCarePlan,
                    primaryCareDisposition

                ]
            },
            pedsFollowupOutpatient: {
                label: "pihcore.visitType.pedsFollowupOutpatient",
                allowedFor: allowedForAll,
                consultEncounterType: EncounterTypes.primaryCareVisit,
                encounterTypeConfig: EncounterTypeConfig,
                elements: [
                    checkIn,
                    vitals,
                    primaryCareConsultInfo,
                    vaccinations,
                    supplements,
                    reviewAllergies,
                    feeding,
                    primaryCareExam,
                    primaryCareDx,
                    primaryCarePlan,
                    primaryCareDisposition
                ]
            }
        };
        _.each(ret, function(it, key) {
            it.name = key;
        });
        return ret;
    }]);


