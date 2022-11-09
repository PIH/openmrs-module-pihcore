<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("emr.user.changeSecretQuestion") ])
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
    breadcrumbs.push({ label: "${ ui.message("emr.user.changeSecretQuestion")}" });

    jQuery(function() {
        let saveButton = jQuery("#save-button");
        saveButton.addClass("disabled").attr("disabled", "disabled");
        jQuery('input').keyup(function() {
            var question = jQuery("#question").val();
            var password = jQuery("#password").val();
            var answer = jQuery("#answer").val();
            var confirmAnswer = jQuery("#confirmAnswer").val();
            if (confirmAnswer.length >= 1 && (answer !== confirmAnswer)) {
                jQuery("#confirmAnswerSection .field-error").text("${ui.message("emr.account.changePassword.newAndConfirmPassword.DoesNotMatch")}").show();
            }
            else {
                jQuery("#confirmAnswerSection .field-error").text("").hide();
            }
            if (question && (password || <%= !isOwnAccount %>) && answer && answer === confirmAnswer) {
                saveButton.removeClass("disabled").removeAttr("disabled");
            }
            else {
                saveButton.addClass("disabled").attr("disabled", "disabled");
            }
        });
    });

</script>

<h3>${ui.message("emr.user.changeSecretQuestion")}</h3>

<div class="section note-container">
    <% if (schemeId) { %>
        <div class="note warning">
            ${ui.message("authentication.secret.configurationMessage")}
        </div>
    <% } %>
</div>

<form method="post" id="changeSecurityQuestionForm">
    <fieldset>
        <p id="questionSection" class="emr_passwordDetails">
            <label class="form-header" for="question">${ ui.message("emr.user.secretQuestion") }</label>
            <input type="text" id="question" name="question" autocomplete="off" value="${currentQuestion ? currentQuestion : ""}"/>
            ${ ui.includeFragment("uicommons", "fieldErrors", [ fieldName: "question" ])}
        </p>
        <p id="answerSection" class="emr_passwordDetails">
            <label class="form-header" for="answer">${ ui.message("emr.user.secretAnswer") }</label>
            <input type="password" id="answer" name="answer" autocomplete="off"/>
            ${ ui.includeFragment("uicommons", "fieldErrors", [ fieldName: "answer" ])}
        </p>
        <p id="confirmAnswerSection" class="emr_passwordDetails">
            <label class="form-header" for="confirmAnswer">${ ui.message("emr.user.secretAnswerConfirmation") }</label>
            <input type="password" id="confirmAnswer" name="confirmAnswer" autocomplete="off"/>
            ${ ui.includeFragment("uicommons", "fieldErrors", [ fieldName: "confirmAnswer" ])}
        </p>
        <% if (isOwnAccount) { %>
            <p id="passwordSection" class="emr_passwordDetails">
                <label class="form-header" for="password">${ ui.message("emr.user.secretAnswerPassword") }</label>
                <input type="password" id="password" name="password" autocomplete="off"/>
                ${ ui.includeFragment("uicommons", "fieldErrors", [ fieldName: "password" ])}
            </p>
        <% } else { %>
            <input type="hidden" name="userId" value="${userToSetup.userId}"/>
        <% } %>
    </fieldset>

    <div>
        <input type="button" class="cancel" value="${ ui.message("emr.cancel") }" onclick="window.location='/${ contextPath }/pihcore/account/${returnUrl}'" />
        <input type="submit" class="confirm" id="save-button" value="${ ui.message("emr.save") }"  />
    </div>

</form>

