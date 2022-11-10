<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("authentication.totp.configureTotp") ])
    ui.includeCss("pihcore", "account.css")
    def returnUrl = isOwnAccount ? "myAccount.page" : "account.page?personId=" + userToSetup.person.personId;
%>

<script type="text/javascript">
    var breadcrumbs = [];
    breadcrumbs.push({ icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' });
    <% if (isOwnAccount) { %>
        breadcrumbs.push({ label: "${ ui.message("emr.app.system.administration.myAccount.label")}", link: '${ui.pageLink("pihcore", "account/myAccount")}' });
    <% } else { %>
        breadcrumbs.push({ label: "${ ui.message("emr.app.systemAdministration.label")}", link: '${ui.pageLink("coreapps", "systemadministration/systemAdministration")}' });
        breadcrumbs.push({ label: "${ ui.message("emr.task.accountManagement.label")}" , link: '${ui.pageLink("pihcore", "account/manageAccounts")}'});
        breadcrumbs.push({ label: "${ ui.format(userToSetup.person) }", link: '${ui.pageLink("pihcore", "account/account", [personId: userToSetup.person.personId])}' });
    <% } %>
    breadcrumbs.push({ label: "${ ui.message("authentication.totp.configureTotp")}" });
</script>

<h3>${ui.message("authentication.totp.configureTotp")}</h3>

<div class="section note-container">
    <% if (schemeId) { %>
        <div class="note warning">
            ${ui.message("authentication.totp.configurationMessage")}
        </div>
    <% } %>
</div>

<form method="post" id="totpVerificationForm">
    <fieldset>
        <div>
            <img src="${qrCodeUri}" />
        </div>
        <div>
            <label for="code-input">${ ui.message("authentication.totp.code") }</label>
            <input type="hidden" name="secret" value="${secret}"/>
            <input id="code-input" type="text" name="code" value="" placeholder="${ ui.message("authentication.totp.code.placeholder") }"/>
        </div>
    </fieldset>
    <div>
        <input type="button" class="cancel" value="${ ui.message("emr.cancel") }" onclick="window.location='/${ contextPath }/pihcore/account/${returnUrl}'" />
        <input type="submit" class="confirm" id="save-button" value="${ ui.message("emr.save") }"  />
    </div>
</form>

