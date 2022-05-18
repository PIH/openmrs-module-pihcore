package org.openmrs.module.pihcore.account;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.account.AccountDomainWrapper;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Extends the AccountDomainWrapper class from emrapi to provide direct support for email address and phone number
 */
public class PihAccountDomainWrapper extends AccountDomainWrapper {

    @Qualifier("personService")
    @Autowired
    protected PersonService personService;

    public PihAccountDomainWrapper() { }

    @Override
    public void initializeWithPerson(Person person) {
        super.initializeWithPerson(person);
    }

    public String getEmail() {
        return (getUser() == null ? null : getUser().getEmail());
    }

    public void setEmail(String email) {
        if (getUser() == null && StringUtils.isNotBlank(email)) {
            initializeUser();
        }
        if (getUser() != null) {
            getUser().setEmail(email);
        }
    }

    public String getPhoneNumber() {
        PersonAttribute att = getPerson().getAttribute(getPhoneNumberAttributeType());
        return (att == null ? null : att.getValue());
    }

    public void setPhoneNumber(String value) {
        getPerson().addAttribute(new PersonAttribute(getPhoneNumberAttributeType(), value));
    }

    public PersonAttributeType getPhoneNumberAttributeType() {
        return personService.getPersonAttributeTypeByUuid(PihEmrConfigConstants.PERSONATTRIBUTETYPE_TELEPHONE_NUMBER_UUID);
    }

    private void initializeUser() {
        super.setDefaultLocale(Context.getLocale()); // This has the effect of initializing a new user, little hack
    }
}
