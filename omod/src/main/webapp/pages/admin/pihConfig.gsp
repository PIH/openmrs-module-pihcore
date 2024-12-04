<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeJavascript("pihcore", "json-formatter.umd.js")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("coreapps.app.system.administration.label") }", link: "${ ui.pageLink("coreapps", "systemadministration/systemAdministration") }" },
        { label: "${ ui.message("pih.app.admin.pihConfig.view") }", link: "${ ui.pageLink("pihcore", "admin/pihConfig") }" }
    ];
</script>

<script type="text/javascript">
    let data = ${configJson};
    let formatter = new JSONFormatter(data, 1, {});
    jq(document).ready(function() {

        console.log(data);

        jq("#pihConfig").html(formatter.render());

        jq("#download-button").click(function() {
            jq("<a/>", {
                download: jq("#download-filename").val(),
                href: "data:application/json," + encodeURIComponent(JSON.stringify(data, null, 2))
            })
                .appendTo("body")
                .click(function() {
                    jq(this).remove()
                })[0].click();
        });
    });
</script>

<div id="pihConfig"></div>

<hr/>

<b>Download to file:</b> <input type="text" id="download-filename" value="pih-config.json" /> <input type="button" id="download-button" value="Download" />