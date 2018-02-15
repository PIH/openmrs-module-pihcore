angular.module("encounterTypeConfig", ["constants"])

    .factory("EncounterTypeConfig", [ "EncounterTypes", "EncounterRoles", function(EncounterTypes, EncounterRoles) {

        var hfeSimpleEditUrl = "/htmlformentryui/htmlform/editHtmlFormWithSimpleUi.page?patientId={{encounter.patient.uuid}}&encounterId={{encounter.uuid}}&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}";
        var hfeStandardEditUrl = "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{encounter.patient.uuid}}&encounterId={{encounter.uuid}}&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}";

        // if use has only "enterConsultNote", must be an active visit; retroConsultNote or retroConsultNoteThisProviderOnly required for retro visits
        // TODO do we use this anywhere?
        var standardConsultNoteRequire = "(user.hasPrivilege('Task: emr.enterConsultNote') && visit && !visit.stopDatetime) || (user.hasPrivilege('Task: emr.retroConsultNote') || user.hasPrivilege('Task: emr.retroConsultNoteThisProviderOnly'))"


        // template model url:
        // if a template operates off an model different that the standard OpenMRS REST representation of an encounter,
        // you specify the URL of the source here; used currently for htmlFormEntry encounter templates, which
        // require the encounter to be formatted using the HFE schema

        /* Define Sections */
        var chiefComplaint = {
            type: "encounter-section",
            id: "pihcore-chief-complaint",
            label: "pihcore.chiefComplaint.title",
            icon: "icon-list-ul",
            classes: "indent",
            shortTemplate: "templates/sections/chiefComplaintSectionShort.page",
            longTemplate: "templates/sections/viewSectionWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/section-chief-complaint.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/section-chief-complaint.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        }

        // we include the edit url here because the "Next" navigator functionality uses it
        var allergies = {
            type: "include-section",
            id: "allergies",
            template: "templates/allergies/reviewAllergies.page",
            editUrl: "/allergyui/allergies.page?patientId={{patient.uuid}}&returnUrl={{returnUrl}}"
        };

        var vaccinations = {
            type: "include-section",
            id: "vaccinations",
            template: "templates/vaccination/vaccinations.page",
            editUrl: ""
        };

        var primaryCareHistory = {
            type: "encounter-section",
            id: "pihcore-history",
            label: "pihcore.history.label",
            icon: "icon-file-alt",
            classes: "indent",
            shortTemplate: "templates/sections/primaryCareHistorySectionShort.page",
            longTemplate: "templates/sections/viewSectionWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/section-history.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/section-history.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        };

        var primaryCareExam = {
            type: "encounter-section",
            id: "pihcore-exam",
            label: "pihcore.exam.label",
            icon: "icon-stethoscope",
            shortTemplate: "templates/sections/examSectionShort.page",
            longTemplate: "templates/sections/viewSectionWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/section-exam.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithSimpleUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/section-exam.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        };

        var feeding = {
            type: "encounter-section",
            id: "pihcore-feeding",
            label: "pihcore.feeding.history.label",
            icon: "icon-food",
            shortTemplate: "templates/sections/feedingSectionShort.page",
            longTemplate: "templates/sections/viewSectionWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/section-peds-feeding.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/section-peds-feeding.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        };

        var supplements = {
            type: "encounter-section",
            id: "pihcore-supplements",
            label: "pihcore.supplements.history.label",
            icon: "icon-asterisk",
            shortTemplate: "templates/sections/supplementsSectionShort.page",
            longTemplate: "templates/sections/viewSectionWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/section-peds-supplements.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/section-peds-supplements.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        };

        var primaryCareDx = {
            type: "encounter-section",
            id: "pihcore-diagnosis",
            label: "pihcore.diagnosis.label",
            icon: "icon-list-ul",
            shortTemplate: "templates/sections/dxSectionShort.page",
            longTemplate: "templates/sections/dxLong.page",
            //templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/section-dx.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/section-dx.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        };

        var primaryCarePlan = {
            type: "encounter-section",
            id: "pihcore-plan",
            label: "pihcore.visitNote.plan",
            icon: "icon-list-ul",
            shortTemplate: "templates/sections/primaryCarePlanSectionShort.page",
            longTemplate: "templates/sections/viewPlanSectionWithHtmlFormLong.page",
            printTemplate: "templates/sections/printPrescriptionsWithHtmlFormLong.page",
            printTemplateUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/section-prescriptions-print.xml",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/section-plan.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/section-plan.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"

        };

        var ncdInitial = {
            type: "encounter-section",
            id: "pihcore-ncd",
            label: "pihcore.visitNote.ncdInitial",
            icon: "icon-heart",
            shortTemplate: "templates/sections/defaultSectionShort.page",
            longTemplate: "templates/sections/viewSectionWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/section-ncd.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/section-ncd.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        }

        var ncdFollowup = {
            type: "encounter-section",
            id: "pihcore-ncd",
            label: "pihcore.visitNote.ncdFollowup",
            icon: "icon-heart",
            shortTemplate: "templates/sections/defaultSectionShort.page",
            longTemplate: "templates/sections/viewSectionWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/section-ncd.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/section-ncd.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        }

        var hivSero = {
            type: "encounter-section",
            id: "hiv-serology",
            label: "pihcore.hiv.serology.title",
            icon: "icon-beaker",
            shortTemplate: "templates/sections/defaultSectionShort.page",
            longTemplate: "templates/sections/viewSectionWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/hiv/zl/section-hiv-serology.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/hiv/zl/section-hiv-serology.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        };

        var labRadOrder = {
            type: "encounter-section",
            id: "lab-rad-order",
            label: "pihcore.order.title",
            icon: "icon-beaker",
            shortTemplate: "templates/sections/defaultSectionShort.page",
            longTemplate: "templates/sections/viewSectionWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/section-lab-order.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/section-lab-order.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        };

        // ToDo: Use chief complaints instead (PrimaryCareDx)
        var hivSymptoms = {
            type: "encounter-section",
            id: "hiv-symptoms",
            label: "pihcore.hiv.symptomSigns",
            icon: "icon-asterisk",
            shortTemplate: "templates/sections/defaultSectionShort.page",
            longTemplate: "templates/sections/viewSectionWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/hiv/zl/section-symptoms.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/hiv/zl/section-symptoms.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        };

        var whoHIVStages = {
            type: "encounter-section",
            id: "hiv-who-stages",
            label: "pihcore.hiv.whoStages",
            icon: "icon-th-large",
            shortTemplate: "templates/sections/hivSectionShort.page",
            longTemplate: "templates/sections/viewSectionWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/hiv/zl/section-who-stages.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/hiv/zl/section-who-stages.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        };

        var familyPlanningHistory = {
            type: "encounter-section",
            id: "family-planning-history",
            label: "pihcore.familyPlanning.title",
            icon: "icon-group",
            shortTemplate: "templates/sections/defaultSectionShort.page",
            longTemplate: "templates/sections/viewSectionWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/section-family-planning.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/section-family-planning.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        };

        var hivIntakePlan = {
            type: "encounter-section",
            id: "hiv-intake-plan",
            label: "pihcore.visitNote.plan",
            icon: "icon-list-ul",
            shortTemplate: "templates/sections/defaultSectionShort.page",
            longTemplate: "templates/sections/viewSectionWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/hiv/zl/section-hiv-intake-plan.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/hiv/zl/section-hiv-intake-plan.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        };

        var hivOI = {
            type: "encounter-section",
            id: "hiv-oi",
            label: "pihcore.oi.short",
            icon: "icon-bolt",
            shortTemplate: "templates/sections/defaultSectionShort.page",
            longTemplate: "templates/sections/viewSectionWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/hiv/zl/section-hiv-oi.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/hiv/zl/section-hiv-oi.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        };

        var hivState = {
            type: "encounter-section",
            id: "hiv-state",
            label: "pihcore.hiv.clinicalState.short",
            icon: "icon-bolt",
            shortTemplate: "templates/sections/defaultSectionShort.page",
            longTemplate: "templates/sections/viewSectionWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/hiv/zl/section-hiv-state.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/hiv/zl/section-hiv-state.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        };


        /* Define Encounter Types */
        var encounterTypeConfig = {
            DEFAULT: {
                defaultState: "short",
                shortTemplate: "templates/encounters/defaultEncounterShort.page",
                longTemplate: "templates/encounters/defaultHtmlFormEncounterLong.page",
                templateModelUrl: "/module/htmlformentry/encounter.json?encounter={{encounter.uuid}}",
                showOnVisitList: false
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
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}",
            icon: "icon-vitals",
            editUrl: hfeSimpleEditUrl
        };

        encounterTypeConfig[EncounterTypes.consultation.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/clinicConsultLong.page",
            icon: "icon-stethoscope",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true
        };

        encounterTypeConfig[EncounterTypes.primaryCarePedsInitialConsult.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultEncounterShort.page",  // no expanded view, instead there are individual sections
            icon: "icon-stethoscope",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true,
            sections: [
                chiefComplaint,
                vaccinations,
                supplements,
                //allergies,
                primaryCareHistory,
                feeding,
                primaryCareExam,
                primaryCareDx,
                primaryCarePlan
            ]
        };

        encounterTypeConfig[EncounterTypes.primaryCarePedsFollowupConsult.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultEncounterShort.page",   // no expanded view, instead there are individual sections
            icon: "icon-stethoscope",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true,
            sections: [
                chiefComplaint,
                vaccinations,
                supplements,
                //allergies,
                feeding,
                primaryCareExam,
                primaryCareDx,
                primaryCarePlan
            ]
        };

        encounterTypeConfig[EncounterTypes.primaryCareAdultInitialConsult.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultEncounterShort.page",   // no expanded view, instead there are individual sections
            icon: "icon-stethoscope",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true,
            sections: [
                chiefComplaint,
                //allergies,
                primaryCareHistory,
                primaryCareExam,
                primaryCareDx,
                primaryCarePlan
            ]
        };

        encounterTypeConfig[EncounterTypes.primaryCareAdultFollowupConsult.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultEncounterShort.page",   // no expanded view, instead there are individual sections
            icon: "icon-stethoscope",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true,
            sections: [
                chiefComplaint,
                //allergies,
                primaryCareExam,
                primaryCareDx,
                primaryCarePlan
            ]
        };

        encounterTypeConfig[EncounterTypes.ncdInitialConsult.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultEncounterShort.page",   // no expanded view, instead there are individual sections
            icon: "icon-heart",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true,
            sections: [
                primaryCareHistory,
                primaryCareExam,
                primaryCareDx,
                ncdInitial,
                primaryCarePlan
            ]
        }

        encounterTypeConfig[EncounterTypes.ncdFollowupConsult.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultEncounterShort.page",   // no expanded view, instead there are individual sections
            icon: "icon-heart",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true,
            sections: [
                primaryCareExam,
                primaryCareDx,
                ncdFollowup,
                primaryCarePlan
            ]
        };

        // ToDo: Replace the icon and add more sections
        encounterTypeConfig[EncounterTypes.zlHivIntake.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultEncounterShort.page",   // no expanded view, instead there are individual sections
            icon: "icon-asterisk",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true,
            sections: [
                hivSero,
                familyPlanningHistory,
                // hivSymptoms,
                primaryCareDx,
                primaryCareExam,
                // labRadOrder,
                hivOI,
                whoHIVStages,
                hivIntakePlan
            ]
        };

        // ToDo: Replace the icon and add sections
        encounterTypeConfig[EncounterTypes.zlHivFollowup.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultEncounterShort.page",   // no expanded view, instead there are individual sections
            icon: "icon-asterisk",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true,
            sections: [
                hivState,
                primaryCareDx,
                primaryCareExam,
                // ToDo: Modify plan for followup
                hivIntakePlan
            ]
        };

        encounterTypeConfig[EncounterTypes.oncologyConsult.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}",
            icon: "icon-paste",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true
        };

        encounterTypeConfig[EncounterTypes.oncologyInitialVisit.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}",
            icon: "icon-paste",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true
        };

        encounterTypeConfig[EncounterTypes.chemotherapySession.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}",
            icon: "icon-retweet",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true
        };

        encounterTypeConfig[EncounterTypes.medicationDispensed.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}",
            icon: "icon-medicine",
            editUrl: hfeStandardEditUrl
        };

        encounterTypeConfig[EncounterTypes.postOperativeNote.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}",
            primaryEncounterRoleUuid: EncounterRoles.attendingSurgeon.uuid,
            icon: "icon-paste",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true
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
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/admissionLong.page",
            icon: "icon-signin",
            editUrl: hfeStandardEditUrl
        };

        encounterTypeConfig[EncounterTypes.cancelAdmission.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}",
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
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}",
            icon: "icon-beaker",
            editUrl: hfeSimpleEditUrl
        };

        encounterTypeConfig[EncounterTypes.radiologyOrder.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
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
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}",
            icon: "icon-remove-circle",
            editUrl: hfeStandardEditUrl
        };

        // because of a bug, we manually append the defintionUiResource to the template and edit urls
        // see: https://tickets.pih-emr.org/browse/UHM-2524
        encounterTypeConfig[EncounterTypes.mentalHealth.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/mentalHealth.xml",
            icon: "icon-user",
            editUrl: hfeStandardEditUrl + "&definitionUiResource=pihcore:htmlforms/mentalHealth.xml",
            showOnVisitList: true
        };

        // HIV forms from MSPP and iSantePlus
        encounterTypeConfig[EncounterTypes.artAdherence.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/hiv/iSantePlus/Adherence.xml",
            icon: "icon-asterisk",
            editUrl: hfeStandardEditUrl + "&definitionUiResource=pihcore:htmlforms/haiti/hiv/iSantePlus/Adherence.xml",
            showOnVisitList: true
        };
        encounterTypeConfig[EncounterTypes.hivIntakeAdult.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/hiv/iSantePlus/SaisiePremiereVisiteAdult.xml",
            icon: "icon-asterisk",
            editUrl: hfeStandardEditUrl + "&definitionUiResource=pihcore:htmlforms/haiti/hiv/iSantePlus/SaisiePremiereVisiteAdult.xml",
            showOnVisitList: true
        };
        encounterTypeConfig[EncounterTypes.hivIntakePeds.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/hiv/iSantePlus/SaisiePremiereVisitePediatrique.xml",
            icon: "icon-asterisk",
            editUrl: hfeStandardEditUrl + "&definitionUiResource=pihcore:htmlforms/haiti/hiv/iSantePlus/SaisiePremiereVisitePediatrique.xml",
            showOnVisitList: true
        };
        encounterTypeConfig[EncounterTypes.hivFollowupAdult.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/hiv/iSantePlus/VisiteDeSuivi.xml",
            icon: "icon-asterisk",
            editUrl: hfeStandardEditUrl + "&definitionUiResource=pihcore:htmlforms/haiti/hiv/iSantePlus/VisiteDeSuivi.xml",
            showOnVisitList: true
        };
        encounterTypeConfig[EncounterTypes.hivFollowupPeds.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/hiv/iSantePlus/VisiteDeSuiviPediatrique.xml",
            icon: "icon-asterisk",
            editUrl: hfeStandardEditUrl + "&definitionUiResource=pihcore:htmlforms/haiti/hiv/iSantePlus/VisiteDeSuiviPediatrique.xml",
            showOnVisitList: true
        };

        // CT
        encounterTypeConfig[EncounterTypes.vct.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/hiv/zl/vct.xml",
            icon: "icon-asterisk",
            editUrl: hfeStandardEditUrl + "&definitionUiResource=pihcore:htmlforms/haiti/hiv/zl/vct.xml",
            showOnVisitList: true
        };

        // Socio-economics (socioEcon)
        encounterTypeConfig[EncounterTypes.socioEconomics.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/socio-econ.xml",
            icon: "icon-home",
            editUrl: hfeStandardEditUrl + "&definitionUiResource=pihcore:htmlforms/socio-econ.xml",
            showOnVisitList: true
        };

        encounterTypeConfig[EncounterTypes.primaryCareVisit.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}",
            icon: "icon-heart",
            editUrl: hfeStandardEditUrl
        };

        encounterTypeConfig[EncounterTypes.edTriage.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/edTriageShort.page",
            icon: "icon-ambulance",
            editUrl: "edtriageapp/edtriageEditPatient.page?editable=true&patientId={{patient.uuid}}&encounterId={{encounter.uuid}}&appId=edtriageapp.app.triageQueue&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}",
            viewUrl: "edtriageapp/edtriageEditPatient.page?editable=false&patientId={{patient.uuid}}&encounterId={{encounter.uuid}}&appId=edtriageapp.app.triageQueue&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        };

        encounterTypeConfig[EncounterTypes.testOrder.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/testOrderLong.page",
            icon: "icon-beaker"
        };

        encounterTypeConfig[EncounterTypes.specimenCollection.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/specimenCollectionLong.page",
            icon: "icon-beaker"
        };

        return encounterTypeConfig;
    }]);