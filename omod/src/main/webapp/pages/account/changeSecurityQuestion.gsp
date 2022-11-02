<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("emr.myaccount") ])
    ui.includeCss("pihcore", "account.css")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("emr.app.system.administration.myAccount.label")}", link: '${ui.pageLink("pihcore", "account/myAccount")}' },
        { label: "${ ui.message("emr.user.changeSecretQuestion")}" }
    ];

    jQuery(function() {
        jQuery("#save-button").addClass("disabled").attr("disabled", "disabled");
        var timer;
        var confirmAnswerInput = jQuery("#confirmAnswer");

        confirmAnswerInput.keyup(function(){
            if (timer) {
                clearTimeout(timer);
            }
            timer = setTimeout(confirmAnswerAction, 500);
        });

        function confirmAnswerAction() {
            var answer = jQuery("#answer").val();
            var confirmAnswer = confirmAnswerInput.val();
            if (confirmAnswer.length >= 1 && (answer !== confirmAnswer)) {
                jQuery("#confirmAnswerSection .field-error").text("${ui.message("emr.account.changePassword.newAndConfirmPassword.DoesNotMatch")}").show();
                jQuery("#save-button").addClass("disabled").attr("disabled", "disabled");
            } else if (answer && answer === confirmAnswer) {
                jQuery("#confirmAnswerSection .field-error").text("").hide();
                jQuery("#save-button").removeClass("disabled").removeAttr("disabled");
            }
        }
    });

</script>

<h3>${ui.message("emr.user.changeSecretQuestion")}</h3>

<form method="post" id="changeSecurityQuestionForm">
    <fieldset>
        <p id="passwordSection" class="emr_passwordDetails">
            <label class="form-header" for="password">${ ui.message("emr.user.password") }</label>
            <input type="password" id="password" name="password" autocomplete="off"/>
            ${ ui.includeFragment("uicommons", "fieldErrors", [ fieldName: "password" ])}
        </p>
        <p id="questionSection" class="emr_passwordDetails">
            <label class="form-header" for="question">${ ui.message("emr.user.secretQuestion") }</label>
            <input type="text" id="question" name="question" autocomplete="off"/>
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
    </fieldset>

    <div>
        <input type="button" class="cancel" value="${ ui.message("emr.cancel") }" onclick="window.location='/${ contextPath }/pihcore/account/myAccount.page'" />
        <input type="submit" class="confirm" id="save-button" value="${ ui.message("emr.save") }"  />
    </div>

</form>

