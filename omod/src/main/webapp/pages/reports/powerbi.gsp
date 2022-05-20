<%
    ui.decorateWith("appui", "standardEmrPage")
%>
<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.escapeJs(ui.message("reportingui.reportsapp.home.title")) }", link: emr.pageLink("reportingui", "reportsapp/home") },
        { label: "${ ui.message(ui.format(reportDefinition)) }" }
    ];
</script>

<style>
    #body-wrapper { height: 85vh; min-height: 85vh; }
    #content { height: 100%; width: 100%; }
</style>

<div style="height: 100%; width: 100%;">
    <iframe style="height: 100%; width: 100%;" src="https://app.powerbi.com/reportEmbed?reportId=${ pbiReportId }&autoAuth=true"></iframe>
</div>

