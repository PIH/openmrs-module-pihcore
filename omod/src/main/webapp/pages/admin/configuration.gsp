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

<script type="text/javascript">
    jq(function() {
        jq.get(openmrsContextPath + '/ws/rest/v1/pihcore/config', function(data) {
            var formatter = new JSONFormatter(data, 1, {});
            jq("#configJson").html(formatter.render());
        });
    });
</script>

<div id="configJson"></div>