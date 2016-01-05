package org.openmrs.module.pihcore.task;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.User;
import org.openmrs.api.ProviderService;
import org.openmrs.api.UserService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RetireProvidersWithRetiredUserAccountsTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private ProviderService providerService;

    @Autowired
    private UserService userService;

    @Before
    public void before() throws Exception {
        executeDataSet("retireProvidersDataset.xml");
    }

    @Test
    public void shouldNotRetireProviderAccountsIfUsersNotRetired() {
        // sanity check that if we haven't retired any of the users, the associated providers aren't retired
        new RetireProvidersWithRetiredUserAccounts().execute();

        assertFalse(providerService.getProvider(1001).isRetired());
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

        new RetireProvidersWithRetiredUserAccounts().execute();

        assertTrue(providerService.getProvider(1003).isRetired());
        assertTrue(providerService.getProvider(1004).isRetired());

        // sanity check other users
        assertFalse(providerService.getProvider(1001).isRetired());
        assertFalse(providerService.getProvider(1002).isRetired());

    }

    @Test
    public void shouldNotRetireProviderAccountsForUserRetiredLessThanAMonthAgo() {

        User user = userService.getUser(1004);
        user.setRetired(true);
        user.setRetiredBy(userService.getUser(1));
        user.setRetireReason("test");
        user.setDateRetired(new Date());

        new RetireProvidersWithRetiredUserAccounts().execute();

        assertFalse(providerService.getProvider(1003).isRetired());
        assertFalse(providerService.getProvider(1004).isRetired());

        // sanity check other users
        assertFalse(providerService.getProvider(1001).isRetired());
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

        new RetireProvidersWithRetiredUserAccounts().execute();
        assertFalse(providerService.getProvider(1002).isRetired());

        // sanity check other users
        assertFalse(providerService.getProvider(1001).isRetired());
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


        new RetireProvidersWithRetiredUserAccounts().execute();
        assertFalse(providerService.getProvider(1002).isRetired());

        // sanity check other users
        assertFalse(providerService.getProvider(1001).isRetired());
        assertFalse(providerService.getProvider(1003).isRetired());
        assertFalse(providerService.getProvider(1004).isRetired());

    }

}
