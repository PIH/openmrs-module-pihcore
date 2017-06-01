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

    ui.includeJavascript("pihcore", "export/exportPatientController.js")

%>
<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("emr.app.systemAdministration.label")}", link: '${ui.pageLink("coreapps", "systemadministration/systemAdministration")}' },
        { label: "${ ui.message("pihcore.patient.export")}" }
    ];
</script>

<h3>${  ui.message("pihcore.patient.export") }</h3>

<div class="container" id="exportPatient-app" ng-controller="ExportPatientController">
<table id="list-patients" cellspacing="0" cellpadding="2">
    <thead>
        <tr>
            <th>${ ui.message("emr.person.name")}</th>
            <th>${ ui.message("emr.gender") }</th>
            <th>${ ui.message("coreapps.age") }</th>
            <th>${ ui.message("coreapps.person.address") }</th>
            <th></th>
        </tr>
    </thead>
    <tbody>
        <% allPatients.sort { it.personName?.familyName.toLowerCase() }.each{  %>
        <tr>
            <td>
                <a href="${ ui.pageLink("coreapps", "clinicianfacing/patient", [
                        "patientId": it.uuid ])}">
                    ${ ui.format(it.personName) }
                </a>
            </td>
            <td>
                ${ ui.format(it.gender) }
            </td>
            <td>
                ${ ui.format(it.age) }
            </td>
            <td>
                <% addressHierarchyLevels.each { addressLevel -> %>
                    <% if(it.personAddress && it.personAddress[addressLevel]) { %>
                        ${it.personAddress[addressLevel]}<% if(addressLevel != addressHierarchyLevels.last()){%>,<%}%>
                    <% }%>
                <% } %>
            </td>
            <td>
                <button type="button" ng-click="exportPatient('${it.uuid}')">${ ui.message("pihcore.export") }</button>
            </td>
        </tr>
    <% } %>
	</tbody>
</table>

<% if ( (allPatients != null) && (allPatients.size() > 0) ) { %>
    ${ ui.includeFragment("uicommons", "widget/dataTable", [ object: "#list-patients",
         options: [
                 bFilter: true,
                 bJQueryUI: true,
                 bLengthChange: false,
                 iDisplayLength: 10,
                 sPaginationType: '\"full_numbers\"',
                 bSort: false,
                 sDom: '\'ft<\"fg-toolbar ui-toolbar ui-corner-bl ui-corner-br ui-helper-clearfix datatables-info-and-pg \"ip>\''
         ]
    ]) }
<% } %>

</div>

<script type="text/javascript">
    angular.module('exportPatientApp').value("allPatients", '${ allPatients }');
    angular.bootstrap("#exportPatient-app", [ "exportPatientApp" ]);
    jq(function () {
        jq(document).on('sessionLocationChanged', function () {
            window.location.reload();
        });
    });
</script>