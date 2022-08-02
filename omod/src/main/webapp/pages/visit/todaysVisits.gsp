<%
    ui.decorateWith("appui", "standardEmrPage")
%>


<h3>${ ui.message("pihcore.todaysVisits.title") }</h3>

<table id="todays-visits-list" width="100%" border="1" cellspacing="0" cellpadding="2">
    <thead>
        <tr>
            <th>${ ui.message("coreapps.patient.identifier") }</th>
            <th>${ ui.message("coreapps.person.name") }</th>
            <th>${ ui.message("emr.location") }</th>
            <th>&nbsp;</th>
        </tr>
    </thead>
    <tbody>
        <% if (visits != null && visits.size() == 0) { %>
            <tr>
                <td colspan="8">${ ui.message("coreapps.none") }</td>
            </tr>
        <% } %>

        <%  visits.each { visit -> %>
            <tr>
                <td>
                    <a href="${ ui.pageLink("pihcore", "visit/visit", [
                            "patient": visit.patient.uuid,
                            "visit": visit.uuid ])}">
                        ${ ui.format(visit.patient.patientIdentifier) }
                    </a>
                </td>
                <td>
                    <a href="${ ui.pageLink("pihcore", "visit/visit", [
                            "patient": visit.patient.uuid,
                            "visit": visit.uuid ])}">
                        ${ ui.format(visit.patient) }
                    </a>
                </td>
                 <td>
                    <a href="${ ui.pageLink("pihcore", "visit/visit", [
                            "patient": visit.patient.uuid,
                            "visit": visit.uuid ])}">
                        ${ visit.location }
                    </a>
                </td>
                <td>
                    <a href="${ ui.pageLink("pihcore", "visit/visit#/print", [
                            "patient": visit.patient.uuid,
                            "visit": visit.uuid ])}" target="_blank">
                        ${ ui.message("pihcore.todaysVisits.printSummary") }
                    </a>
                </td>
            </tr>
        <% } %>

    </tbody>
</table>
