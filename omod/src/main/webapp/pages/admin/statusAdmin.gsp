<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeCss("pihcore", "codemirror/lib/codemirror.css")
    ui.includeCss("pihcore", "codemirror/addon/hint/show-hint.css")
    ui.includeCss("pihcore", "codemirror/theme/monokai.css")
    ui.includeJavascript("pihcore", "json-formatter.umd.js")
    ui.includeJavascript("pihcore", "codemirror/lib/codemirror.js")
    ui.includeJavascript("pihcore", "codemirror/mode/sql/sql.js")
    ui.includeJavascript("pihcore", "codemirror/mode/velocity/velocity.js")
    ui.includeJavascript("pihcore", "codemirror/addon/edit/matchbrackets.js")
    ui.includeJavascript("pihcore", "codemirror/addon/hint/show-hint.js")
    ui.includeJavascript("pihcore", "codemirror/addon/hint/sql-hint.js")
%>

<style>
    .CodeMirror {
        height: auto;
    }
    #query-data-values {
        padding:10px;
        background-color: lightyellow;
        width: 100%;
    }
    #expression-value {
        padding:10px;
        background-color: lightyellow;
        width: 100%;
    }
    #error-details {
        padding: 10px;
        color: red;
    }
    .query-data-key {
        font-weight: bold;
        display: inline-block;
    }
    .query-data-value {
        display: inline-block;
        padding-left: 10px;
    }
</style>

<script type="text/javascript" xmlns="http://www.w3.org/1999/html" xmlns="http://www.w3.org/1999/html">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("coreapps.app.system.administration.label") }", link: "${ ui.pageLink("coreapps", "systemadministration/systemAdministration") }" },
        { label: "${ ui.message("pih.app.admin.statusData.admin") }", link: "${ ui.pageLink("pihcore", "admin/statusAdmin") }" }
    ];
</script>

<script type="text/javascript">
    jq(document).ready(function() {
        jq("#submit-button").click(function(event) {
            jq("#error-details").html("");
            jq("#query-data-values").html("");
            jq("#expression-value").html("");
            let pId = jq("#patient-selector").val();
            let sql = window.statusDataQueryEditor.getValue();
            let expr = window.statusExpressionEditor.getValue();
            if (!pId) {
                jq("#results").html("Please select a patient.");
            }
            jq.post(openmrsContextPath + '/ws/rest/v1/pihcore/statusData', {
                "patientId": pId,
                "statusDataQuery": sql,
                "expression": expr
            }, function(data) {
                let queryDataHtml = "";
                for (key in data.queryData) {
                    let val = data.queryData[key];
                    if (key !== 'data') {
                        queryDataHtml += "<div class='query-data-key'>" + key + ":</div>";
                        queryDataHtml += "<div class='query-data-value'>" + JSON.stringify(val) + "</div>";
                        queryDataHtml += "<br/>";
                    }
                }
                jq("#query-data-values").html(queryDataHtml);
                jq("#expression-value").html(data.displayValue);
            }).fail(function (data) {
                jq("#error-details").html(data.responseJSON.errorMessages.join('<br/>'));
            });
        });

        window.statusDataQueryEditor = CodeMirror.fromTextArea(document.getElementById('status-data-query-input'), {
            mode: 'text/x-mysql',
            indentWithTabs: true,
            smartIndent: true,
            lineNumbers: true,
            viewportMargin: Infinity,
            matchBrackets : true,
            autofocus: true,
            theme: "monokai",
            extraKeys: {"Ctrl-Space": "autocomplete"},
            hintOptions: {tables: {
                patient: ["patient_id", "birthdate", "birthdate_estimated", "gender", "voided"],
                encounter: ["patient_id", "encounter_datetime", "encounter_type", "voided"],
            }}
        });

        window.statusExpressionEditor = CodeMirror.fromTextArea(document.getElementById('status-expression-input'), {
            mode: 'text/velocity',
            indentWithTabs: true,
            smartIndent: true,
            lineNumbers: true,
            viewportMargin: Infinity,
            matchBrackets : true,
            autofocus: true,
            theme: "monokai",
            extraKeys: {"Ctrl-Space": "autocomplete"}
        });
    });
</script>

<div>
    <br/>
    <label for="patient-selector">Patient: </label>
    <input id="patient-selector" name="patientId" type="text" size="40" value="${patientId}" placeholder="Enter Patient ID or UUID" autocomplete="off"/>
    <div id="error-details"></div>
    <label for="status-data-query-input">Data Query:</label>
    <textarea id="status-data-query-input" name="statusDataQuery">select p.patient_id\nfrom   patient p\nwhere  p.patient_id = @patientId;</textarea>
    <br/>
    <div id="query-data-values"></div>
    <br/>
    <label for="status-expression-input">Expression:</label>
    <textarea id="status-expression-input" name="expression">\$patient_id</textarea>
    <br/>
    <div id="expression-value"></div>
    <br/>
    <label for="submit-button">&nbsp;</label>
    <input id="submit-button" type="submit" value="Evaluate"/>
    <hr/>
</div>