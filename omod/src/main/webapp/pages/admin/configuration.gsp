<%
    ui.decorateWith("appui", "standardEmrPage")
%>

<script type="text/javascript" xmlns="http://www.w3.org/1999/html" xmlns="http://www.w3.org/1999/html">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("coreapps.app.system.administration.label") }", link: "${ ui.pageLink("coreapps", "systemadministration/systemAdministration") }" },
        { label: "${ ui.message("pih.app.admin.configuration") }", link: "${ ui.pageLink("pihcore", "admin/configuration") }" }
    ];
</script>

<style>
    .action-button {
        width: 300px;
        margin: 5px;
    }
    #error-details {
        color: red;
    }
</style>

<script type="text/javascript">
    jq(document).ready(function() {

        const actionInitiated = function() {
            jq(".action-button").prop('disabled', 'true');
            jq("#error-details").html("");
        }

        jq("#refresh-messageproperties-action").click(function() {
            actionInitiated();
            jq.ajax({type: "PUT", url: openmrsContextPath + '/ws/rest/v1/pihcore/config/messageproperties'})
                .fail(function (data) {
                    jq("#error-details").html('An error occurred: ' + data.responseText);
                })
                .always(function() {
                    jq(".action-button").removeProp('disabled');
                });
        });

        jq("#refresh-reports-action").click(function() {
            actionInitiated();
            jq.ajax({type: "PUT", url: openmrsContextPath + '/ws/rest/v1/pihcore/config/reports'})
                .fail(function (data) {
                    jq("#error-details").html('An error occurred: ' + data.responseText);
                })
                .always(function() {
                    jq(".action-button").removeProp('disabled');
                });
        });

        jq("#refresh-apps-action").click(function() {
            actionInitiated();
            jq.ajax({type: "PUT", url: openmrsContextPath + '/ws/rest/v1/pihcore/config/appframework'})
                .fail(function (data) {
                    jq("#error-details").html('An error occurred: ' + data.responseText);
                })
                .always(function() {
                    jq(".action-button").removeProp('disabled');
                });
        });

        jq("#refresh-htmlforms-action").click(function() {
            actionInitiated();
            jq.ajax({type: "PUT", url: openmrsContextPath + '/ws/rest/v1/pihcore/config/htmlforms'})
                .fail(function (data) {
                    jq("#error-details").html('An error occurred: ' + data.responseText);
                })
                .always(function() {
                    jq(".action-button").removeProp('disabled');
                });
        });

        jq("#refresh-system-action").click(function() {
            actionInitiated();
            jq.ajax({type: "PUT", url: openmrsContextPath + '/ws/rest/v1/pihcore/config'})
                .fail(function (data) {
                    jq("#error-details").html('An error occurred: ' + data.responseText);
                })
                .always(function() {
                    jq(".action-button").removeProp('disabled');
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
<input id="refresh-htmlforms-action" type="button" class="action-button" value="Refresh HTML Forms" />
<br/>
<input id="refresh-system-action" type="button" class="action-button" value="Refresh Entire Configuration" />
<br/>
<br/>
<div id="error-details"></div>