<%
    ui.decorateWith("appui", "standardEmrPage")
%>

<script type="text/javascript" xmlns="http://www.w3.org/1999/html" xmlns="http://www.w3.org/1999/html">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("coreapps.app.system.administration.label") }", link: "${ ui.pageLink("coreapps", "systemadministration/systemAdministration") }" },
        { label: "${ ui.message('pihcore.admin.staleAdmissionRequests') }", link: "${ ui.pageLink("pihcore", "admin/staleAdmissionRequests") }" }
    ];
</script>

<h3>${ ui.message('pihcore.admin.staleAdmissionRequests') }</h3>

<form id="admission-requests-search" method="post">
    <fieldset>
        <p>
            ${ ui.message('pihcore.admin.admissionRequestsSearchText1') }  ${ ui.includeFragment("uicommons", "field/datetimepicker", [
                                                                               id: "admissionRequestOnOrBefore",
                                                                               formFieldName: "admissionRequestOnOrBefore",
                                                                               label:"",
                                                                               defaultDate: admissionRequestOnOrBefore,
                                                                               endDate: new Date(),
                                                                               useTime: false,
                                                                            ])}
         </p>
         <p>
            ${ ui.message('pihcore.admin.admissionRequestsSearchText2') } <input style="min-width:0%;display:inline" type="number" name="mostRecentEncounterThresholdInDays" value="${ mostRecentEncounterThresholdInDays }" required/> ${ ui.message('pihcore.admin.admissionRequestsSearchText3') }
        </p>
        <p>
            <input type="submit" name="action" value="${ ui.message('pihcore.admin.search') }" />
        </p>
    </fieldset>
</form>

<% if (staleAdmissionRequests != null) {
    if (staleAdmissionRequests.isEmpty()) { %>
        <p>${ ui.message('pihcore.admin.noStaleAdmissionRequestsFound') }</p>
    <% } else { %>
        <form id="admission-requests-close" method="post">
            <table id="stale-admission-requests">
                <thead>
                    <tr>
                        <th>&nbsp;</th>
                        <th>${ ui.message('general.patient') }</th>
                        <th>${ ui.message('pihcore.admin.visitStartDate') }</th>
                        <th>${ ui.message('pihcore.admin.admissionRequestDate') }</th>
                        <th>${ ui.message('pihcore.admin.latestEncounterDate') }</th>
                    </tr>
                </thead>
                <tbody>
                    <% staleAdmissionRequests.each { request -> %>
                        <tr>
                            <td><input type="checkbox" name="visitsToClose" value="${ request.visit.id }" checked/></td>
                            <td>${ request.patient.formattedName} - ${ request.patient?.primaryIdentifier } </td>
                            <td>${ ui.format(request.visit?.startDatetime) }</td>
                            <td>${ ui.format(request.dispositionEncounter?.encounterDatetime) }</td>
                            <td>${ ui.format(request.latestEncounter?.encounterDatetime) }</td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
            <p>
                <input type="hidden" name="admissionRequestOnOrBefore" value="${ ui.dateToISOString(admissionRequestOnOrBefore) }" />
                <input type="hidden" name="mostRecentEncounterThresholdInDays" value="${ mostRecentEncounterThresholdInDays }" />
                <input type="submit" name="action" value="${ ui.message('pihcore.admin.closeSelectedVisits') }" />
            </p>
        </form>
    <% } %>
<% } %>