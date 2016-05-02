<%
    ui.decorateWith("appui", "standardEmrPage")

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