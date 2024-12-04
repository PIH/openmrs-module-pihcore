<%
    ui.decorateWith("appui", "standardEmrPage")
%>

<script type="text/javascript" xmlns="http://www.w3.org/1999/html" xmlns="http://www.w3.org/1999/html">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("coreapps.app.system.administration.label") }", link: "${ ui.pageLink("coreapps", "systemadministration/systemAdministration") }" },
        { label: "Stale Inpatient Admissions", link: "${ ui.pageLink("pihcore", "admin/staleInpatientAdmissions") }" }
    ];
</script>

<h3>Stale Inpatient Admissions</h3>

<form id="inpatient-admisssins-search" method="post">
    <fieldset>
        <p>
            Show inpatients admitted more than <input style="min-width:0%;display:inline" type="number" name="staleInpatientAdmissionThresholdInDays" value="${ staleInpatientAdmissionThresholdInDays }" /> days ago,
         </p>
         <p>
            where the last encounter is more than <input style="min-width:0%;display:inline" type="number" name="mostRecentEncounterThresholdInDays" value="${ mostRecentEncounterThresholdInDays }" /> days ago.
        </p>
        <p>
            <input type="submit" name="action" value="Search" />
        </p>
    </fieldset>
</form>

<% if (staleInpatientAdmissions != null) { %>
    <form id="inpatient-admissions-close" method="post">
        <p>
            <table id="stale-inpatient-admissions">
                <thead>
                    <tr>
                        <th>&nbsp;</th>
                        <th>Patient</th>
                        <th>Visit Start Date</th>
                        <th>Admission Date</th>
                        <th>Latest Encounter Date</th>
                    </tr>
                </thead>
                <tbody>
                    <% staleInpatientAdmissions.each { admission -> %>
                        <tr>
                            <td><input type="checkbox" name="visitsToClose" value="${ admission.visit.id }" checked/></td>
                            <td>${ ui.format(admission.patient) } </td>
                            <td>${ ui.format(admission.visit?.startDatetime) }</td>
                            <td>${ ui.format(admission.firstAdmissionOrTransferEncounter?.encounterDatetime) }</td>
                            <td>${ ui.format(admission.latestEncounter?.encounterDatetime) }</td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        </p>
        <p>
            <input type="hidden" name="staleInpatientAdmissionThresholdInDays" value="${ staleInpatientAdmissionThresholdInDays }" />
            <input type="hidden" name="mostRecentEncounterThresholdInDays" value="${ mostRecentEncounterThresholdInDays }" />
            <input type="submit" name="action" value="Close Selected Visits" />
        </p>
    </form>
<% } %>



