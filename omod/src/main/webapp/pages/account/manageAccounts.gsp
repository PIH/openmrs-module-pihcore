<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeCss("pihcore", "account.css")
%>
<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("emr.app.systemAdministration.label")}", link: '${ui.pageLink("coreapps", "systemadministration/systemAdministration")}' },
        { label: "${ ui.message("emr.task.accountManagement.label")}" }
    ];
</script>

<h3>${  ui.message("emr.task.accountManagement.label") }</h3>

<div style="display:flex; justify-content: space-between;">
	<div>
		<a href="${ ui.pageLink("pihcore", "account/account") }">
			<button id="create-account-button">${ ui.message("emr.createAccount") }</button>
		</a>
	</div>
</div>

<form>
	<div style="display:flex; padding: 20px;">
		<div style="width:auto;">
			<input type="text" size="50" name="nameOrIdentifier" placeholder="${ ui.message("Provider.search") }" value="${nameOrIdentifier}" />
		</div>
		<div style="padding: 10px;">
			<input type="checkbox" name="showOnlyEnabledUsers" value="true" <%= showOnlyEnabledUsers ? "checked" : "" %> />
			${ ui.message("emr.account.showOnlyEnabled.label") }
		</div>
		<input type="submit" value="${ ui.message("general.search") }"/>
	</div>
</form>
<hr>
<% if (accounts && !accounts.isEmpty()) { %>
	<table id="list-accounts" cellspacing="0" cellpadding="2">
		<thead>
			<tr>
				<th>${ ui.message("emr.person.name")}</th>
				<th>${ ui.message("emr.user.username") }</th>
				<th>${ ui.message("emr.gender") }</th>
				<th>${ ui.message("emr.account.providerRole.label") }</th>
				<th>${ ui.message("emr.account.providerIdentifier.label") }</th>
				<th>${ ui.message("emr.account.enabled.label") }</th>
				<th></th>
			</tr>
		</thead>
		<tbody>
			<% accounts.sort { (it.person.personName !=null && it.person.personName.familyName != null) ? it.person.personName.familyName.toLowerCase() : false }.each{  %>
			<tr>
				<td>
					${ ui.format(it.person.personName)}
				</td>
				<td>
					<% if(it.username && it.username != '') {%>
						<a href="/${ contextPath }/authenticationui/account/userAccount.page?userId=${ it.user.id }">
							${ ui.format(it.username) }
						</a>
					<% } %>
				</td>
				<td>
					${ ui.format(it.person.gender) }
				</td>
				<td>
					${ ui.format(it.providerRole) }
				</td>
				<td>
					${ ui.format(it.provider?.identifier) }
				</td>
				<td>
					${ it.userEnabled ? ui.message("emr.yes") : ui.message("emr.no") }
				</td>
				<td>
					<a href="/${ contextPath }/pihcore/account/account.page?personId=${ it.person.personId }">
						<button>${ ui.message("emr.edit") }</button>
					</a>
				</td>
			</tr>
			<% } %>
		</tbody>
	</table>

	<% if ( (accounts != null) && (accounts.size() > 0) ) { %>
	${ ui.includeFragment("uicommons", "widget/dataTable", [ object: "#list-accounts",
			options: [
					bFilter: false,
					bJQueryUI: true,
					bLengthChange: false,
					iDisplayLength: 10,
					sPaginationType: '\"full_numbers\"',
					bSort: false,
					sDom: '\'ft<\"fg-toolbar ui-toolbar ui-corner-bl ui-corner-br ui-helper-clearfix datatables-info-and-pg \"ip>\''
			]
	]) }
	<% } %>
<% } %>