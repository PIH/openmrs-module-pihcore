<%
    ui.decorateWith("appui", "standardEmrPage")

    ui.includeCss("uicommons", "ngDialog/ngDialog.min.css")
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
    ui.includeJavascript("uicommons", "services/visitService.js")
    ui.includeJavascript("uicommons", "services/encounterService.js")
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

    ui.includeJavascript("pihcore", "waitingforconsult/waitingForConsultApp.js")
    ui.includeJavascript("pihcore", "waitingforconsult/waitingForConsultService.js")
    ui.includeJavascript("pihcore", "waitingforconsult/primaryCareWaitingForConsultController.js")

    def formatDiagnoses = {
        it.collect{ ui.escapeHtml(it.diagnosis.formatWithoutSpecificAnswer(context.locale)) } .join(", ")
    }
%>
<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("pihcore.waitingForConsult.title")}"}
    ];
</script>

<div class="container" id="waitingForConsult-app" ng-controller="WaitingForConsultController">
    <div class="alert alert-{{message.type}} alert-dismissible fade in" role="alert" ng-show="message.text.length > 0">
        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
            <span aria-hidden="true">&times;</span>
        </button>
        {{message.text}}
    </div>

<h3>${ ui.message("pihcore.waitingForConsult.title") }</h3>

<table id="waiting-for-consult-list" width="100%" border="1" cellspacing="0" cellpadding="2">
    <thead>
    <tr>
        <th>${ ui.message("coreapps.patient.identifier") }</th>
        <th>${ ui.message("coreapps.person.name") }</th>
        <th>${ ui.message("pihcore.attributes.mothersFirstName") }</th>
        <th>${ ui.message("coreapps.age") }</th>
        <th>${ ui.message("coreapps.person.address") }</th>
        <th>${ ui.message("pihcore.waitingForConsult.lastVisitDate") }</th>
        <th>${ ui.message("pihcore.waitingForConsult.lastDiagnoses") }</th>
        <th></th>
    </tr>
    </thead>
    <tbody>
    <% if (patientsWhoNeedConsult == null || (patientsWhoNeedConsult != null && patientsWhoNeedConsult.size() == 0) ) { %>
    <tr>
        <td colspan="6">${ ui.message("coreapps.none") }</td>
    </tr>
    <% } %>
    <%  patientsWhoNeedConsult.each { p ->

            def allVisits = p.allVisitsUsingWrappers;
            def currentVisit = allVisits.size() > 0 ? allVisits[0]: null;
            def lastVisit = allVisits.size() > 1 ? allVisits[1]: null;
            def diagnosesLastVisit = lastVisit ? lastVisit.getUniqueDiagnoses(false, false) : null;

    %>
    <tr id="patient-${ p.patient.id }">
        <td>
            <a href="${ ui.pageLink("pihcore", "visit/visit", [
                    "patient": p.patient.uuid,
                    "visit": currentVisit?.visit.uuid ])}">
                ${ ui.format(p.primaryIdentifier?.identifier) }
            </a>
        </td>
        <td>
            <a href="${ ui.pageLink("pihcore", "visit/visit", [
                    "patient": p.patient.uuid,
                    "visit": currentVisit?.visit.uuid ])}">
                ${ ui.format(p.patient) }
            </a>
        </td>
        <td>
            ${ ui.format(p.patient.getAttribute(mothersFirstName)) }
        </td>
        <td>
            ${ ui.format(p.patient.age) }
        </td>
        <td>
            ${ ui.format(p.personAddress).replaceAll(/\n/,"<br/>") }
        </td>
        <td>
            ${ lastVisit ? ui.format(lastVisit.startDate) : ''}
        </td>
        <td>
            ${ diagnosesLastVisit ? formatDiagnoses(diagnosesLastVisit) : ''}
        </td>
        <td class="edtriage-queue-button-column">
            <button type="button" class="btn btn-xs btn-primary edtriage-queue-button" ng-disabled="isSaving" ng-click="beginConsult('${p.patient.uuid}', '${currentVisit?.visit.uuid}')">${ ui.message("edtriageapp.beginConsult") }</button>
            <button type="button" class="btn btn-xs btn-default" ng-click="removeFromQueue('${p.patient.uuid}', '${currentVisit?.visit.uuid}')">${ ui.message("edtriageapp.remove") }</button>
        </td>
    </tr>
    <% } %>
    </tbody>
</table>

<% if (patientsWhoNeedConsult !=null && patientsWhoNeedConsult.size() > 0) { %>
${ ui.includeFragment("uicommons", "widget/dataTable", [ object: "#waiting-for-consult-list",
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
    var app = angular.module('waitingForConsultApp');
    angular.bootstrap("#waitingForConsult-app", [ "waitingForConsultApp" ])
    jq(function () {
        // make sure we reload the page if the location is changes; this custom event is emitted by by the location selector in the header
        jq(document).on('sessionLocationChanged', function () {
            window.location.reload();
        });
    });
</script>

