<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeCss("pihcore", "account.css")
%>
<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("emr.app.systemAdministration.label")}", link: '${ui.pageLink("coreapps", "systemadministration/systemAdministration")}' },
        { label: "${ ui.message("pihcore.activeUsers.title")}" }
    ];
</script>

<h3>${  ui.message("pihcore.activeUsers.title") }</h3>
<hr>
<table id="active-users-table">
	<thead>
		<tr>
			<th>${ ui.message("mirebalais.login.username")}</th>
			<th>${ ui.message("pihcore.activeUsers.ipAddress") }</th>
			<th>${ ui.message("pihcore.activeUsers.loginDate") }</th>
			<th>${ ui.message("pihcore.activeUsers.lastActivityDate") }</th>
		</tr>
	</thead>
	<tbody>
		<% activeUsers.each {  %>
	 	<tr>
	 		<td>${ ui.format(it.username) }</td>
			<td>${ ui.format(it.ipAddress) }</td>
			<td>${ ui.format(it.loginDate) }</td>
			<td>${ ui.format(it.lastActivityDate) }</td>
		</tr>
		<% } %>
	</tbody>
</table>

<% if ( (activeUsers != null) && (activeUsers.size() > 0) ) { %>
	${ ui.includeFragment("uicommons", "widget/dataTable", [ object: "#active-users-table",
			options: [
					bFilter: true,
					bJQueryUI: true,
					bLengthChange: false,
					iDisplayLength: 10,
					sPaginationType: '\"full_numbers\"',
					bSort: false,
					sDom: '\'ft<\"fg-toolbar ui-toolbar ui-corner-bl ui-corner-br ui-helper-clearfix datatables-info-and-pg \"ip>\''
			]
	]) }
<% } %>
