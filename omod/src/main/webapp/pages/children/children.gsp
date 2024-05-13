<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeJavascript("uicommons", "datatables/jquery.dataTables.min.js")

%>
<style>
    .date-column {
        width: 125px;
    }
    .name-link {
        cursor:pointer;
        color:blue;
        text-decoration:underline;
    }

</style>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient ]) }

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.escapeJs(ui.format(patient)) }" , link: '${ui.pageLink("pihcore", "router/programDashboard", ["patientId": patient.id])}'},
        { label: "${ ui.message("registration.patient.children.label") }" , link: '${ui.pageLink("pihcore", "children/children", ["patientId": patient.id])}'}
    ];

    jq(document).ready(function() {
        jq("#return-button").click(function(event) {
            document.location.href = '${ ui.escapeJs(returnUrl) }';
        });

    });
</script>

<h3>${ ui.message("registration.patient.children.label") }</h3>

<table id="children-list-table">
    <thead>
    <tr>
        <th>${ ui.message("pihcore.children.name") }</th>
        <th>${ ui.message("pihcore.birthdate") }</th>
        <th>${ ui.message("pihcore.age") }</th>
        <th>${ ui.message("pihcore.gender") }</th>
        <th>${ ui.message("mirebalais.deathCertificate.date_of_death") }</th>
        <th>${ ui.message("pihcore.children.removeRelationship") }</th>
    </tr>
    </thead>
    <tbody>
        <% if (children.size() == 0) {  %>
            <tr>
                <td colspan="6">${ ui.message("emr.none") }</td>
            </tr>
        <% } %>
    </tbody>
    <% children.each { child -> %>
            <tr>
                <td class="name-link"><a href="${ ui.urlBind("/" + contextPath + dashboardUrl, [ patientId: child.personId ]) }">${child.givenName}, ${child.familyName}</a></td>
                <td class="date-column">${ ui.format(child.birthdate) }</td>
                <td>${child.age}</td>
                <td>${child.gender}</td>
                <td class="date-column">${ ui.format(child.deathDate) }</td>
                <td><i class="icon-remove delete-action"></i></td>
            </tr>
    <% } %>
</table>

<div>
    <input id="return-button" type="button" class="cancel" value="${ ui.message("pihcore.encounterList.return") }"/>
</div>

