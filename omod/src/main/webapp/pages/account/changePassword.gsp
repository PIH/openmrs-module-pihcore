<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("emr.task.myAccount.changePassword.label") ])
    ui.includeCss("pihcore", "account.css")
    ui.includeJavascript("pihcore", "account/changePassword.js")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("emr.app.system.administration.myAccount.label")}", link: '${ui.pageLink("pihcore", "account/myAccount")}' },
        { label: "${ ui.message("emr.task.myAccount.changePassword.label")}" }
    ];
    var errorMessageOldPassword = "${ui.message("emr.account.changePassword.oldPassword.required")}";
    var errorMessageNewPassword = "${ui.message("emr.account.changePassword.newPassword.required")}";
    var errorMessageNewAndConfirmPassword = "${ui.message("emr.account.changePassword.newAndConfirmPassword.DoesNotMatch")}";
</script>

<h3>${ui.message("emr.myAccount.changePassword")}</h3>

<form method="post" id="accountForm">
    <fieldset>
        <p id="oldPasswordSection" class="emr_passwordDetails">
            <label class="form-header" for="oldPassword">${ ui.message("emr.account.oldPassword") }</label>
            <input type="password" id="oldPassword" name="oldPassword"  autocomplete="off"/>
            ${ ui.includeFragment("uicommons", "fieldErrors", [ fieldName: "oldPassword" ])}
        </p>
        <p id="newPasswordSection" class="emr_passwordDetails">
            <label class="form-header" for="newPassword">${ ui.message("emr.account.newPassword") }</label>
            <input type="password" id="newPassword" name="newPassword"  autocomplete="off"/>
            <label id="format-password">${ ui.message("emr.account.passwordFormat") }</label>
            ${ ui.includeFragment("uicommons", "fieldErrors", [ fieldName: "newPassword" ])}
        </p>
        <p id="confirmPasswordSection" class="emr_passwordDetails">
            <label class="form-header" for="confirmPassword">${ ui.message("emr.user.confirmPassword") }</label>
            <input type="password" id="confirmPassword" name="confirmPassword"  autocomplete="off"/>
            ${ ui.includeFragment("uicommons", "fieldErrors", [ fieldName: "confirmPassword" ])}
        </p>
    </fieldset>

    <div>
        <input type="button" class="cancel" value="${ ui.message("emr.cancel") }" onclick="javascript:window.location='/${ contextPath }/pihcore/account/myAccount.page'" />
        <input type="submit" class="confirm" id="save-button" value="${ ui.message("emr.save") }"  />
    </div>

</form>

