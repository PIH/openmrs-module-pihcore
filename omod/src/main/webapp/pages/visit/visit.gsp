<%
    ui.decorateWith("appui", "standardEmrPage")

    ui.includeCss("uicommons", "ngDialog/ngDialog.min.css")
    ui.includeCss("pihcore", "visit/visit.css")

    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.13.0.js")
    ui.includeJavascript("uicommons", "angular-ui/angular-ui-router.min.js")
    ui.includeJavascript("uicommons", "ngDialog/ngDialog.min.js")
    ui.includeJavascript("uicommons", "angular-resource.min.js")
    ui.includeJavascript("uicommons", "angular-common.js")
    ui.includeJavascript("uicommons", "angular-app.js")
    ui.includeJavascript("uicommons", "angular-translate.min.js")
    ui.includeJavascript("uicommons", "angular-translate-loader-url.min.js")
    ui.includeJavascript("uicommons", "services/visitService.js")
    ui.includeJavascript("uicommons", "services/encounterService.js")
    ui.includeJavascript("uicommons", "services/locationService.js")
    ui.includeJavascript("uicommons", "services/obsService.js")
    ui.includeJavascript("uicommons", "services/conceptService.js")
    ui.includeJavascript("uicommons", "services/orderService.js")
    ui.includeJavascript("uicommons", "services/drugService.js")
    ui.includeJavascript("uicommons", "services/session.js")
    ui.includeJavascript("uicommons", "services/appFrameworkService.js")
    ui.includeJavascript("uicommons", "model/user-model.js")
    ui.includeJavascript("uicommons", "model/encounter-model.js")
    ui.includeJavascript("uicommons", "model/visit-model.js")
    ui.includeJavascript("uicommons", "filters/display.js")
    ui.includeJavascript("uicommons", "filters/serverDate.js")
    ui.includeJavascript("uicommons", "directives/select-drug.js")
    ui.includeJavascript("uicommons", "directives/select-concept-from-list.js")
    ui.includeJavascript("uicommons", "directives/select-order-frequency.js")
    ui.includeJavascript("uicommons", "handlebars/handlebars.js")
    ui.includeJavascript("uicommons", "moment.min.js")
    ui.includeJavascript("pihcore", "visit/constants.js")
    ui.includeJavascript("pihcore", "visit/filters.js")
    ui.includeJavascript("pihcore", "visit/allergies.js")
    ui.includeJavascript("pihcore", "visit/vaccinations.js")
    ui.includeJavascript("pihcore", "visit/visit.js")
    ui.includeJavascript("pihcore", "visit/encounterTransaction.js")
    ui.includeJavascript("pihcore", "services/configService.js")
    ui.includeJavascript("pihcore", "services/queueEntryService.js")
    ui.includeJavascript("file", "configuration/pih/scripts/visit/encounterTypeConfig.js", /* priority= */ null, /* pathIsRelativeToScripts= */ false)

    if (locale != "en") {
        ui.includeJavascript("pihcore", "angular-locale/angular-locale_" + locale + ".js")
    }
%>

<link href="/${ contextPath }/moduleResources/htmlformentry/orderWidget.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="/${ contextPath }/moduleResources/htmlformentry/orderWidget.js"></script>


<script type="text/javascript">

  // breadcrumbs for the enconuter overview (view)
  <% if (initialRouterState == 'encounterOverview') { %>
      var breadcrumbsOverview = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.escapeJs(patient.formattedName) }", link: '${ ui.urlBind("/" + contextPath + dashboardUrl, [ patientId: patient.patient.uuid ] ) }'},
        { label: "${ ui.message("pihcore.encounterList") }" , link: '${ui.pageLink("pihcore", "patient/encounterList", ["patientId": patient.id])}'},
        { label: "${ ui.format(encounter.encounterType)}" }
      ];

      var breadcrumbOverride = [
        { label: "${ ui.escapeJs(patient.formattedName) }", link: '${ ui.urlBind("/" + contextPath + dashboardUrl, [ patientId: patient.patient.uuid ] ) }'},
        { label: "${ ui.message("pihcore.encounterList") }" , link: '${ui.pageLink("pihcore", "patient/encounterList", ["patientId": patient.id])}'},
        { label: "${ ui.format(encounter.encounterType) }", link: "${visit ? ui.escapeJs(ui.pageLink("pihcore", "visit/visit", ["visit":visit.uuid, "initialRouterState": initialRouterState, "encounter": encounter.uuid ])) : ''}" }
      ];

  // breadcrumbs for the standard view
  <% } else { %>
      var breadcrumbsOverview = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.escapeJs(patient.formattedName) }", link: '${ ui.urlBind("/" + contextPath + dashboardUrl, [ patientId: patient.patient.uuid ] ) }'},
        { label: "${ui.message("pihcore.visitNote.Visit")}" }
      ];

      var breadcrumbOverride = [
        { label: "${ ui.escapeJs(patient.formattedName) }", link: '${ ui.urlBind("/" + contextPath + dashboardUrl, [ patientId: patient.patient.uuid ] ) }'},
        { label: "${ui.message("pihcore.visitNote.Visit")}", link: "${visit ? ui.escapeJs(ui.pageLink("pihcore", "visit/visit", ["visit":visit.uuid, "initialRouterState": initialRouterState])) : ''}" }
      ];

  <% } %>


    var breadcrumbs = breadcrumbsOverview;

    emr.loadGlobalProperties(["order.drugRoutesConceptUuid", "order.drugDosingUnitsConceptUuid", "order.drugDispensingUnitsConceptUuid",
        "order.durationUnitsConceptUuid", "order.testSpecimenSourcesConceptUuid"]);

</script>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient.patient ]) }

<div id="visit-app" ng-controller="VisitController">
    <div class="row">
        <div class="col-12" ui-view></div>
    </div>
</div>

<script type="text/javascript">
    angular.module('visit')
            .value('patientUuid', '${ patient.patient.uuid }')
            .value('visitUuid', ${ visit?.uuid ? "'" + visit.uuid + "'" : null})
            .value('visitTypeUuid', ${ visitType?.uuid ? "'" + visitType.uuid + "'" : null})
            .value('encounterTypeUuid', ${ encounterType?.uuid ? "'" + encounterType.uuid + "'" : null})
            .value('suppressActions', ${ suppressActions ? suppressActions : false})
            .value('encounterUuid', ${ encounter?.uuid ? "'" + encounter.uuid + "'" : null})
            .value('locale', '${ locale }')
            .value('currentSection', ${ currentSection ? "'" + currentSection + "'" : null})
            .value('goToNext', ${ goToNext ? true : false})
            .value('nextSection', ${ nextSection ? "'" +  nextSection + "'"  : null })
            .value('initialRouterState', ${initialRouterState ? "'" + initialRouterState + "'" : null})
            .value('country', '${ country }')
            .value('site', '${ site }');
    angular.bootstrap("#visit-app", [ "visit" ]);

    jq(function() {
        // make sure we reload the page if the location is changes; this custom event is emitted by by the location selector in the header
        jq(document).on('sessionLocationChanged', function() {
            window.location.reload();
        });
    });

</script>
