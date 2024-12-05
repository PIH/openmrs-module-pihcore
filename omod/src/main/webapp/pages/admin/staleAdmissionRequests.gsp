<%
    ui.decorateWith("appui", "standardEmrPage")
%>

<script type="text/javascript" xmlns="http://www.w3.org/1999/html" xmlns="http://www.w3.org/1999/html">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("coreapps.app.system.administration.label") }", link: "${ ui.pageLink("coreapps", "systemadministration/systemAdministration") }" },
        { label: "Stale Admission Requests", link: "${ ui.pageLink("pihcore", "admin/staleAdmissionRequests") }" }
    ];
</script>

<h3>Stale Admission Requests</h3>

<form id="admission-requests-search" method="post">
    <fieldset>
        <p>
            Show visits with admission requests older than <input style="min-width:0%;display:inline" type="number" name="staleAdmissionRequestsThresholdInDays" value="${ staleAdmissionRequestsThresholdInDays }" /> days,
         </p>
         <p>
            where the last encounter is more than <input style="min-width:0%;display:inline" type="number" name="mostRecentEncounterThresholdInDays" value="${ mostRecentEncounterThresholdInDays }" /> days ago.
        </p>
        <p>
            <input type="submit" name="action" value="Search" />
        </p>
    </fieldset>
</form>

<% if (staleAdmissionRequests != null) {
    if (staleAdmissionRequests.isEmpty()) { %>
        <p>No stale admission requests found.</p>
    <% } else { %>
        <form id="admission-requests-close" method="post">
            <table id="stale-admission-requests">
                <thead>
                    <tr>
                        <th>&nbsp;</th>
                        <th>Patient</th>
                        <th>Visit Start Date</th>
                        <th>Request Date</th>
                        <th>Latest Encounter Date</th>
                    </tr>
                </thead>
                <tbody>
                    <% staleAdmissionRequests.each { request -> %>
                        <tr>
                            <td><input type="checkbox" name="visitsToClose" value="${ request.visit.id }" checked/></td>
                            <td>${ ui.format(request.patient) }</td>
                            <td>${ ui.format(request.visit?.startDatetime) }</td>
                            <td>${ ui.format(request.dispositionEncounter?.encounterDatetime) }</td>
                            <td>${ ui.format(request.latestEncounter?.encounterDatetime) }</td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
            <p>
                <input type="hidden" name="staleAdmissionRequestsThresholdInDays" value="${ staleAdmissionRequestsThresholdInDays }" />
                <input type="hidden" name="mostRecentEncounterThresholdInDays" value="${ mostRecentEncounterThresholdInDays }" />
                <input type="submit" name="action" value="Close Selected Visits" />
            </p>
        </form>
    <% } %>
<% } %>