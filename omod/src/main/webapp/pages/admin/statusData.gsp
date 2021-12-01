<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeJavascript("pihcore", "json-formatter.umd.js")
%>

<script type="text/javascript" xmlns="http://www.w3.org/1999/html" xmlns="http://www.w3.org/1999/html">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("coreapps.app.system.administration.label") }", link: "${ ui.pageLink("coreapps", "systemadministration/systemAdministration") }" },
        { label: "${ ui.message("pih.app.admin.statusData.view") }", link: "${ ui.pageLink("pihcore", "admin/statusData") }" }
    ];
</script>

<script type="text/javascript">
    jq(document).ready(function() {
        jq("#error-details").html("");
        let pId = jq("#patient-selector").val();
        let path = jq("#path-selector").val();
        if (pId && pId !== '' && path && path !== '') {
            let requestPath = openmrsContextPath + '/ws/rest/v1/pihcore/statusData?patientId=' + pId + '&path=' + path;
            let defId = jq("#id-selector").val();
            if (defId && defId !== '') {
                requestPath += '&definitionId=' + defId;
            }
            jq.get(requestPath, function (data) {
                var formatter = new JSONFormatter(data, 1, {});
                jq("#resultsJson").html(formatter.render());
                formatter.openAtDepth(10);
            }).fail(function (data) {
                jq("#error-details").html(data.responseJSON.errorMessages.join('<br/>'));
            });
        }
    });
</script>

<div>
    <form>
        <br/>
        <label for="patient-selector">Patient: </label>
        <input id="patient-selector" name="patientId" type="text" size="20" value="${patientId}" placeholder="Enter Patient ID or UUID" autocomplete="off"/>
        <br/>
        <div style="display: table-cell">
            <label for="path-selector">Config File: </label>
            <select id="path-selector" name="path">
                <% configPaths.each{pathOption -> %>
                    <option value="">Choose...</option>
                    <option value="${pathOption}"<%= pathOption.equals(path) ? "selected" : "" %>>${pathOption}</option>
                <% } %>
            </select>
        </div>
        <% if (path) { %>
            <div style="display: table-cell; padding-left: 5px;">
                <label for="id-selector">Status Definition: </label>
                <select id="id-selector" name="definitionId">
                    <% definitions.each{definitionOption -> %>
                    <option value="">All</option>
                    <option value="${definitionOption.id}"<%= definitionOption.id.equals(definitionId) ? "selected" : "" %>>${definitionOption.id}</option>
                    <% } %>
                </select>
            </div>
        <% } %>
        <div style="display: table-cell; padding-left: 5px;">
            <label for="submit-button">&nbsp;</label>
            <input id="submit-button" type="submit" value="Evaluate"/>
        </div>
        <br/>
    </form>
    <hr/>

    <div id="error-details" style="color:red;"></div>

    <div id="resultsJson"></div>
</div>