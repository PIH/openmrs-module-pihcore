<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeJavascript("pihcore", "json-formatter.umd.js")
%>

<script type="text/javascript" xmlns="http://www.w3.org/1999/html" xmlns="http://www.w3.org/1999/html">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "Status Data Test", link: "${ ui.pageLink("pihcore", "admin/statusData.page") }" }
    ];
</script>

<script type="text/javascript">
    jq(document).ready(function() {
        jq("#evaluate-button").click(function(event) {
            jq("#errorDetails").html("");
            let pId = jq("#patient-selector").val();
            let path = jq("#path-selector").val();
            jq.get(openmrsContextPath + '/ws/rest/v1/pihcore/statusData?patientId=' + pId + "&path="+path, function(data) {
                var formatter = new JSONFormatter(data, 1, {});
                jq("#configJson").html(formatter.render());
                formatter.openAtDepth(3);
            }).fail(function(data) {
                jq("#errorDetails").html(data.responseText);
            });
        });
    });
</script>

<br/>
<input id="patient-selector" type="text" size="50" placeholder="Enter Patient ID or UUID"/>
<br/>
<input id="path-selector" type="text" size="50" placeholder="Enter Path to Definition Config"/>
<br/>
<input id="evaluate-button" type="button" value="Evaluate For Patient"/>
<br/>
<hr/>

<div id="errorDetails"></div>

<div id="configJson"></div>