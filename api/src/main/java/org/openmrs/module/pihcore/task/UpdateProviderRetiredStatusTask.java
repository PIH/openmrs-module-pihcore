package org.openmrs.module.pihcore.task;

import org.joda.time.DateTime;
import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.ProviderService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;

import java.util.HashSet;
import java.util.Set;

/**
 * Runs through all users in the system and finds all persons for which all their associated
 * user accounts were retired a month ago or earlier; retires all providers associated with these users
 *
 * Also makes sure that all persons with at least one unretired user have at least one unretired provider
 * acct (assuming there are any provider accts for that user).  This is our best-guess "undo" for the retire functionality.
 *
 * Not currently used.  See UHM-2225
 * To enable, add to constructor of PihCoreScheduledTaskExecutor to run one per day (or other interval as desired)
 */
public class UpdateProviderRetiredStatusTask implements Runnable {

    @Override
    public void run() {

        UserService userService = Context.getUserService();
        ProviderService providerService = Context.getProviderService();

        Set<Person> personsToIncludeWhenRetiringProviderAccts = new HashSet<Person>();
        Set<Person> personsToExcludeWhenRetiringProviderAccts = new HashSet<Person>();
       // Set<Person> personsToMakeSureHaveActiveProviderAcct = new HashSet<Person>();

        for (User user : userService.getUsers(null, null, true)) {

            // the user account has been retired over a month ago
            if (user.isRetired() && user.getDateRetired() != null &&
                    user.getDateRetired().before(new DateTime().minusMonths(1).toDate())) {
                personsToIncludeWhenRetiringProviderAccts.add(user.getPerson());
            }
            else {
                personsToExcludeWhenRetiringProviderAccts.add(user.getPerson());
               /* if (!user.isRetired()) {
                    personsToMakeSureHaveActiveProviderAcct.add(user.getPerson());
                }*/
            }
        }

        personsToIncludeWhenRetiringProviderAccts.removeAll(personsToExcludeWhenRetiringProviderAccts);

        // retire all provider accts associated with persons with retired user accts
        for (Person person : personsToIncludeWhenRetiringProviderAccts) {
            for (Provider provider : providerService.getProvidersByPerson(person)) {
                if (!provider.isRetired()) {
                    providerService.retireProvider(provider, "user account retired over 1 month ago");
                }
            }
        }

     /*   // check all persons to include and make sure, if they have provider accts, that at least one is unretired
        for (Person person : personsToMakeSureHaveActiveProviderAcct) {

            Collection<Provider> providers = providerService.getProvidersByPerson(person);

            if (providers.size() == 1 && providers.iterator().next().isRetired()) {
                providerService.unretireProvider(providers.iterator().next());
            }
            else if (providers.size() > 1) {

                Provider mostRecentlyRetiredProvider = null;
                Boolean noUnretiredProviders = true;
                Iterator<Provider> i = providers.iterator();

                while (i.hasNext()) {
                    Provider provider = i.next();
                    if (!provider.isRetired()) {
                        noUnretiredProviders = false;
                        break;
                    }
                    else if (mostRecentlyRetiredProvider == null || mostRecentlyRetiredProvider.getDateRetired().before(provider.getDateRetired())) {
                        mostRecentlyRetiredProvider = provider;
                    }
                }

                if (noUnretiredProviders) {
                    providerService.unretireProvider(mostRecentlyRetiredProvider);
                }
            }
        }*/

    }
}
