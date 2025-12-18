<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeJavascript("uicommons", "datatables/jquery.dataTables.min.js")
    ui.includeJavascript("uicommons", "moment-with-locales.min.js")
    ui.includeJavascript("pihapps", "dateUtils.js")
    def now = new Date()
%>

<style>
    #report-name {
        font-weight: bold;
        font-size: 1.6em;
    }
    #report-description {
        font-size: 0.8em;
        padding: 5px;
    }
    #report-datasets {
        padding-top: 10px;
    }
    #report-dataset-content {
        font-size: 0.9em;
    }
    .date {
        white-space: nowrap;
    }
    .parameter-wrapper {
        white-space: nowrap;
    }
    .action-button {
        padding: unset;
        margin-left: 10px;
    }
</style>

<script type="text/javascript">
    const breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.escapeJs(ui.message("reportingui.reportsapp.home.title")) }", link: emr.pageLink("reportingui", "reportsapp/home") },
        { label: "${ ui.message(ui.format(reportDefinition)) }", link: "${ ui.escapeJs(ui.thisUrl()) }" }
    ];
    moment.locale(window.sessionContext?.locale ?? 'en');
    const dateUtils = new PihAppsDateUtils(moment);
    const dateFormat = "${dateFormat}";
    const dateTimeFormat = "${dateTimeFormat}";

    let currentDataSetKey = '';

    jq(document).ready(function() {
        const urlParams = new URLSearchParams(document.location.search);
        const reportDefinitionUuid = urlParams.get("reportDefinition");
        const reportDefinitionRep = "full";

        jq.get(openmrsContextPath + "/ws/rest/v1/reportingrest/reportDefinition/" + reportDefinitionUuid + "?v=" + reportDefinitionRep, function(reportDefinition) {
            console.debug(reportDefinition);

            jq("#report-name").html(reportDefinition.display);
            jq("#report-description").html(reportDefinition.descriptionDisplay);

            // TODO: Parameters currently handled in GSP.  Consider moving to JS at some point

            if (reportDefinition.dataSetDefinitions.length > 1) {
                const navElement = jq("<nav>").addClass("nav nav-pills");
                reportDefinition.dataSetDefinitions.forEach((dsd, index) => {
                    const navLink = jq("<button>")
                        .addClass("nav-link" + (index === 0 ? " active" : ""))
                        .attr("data-bs-toggle", "tab").attr("data-bs-target", "#report-dataset-content")
                        .attr("aria-current", "page")
                        .html(dsd.key);
                    navElement.append(navLink)
                });
                jq("#report-dataset-tabs").append(navElement);
            }
            currentDataSetKey = reportDefinition.dataSetDefinitions[0].key;

            const getParameterValues = function() {
                let ret = {}
                reportDefinition.parameters.forEach((parameter) => {
                    ret[parameter.name] = jq(".parameter-wrapper input[name=\"" + parameter.name + "\"]").val();
                });
                return ret;
            }

            // TODO:  Add formatting for particular values
            const formatColumnValue = function(dataSetColumn, columnValue) {
                if (columnValue) {
                    if (dataSetColumn.datatype === "java.util.Date") {
                        columnValue = dateUtils.formatDateWithTimeIfPresent(columnValue, dateFormat, dateTimeFormat);
                    }
                }
                return columnValue;
            }

            const loadDataSet = function(dataSetKey) {
                const dataSetRep = "custom:(metadata,rows)";
                const endpoint = openmrsContextPath + "/ws/rest/v1/reportingrest/reportDataSet/" + reportDefinitionUuid + "/" + dataSetKey + "?v=" + dataSetRep;
                const parameterValues = getParameterValues();
                console.debug("Loading Data Set", endpoint, parameterValues);

                const reportSpinnerDiv = jq("#report-spinner");
                const contentSectionDiv = jq("#report-dataset-content");
                contentSectionDiv.html("").hide();
                reportSpinnerDiv.show();
                jq.get(endpoint, parameterValues, function(dataSet) {
                    console.debug(dataSet);
                    const contentTable = jq("<table>").addClass("table");
                    contentSectionDiv.append(contentTable);
                    // Add columns
                    const tableHead = jq("<thead>").addClass("dataset-header");
                    contentTable.append(tableHead);
                    const tableHeadRow = jq("<tr>").addClass("dataset-header-row");
                    tableHead.append(tableHeadRow);
                    dataSet.metadata.columns.forEach((column) => {
                        const tableHeadCell = jq("<th>").prop("scope", "col").prop("id", "col-" + dataSetKey + "-" + column.name).html(column.display);
                        tableHeadRow.append(tableHeadCell);
                    });
                    // Add rows
                    const tableBody = jq("<tbody>").addClass("dataset-body");
                    contentTable.append(tableBody);
                    if (dataSet.rows.length === 0) {
                        const tableBodyRow = jq("<tr>").addClass("dataset-row");
                        tableBody.append(tableBodyRow);
                        tableBodyRow.append(jq("<td>").html("${ui.message("reportingui.adHocReport.noResults")}"));
                    }
                    else {
                        dataSet.rows.forEach((row) => {
                            const tableBodyRow = jq("<tr>").addClass("dataset-row");
                            tableBody.append(tableBodyRow);
                            dataSet.metadata.columns.forEach((column) => {
                                const columnValue = formatColumnValue(column, row[column.name]);
                                const tableBodyCell = jq("<td>").addClass("dataset-column-value").html(columnValue);
                                tableBodyRow.append(tableBodyCell);
                            });
                        });
                        contentTable.dataTable(
                            {
                                bFilter: false,
                                bJQueryUI: true,
                                bLengthChange: false,
                                iDisplayLength: 8,
                                sPaginationType: 'full_numbers',
                                bSort: false,
                                sDom: 'ft<\"fg-toolbar ui-toolbar ui-corner-bl ui-corner-br ui-helper-clearfix datatables-info-and-pg \"ip>',
                                oLanguage: {
                                    oPaginate: {
                                        sFirst: "${ ui.message("uicommons.dataTable.first") }",
                                        sLast: "${ ui.message("uicommons.dataTable.last") }",
                                        sNext: "${ ui.message("uicommons.dataTable.next") }",
                                        sPrevious: "${ ui.message("uicommons.dataTable.previous") }"
                                    },
                                    sInfo: "${ ui.message("uicommons.dataTable.info") }",
                                    sSearch: "${ ui.message("uicommons.dataTable.search") }",
                                    sZeroRecords: "${ ui.message("uicommons.dataTable.zeroRecords") }",
                                    sEmptyTable: "${ ui.message("uicommons.dataTable.emptyTable") }",
                                    sInfoFiltered: "${ ui.message("uicommons.dataTable.infoFiltered") }",
                                    sInfoEmpty: "${ ui.message("uicommons.dataTable.infoEmpty") }",
                                    sLengthMenu: "${ ui.message("uicommons.dataTable.lengthMenu") }",
                                    sLoadingRecords: "${ ui.message("uicommons.dataTable.loadingRecords") }",
                                    sProcessing: "${ ui.message("uicommons.dataTable.processing") }",
                                    oAria: {
                                        sSortAscending: "${ ui.message("uicommons.dataTable.sortAscending") }",
                                        sSortDescending: "${ ui.message("uicommons.dataTable.sortDescending") }"
                                    }
                                }
                            }
                        );
                    }
                    reportSpinnerDiv.hide();
                    contentSectionDiv.show();
                }).fail((response) => {
                    reportSpinnerDiv.hide();
                    const errorDiv = jq("<div>").addClass("error").html(response.responseJSON.error.message);
                    jq("#report-dataset-content").html(errorDiv).show();
                });
            };
            loadDataSet(currentDataSetKey);

            jq("#refresh-button").click(() => {
                loadDataSet(currentDataSetKey);
            });

            jq("#change-report-button").click(() => {
                jq("#report-name").hide();
                const reportSelector = jq("<select>").attr("name", "reportDefinition");
                jq.get(openmrsContextPath + "/ws/rest/v1/reportingrest/reportDefinition", function(results) {
                    const sortedReports = results.results.sort((a, b) => a.display.toLowerCase().localeCompare(b.display.toLowerCase()))
                    sortedReports.forEach((rd) => {
                        const option = jq("<option value=\"" + rd.uuid + "\">" + rd.display + "</option>");
                        if (rd.uuid === reportDefinitionUuid) {
                            option.attr("selected", "selected");
                        }
                        reportSelector.append(option);
                    });
                    reportSelector.change(function() {
                        const url = new URL(window.location);
                        url.searchParams.forEach((key) => {
                            url.searchParams.delete(key);
                        });
                        const selectedReport = jq(this).val();
                        url.searchParams.set("reportDefinition", selectedReport);
                        document.location.href = url.toString();
                    });
                    jq("#report-selector").html(reportSelector).show();
                });
            });
        });
    });
</script>

<div id="report-section">
    <div class="row">
        <div class="col">
            <div>
                <span id="report-name"></span>
                <span id="report-selector" style="display:none;"></span>
                <button id="change-report-button" class="action-button"><i class="small icon-pencil"></i></button>
            </div>
            <div id="report-description"></div>
        </div>
        <div class="col-auto">
            <div id="report-parameters" class="row">
                <% reportDefinition.parameters.each{ parameter ->
                    def initialValue = parameter.defaultValue ?: parameter.type.simpleName.toLowerCase() == 'date' ? now : "" %>
                    <div class="parameter-wrapper col" id="parameter-wrapper-${parameter.name}">
                        ${ ui.includeFragment("pihcore", "field/reportParameter", [ parameter: parameter, initialValue: initialValue ]) }
                    </div>
                <% } %>
                <span class="text-right" style="padding-right:10px;">
                    <button id="refresh-button" class="action-button"><i class="small icon-refresh"></i></button>
                </span>
            </div>
        </div>
    </div>
    <div id="report-datasets">
        <div id="report-dataset-tabs"></div>
        <div id="report-spinner"><img src="${ui.resourceLink("uicommons", "images/spinner.gif")}"></div>
        <div id="report-dataset-content" class="table-responsive"></div>
    </div>
</div>
