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
            classes: "indent",
            include: "templates/allergies/reviewAllergies.page"
        };

        var vaccinations = {
            type: "include",
            classes: "indent",
            include: "templates/vaccination/vaccinations.page"
        };

      /*  var primaryCareConsultInfo = {
            type: "consult-section",
            label: "pihcore.visitNote.consultInfo.label",
            icon: "icon-file-alt",
            shortTemplate: "templates/sections/defaultSectionShort.page",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{consultEncounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/primary-care-consult-info.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}",
            showEncounterDetails: true,
            hideIncompleteAlert: true
        }
*/


        var mentalHealth = {
            type: "consult-section",
            label: "pih.task.mentalHealth.label",
            icon: "icon-user",
            classes: "indent",
            shortTemplate: "templates/sections/defaultSectionShort.page",
            longTemplate: "templates/sections/viewSectionWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{consultEncounter.uuid}}&definitionUiResource=pihcore:htmlforms/mentalHealth.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{consultEncounter.uuid}}&definitionUiResource=pihcore:htmlforms/mentalHealth.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}",
            showEncounterDetails: true
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
            },
            mentalHealth: {
                label: "pihcore.visitType.mentalHealth",
                allowedFor: allowedForAll,
                consultEncounterType: EncounterTypes.mentalHealth,
                encounterTypeConfig: EncounterTypeConfig,
                elements: [
                    mentalHealth
                ]
            }
        };
        _.each(ret, function(it, key) {
            it.name = key;
        });
        return ret;
    }]);


