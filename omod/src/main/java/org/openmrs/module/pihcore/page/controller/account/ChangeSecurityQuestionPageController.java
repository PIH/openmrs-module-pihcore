package org.openmrs.module.pihcore.page.controller.account;


import org.apache.commons.lang.StringUtils;
import org.openmrs.User;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.MethodParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class ChangeSecurityQuestionPageController {

    public ChangeSecurityQuestion getChangeSecurityQuestion(
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "question", required = false) String question,
            @RequestParam(value = "answer", required = false) String answer,
            @RequestParam(value = "confirmAnswer", required = false) String confirmAnswer) {
        return new ChangeSecurityQuestion(password, question, answer, confirmAnswer);
    }

    public String get(PageModel model,
                      @SpringBean("userService") UserService userService) {
        User user = Context.getAuthenticatedUser();
        String existingQuestion = userService.getSecretQuestion(user);
        model.addAttribute("currentQuestion", existingQuestion);
        return "account/changeSecurityQuestion";
    }

    public String post(@MethodParam("getChangeSecurityQuestion") @BindParams ChangeSecurityQuestion securityQuestion,
                       BindingResult errors,
                       @SpringBean("userService") UserService userService,
                       @SpringBean("messageSourceService") MessageSourceService messageSourceService,
                       @SpringBean("messageSource") MessageSource messageSource,
                       HttpServletRequest request,
                       PageModel model) {

        String currentQuestion = securityQuestion.getQuestion();
        if (StringUtils.isBlank(currentQuestion)) {
            User user = Context.getAuthenticatedUser();
            currentQuestion = userService.getSecretQuestion(user);
        }
        model.addAttribute("currentQuestion", currentQuestion);

        // Validate submission
        if (StringUtils.isBlank(securityQuestion.getPassword())) {
            errors.rejectValue("password", "emr.user.password.required",
                    new Object[]{messageSourceService.getMessage("emr.user.password.required")}, null);
        }
        if (StringUtils.isBlank(securityQuestion.getQuestion())) {
            errors.rejectValue("question", "emr.user.secretQuestion.required",
                    new Object[]{messageSourceService.getMessage("emr.user.secretQuestion.required")}, null);
        }
        if (StringUtils.isBlank(securityQuestion.getAnswer())) {
            errors.rejectValue("answer", "emr.user.secretAnswer.required",
                    new Object[]{messageSourceService.getMessage("emr.user.secretAnswer.required")}, null);
        }
        if (StringUtils.isBlank(securityQuestion.getConfirmAnswer())) {
            errors.rejectValue("confirmAnswer", "emr.user.secretAnswerConfirmation.required",
                    new Object[]{messageSourceService.getMessage("emr.user.secretAnswerConfirmation.required")}, null);
        }
        if (!securityQuestion.getAnswer().equalsIgnoreCase(securityQuestion.getConfirmAnswer())) {
            errors.rejectValue("confirmAnswer", "emr.user.secretAnswerConfirmation.noMatch",
                    new Object[]{messageSourceService.getMessage("emr.user.secretAnswerConfirmation.noMatch")}, null);
        }

        if (errors.hasErrors()) {
            model.addAttribute("errors", errors);
            List<ObjectError> allErrors = errors.getAllErrors();
            String message = "";
            for (ObjectError error : allErrors) {
                Object[] arguments = error.getArguments();
                if (error.getCode() != null) {
                    String errorMessage = messageSource.getMessage(error.getCode(), arguments, Context.getLocale());
                    if (arguments != null) {
                        for (int i = 0; i < arguments.length; i++) {
                            String argument = (String) arguments[i];
                            errorMessage = errorMessage.replaceAll("\\{" + i + "}", argument);
                        }
                    }
                    message = message.concat(errorMessage.concat("<br>"));
                }
            }
            request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE, message);
            return "account/changeSecurityQuestion";
        }
        else {
            try {
                userService.changeQuestionAnswer(securityQuestion.getPassword(), securityQuestion.getQuestion(), securityQuestion.getAnswer());
                String msg = messageSourceService.getMessage("emr.user.changeSecretQuestion.success", null, Context.getLocale());
                request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_INFO_MESSAGE, msg);
                request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_TOAST_MESSAGE, "true");
            }
            catch (Exception e) {
                String msg = messageSourceService.getMessage("emr.user.changeSecretQuestion.fail", new Object[]{e.getMessage()}, Context.getLocale());
                request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE, msg);
                return "account/changeSecurityQuestion";
            }

            return "redirect:pihcore/account/myAccount.page";
        }

    }

    private static class ChangeSecurityQuestion {

        private final String password;
        private final String question;
        private final String answer;
        private final String confirmAnswer;

        public ChangeSecurityQuestion(String password, String question, String answer, String confirmAnswer) {
            this.password = password;
            this.question = question;
            this.answer = answer;
            this.confirmAnswer = confirmAnswer;
        }

        public String getPassword() {
            return password;
        }

        public String getQuestion() {
            return question;
        }

        public String getAnswer() {
            return answer;
        }

        public String getConfirmAnswer() {
            return confirmAnswer;
        }
    }
}
