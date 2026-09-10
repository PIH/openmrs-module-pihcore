package org.openmrs.module.pihcore.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Obs;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Covers searching observations by the user who created or voided them. The fixture's creation and
 * voiding dates run in a different order from the observation datetimes, so an implementation that
 * ordered by the wrong column would fail here rather than look plausible.
 */
public class ObsAuditSearchTest extends PihCoreContextSensitiveTest {

    private PihCoreService service;

    private User bruno;

    private User butch;

    @BeforeEach
    public void setup() {
        executeDataSet("obsAuditTestDataset.xml");
        service = Context.getService(PihCoreService.class);
        bruno = Context.getUserService().getUser(501);
        butch = Context.getUserService().getUser(502);
    }

    private List<Integer> obsIds(List<Obs> obs) {
        return obs.stream().map(Obs::getObsId).collect(Collectors.toList());
    }

    /** A moment on a September 2026 day, matching the fixture's audit dates. */
    private Date september(int dayOfMonth, int hourOfDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(2026, Calendar.SEPTEMBER, dayOfMonth, hourOfDay, 0, 0);
        return calendar.getTime();
    }

    @Test
    public void shouldFindObsCreatedByAUserMostRecentlyCreatedFirst() {
        List<Obs> results = service.getObsByAuditUser(bruno, null, null, null, null, null);

        assertThat(obsIds(results), contains(2002, 2001, 2004));
    }

    @Test
    public void shouldIncludeVoidedObsWhenSearchingByCreator() {
        List<Obs> results = service.getObsByAuditUser(bruno, null, null, null, null, null);

        assertThat(results.stream().anyMatch(Obs::getVoided), is(true));
    }

    @Test
    public void shouldFindObsVoidedByAUserMostRecentlyVoidedFirst() {
        List<Obs> results = service.getObsByAuditUser(null, butch, null, null, null, null);

        assertThat(obsIds(results), contains(2005, 2004));
    }

    @Test
    public void shouldNotFindObsVoidedByAnotherUser() {
        assertThat(service.getObsByAuditUser(null, bruno, null, null, null, null), is(java.util.Collections.emptyList()));
    }

    @Test
    public void shouldNarrowByBothUsersWhenBothAreGiven() {
        List<Obs> results = service.getObsByAuditUser(bruno, butch, null, null, null, null);

        assertThat(obsIds(results), contains(2004));
    }

    @Test
    public void shouldPageResults() {
        assertThat(obsIds(service.getObsByAuditUser(bruno, null, null, null, 0, 2)), contains(2002, 2001));
        assertThat(obsIds(service.getObsByAuditUser(bruno, null, null, null, 2, 2)), contains(2004));
        assertThat(obsIds(service.getObsByAuditUser(bruno, null, null, null, 1, 1)), contains(2001));
    }

    @Test
    public void shouldCountTheWholeResultSetRatherThanThePage() {
        assertThat(service.getCountOfObsByAuditUser(bruno, null, null, null), is(3L));
        assertThat(service.getCountOfObsByAuditUser(null, butch, null, null), is(2L));
        assertThat(service.getCountOfObsByAuditUser(bruno, butch, null, null), is(1L));
    }

    @Test
    public void shouldBoundACreatedBySearchByWhenTheObsWasCreated() {
        // obs 2001 was created on 1 Sep, 2002 on 3 Sep, 2004 on 28 Aug
        List<Obs> results = service.getObsByAuditUser(bruno, null, september(1, 0), september(2, 0), null, null);

        assertThat(obsIds(results), contains(2001));
    }

    @Test
    public void shouldBoundACreatedBySearchWithOnlyOneEndGiven() {
        assertThat(obsIds(service.getObsByAuditUser(bruno, null, september(2, 0), null, null, null)), contains(2002));
        assertThat(obsIds(service.getObsByAuditUser(bruno, null, null, september(2, 0), null, null)),
                contains(2001, 2004));
    }

    @Test
    public void shouldBoundAVoidedBySearchByWhenTheObsWasVoided() {
        // obs 2004 was voided on 4 Sep and 2005 on 5 Sep
        List<Obs> results = service.getObsByAuditUser(null, butch, september(5, 0), null, null, null);

        assertThat(obsIds(results), contains(2005));
    }

    /**
     * The fixture's creation dates deliberately run in a different order from its obs datetimes, so
     * a range applied to the wrong column would pick different rows.
     */
    @Test
    public void shouldBoundByTheAuditActionRatherThanTheObsDatetime() {
        // obs 2005 has an obs_datetime of 25 Aug but was created on 20 Aug and voided on 5 Sep
        assertThat(obsIds(service.getObsByAuditUser(butch, null, september(1, 0), null, null, null)), contains(2003));
        assertThat(obsIds(service.getObsByAuditUser(null, butch, september(1, 0), null, null, null)),
                contains(2005, 2004));
    }

    @Test
    public void shouldTreatBothEndsOfTheRangeAsInclusive() {
        // obs 2001 was created at 08:00 on 1 Sep, so a range of exactly that hour includes it
        List<Obs> results = service.getObsByAuditUser(bruno, null, september(1, 8), september(1, 8), null, null);

        assertThat(obsIds(results), contains(2001));
    }

    @Test
    public void shouldCountAndPageWithinTheRange() {
        Date from = september(1, 0);
        assertThat(service.getCountOfObsByAuditUser(bruno, null, from, null), is(2L));
        assertThat(obsIds(service.getObsByAuditUser(bruno, null, from, null, 0, 1)), contains(2002));
        assertThat(obsIds(service.getObsByAuditUser(bruno, null, from, null, 1, 1)), contains(2001));
    }

    @Test
    public void shouldFindNothingWhenTheRangeExcludesEverything() {
        List<Obs> results = service.getObsByAuditUser(bruno, null, september(20, 0), september(21, 0), null, null);

        assertThat(results, is(java.util.Collections.emptyList()));
        assertThat(service.getCountOfObsByAuditUser(bruno, null, september(20, 0), september(21, 0)), is(0L));
    }

    @Test
    public void shouldRefuseAnUnfilteredSearch() {
        assertThrows(APIException.class, () -> service.getObsByAuditUser(null, null, null, null, null, null));
        assertThrows(APIException.class, () -> service.getCountOfObsByAuditUser(null, null, null, null));
    }
}

