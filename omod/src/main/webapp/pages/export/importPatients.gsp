<%
    ui.decorateWith("appui", "standardEmrPage")

    ui.includeCss("uicommons", "ngDialog/ngDialog.min.css")
    ui.includeCss("pihcore", "visit/visit.css")
    ui.includeCss("pihcore", "bootstrap.css")

    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.13.0.js")
    ui.includeJavascript("uicommons", "angular-ui/angular-ui-router.min.js")
    ui.includeJavascript("uicommons", "ngDialog/ngDialog.min.js")
    ui.includeJavascript("uicommons", "angular-resource.min.js")
    ui.includeJavascript("uicommons", "angular-common.js")
    ui.includeJavascript("uicommons", "angular-app.js")
    ui.includeJavascript("uicommons", "angular-translate.min.js")
    ui.includeJavascript("uicommons", "angular-translate-loader-url.min.js")
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

    ui.includeJavascript("pihcore", "export/importPatientController.js")
%>
<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("emr.app.systemAdministration.label")}", link: '${ui.pageLink("coreapps", "systemadministration/systemAdministration")}' },
        { label: "${ ui.message("pihcore.patient.import")}" }
    ];
</script>

<h3>${  ui.message("pihcore.patient.import") }</h3>

<div class="container" id="importPatient-app" ng-controller="ImportPatientController">

    <h1>${ ui.message("pihcore.import.patientFile") }:</h1>
    <input type="file" on-read-file="showContent(fileContent)" />
    <br>
    <div ng-if="content && !errorMessage">
        <button type="button" class="confirm" ng-click="importPatients()">${ ui.message("pihcore.import") }</button>
        <br>
        <br>
        <pre>{{ content }}</pre>
    </div>

    <div ng-if="errorMessage">
        <div class="alert">${ ui.message("pihcore.import.error") }:</div>
        <br>
        <br>
        <pre>{{ errorMessage }}</pre>
    </div>

    <div ng-if="importedPatients.length != 0">
        <h3>${ ui.message("pihcore.import.patientsImported") }:</h3>
        <table id="list-patients" cellspacing="0" cellpadding="2">
            <thead>
            <tr>
                <th>${ ui.message("emr.person.name")}</th>
                <th>${ ui.message("emr.gender") }</th>
                <th>${ ui.message("coreapps.age") }</th>
            </tr>
            </thead>
            <tbody>
            <tr ng-repeat="record in importedPatients">
                <td>
                    <a ng-click="navigateTo(record.patient.uuid)">{{ record.patient.display }}</a>
                </td>
                <td>
                    {{ record.patient.person.gender }}
                </td>
                <td>
                    {{ record.patient.person.age }}
                </td>
            </tr>
            </tbody>
        </table>
    </div>

</div>

<script type="text/javascript">
    angular.module('importPatientApp');
    angular.bootstrap("#importPatient-app", [ "importPatientApp" ]);
    jq(function () {
        jq(document).on('sessionLocationChanged', function () {
            window.location.reload();
        });
    });
</script>

