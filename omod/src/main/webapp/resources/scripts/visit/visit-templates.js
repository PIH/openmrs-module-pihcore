angular.module("visit-templates", ["constants"])

    .factory("VisitTemplates", [ "EncounterTypes", "EncounterRoles", function(EncounterTypes, EncounterRoles) {
        var hfeSimpleEditUrl = "/htmlformentryui/htmlform/editHtmlFormWithSimpleUi.page?patientId={{encounter.patient.uuid}}&encounterId={{encounter.uuid}}&returnUrl={{returnUrl}}";
        var hfeStandardEditUrl = "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{encounter.patient.uuid}}&encounterId={{encounter.uuid}}&returnUrl={{returnUrl}}";

        // template model url:
        // if a template operates off an model different that the standard OpenMRS REST representation of an encounter,
        // you specify the URL of the source here; used currently for htmlFormEntry encounter templates, which
        // require the encounter to be formatted using the HFE schema

        var encounterTypeConfig = {
            DEFAULT: {
                defaultState: "short",
                shortTemplate: "templates/encounters/defaultEncounterShort.page",
                longTemplate: "templates/encounters/defaultHtmlFormEncounterLong.page",
                templateModelUrl: "/module/htmlformentry/encounter.json?encounter={{encounter.uuid}}"
            }
        };
        encounterTypeConfig[EncounterTypes.patientRegistration.uuid] = {  // should never appear on dashboard?
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultEncounterLong.page"
        };
        encounterTypeConfig[EncounterTypes.checkIn.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/checkInShort.page",
            longTemplate: "templates/encounters/defaultHtmlFormEncounterLong.page",
            templateModelUrl: "/module/htmlformentry/encounter.json?encounter={{encounter.uuid}}",
            icon: "icon-check-in",
            editUrl: hfeSimpleEditUrl
        };
        encounterTypeConfig[EncounterTypes.vitals.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/vitalsShort.page",
            longTemplate: "templates/encounters/defaultHtmlFormEncounterLong.page",
            templateModelUrl: "/module/htmlformentry/encounter.json?encounter={{encounter.uuid}}",
            icon: "icon-vitals",
            editUrl: hfeSimpleEditUrl
        };
        encounterTypeConfig[EncounterTypes.consultation.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/clinicConsultShort.page",
            longTemplate: "templates/encounters/clinicConsultLong.page",
            icon: "icon-stethoscope",
            editUrl: hfeStandardEditUrl
        };
        encounterTypeConfig[EncounterTypes.oncologyConsult.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultHtmlFormEncounterLong.page",
            templateModelUrl: "/module/htmlformentry/encounter.json?encounter={{encounter.uuid}}",
            icon: "icon-paste",
            editUrl: hfeStandardEditUrl
        };
        encounterTypeConfig[EncounterTypes.oncologyInitialVisit.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultHtmlFormEncounterLong.page",
            templateModelUrl: "/module/htmlformentry/encounter.json?encounter={{encounter.uuid}}",
            icon: "icon-paste",
            editUrl: hfeStandardEditUrl
        };
        encounterTypeConfig[EncounterTypes.chemotherapySession.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultHtmlFormEncounterLong.page",
            templateModelUrl: "/module/htmlformentry/encounter.json?encounter={{encounter.uuid}}",
            icon: "icon-retweet",
            editUrl: hfeStandardEditUrl
        };
        encounterTypeConfig[EncounterTypes.medicationDispensed.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultHtmlFormEncounterLong.page",
            templateModelUrl: "/module/htmlformentry/encounter.json?encounter={{encounter.uuid}}",
            icon: "icon-medicine",
            editUrl: hfeStandardEditUrl
        };
        encounterTypeConfig[EncounterTypes.postOperativeNote.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultHtmlFormEncounterLong.page",
            templateModelUrl: "/module/htmlformentry/encounter.json?encounter={{encounter.uuid}}",
            primaryEncounterRoleUuid: EncounterRoles.attendingSurgeon.uuid,
            icon: "icon-paste",
            editUrl: hfeStandardEditUrl
        };
        encounterTypeConfig[EncounterTypes.transfer.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultHtmlFormEncounterLong.page",
            templateModelUrl: "/module/htmlformentry/encounter.json?encounter={{encounter.uuid}}",
            icon: "icon-share",
            editUrl: hfeStandardEditUrl
        };
        encounterTypeConfig[EncounterTypes.admission.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/admissionShort.page",
            icon: "icon-signin",
            editUrl: hfeStandardEditUrl
        };
        encounterTypeConfig[EncounterTypes.cancelAdmission.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultHtmlFormEncounterLong.page",
            templateModelUrl: "/module/htmlformentry/encounter.json?encounter={{encounter.uuid}}",
            icon: "icon-ban-circle",
            editUrl: hfeStandardEditUrl
        };
        encounterTypeConfig[EncounterTypes.exitFromCare.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            icon: "icon-signout",
            editUrl: hfeStandardEditUrl
        };
        encounterTypeConfig[EncounterTypes.labResults.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultHtmlFormEncounterLong.page",
            templateModelUrl: "/module/htmlformentry/encounter.json?encounter={{encounter.uuid}}",
            icon: "icon-beaker",
            editUrl: hfeSimpleEditUrl
        };
        encounterTypeConfig[EncounterTypes.radiologyOrder.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultOrderEncounterShort.page",
            longTemplate: "templates/encounters/defaultEncounterLong.page",
            icon: "icon-x-ray"
        };
        encounterTypeConfig[EncounterTypes.radiologyStudy.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultEncounterLong.page",
            icon: "icon-x-ray"
        };
        encounterTypeConfig[EncounterTypes.radiologyReport.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultEncounterLong.page",
            icon: "icon-x-ray"
        };
        encounterTypeConfig[EncounterTypes.deathCertificate.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultHtmlFormEncounterLong.page",
            templateModelUrl: "/module/htmlformentry/encounter.json?encounter={{encounter.uuid}}",
            icon: "icon-remove-circle",
            editUrl: hfeStandardEditUrl
        };
        encounterTypeConfig[EncounterTypes.ncdConsult.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultHtmlFormEncounterLong.page",
            templateModelUrl: "/module/htmlformentry/encounter.json?encounter={{encounter.uuid}}",
            icon: "icon-heart",
            editUrl: hfeStandardEditUrl
        };
        // Tthe follow are encounters only present within the "new visit note" view
        encounterTypeConfig[EncounterTypes.primaryCareHistory.uuid] = {
            defaultState: "long",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/primaryCareAdultHistoryLong.page",
            icon: "icon-file-alt",
            editUrl: hfeStandardEditUrl
        };
        encounterTypeConfig[EncounterTypes.primaryCareExam.uuid] = {
            defaultState: "long",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultEncounterLong.page",
            icon: "icon-stethoscope",
            editUrl: hfeSimpleEditUrl
        };
        encounterTypeConfig[EncounterTypes.primaryCareDx.uuid] = {
            defaultState: "long",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultEncounterLong.page",
            icon: "icon-list-ul",
            editUrl: hfeStandardEditUrl
        };
        encounterTypeConfig[EncounterTypes.consultationPlan.uuid] = {
            defaultState: "long",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultEncounterLong.page",
            icon: "icon-list-ol",
            editUrl: null
        };

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
            addInline: true,
            encounter: {
                encounterType: {
                    uuid: EncounterTypes.checkIn.uuid
                },
                shortTemplate: "templates/encounters/checkInShort.page"
            },
            action: {
                label: "Check In",
                icon: "icon-check-in",
                href: "/{{contextPath}}/htmlformentryui/htmlform/enterHtmlFormWithSimpleUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&definitionUiResource=pihcore:htmlforms/checkin.xml&returnUrl={{returnUrl}}"
            }
        };

        var vitals = {
            type: "encounter",
            addInline: true,
            encounter: {
                encounterType: {
                    uuid: EncounterTypes.vitals.uuid
                },
                shortTemplate: "templates/encounters/vitalsShort.page",
                longTemplate: "templates/encounters/vitalsLong.page"
            },
            action: {
                label: "Vitals",
                icon: "icon-vitals",
                href: "/{{contextPath}}/htmlformentryui/htmlform/enterHtmlFormWithSimpleUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&definitionUiResource=pihcore:htmlforms/vitals.xml&returnUrl={{returnUrl}}"
            }
        };

        var reviewAllergies = {
            type: "include",
            include: "templates/reviewAllergies.page"
        };
        var vaccinations = {
            type: "include",
            include: "templates/vaccinations.page"
        };
        var primaryCareAdultHistory = {
            type: "encounter",
            encounter: {
                encounterType: {
                    uuid: EncounterTypes.primaryCareHistory.uuid
                },
                longTemplate: "templates/encounters/primaryCareAdultHistoryLong.page"
            },
            action: {
                label: "History (Adult)",
                icon: "icon-file-alt",
                href: "/{{contextPath}}/htmlformentryui/htmlform/enterHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/primary-care-adult-history.xml&returnUrl={{returnUrl}}"
            }
        };
        var primaryCareExam = {
            type: "encounter",
            encounter: {
                encounterType: {
                    uuid: EncounterTypes.primaryCareExam.uuid
                },
                longTemplate: "templates/encounters/defaultEncounterLong.page"
            },
            action: {
                label: "Exam (Adult)",
                icon: "icon-stethoscope",
                href: "/{{contextPath}}/htmlformentryui/htmlform/enterHtmlFormWithSimpleUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/primary-care-adult-exam.xml&returnUrl={{returnUrl}}"
            }
        };

        var primaryCareDx = {
            type: "encounter",
            encounter: {
                encounterType: {
                    uuid: EncounterTypes.primaryCareDx.uuid
                },
                longTemplate: "templates/encounters/defaultEncounterLong.page"
            },
            action: {
                label: "Diagnosis",
                icon: "icon-list-ul",
                href: "/{{contextPath}}/htmlformentryui/htmlform/enterHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/primary-care-adult-dx.xml&returnUrl={{returnUrl}}"
            }
        };

        var labResults = {
            type: "encounter",
            encounter: {
                encounterType: {
                    uuid: EncounterTypes.labResults.uuid
                },
                longTemplate: "templates/encounters/defaultEncounterLong.page"
            },
            action: {
                label: "Lab results",
                icon: "icon-beaker",
                href: "/{{contextPath}}/htmlformentryui/htmlform/enterHtmlFormWithSimpleUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&definitionUiResource=pihcore:htmlforms/labResults.xml&returnUrl={{returnUrl}}"
            }
        };

        var outpatientPlan = {
            type: "encounter",
            allowMultiple: true,
            encounter: {
                encounterType: EncounterTypes.consultationPlan
            },
            action: {
                label: "Plan",
                icon: "icon-list-ol",
                sref: "editPlan"
            }
        };

        var addExpectedEncounters = {
            type: "include",
            include: "templates/add-expected-encounters.page"
        }

        var allowedForAll = function(visit) {
            return true;
        };

        var ret = {
            timeline: {
                label: "Visit (Generic)",
                encounterTypeConfig: encounterTypeConfig,
                allowedFor: allowedForAll,
                elements: [
                    visitActions,
                    reverseChronologicalEncounters
                ]
            },
            adultInitialOutpatient: {
                label: "Adult Initial Outpatient Visit",
                allowedFor: allowedForAll,
                encounterTypeConfig: encounterTypeConfig,
                elements: [
                    checkIn,
                    vitals,
                    reviewAllergies,
                    labResults,
                    primaryCareAdultHistory,
                    primaryCareExam,
                    primaryCareDx,
                    outpatientPlan,
                    addExpectedEncounters
                ]
            },
            adultFollowupOutpatient: {
                label: "Adult Followup Outpatient Visit",
                allowedFor: allowedForAll,
                encounterTypeConfig: encounterTypeConfig,
                elements: [
                    checkIn,
                    vitals,
                    reviewAllergies,
                    primaryCareExam,
                    primaryCareDx,
                    outpatientPlan,
                    addExpectedEncounters
                ]
            },
            pedsInitialOutpatient: {
                label: "Peds Initial Outpatient Visit",
                allowedFor: allowedForAll,
                encounterTypeConfig: encounterTypeConfig,
                elements: [
                    checkIn,
                    vaccinations,
                    vitals,
                    reviewAllergies,
                    labResults,
                    primaryCareAdultHistory,
                    primaryCareExam,
                    primaryCareDx,
                    outpatientPlan,
                    addExpectedEncounters
                ]
            }
        };
        _.each(ret, function(it, key) {
            it.name = key;
        });
        return ret;
    }]);