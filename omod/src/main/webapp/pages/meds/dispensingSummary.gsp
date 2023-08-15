<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeCss("dispensing", "patient.css")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.escapeJs(ui.format(patient.patient)) }" , link: '${ui.pageLink("pihcore", "router/programDashboard", ["patientId": patient.id])}'},
        { label: "${ ui.message("pihcore.medicationDispensingHistory") }"}
    ];
</script>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient.patient ]) }

<div class="container">

    <div id="medication-list">
        <h3>${ ui.message("pihcore.medicationDispensingHistory") }</h3>
        <table id="medicationTable">
            <thead>
            <tr>
                <th>${ ui.message("coreapps.patientDashBoard.date") }</th>
                <th>${ ui.message("dispensing.medication.name") }</th>
                <th>${ ui.message("dispensing.medication.dose") }</th>
                <th>${ ui.message("dispensing.medication.frequency") }</th>
                <th>${ ui.message("dispensing.medication.duration") }</th>
                <th>${ ui.message("dispensing.medication.dispensed") }</th>
                <th>${ ui.message("dispensing.medication.origin") }</th>
                <th>${ ui.message("dispensing.medication.instructions") }</th>
            </tr>
            </thead>
            <tbody>
                <% if (dispenses.isEmpty()) { %>
                    <tr>
                        <td>${ ui.message("uicommons.dataTable.emptyTable") }</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                <% } else { %>
                    <% dispenses.each { d -> %>
                        <tr>
                            <td>${ ui.format(d.dispenseDate) }</td>
                            <td class="${ d.drug.retired ? 'retiredDrug' : ''}">${ d.drug.displayName }</td>
                            <td>${ d.dose }</td>
                            <td>${ d.frequency }</td>
                            <td>${ d.duration }</td>
                            <td>${ d.quantity }</td>
                            <td>${ d.origin }</td>
                            <td>${ ui.format(d.instructions) }</td>
                        </tr>
                    <% } %>
                <% } %>
            </tbody>
        </table>


        ${ ui.includeFragment("uicommons", "widget/dataTable", [ object: "#medicationTable",
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

    </div>

</div>