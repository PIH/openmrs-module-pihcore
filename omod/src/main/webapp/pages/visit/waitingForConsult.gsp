<%
    ui.decorateWith("appui", "standardEmrPage")

    ui.includeCss("uicommons", "ngDialog/ngDialog.min.css")
    ui.includeCss("pihcore", "bootstrap.css")
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

    def formatDiagnoses = {
        it.collect{ ui.escapeHtml(it.diagnosis.formatWithoutSpecificAnswer(context.locale)) } .join(", ")
    }

    def statusOptions = [ [label: ui.message("pihcore.waitingForConsult.filter.waitingForConsult"), value: 'waiting_for_consult'],
                          [label: ui.message("pihcore.waitingForConsult.filter.inConsultation"), value: 'in_consultation']]
%>
<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("pihcore.waitingForConsult.title")}"}
    ];

    jq(function() {
        // make sure we reload the page if the location is changes; this custom event is emitted by by the location selector in the header
        jq('#waitingFilterByStatus').change(function() {
            window.location.href = '?filter=' + jq('#waitingFilterByStatus select').val();
        });
    });

    // set up auto-refresh of tables every 5 minutes
    setTimeout(function() {
        window.location = window.location;
    }, 300000)

</script>

<h3>${ ui.message("pihcore.waitingForConsult.title") }</h3>
    <div class="waitingForConsult-filter">
        ${ ui.includeFragment("uicommons", "field/dropDown", [
                id: "waitingFilterByStatus",
                formFieldName: "waitingFilterByStatus",
                label: ui.message("pihcore.waitingForConsult.filter.label"),
                options: statusOptions,
                initialValue: filter,
                hideEmptyLabel: true
        ] ) }
    </div>

<table id="waiting-for-consult-list" width="100%" border="1" cellspacing="0" cellpadding="2">
    <thead>
    <tr>
        <th>${ ui.message("coreapps.patient.identifier") }</th>
        <th>${ ui.message("ui.i18n.PatientIdentifierType.name.e66645eb-03a8-4991-b4ce-e87318e37566") }</th>
        <th>${ ui.message("coreapps.person.name") }</th>
        <th>${ ui.message("pihcore.attributes.mothersFirstName") }</th>
        <th>${ ui.message("coreapps.age") }</th>
        <th>${ ui.message("coreapps.person.address") }</th>
        <th>${ ui.message("pihcore.waitingForConsult.lastVisitDate") }</th>
        <th>${ ui.message("pihcore.waitingForConsult.lastDiagnoses") }</th>
        <% if (filter == 'waiting_for_consult') { %>
            <th></th>
        <% } %>
    </tr>
    </thead>
    <tbody>
    <% if (patientList == null || (patientList != null && patientList.size() == 0) ) { %>
    <tr>
        <td colspan="${ filter == 'waiting_for_consult' ? '9' : '8' }">${ ui.message("coreapps.none") }</td>
    </tr>
    <% } %>
    <%  patientList.each { p ->

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
        <td><% if(dossierIdentifierName != null ) { %>
            ${ p.patient.getPatientIdentifier(dossierIdentifierName) }
        <% } %>
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
        <% if (filter == 'waiting_for_consult') { %>
        <td class="consult-queue-button-column">
            <a href="${ ui.pageLink("pihcore", "visit/visit", [
                    "patient": p.patient.uuid,
                    "visit": currentVisit?.visit.uuid ])}">
                <button type="button" class="btn btn-xs btn-primary">${ ui.message("pihcore.beginConsult") }</button>
            </a>
        </td>
        <% } %>
    </tr>
    <% } %>
    </tbody>
</table>

<% if (patientList !=null && patientList.size() > 0) { %>
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
    jq(function () {
        // make sure we reload the page if the location is changes; this custom event is emitted by by the location selector in the header
        jq(document).on('sessionLocationChanged', function () {
            window.location.reload();
        });
    });
</script>

