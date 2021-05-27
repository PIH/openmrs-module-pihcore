package org.openmrs.module.pihcore.task;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.User;
import org.openmrs.api.ProviderService;
import org.openmrs.api.UserService;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UpdateProviderRetiredStatesBasedOnAssociatedUserAccountsTest extends PihCoreContextSensitiveTest {

    @Autowired
    private ProviderService providerService;

    @Autowired
    private UserService userService;

    @Before
    public void before() throws Exception {
        executeDataSet("updateProviderRetiredStatesDataset.xml");
    }

    @Test
    public void shouldNotRetireProviderAccountsIfUsersNotRetired() {
        // sanity check that if we haven't retired any of the users, the associated providers aren't retired
        new UpdateProviderRetiredStatesBasedOnAssociatedUserAccounts().execute();

        assertFalse(providerService.getProvider(1002).isRetired());
        assertFalse(providerService.getProvider(1003).isRetired());
        assertFalse(providerService.getProvider(1004).isRetired());
    }

    @Test
    public void shouldRetireAllProviderAccountsForUserRetiredMoreThanAMonthAgo() {

        User user = userService.getUser(1004);
        user.setRetired(true);
        user.setRetiredBy(userService.getUser(1));
        user.setRetireReason("test");
        user.setDateRetired(new DateTime(2015,1,1,0,0,0).toDate());

        new UpdateProviderRetiredStatesBasedOnAssociatedUserAccounts().execute();

        assertTrue(providerService.getProvider(1003).isRetired());
        assertTrue(providerService.getProvider(1004).isRetired());

        // sanity check other providers (should maintain whatever retired state was set in the test dataset)
        assertTrue(providerService.getProvider(1001).isRetired());
        assertTrue(providerService.getProvider(1005).isRetired());
        assertTrue(providerService.getProvider(1005).isRetired());
        assertFalse(providerService.getProvider(1002).isRetired());

    }

    @Test
    public void shouldNotRetireProviderAccountsForUserRetiredLessThanAMonthAgo() {

        User user = userService.getUser(1004);
        user.setRetired(true);
        user.setRetiredBy(userService.getUser(1));
        user.setRetireReason("test");
        user.setDateRetired(new Date());

        new UpdateProviderRetiredStatesBasedOnAssociatedUserAccounts().execute();

        assertFalse(providerService.getProvider(1003).isRetired());
        assertFalse(providerService.getProvider(1004).isRetired());

        // sanity check other providers (should maintain whatever retired state was set in the test dataset)
        assertTrue(providerService.getProvider(1001).isRetired());
        assertTrue(providerService.getProvider(1005).isRetired());
        assertTrue(providerService.getProvider(1006).isRetired());
        assertFalse(providerService.getProvider(1002).isRetired());

    }

    @Test
    public void shouldNotRetireProviderIfAnyUserAccountStillActive() {

        // only retire one of the user accounts for this user
        User user = userService.getUser(1002);
        user.setRetired(true);
        user.setRetiredBy(userService.getUser(1));
        user.setRetireReason("test");
        user.setDateRetired(new DateTime(2015,1,1,0,0,0).toDate());

        new UpdateProviderRetiredStatesBasedOnAssociatedUserAccounts().execute();
        assertFalse(providerService.getProvider(1002).isRetired());

        // sanity check other providers (should maintain whatever retired state was set in the test dataset)
        assertTrue(providerService.getProvider(1001).isRetired());
        assertTrue(providerService.getProvider(1005).isRetired());
        assertTrue(providerService.getProvider(1005).isRetired());
        assertFalse(providerService.getProvider(1003).isRetired());
        assertFalse(providerService.getProvider(1004).isRetired());

    }


    @Test
    public void shouldNotRetireProviderIfAnyUserAccountActiveLessThanAMonthAgo() {

        // retire first account more than a month ago
        User user = userService.getUser(1002);
        user.setRetired(true);
        user.setRetiredBy(userService.getUser(1));
        user.setRetireReason("test");
        user.setDateRetired(new DateTime(2015,1,1,0,0,0).toDate());

        // retire second account on current date
        user = userService.getUser(1003);
        user.setRetired(true);
        user.setRetiredBy(userService.getUser(1));
        user.setRetireReason("test");
        user.setDateRetired(new Date());


        new UpdateProviderRetiredStatesBasedOnAssociatedUserAccounts().execute();
        assertFalse(providerService.getProvider(1002).isRetired());

        // sanity check other providers (should maintain whatever retired state was set in the test dataset)
        assertTrue(providerService.getProvider(1001).isRetired());
        assertTrue(providerService.getProvider(1005).isRetired());
        assertTrue(providerService.getProvider(1005).isRetired());
        assertFalse(providerService.getProvider(1003).isRetired());
        assertFalse(providerService.getProvider(1004).isRetired());

    }

   /* @Test
    public void shouldUnretireProviderAccountWhenUserUnretired() {

        // sanity check
        assertTrue(providerService.getProvider(1006).isRetired());

        User user = userService.getUser(1005);
        user.setRetired(false);
        user.setRetiredBy(null);
        user.setRetireReason(null);
        user.setDateRetired(null);

        new UpdateProviderRetiredStatesBasedOnAssociatedUserAccounts().execute();

        assertFalse(providerService.getProvider(1006).isRetired());

    }


    @Test
    public void shouldUnretireMostRecentProviderAccountWhenUserUnretired() {

        User user = userService.getUser(1001);
        user.setRetired(false);
        user.setRetiredBy(null);
        user.setRetireReason(null);
        user.setDateRetired(null);

        new UpdateProviderRetiredStatesBasedOnAssociatedUserAccounts().execute();

        // if the provider has multiple retired accounts, only the most recent one should be unretired
        assertTrue(providerService.getProvider(1001).isRetired());
        assertFalse(providerService.getProvider(1005).isRetired());

    }

    @Test
    public void shouldNotUnretireMostRecentProviderAccountIfAnotherActiveProviderForTheSameUserExists() {

        User user = userService.getUser(1001);
        user.setRetired(false);
        user.setRetiredBy(null);
        user.setRetireReason(null);
        user.setDateRetired(null);

        Provider provider = providerService.getProvider(1001);
        provider.setRetired(false);

        new UpdateProviderRetiredStatesBasedOnAssociatedUserAccounts().execute();

        assertFalse(providerService.getProvider(1001).isRetired());
        assertTrue(providerService.getProvider(1005).isRetired());

    }
*/
}
