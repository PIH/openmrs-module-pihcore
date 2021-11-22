<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeJavascript("pihcore", "json-formatter.umd.js")
%>

<script type="text/javascript" xmlns="http://www.w3.org/1999/html" xmlns="http://www.w3.org/1999/html">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("pihcore.configuration") }", link: "${ ui.pageLink("pihcore", "admin/configuration.page") }" }
    ];
</script>

<style>
    .action-button {
        width: 300px;
        margin: 5px;
    }
</style>

<script type="text/javascript">
    jq(document).ready(function() {
        const spinnerImage = '<span><img class="search-spinner" src="'+emr.resourceLink('uicommons', 'images/spinner.gif')+'" /></span>';
        const reloadConfigJson = function () {
            jq("#configJson").html(spinnerImage);
            jq.get(openmrsContextPath + '/ws/rest/v1/pihcore/config', function(data) {
                var formatter = new JSONFormatter(data, 1, {});
                setTimeout(() => {jq("#configJson").html(formatter.render());}, 100);
                jq(".action-button").removeProp('disabled');
            });
        }
        reloadConfigJson();

        const actionInitiated = function() {
            jq(".action-button").prop('disabled', 'true');
        }

        jq("#refresh-messageproperties-action").click(function() {
            actionInitiated();
            jq.ajax({type: "PUT", url: openmrsContextPath + '/ws/rest/v1/pihcore/config/messageproperties'}).done(function() {
                reloadConfigJson();
            });
        });

        jq("#refresh-reports-action").click(function() {
            actionInitiated();
            jq.ajax({type: "PUT", url: openmrsContextPath + '/ws/rest/v1/pihcore/config/reports'}).done(function() {
                reloadConfigJson();
            });
        });

        jq("#refresh-apps-action").click(function() {
            actionInitiated();
            jq.ajax({type: "PUT", url: openmrsContextPath + '/ws/rest/v1/pihcore/config/appframework'}).done(function() {
                reloadConfigJson();
            });
        });

        jq("#refresh-system-action").click(function() {
            actionInitiated();
            jq.ajax({type: "PUT", url: openmrsContextPath + '/ws/rest/v1/pihcore/config'}).done(function() {
                reloadConfigJson();
            });
        });
    });
</script>

<h3>Refresh Configuration Actions</h3>

<input id="refresh-messageproperties-action" type="button" class="action-button" value="Refresh Message Properties" />
<br/>
<input id="refresh-reports-action" type="button" class="action-button" value="Refresh Reports" />
<br/>
<input id="refresh-apps-action" type="button" class="action-button" value="Refresh Apps and Extensions" />
<br/>
<input id="refresh-system-action" type="button" class="action-button" value="Refresh All" />
<br/>
<br/>

<h3>Current System Configuration</h3>

<div id="configJson"></div>