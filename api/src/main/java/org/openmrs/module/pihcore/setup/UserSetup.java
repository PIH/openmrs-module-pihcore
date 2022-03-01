package org.openmrs.module.pihcore.setup;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.account.PihAccountDomainWrapper;
import org.openmrs.module.pihcore.service.PihCoreService;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.util.LocaleUtility;
import org.openmrs.util.OpenmrsUtil;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.UniqueHashCode;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapReader;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.UUID;

public class UserSetup {

    public static void registerUsers() {
        try {
            File userFile = new File(OpenmrsUtil.getApplicationDataDirectory(), "users.csv");
            if (userFile.exists()) {
                try (CsvMapReader reader = new CsvMapReader(new FileReader(userFile), CsvPreference.STANDARD_PREFERENCE)) {
                    final String[] header = reader.getHeader(true);
                    final CellProcessor[] processors = getColumnProcessors();
                    for (Map<String, Object> row = reader.read(header, processors); row != null; row = reader.read(header, processors)) {
                        registerUser(row);
                    }
                }
            }
        }
        catch (Exception e) {
            throw new IllegalStateException("Unable to load users from users.csv", e);
        }
    }

    public static void registerUser(Map<String, Object> row) {
        String username = (String)row.get("username");
        String firstName = (String)row.get("first_name");
        String lastName = (String)row.get("last_name");
        String gender = (String)row.get("gender");
        String locale = (String)row.get("locale");
        String passwordHash = (String)row.get("hashed_password");
        String salt = (String)row.get("salt");
        String email = (String)row.get("email");
        String roles = (String) row.get("roles");
        String providerRole = (String) row.get("provider_role");
        registerUser(username, firstName, lastName, gender, locale, passwordHash, salt, email, roles, providerRole);
    }

    public static User registerUser(String username, String firstname, String lastName, String gender, String locale,
                                    String passwordHash, String salt, String email, String roles, String providerRole) {

        User user = Context.getUserService().getUserByUsername(username);
        if (user == null) {
            user = new User();
            user.setUsername(username);
            user.setEmail(email);
            Person person = new Person();
            person.addName(new PersonName(firstname, null, lastName));
            person.setGender(gender);
            user.setPerson(person);
            for (String role : roles.split("\\|")) {
                Role r = Context.getUserService().getRole(role);
                if (r == null) {
                    throw new IllegalArgumentException("Unable to find role: " + role);
                }
                user.addRole(r);
            }
            Context.getUserService().createUser(user, UUID.randomUUID().toString());
            if (StringUtils.isNotBlank(passwordHash) && StringUtils.isNotBlank(salt)) {
                Context.getAdministrationService().executeSQL("update users set password = '" + passwordHash + "', salt='" + salt + "' where username = '" + username + "'", false);
            }

            PihAccountDomainWrapper account = Context.getService(PihCoreService.class).newPihAccountDomainWrapper(person);
            account.setDefaultLocale(LocaleUtility.fromSpecification(locale));
            for (ProviderRole pr : Context.getService(ProviderManagementService.class).getAllProviderRoles(false)) {
                if (pr.getName().equalsIgnoreCase(providerRole)) {
                    account.setProviderRole(pr);
                }
            }
            account.save();
        }
        return user;
    }

    private static CellProcessor[] getColumnProcessors() {
        final CellProcessor[] processors = new CellProcessor[] {
                new UniqueHashCode(), // username
                new NotNull(), // first name
                new NotNull(), // last Name
                new Optional(), // gender
                new Optional(), // preferred locale
                new Optional(), // hashed password
                new Optional(), // salt
                new Optional(), // email
                new NotNull(), // roles, pipe-separated
                new Optional() // provider role
        };
        return processors;
    }
}

