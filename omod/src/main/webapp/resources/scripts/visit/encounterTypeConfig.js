angular.module("encounterTypeConfig", ["constants"])

    .factory("EncounterTypeConfig", [ "EncounterTypes", "EncounterRoles", function(EncounterTypes, EncounterRoles) {

        var hfeSimpleEditUrl = "/htmlformentryui/htmlform/editHtmlFormWithSimpleUi.page?patientId={{encounter.patient.uuid}}&encounterId={{encounter.uuid}}&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}";
        var hfeStandardEditUrl = "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{encounter.patient.uuid}}&encounterId={{encounter.uuid}}&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}";

        // template model url:
        // if a template operates off an model different that the standard OpenMRS REST representation of an encounter,
        // you specify the URL of the source here; used currently for htmlFormEntry encounter templates, which
        // require the encounter to be formatted using the HFE schema

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
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}",
            icon: "icon-check-in",
            editUrl: hfeSimpleEditUrl
        };
        encounterTypeConfig[EncounterTypes.vitals.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultHtmlFormEncounterLong.page",
            templateModelUrl: "/module/htmlformentry/encounter.json?encounter={{encounter.uuid}}",
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
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}",
            icon: "icon-stethoscope",
            editUrl: hfeSimpleEditUrl
        };
        encounterTypeConfig[EncounterTypes.primaryCareDisposition.uuid] = {
            defaultState: "long",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultHtmlFormEncounterLong.page",
            templateModelUrl: "/module/htmlformentry/encounter.json?encounter={{encounter.uuid}}",
            icon: "icon-stethoscope",
            editUrl: hfeStandardEditUrl
        };
        encounterTypeConfig[EncounterTypes.primaryCarePedsFeeding.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}",
            icon: "icon-food",
            editUrl: hfeStandardEditUrl
        };
        encounterTypeConfig[EncounterTypes.primaryCarePedsSupplements.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}",
            icon: "icon-asterisk",
            editUrl: hfeStandardEditUrl
        };
        encounterTypeConfig[EncounterTypes.primaryCareDx.uuid] = {
            defaultState: "long",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}",
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

        return encounterTypeConfig;
    }]);