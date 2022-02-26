<!-- This is the login page -->
<%
    ui.includeFragment("appui", "standardEmrIncludes")
    ui.includeCss("pihcore", "account.css")
    ui.includeJavascript("pihcore", "account/changePassword.js")
%>

<!DOCTYPE html>
<html>
<head>
    <title></title>
    <link rel="shortcut icon" type="image/ico" href="/${ ui.contextPath() }/images/openmrs-favicon.ico"/>
    <link rel="icon" type="image/png\" href="/${ ui.contextPath() }/images/openmrs-favicon.png"/>
    ${ ui.resourceLinks() }
</head>
<body>
<script type="text/javascript">
    var OPENMRS_CONTEXT_PATH = '${ ui.contextPath() }';
    var errorMessageOldPassword = "${ui.message("emr.account.changePassword.oldPassword.required")}";
    var errorMessageNewPassword = "${ui.message("emr.account.changePassword.newPassword.required")}";
    var errorMessageNewAndConfirmPassword = "${ui.message("emr.account.changePassword.newAndConfirmPassword.DoesNotMatch")}";
</script>

${ ui.includeFragment("appui", "header") }

<h3>${ui.message("mirebalais.login.resetPassword")}</h3>

<div id="body-wrapper" class="container">
    <div id="reset-password-page">
        <form method="post" id="reset-password-forms">
            <fieldset>

                <legend>${ ui.message("emr.account.newPassword") }</legend>

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
                <input type="button" class="cancel" value="${ ui.message("emr.cancel") }" onclick="javascript:window.location='/${ contextPath }/index.htm'" />
                <input type="submit" class="confirm" id="save-button" value="${ ui.message("emr.save") }"  />
            </div>

        </form>
    </div>
</div>
