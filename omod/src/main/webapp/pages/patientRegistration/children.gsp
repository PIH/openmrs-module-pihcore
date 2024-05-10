<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeJavascript("uicommons", "datatables/jquery.dataTables.min.js")

%>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient ]) }

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.escapeJs(ui.format(patient.patient)) }" , link: '${ui.pageLink("pihcore", "router/programDashboard", ["patientId": patient.id])}'},
        { label: "${ ui.message("pihcore.encounterList") }" , link: '${ui.pageLink("pihcore", "patient/encounterList", ["patientId": patient.id])}'}
    ];

    jq(document).ready(function() {
        jq("#return-button").click(function(event) {
            document.location.href = '${ui.pageLink("pihcore", "router/programDashboard", ["patientId": patient.id])}';
        });

        let childrenTable = jq("#children-list-table").dataTable(
            {
                bFilter: true,
                bJQueryUI: true,
                bLengthChange: false,
                iDisplayLength: 5,
                sPaginationType: 'full_numbers',
                bSort: false,
                sDom: 'ft<\"fg-toolbar ui-toolbar ui-corner-bl ui-corner-br ui-helper-clearfix datatables-info-and-pg \"ip>',
                oLanguage:  {
                    oPaginate: {
                        sFirst: "${ ui.message("uicommons.dataTable.first") }",
                        sLast: "${ ui.message("uicommons.dataTable.last") }",
                        sNext:  "${ ui.message("uicommons.dataTable.next") }",
                        sPrevious:  "${ ui.message("uicommons.dataTable.previous") }"
                    },

                    sInfo:  "${ ui.message("uicommons.dataTable.info") }",
                    sSearch: "${ ui.message("uicommons.dataTable.search") }",
                    sZeroRecords: "${ ui.message("uicommons.dataTable.zeroRecords") }",
                    sEmptyTable: "${ ui.message("uicommons.dataTable.emptyTable") }",
                    sInfoFiltered:  "${ ui.message("uicommons.dataTable.infoFiltered") }",
                    sInfoEmpty:  "${ ui.message("uicommons.dataTable.infoEmpty") }",
                    sLengthMenu:  "${ ui.message("uicommons.dataTable.lengthMenu") }",
                    sLoadingRecords:  "${ ui.message("uicommons.dataTable.loadingRecords") }",
                    sProcessing:  "${ ui.message("uicommons.dataTable.processing") }",

                    oAria: {
                        sSortAscending:  "${ ui.message("uicommons.dataTable.sortAscending") }",
                        sSortDescending:  "${ ui.message("uicommons.dataTable.sortDescending") }"
                    }
                }
            }
        );
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
        <tr>
            <td colspan="6">${ ui.message("emr.none") }</td>
        </tr>
    </tbody>
</table>

<div>
    <input id="return-button" type="button" class="cancel" value="${ ui.message("pihcore.encounterList.return") }"/>
</div>

