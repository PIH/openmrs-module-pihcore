<%
    ui.decorateWith("appui", "standardEmrPage")
%>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient.patient ]) }

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.escapeJs(ui.format(patient.patient)) }" , link: '${ui.pageLink("pihcore", "router/programDashboard", ["patientId": patient.id])}'},
        { label: "${ ui.message("pihcore.encounterList") }" , link: '${ui.pageLink("pihcore", "patient/encounterList", ["patient": patient.id])}'}
    ];

    jq(document).ready(function() {
       jq("#return-button").click(function(event) {
           document.location.href = '${ui.pageLink("pihcore", "router/programDashboard", ["patientId": patient.id])}';
       })
    });
</script>

<style>
    .date-column {
        width: 125px;
    }
</style>
<h3>${ ui.message("pihcore.encounterList") }</h3>

<table id="encounter-list-table">
    <thead>
        <tr>
            <th>${ ui.message("pihcore.encounterList.encounterDatetime") }</th>
            <th>${ ui.message("pihcore.encounterList.encounterType") }</th>
            <th>${ ui.message("pihcore.encounterList.provider") }</th>
            <th>${ ui.message("pihcore.encounterList.location") }</th>
            <th>${ ui.message("pihcore.encounterList.enteredDatetime") }</th>
        </tr>
    </thead>
    <tbody>
    <% if (encounters.size() == 0) { %>
        <tr>
            <td colspan="5">${ ui.message("emr.none") }</td>
        </tr>
    <% } %>
    <% encounters.each { e -> %>
        <tr id="encounter-${ e.encounterId }">
            <td class="date-column">${ ui.format(e.encounterDatetime) }</td>
            <td>${ ui.format(e.encounterType) }</td>
            <td>
                <% e.encounterProviders.eachWithIndex { ep, index -> %>
                    ${ ui.format(ep.provider) }${ e.encounterProviders.size() - index > 1 ? "<br/>" : ""}
                <% } %>
            </td>
            <td>${ ui.format(e.location) }</td>
            <td class="date-column">${ ui.format(e.dateCreated) }</td>
        </tr>
    <% } %>
    </tbody>
</table>

<br/>
<input id="return-button" type="button" value="${ ui.message("pihcore.encounterList.return") }"/>

<% if (encounters.size() > 0) { %>
    ${ ui.includeFragment("uicommons", "widget/dataTable", [ object: "#encounter-list-table", options: [
         bFilter: true,
         bJQueryUI: true,
         bLengthChange: false,
         iDisplayLength: 5,
         sPaginationType: '\"full_numbers\"',
         bSort: false,
         sDom: '\'ft<\"fg-toolbar ui-toolbar ui-corner-bl ui-corner-br ui-helper-clearfix datatables-info-and-pg \"ip>\''
    ]]) }
<% } %>