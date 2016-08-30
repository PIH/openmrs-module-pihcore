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
        var allergies = {
            type: "include-section",
            template: "templates/allergies/reviewAllergies.page"
        };

        var vaccinations = {
            type: "include-section",
            template: "templates/vaccination/vaccinations.page"
        };

        var primaryCareHistory = {
            type: "encounter-section",
            id: "pihcore-history",
            label: "pihcore.history.label",
            icon: "icon-file-alt",
            classes: "indent",
            shortTemplate: "templates/sections/primaryCareHistorySectionShort.page",
            longTemplate: "templates/sections/viewSectionWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/section-history.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/section-history.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        };

        var primaryCareExam = {
            type: "encounter-section",
            id: "pihcore-exam",
            label: "pihcore.exam.label",
            icon: "icon-stethoscope",
            shortTemplate: "templates/sections/defaultSectionShort.page",
            longTemplate: "templates/sections/viewSectionWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/section-exam.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithSimpleUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/section-exam.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        };

        var primaryCareDisposition = {
            type: "encounter-section",
            id: "pihcore-disposition",
            label: "pihcore.disposition.label",
            icon: "icon-stethoscope",
            shortTemplate: "templates/sections/defaultSectionShort.page",
            longTemplate: "templates/sections/dispositionLong.page",
            //templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/section-disposition.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/section-disposition.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        };

        var feeding = {
            type: "encounter-section",
            id: "pihcore-feeding",
            label: "pihcore.feeding.history.label",
            icon: "icon-food",
            shortTemplate: "templates/sections/feedingSectionShort.page",
            longTemplate: "templates/sections/viewSectionWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/section-peds-feeding.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/section-peds-feeding.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        };

        var supplements = {
            type: "encounter-section",
            id: "pihcore-supplements",
            label: "pihcore.supplements.history.label",
            icon: "icon-asterisk",
            shortTemplate: "templates/sections/supplementsSectionShort.page",
            longTemplate: "templates/sections/viewSectionWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/section-peds-supplements.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/section-peds-supplements.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        };

        var primaryCareDx = {
            type: "encounter-section",
            id: "pihcore-diagnosis",
            label: "pihcore.diagnosis.label",
            icon: "icon-list-ul",
            shortTemplate: "templates/sections/dxSectionShort.page",
            longTemplate: "templates/sections/dxLong.page",
            //templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/section-dx.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/section-dx.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        };

        var primaryCarePlan = {
            type: "encounter-section",
            id: "pihcore-plan",
            label: "pihcore.visitNote.plan",
            icon: "icon-list-ul",
            shortTemplate: "templates/sections/defaultSectionShort.page",
            longTemplate: "templates/sections/viewSectionWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/section-plan.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/section-plan.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"

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
            shortTemplate: "templates/encounters/old/vitalsShort.page",
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
                vaccinations,
                supplements,
                allergies,
                primaryCareHistory,
                feeding,
                primaryCareExam,
                primaryCareDx,
                primaryCarePlan,
                primaryCareDisposition
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
                vaccinations,
                supplements,
                allergies,
                feeding,
                primaryCareExam,
                primaryCareDx,
                primaryCarePlan,
                primaryCareDisposition
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
                allergies,
                primaryCareHistory,
                primaryCareExam,
                primaryCareDx,
                primaryCarePlan,
                primaryCareDisposition
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
                allergies,
                primaryCareExam,
                primaryCareDx,
                primaryCarePlan,
                primaryCareDisposition
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
            editUrl: hfeStandardEditUrl
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

        encounterTypeConfig[EncounterTypes.ncdConsult.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}",
            icon: "icon-heart",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true
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
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            icon: "icon-ambulance",
            editUrl: "edtriageapp/edtriageEditPatient.page?editable=true&patientId={{patient.uuid}}&encounterId={{encounter.uuid}}&appId=edtriageapp.app.triageQueue&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}",
            viewUrl: "edtriageapp/edtriageEditPatient.page?editable=false&patientId={{patient.uuid}}&encounterId={{encounter.uuid}}&appId=edtriageapp.app.triageQueue&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        };

        return encounterTypeConfig;
    }]);