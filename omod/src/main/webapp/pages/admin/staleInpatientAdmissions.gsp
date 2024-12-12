<%
    ui.decorateWith("appui", "standardEmrPage")
%>

<script type="text/javascript" xmlns="http://www.w3.org/1999/html" xmlns="http://www.w3.org/1999/html">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("coreapps.app.system.administration.label") }", link: "${ ui.pageLink("coreapps", "systemadministration/systemAdministration") }" },
        { label: "${ ui.message('pihcore.admin.staleInpatientAdmissions') }", link: "${ ui.pageLink("pihcore", "admin/staleInpatientAdmissions") }" }
    ];
</script>

<h3>${ ui.message('pihcore.admin.staleInpatientAdmissions') }</h3>

<form id="inpatient-admisssins-search" method="post">
    <fieldset>
        <p>
             ${ ui.message('pihcore.admin.inpatientAdmissionsSearchText1') } ${ ui.includeFragment("uicommons", "field/datetimepicker", [
                                                                                                 id: "admittedOnOrBefore",
                                                                                                 formFieldName: "admittedOnOrBefore",
                                                                                                 label:"",
                                                                                                 defaultDate: admittedOnOrBefore,
                                                                                                 endDate: new Date(),
                                                                                                 useTime: false,
                                                                                         ])}
         </p>
         <p>
              ${ ui.message('pihcore.admin.inpatientAdmissionsSearchText2') } <input style="min-width:0%;display:inline" type="number" name="mostRecentEncounterThresholdInDays" value="${ mostRecentEncounterThresholdInDays }" required/>  ${ ui.message('pihcore.admin.inpatientAdmissionsSearchText3') }
        </p>
        <p>
            <input type="submit" name="action" value="${ ui.message('pihcore.admin.search') }" />
        </p>
    </fieldset>
</form>

<% if (staleInpatientAdmissions != null) {
    if (staleInpatientAdmissions.isEmpty()) { %>
        <p>${ ui.message('pihcore.admin.noStaleInpatientAdmissionsFound') }</p>
    <% } else { %>
        <form id="inpatient-admissions-close" method="post">
            <p>
                <table id="stale-inpatient-admissions">
                    <thead>
                        <tr>
                            <th>&nbsp;</th>
                            <th>${ ui.message('general.patient') }</th>
                            <th>${ ui.message('pihcore.admin.visitStartDate') }</th>
                            <th>${ ui.message('pihcore.admin.admissionDate') }</th>
                            <th>${ ui.message('pihcore.admin.latestEncounterDate') }</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% staleInpatientAdmissions.each { admission -> %>
                            <tr>
                                <td><input type="checkbox" name="visitsToClose" value="${ admission.visit.id }" checked/></td>
                                <td>${ admission.patient.formattedName} - ${ admission.patient?.primaryIdentifier } </td>
                                <td>${ ui.format(admission.visit.startDatetime) }</td>
                                <td>${ ui.format(admission.firstAdmissionOrTransferEncounter?.encounterDatetime) }</td>
                                <td>${ ui.format(admission.latestEncounter?.encounterDatetime) }</td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </p>
            <p>
                <input type="hidden" name="admittedOnOrBefore" value="${ ui.dateToISOString(admittedOnOrBefore) }" />
                <input type="hidden" name="mostRecentEncounterThresholdInDays" value="${ mostRecentEncounterThresholdInDays }" />
                <input type="submit" name="action" value="${ ui.message('pihcore.admin.closeSelectedVisits') }" />
            </p>
        </form>
    <% } %>
<% } %>



