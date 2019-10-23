angular.module("encounterTypeConfig", ["constants"])

    .factory("EncounterTypeConfig", [ "EncounterTypes", "EncounterRoles", function(EncounterTypes, EncounterRoles) {

        var hfeSimpleEditUrl = "/htmlformentryui/htmlform/editHtmlFormWithSimpleUi.page?patientId={{encounter.patient.uuid}}&encounterId={{encounter.uuid}}&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}";
        var hfeStandardEditUrl = "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{encounter.patient.uuid}}&encounterId={{encounter.uuid}}&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}";

        var cloneSectionAndUpdateCountryPathInUrls  = function(section, countryPath) {
          var newSection = Object.assign({}, section);
          newSection.templateModelUrl =
            newSection.templateModelUrl != null ?
            newSection.templateModelUrl.replace("pihcore:htmlforms", "pihcore:htmlforms/" + countryPath) : null;
          newSection.editUrl =
            newSection.editUrl != null ?
            newSection.editUrl.replace("pihcore:htmlforms", "pihcore:htmlforms/" + countryPath) : null;
          return newSection;
        };

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
        };

        // we include the edit url here because the "Next" navigator functionality uses it
        var allergies = {
            type: "include-section",
            id: "allergies",
            template: "templates/allergies/reviewAllergies.page",
            editUrl: "/allergyui/allergies.page?patientId={{patient.uuid}}&returnUrl={{returnUrl}}"
        };

        var vaccinations = {
            id: "chVaccinations",
            type: "include-section",
            template: "templates/vaccination/vaccinations.page",
            editUrl: ""
        };

        var pedsVaccinations = {
            type: "include-section",
            id: "vaccinations",
            template: "templates/vaccination/vaccinations.page",
            editUrl: "",
            require: "patientAgeInYearsOnDate(visit.startDatetime) < 15"
        };

        var ancVaccinations = {
            type: "include-section",
            id: "vaccinations",
            template: "templates/vaccination/vaccinations.page",
            editUrl: "",
            require: "patient.person.gender == 'F'"
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
            id: "physical-exam",
            label: "pihcore.exam.label",
            icon: "icon-stethoscope",
            shortTemplate: "templates/sections/examSectionShort.page",
            longTemplate: "templates/sections/viewSectionWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/section-exam.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/section-exam.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        };

        var pedsFoodAndSupplements = {
            type: "encounter-section",
            id: "pihcore-peds",
            label: "pihcore.foodAndSupplements.label",
            icon: "icon-food",
            shortTemplate: "templates/sections/pedsSectionShort.page",
            longTemplate: "templates/sections/viewSectionWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/section-peds.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/section-peds.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}",
            require: "patientAgeInYearsOnDate(visit.startDatetime) < 15"
        };

        var primaryCareDx = {
            type: "encounter-section",
            id: "pihcore-diagnosis",
            label: "pihcore.diagnosis.label",
            icon: "icon-diagnosis",
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

        var ncd = {
            type: "encounter-section",
            id: "pihcore-ncd",
            label: "pihcore.visitNote.ncdInitial",
            icon: "icon-heart-empty",
            shortTemplate: "templates/sections/ncdSectionShort.page",
            longTemplate: "templates/sections/viewSectionWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/section-ncd.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/section-ncd.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        }

        // ToDo: ncdInitial and ncdFollowup are the same and replaced by ncd
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

        var hivHistory = {
            type: "encounter-section",
            id: "hiv-history",
            label: "pihcore.history.label",
            icon: "icon-beaker",
            shortTemplate: "templates/sections/defaultSectionShort.page",
            longTemplate: "templates/sections/viewSectionWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/hiv/zl/section-hiv-history.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/hiv/zl/section-hiv-history.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
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


        var hivAssessment = {
            type: "encounter-section",
            id: "hiv-assessment",
            label: "pihcore.assessment",
            icon: "icon-th-large",
            shortTemplate: "templates/sections/hivSectionShort.page",
            longTemplate: "templates/sections/viewSectionWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/hiv/zl/section-hiv-assessment.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/hiv/zl/section-hiv-assessment.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
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

        var ancInitial = {
            type: "encounter-section",
            id: "section-anc-intake",
            label: "pihcore.ancIntake.title",
            icon: "icon-gift",
            shortTemplate: "templates/sections/ancIntakeSectionShort.page",
            longTemplate: "templates/sections/viewSectionWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/section-anc-intake.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/section-anc-intake.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        }

        var ancFollowup = {
            type: "encounter-section",
            id: "section-anc-followup",
            label: "pihcore.ancFollowup.title",
            icon: "icon-gift",
            shortTemplate: "templates/sections/ancIntakeSectionShort.page",
            longTemplate: "templates/sections/viewSectionWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/section-anc-followup.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/section-anc-followup.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        }

        var delivery = {
            type: "encounter-section",
            id: "section-delivery",
            label: "pihcore.delivery.title",
            icon: "icon-gift",
            shortTemplate: "templates/sections/deliverySectionShort.page",
            longTemplate: "templates/sections/viewSectionWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/section-delivery.xml",
            editUrl: "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/section-delivery.xml&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        }

        /**
         * Define Encounter Types
         * Should support all of the following formats:
         *
         * encounterTypes['some-uuid'] = {
         *    // single config
         * }
         *
         * encounterTypes['some-uuid'] = {
         *    DEFAULT: {
         *      // default config
         *    }
         *    SPECIFIC_COUNTRY: {
         *      // specific country config
         *    }
         * }
         *
         *
         * encounterTypes['some-uuid'] = {
         *    DEFAULT: {
         *      // default config
         *    }
         *    SPECIFIC_COUNTRY: {
         *      DEFAULT: {
         *        // specific country config
         *      }
         *      SPECIFIC_SITE: {
         *        // specific site config
         *      }
         *    }
         * }
         **/

        var encounterTypes = {
            DEFAULT: {
                defaultState: "short",
                shortTemplate: "templates/encounters/defaultEncounterShort.page",
                longTemplate: "templates/encounters/defaultHtmlFormEncounterLong.page",
                templateModelUrl: "/module/htmlformentry/encounter.json?encounter={{encounter.uuid}}",
                showOnVisitList: false
            }
        };

        encounterTypes[EncounterTypes.patientRegistration.uuid] = {  // should never appear on dashboard?
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultEncounterLong.page"
        };

        encounterTypes[EncounterTypes.checkIn.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/checkInShort.page",
            longTemplate: "templates/encounters/defaultHtmlFormEncounterLong.page",
            templateModelUrl: "/module/htmlformentry/encounter.json?encounter={{encounter.uuid}}",
            icon: "icon-check-in",
            editUrl: hfeSimpleEditUrl
        };

        encounterTypes[EncounterTypes.vitals.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/vitalsShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}",
            icon: "icon-vitals",
            editUrl: hfeSimpleEditUrl
        };

        encounterTypes[EncounterTypes.consultation.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/clinicConsultLong.page",
            icon: "icon-stethoscope",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true
        };

        encounterTypes[EncounterTypes.primaryCarePedsInitialConsult.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultEncounterShort.page",  // no expanded view, instead there are individual sections
            icon: "icon-stethoscope",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true,
            sections: [
                primaryCareHistory,
                pedsVaccinations,
                pedsFoodAndSupplements,
                primaryCareExam,
                primaryCareDx,
                primaryCarePlan
            ]
        };

        encounterTypes[EncounterTypes.primaryCarePedsFollowupConsult.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultEncounterShort.page",   // no expanded view, instead there are individual sections
            icon: "icon-stethoscope",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true,
            sections: [
                chiefComplaint,
                pedsVaccinations,
                pedsFoodAndSupplements,
                primaryCareExam,
                primaryCareDx,
                primaryCarePlan
            ]
        };

        encounterTypes[EncounterTypes.primaryCareAdultInitialConsult.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultEncounterShort.page",   // no expanded view, instead there are individual sections
            icon: "icon-stethoscope",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true,
            sections: [
                primaryCareHistory,
                primaryCareExam,
                primaryCareDx,
                primaryCarePlan
            ]
        };

        encounterTypes[EncounterTypes.primaryCareAdultFollowupConsult.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultEncounterShort.page",   // no expanded view, instead there are individual sections
            icon: "icon-stethoscope",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true,
            sections: [
                chiefComplaint,
                primaryCareExam,
                primaryCareDx,
                primaryCarePlan
            ]
        };

        encounterTypes[EncounterTypes.ncdInitialConsult.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultEncounterShort.page",
            icon: "icon-heart",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true,
            sections: [
                primaryCareHistory,
                primaryCareExam,
                pedsVaccinations,
                pedsFoodAndSupplements,
                ncd,
                primaryCareDx,
                primaryCarePlan
            ]
        };

        encounterTypes[EncounterTypes.ncdFollowupConsult.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultEncounterShort.page",
            icon: "icon-heart",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true,
            sections: [
                primaryCareExam,
                pedsVaccinations,
                ncd,
                primaryCareDx,
                primaryCarePlan
            ]
        };

        encounterTypes[EncounterTypes.echocardiogramConsult.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}",
            icon: "icon-heart-empty",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true
        };

        // ToDo: Replace the icon and add more sections
        encounterTypes[EncounterTypes.zlHivIntake.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultEncounterShort.page",   // no expanded view, instead there are individual sections
            icon: "icon-asterisk",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true,
            sections: [
                hivHistory,
                primaryCareExam,
                pedsVaccinations,
                // labRadOrder,
                hivAssessment,
                hivIntakePlan
            ]
        };

        // ToDo: Replace the icon and add sections
        encounterTypes[EncounterTypes.zlHivFollowup.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultEncounterShort.page",   // no expanded view, instead there are individual sections
            icon: "icon-asterisk",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true,
            sections: [
                hivState,
                primaryCareExam,
                pedsVaccinations,
                primaryCareDx,
                // ToDo: Modify plan for followup
                hivIntakePlan
            ]
        };

        encounterTypes[EncounterTypes.oncologyConsult.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}",
            icon: "icon-paste",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true
        };

        encounterTypes[EncounterTypes.oncologyInitialVisit.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}",
            icon: "icon-paste",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true
        };

        encounterTypes[EncounterTypes.chemotherapySession.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}",
            icon: "icon-retweet",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true
        };

        encounterTypes[EncounterTypes.medicationDispensed.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}",
            icon: "icon-medicine",
            editUrl: hfeStandardEditUrl
        };

        encounterTypes[EncounterTypes.postOperativeNote.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}",
            primaryEncounterRoleUuid: EncounterRoles.attendingSurgeon.uuid,
            icon: "icon-paste",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true
        };

        encounterTypes[EncounterTypes.transfer.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultHtmlFormEncounterLong.page",
            templateModelUrl: "/module/htmlformentry/encounter.json?encounter={{encounter.uuid}}",
            icon: "icon-share",
            editUrl: hfeStandardEditUrl
        };
        encounterTypes[EncounterTypes.admission.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/admissionLong.page",
            icon: "icon-signin",
            editUrl: hfeStandardEditUrl
        };

        encounterTypes[EncounterTypes.cancelAdmission.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}",
            icon: "icon-ban-circle",
            editUrl: hfeStandardEditUrl
        };

        encounterTypes[EncounterTypes.exitFromCare.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            icon: "icon-signout",
            editUrl: hfeStandardEditUrl
        };

        encounterTypes[EncounterTypes.labResults.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}",
            icon: "icon-beaker",
            editUrl: hfeSimpleEditUrl
        };

        encounterTypes[EncounterTypes.radiologyOrder.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultEncounterLong.page",
            icon: "icon-x-ray"
        };

        encounterTypes[EncounterTypes.radiologyStudy.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultEncounterLong.page",
            icon: "icon-x-ray"
        };

        encounterTypes[EncounterTypes.radiologyReport.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultEncounterLong.page",
            icon: "icon-x-ray"
        };

        encounterTypes[EncounterTypes.deathCertificate.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}",
            icon: "icon-remove-circle",
            editUrl: hfeStandardEditUrl
        };

        // because of a bug, we manually append the defintionUiResource to the template and edit urls
        // see: https://tickets.pih-emr.org/browse/UHM-2524
        encounterTypes[EncounterTypes.mentalHealth.uuid] = {
          DEFAULT: {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/mentalHealth.xml",
            icon: "icon-user",
            editUrl: hfeStandardEditUrl + "&definitionUiResource=pihcore:htmlforms/mentalHealth.xml",
            showOnVisitList: true
          },
          "liberia": {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/liberia/mentalHealth.xml",
            icon: "icon-user",
            editUrl: hfeStandardEditUrl + "&definitionUiResource=pihcore:htmlforms/liberia/mentalHealth.xml",
            showOnVisitList: true
          }
        };

        // HIV forms from MSPP and iSantePlus
        encounterTypes[EncounterTypes.artAdherence.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/hiv/iSantePlus/Adherence.xml",
            icon: "icon-asterisk",
            editUrl: hfeStandardEditUrl + "&definitionUiResource=pihcore:htmlforms/haiti/hiv/iSantePlus/Adherence.xml",
            showOnVisitList: true
        };
        encounterTypes[EncounterTypes.hivIntakeAdult.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/hiv/iSantePlus/SaisiePremiereVisiteAdult.xml",
            icon: "icon-asterisk",
            editUrl: hfeStandardEditUrl + "&definitionUiResource=pihcore:htmlforms/haiti/hiv/iSantePlus/SaisiePremiereVisiteAdult.xml",
            showOnVisitList: true
        };
        encounterTypes[EncounterTypes.hivIntakePeds.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/hiv/iSantePlus/SaisiePremiereVisitePediatrique.xml",
            icon: "icon-asterisk",
            editUrl: hfeStandardEditUrl + "&definitionUiResource=pihcore:htmlforms/haiti/hiv/iSantePlus/SaisiePremiereVisitePediatrique.xml",
            showOnVisitList: true
        };
        encounterTypes[EncounterTypes.hivFollowupAdult.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/hiv/iSantePlus/VisiteDeSuivi.xml",
            icon: "icon-asterisk",
            editUrl: hfeStandardEditUrl + "&definitionUiResource=pihcore:htmlforms/haiti/hiv/iSantePlus/VisiteDeSuivi.xml",
            showOnVisitList: true
        };
        encounterTypes[EncounterTypes.hivFollowupPeds.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/hiv/iSantePlus/VisiteDeSuiviPediatrique.xml",
            icon: "icon-asterisk",
            editUrl: hfeStandardEditUrl + "&definitionUiResource=pihcore:htmlforms/haiti/hiv/iSantePlus/VisiteDeSuiviPediatrique.xml",
            showOnVisitList: true
        };

        // CT
        encounterTypes[EncounterTypes.vct.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/hiv/zl/vct.xml",
            icon: "icon-asterisk",
            editUrl: hfeStandardEditUrl + "&definitionUiResource=pihcore:htmlforms/haiti/hiv/zl/vct.xml",
            showOnVisitList: true
        };

        // Socio-economics (socioEcon)
        encounterTypes[EncounterTypes.socioEconomics.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/socio-econ.xml",
            icon: "icon-home",
            editUrl: hfeStandardEditUrl + "&definitionUiResource=pihcore:htmlforms/socio-econ.xml",
            showOnVisitList: true
        };

        encounterTypes[EncounterTypes.primaryCareVisit.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}",
            icon: "icon-heart",
            editUrl: hfeStandardEditUrl
        };

        encounterTypes[EncounterTypes.edTriage.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/edTriageShort.page",
            icon: "icon-ambulance",
            editUrl: "edtriageapp/edtriageEditPatient.page?editable=true&patientId={{patient.uuid}}&encounterId={{encounter.uuid}}&appId=edtriageapp.app.triageQueue&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}",
            viewUrl: "edtriageapp/edtriageEditPatient.page?editable=false&patientId={{patient.uuid}}&encounterId={{encounter.uuid}}&appId=edtriageapp.app.triageQueue&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}"
        };

        encounterTypes[EncounterTypes.testOrder.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/testOrderLong.page",
            icon: "icon-beaker"
        };

        encounterTypes[EncounterTypes.pathologySpecimenCollection.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/pathologySpecimenCollectionLong.page",
            icon: "icon-beaker"
        };

        encounterTypes[EncounterTypes.labSpecimenCollection.uuid] = {
          defaultState: "short",
          shortTemplate: "templates/encounters/defaultEncounterShort.page",
          longTemplate: "templates/encounters/labsSpecimenEncounterLong.page",
          icon: "icon-beaker"
        };

        // MCH/Prenatal
        encounterTypes[EncounterTypes.ancIntake.uuid] = {
          DEFAULT: {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultEncounterShort.page",
            icon: "icon-gift",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true,
            sections: [
              ancInitial,
              ancVaccinations,
              primaryCareDx
            ]
          },
          'sierra_leone': {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultEncounterShort.page",
            icon: "icon-gift",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true,
            sections: [
              cloneSectionAndUpdateCountryPathInUrls(ancInitial, "sierra_leone"),
              ancVaccinations,
              cloneSectionAndUpdateCountryPathInUrls(primaryCareDx, "sierra_leone")
            ]
          }
        };

        encounterTypes[EncounterTypes.vaccination.uuid] = {
            defaultState: "long",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/vaccination/chVaccinations.page",
            icon: "icon-umbrella",
            showOnVisitList: true
        };


        encounterTypes[EncounterTypes.ancFollowup.uuid] = {
          DEFAULT: {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultEncounterShort.page",
            icon: "icon-gift",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true,
            sections: [
              ancFollowup,
              ancVaccinations,
              primaryCareDx
            ]
          },
          'sierra_leone': {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultEncounterShort.page",
            icon: "icon-gift",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true,
            sections: [
              cloneSectionAndUpdateCountryPathInUrls(ancFollowup, "sierra_leone"),
              ancVaccinations,
              cloneSectionAndUpdateCountryPathInUrls(primaryCareDx, "sierra_leone")
            ]
          }
        };

        encounterTypes[EncounterTypes.delivery.uuid] = {
          DEFAULT: {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultEncounterShort.page",
            icon: "icon-gift",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true,
            sections: [
              delivery,
              primaryCareDx
            ]
          },
          'sierra_leone': {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultEncounterShort.page",
            icon: "icon-gift",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true,
            sections: [
              cloneSectionAndUpdateCountryPathInUrls(delivery, "sierra_leone"),
              cloneSectionAndUpdateCountryPathInUrls(primaryCareDx, "sierra_leone")
            ]
          }
        };

        // TODO change Mexico and Sierra Leone consults to use standard outpatient encounter types now that we support multiple configs per encounter type?
        encounterTypes[EncounterTypes.mexicoConsult.uuid] = {
            defaultState: "long",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/viewEncounterWithHtmlFormLong.page",
            templateModelUrl: "/htmlformentryui/htmlform/viewEncounterWithHtmlForm/getAsHtml.action?encounterId={{encounter.uuid}}&definitionUiResource=pihcore:htmlforms/mexico/consult.xml",
            icon: "icon-stethoscope",
            editUrl: hfeStandardEditUrl + "&definitionUiResource=pihcore:htmlforms/mexico/consult.xml",
            showOnVisitList: true
        };

        encounterTypes[EncounterTypes.sierraLeoneOutpatientInitial.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultEncounterShort.page",
            icon: "icon-stethoscope",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true,
            sections: [
                cloneSectionAndUpdateCountryPathInUrls(primaryCareHistory, "sierra_leone"),
                pedsVaccinations,
                cloneSectionAndUpdateCountryPathInUrls(pedsFoodAndSupplements,"sierra_leone"),
                cloneSectionAndUpdateCountryPathInUrls(primaryCareExam,"sierra_leone"),
                cloneSectionAndUpdateCountryPathInUrls(primaryCareDx,"sierra_leone"),
                cloneSectionAndUpdateCountryPathInUrls(primaryCarePlan, "sierra_leone")
            ]
        };

        encounterTypes[EncounterTypes.sierraLeoneOutpatientFollowup.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/encounters/defaultEncounterShort.page",
            longTemplate: "templates/encounters/defaultEncounterShort.page",
            icon: "icon-stethoscope",
            editUrl: hfeStandardEditUrl,
            showOnVisitList: true,
            sections: [
                cloneSectionAndUpdateCountryPathInUrls(chiefComplaint,"sierra_leone"),
                pedsVaccinations,
                cloneSectionAndUpdateCountryPathInUrls(pedsFoodAndSupplements,"sierra_leone"),
                cloneSectionAndUpdateCountryPathInUrls(primaryCareExam,"sierra_leone"),
                cloneSectionAndUpdateCountryPathInUrls(primaryCareDx,"sierra_leone"),
                cloneSectionAndUpdateCountryPathInUrls(primaryCarePlan, "sierra_leone")
            ]
        };

        return {
          get: function(uuid, country, site) {
            var encounterType = encounterTypes[uuid];

            if (encounterType == null) {
              return null;
            }

            if (encounterType.hasOwnProperty(country)) {
               if (encounterType[country].hasOwnProperty(site)) {
                 return encounterType[country][site];
               }
               else {
                 return encounterType[country].hasOwnProperty('DEFAULT') ?
                   encounterType[country]['DEFAULT'] : encounterType[country];
               }
            }

            return encounterType.hasOwnProperty(['DEFAULT']) ?
              encounterType['DEFAULT'] : encounterType;
          }
        };
    }]);
