<%
    ui.decorateWith("appui", "standardEmrPage")
%>
<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("pihcore.vitalsList.title")}"}
    ];
</script>

<h3>${ ui.message("pihcore.vitalsList.title") }</h3>

<table id="vitals-list" width="100%" border="1" cellspacing="0" cellpadding="2">
    <thead>
    <tr>
        <th>${ ui.message("coreapps.patient.identifier") }</th>
        <th>${ ui.message("coreapps.person.name") }</th>
        <th>${ ui.message("pihcore.attributes.mothersFirstName") }</th>
        <th>${ ui.message("coreapps.age") }</th>
        <th>${ ui.message("pihcore.vitalsList.checkInTime") }</th>
    </tr>
    </thead>
    <tbody>
    <% if (patientWithCheckInEncounter == null || (patientWithCheckInEncounter != null && patientWithCheckInEncounter.size() == 0) ) { %>
    <tr>
        <td colspan="5">${ ui.message("coreapps.none") }</td>
    </tr>
    <% } %>
    <%  patientWithCheckInEncounter.keySet().each { p ->
    %>
    <tr id="patient-${ p.id }">
        <td>
            <a href="${ ui.pageLink("htmlformentryui", "htmlform/enterHtmlFormWithSimpleUi",
                    [   "definitionUiResource": "pihcore:htmlforms/vitals.xml",
                        "visitId": patientWithCheckInEncounter.get(p).visit.id,
                        "patientId": p.id,
                        "returnUrl": ui.escapeJs(ui.pageLink("pihcore", "vitals/vitalsList")),
                        "breadcrumbOverride": breadcrumbOverride ])}">
                ${ ui.format(p.patientIdentifier.identifier) }
            </a>
        </td>
        <td>
            <a href="${ ui.pageLink("htmlformentryui", "htmlform/enterHtmlFormWithSimpleUi",
                    [   "definitionUiResource": "pihcore:htmlforms/vitals.xml",
                        "visitId": patientWithCheckInEncounter.get(p).visit.id,
                        "patientId": p.id,
                        "returnUrl": ui.escapeJs(ui.pageLink("pihcore", "vitals/vitalsList")),
                        "breadcrumbOverride": breadcrumbOverride ])}">
                ${ ui.format(p) }
            </a>
        </td>
        <td>
            ${ ui.format(p.getAttribute(mothersFirstName)) }
        </td>
        <td>
            ${ ui.format(p.age) }
        </td>
        <td>
            ${ ui.format(patientWithCheckInEncounter.get(p).encounterDatetime) }
        </td>
    </tr>
    <% } %>
    </tbody>
</table>

<% if (patientWithCheckInEncounter !=null && patientWithCheckInEncounter.size() > 0) { %>
${ ui.includeFragment("uicommons", "widget/dataTable", [ object: "#vitals-list",
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