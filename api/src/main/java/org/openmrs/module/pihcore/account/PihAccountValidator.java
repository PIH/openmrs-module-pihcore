package org.openmrs.module.pihcore.account;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.openmrs.User;
import org.openmrs.annotation.Handler;
import org.openmrs.api.UserService;
import org.openmrs.module.emrapi.account.AccountValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Handler(supports = {PihAccountDomainWrapper.class}, order = 50)
public class PihAccountValidator extends AccountValidator {

    @Autowired
    @Qualifier("userService")
    private UserService userService;

    /**
     * @see Validator#supports(Class)
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return PihAccountDomainWrapper.class.isAssignableFrom(clazz);
    }

    /**
     * @see Validator#validate(Object, Errors)
     */
    @Override
    public void validate(Object obj, Errors errors) {
        super.validate(obj, errors);
        if (!(obj instanceof PihAccountDomainWrapper)) {
            throw new IllegalArgumentException("Must be of type" + PihAccountDomainWrapper.class);
        }
        PihAccountDomainWrapper account = (PihAccountDomainWrapper) obj;
        validateEmail(errors, account);
    }

    private void validateEmail(Errors errors, PihAccountDomainWrapper account) {
        String email = account.getEmail();
        if (StringUtils.isNotBlank(email)) {
            if (!EmailValidator.getInstance().isValid(email)) {
                errors.rejectValue("email", "error.email.invalid");
            }
            else {
                User existingUser = userService.getUserByUsernameOrEmail(email);
                if (existingUser != null && !existingUser.equals(account.getUser())) {
                    if (email.equalsIgnoreCase(existingUser.getEmail())) {
                        errors.rejectValue("email", "emr.account.error.emailAlreadyInUse");
                    }
                }
            }
        }
    }
}
