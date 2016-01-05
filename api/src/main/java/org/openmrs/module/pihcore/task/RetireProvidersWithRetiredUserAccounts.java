package org.openmrs.module.pihcore.task;

import org.joda.time.DateTime;
import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.ProviderService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.scheduler.tasks.AbstractTask;

import java.util.HashSet;
import java.util.Set;

/**
 * Runs through all users in the system and finds all persons for which all their associated
 * user accounts were retired a month ago or earlier; retires all providers associated with these users
 */
public class RetireProvidersWithRetiredUserAccounts extends AbstractTask {


    @Override
    public void execute() {

        UserService userService = Context.getUserService();
        ProviderService providerService = Context.getProviderService();

        Set<Person> personsToIncludeWhenRetiringProviderAccts = new HashSet<Person>();
        Set<Person> personsToExcludeWhenRetiringProviderAccts = new HashSet<Person>();

        for (User user : userService.getUsers(null, null, true)) {

            // the user account has been retired over a month ago
            if (user.isRetired() && user.getDateRetired() != null &&
                    user.getDateRetired().before(new DateTime().minusMonths(1).toDate())) {
                personsToIncludeWhenRetiringProviderAccts.add(user.getPerson());
            }
            else {
                personsToExcludeWhenRetiringProviderAccts.add(user.getPerson());
            }
        }

        personsToIncludeWhenRetiringProviderAccts.removeAll(personsToExcludeWhenRetiringProviderAccts);

        for (Person person : personsToIncludeWhenRetiringProviderAccts) {
            for (Provider provider : providerService.getProvidersByPerson(person)) {
                if (!provider.isRetired()) {
                    providerService.retireProvider(provider, "user account retired over 1 month ago");
                }
            }
        }

    }
}
