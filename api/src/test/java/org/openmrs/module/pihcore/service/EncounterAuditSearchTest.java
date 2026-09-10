package org.openmrs.module.pihcore.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Covers searching encounters by the users in their audit trail, by the provider recorded on them,
 * and by encounter type and date. The fixture's audit dates run in a different order from its
 * encounter datetimes, so an implementation that bounded or ordered by the wrong column would fail
 * here rather than look plausible.
 */
public class EncounterAuditSearchTest extends PihCoreContextSensitiveTest {

    private PihCoreService service;

    private User bruno;

    private User butch;

    private Provider provider;

    private EncounterType typeOne;

    @BeforeEach
    public void setup() {
        executeDataSet("encounterAuditTestDataset.xml");
        service = Context.getService(PihCoreService.class);
        bruno = Context.getUserService().getUser(501);
        butch = Context.getUserService().getUser(502);
        provider = Context.getProviderService().getProvider(1);
        typeOne = Context.getEncounterService().getEncounterType(1);
    }

    /**
     * The search under test, with the paging arguments left off. Keeping the argument list in one
     * place means a test cannot silently pass a value in the wrong position.
     */
    private List<Encounter> search(User createdBy, User changedBy, User voidedBy, Provider byProvider,
            EncounterType encounterType, Date fromDate, Date toDate) {
        return service.getEncountersByAuditUser(createdBy, changedBy, voidedBy, byProvider, encounterType, fromDate,
            toDate, null, null);
    }

    private List<Integer> encounterIds(List<Encounter> encounters) {
        return encounters.stream().map(Encounter::getEncounterId).collect(Collectors.toList());
    }

    /** Only this fixture's encounters, so the standard test dataset's own rows do not interfere. */
    private List<Integer> auditedIds(List<Encounter> encounters) {
        return encounterIds(encounters).stream().filter(id -> id >= 3000).collect(Collectors.toList());
    }

    private Date day(int month, int dayOfMonth, int hourOfDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(2026, month, dayOfMonth, hourOfDay, 0, 0);
        return calendar.getTime();
    }

    private Date september(int dayOfMonth) {
        return day(Calendar.SEPTEMBER, dayOfMonth, 0);
    }

    @Test
    public void shouldFindEncountersCreatedByAUserMostRecentlyCreatedFirst() {
        assertThat(auditedIds(search(bruno, null, null, null, null, null, null)), contains(3001, 3002));
    }

    @Test
    public void shouldFindEncountersChangedByAUserMostRecentlyChangedFirst() {
        // 3004 was changed on 4 Sep by bruno, 3002 on 3 Sep by butch
        assertThat(auditedIds(search(null, bruno, null, null, null, null, null)), contains(3004));
        assertThat(auditedIds(search(null, butch, null, null, null, null, null)), contains(3002));
    }

    @Test
    public void shouldFindEncountersVoidedByAUser() {
        assertThat(auditedIds(search(null, null, butch, null, null, null, null)), contains(3003));
    }

    @Test
    public void shouldIncludeVoidedEncountersWhenSearchingByCreator() {
        List<Encounter> results = search(butch, null, null, null, null, null, null);

        assertThat(auditedIds(results), contains(3005, 3004, 3003));
        assertThat(results.stream().anyMatch(Encounter::getVoided), is(true));
    }

    @Test
    public void shouldNarrowByEveryUserGiven() {
        // 3004 was created by butch and changed by bruno
        assertThat(auditedIds(search(butch, bruno, null, null, null, null, null)), contains(3004));
        assertThat(search(bruno, bruno, null, null, null, null, null), is(Collections.emptyList()));
    }

    @Test
    public void shouldFindEncountersByProvider() {
        // 3005 names the provider too, but on a voided row, so it does not count
        assertThat(auditedIds(search(null, null, null, provider, null, null, null)), contains(3004));
    }

    @Test
    public void shouldNarrowByProviderAndUserTogether() {
        assertThat(auditedIds(search(butch, null, null, provider, null, null, null)), contains(3004));
        assertThat(search(bruno, null, null, provider, null, null, null), is(Collections.emptyList()));
    }

    @Test
    public void shouldNarrowByEncounterType() {
        // every encounter in the fixture is of type one except 3005
        assertThat(auditedIds(search(butch, null, null, null, typeOne, null, null)), contains(3004, 3003));
    }

    @Test
    public void shouldNarrowByEncounterTypeAndProviderTogether() {
        assertThat(auditedIds(search(null, null, null, provider, typeOne, null, null)), contains(3004));

        // 3005 is of the other type, but its only provider row is voided, so it still does not match
        // (the standard dataset has encounters of that type on this provider, hence the scoping)
        EncounterType otherType = Context.getEncounterService().getEncounterType(2);
        assertThat(auditedIds(search(null, null, null, provider, otherType, null, null)), is(Collections.emptyList()));
    }

    @Test
    public void shouldRefuseATypeOnlySearch() {
        assertThrows(APIException.class, () -> search(null, null, null, null, typeOne, null, null));
    }

    @Test
    public void shouldBoundEachNamedActionByItsOwnDateColumn() {
        // bruno created 3001 on 1 Sep and 3002 on 25 Aug
        assertThat(auditedIds(search(bruno, null, null, null, null, september(1), null)), contains(3001));
        // bruno changed 3004 on 4 Sep, so nothing of his was changed from 5 Sep onwards
        assertThat(search(null, bruno, null, null, null, september(5), null), is(Collections.emptyList()));
    }

    /**
     * 3003's encounter_datetime is 20 Aug but it was voided on 5 Sep, so a range applied to the
     * encounter's own datetime would miss it.
     */
    @Test
    public void shouldBoundByTheAuditActionRatherThanTheEncounterDatetime() {
        assertThat(auditedIds(search(null, null, butch, null, null, september(1), null)), contains(3003));
    }

    /**
     * A provider search names no audit action, so its range bounds the encounter's own datetime —
     * what a provider's caseload is asked about, and the one thing core cannot filter by provider.
     */
    @Test
    public void shouldBoundAProviderSearchByTheEncounterDatetime() {
        // encounter 3004 happened on 30 Aug and was entered on 2 Sep
        assertThat(auditedIds(search(null, null, null, provider, null, day(Calendar.AUGUST, 1, 0), null)),
            contains(3004));
        assertThat(search(null, null, null, provider, null, september(1), null), is(Collections.emptyList()));
    }

    @Test
    public void shouldTreatBothEndsOfTheRangeAsInclusive() {
        assertThat(auditedIds(search(bruno, null, null, null, null, day(Calendar.SEPTEMBER, 1, 8),
            day(Calendar.SEPTEMBER, 1, 8))), contains(3001));
    }

    /**
     * The standard test dataset has encounters of its own attributed to these users and this
     * provider, so the counts are asserted against the unpaged result rather than against a number
     * this fixture alone would explain.
     */
    @Test
    public void shouldCountTheWholeResultSetRatherThanThePage() {
        List<Encounter> byCreator = search(butch, null, null, null, null, null, null);
        assertThat(service.getCountOfEncountersByAuditUser(butch, null, null, null, null, null, null),
            is((long) byCreator.size()));

        List<Encounter> byProvider = search(null, null, null, provider, null, null, null);
        assertThat(service.getCountOfEncountersByAuditUser(null, null, null, provider, null, null, null),
            is((long) byProvider.size()));
    }

    @Test
    public void shouldCountWithinAFilteredSearch() {
        List<Encounter> filtered = search(butch, null, null, null, typeOne, null, null);

        assertThat(service.getCountOfEncountersByAuditUser(butch, null, null, null, typeOne, null, null),
            is((long) filtered.size()));
    }

    @Test
    public void shouldPageThroughTheResults() {
        // this fixture's encounters were entered in 2026, so they sort ahead of the standard ones
        assertThat(auditedIds(service.getEncountersByAuditUser(butch, null, null, null, null, null, null, 0, 2)),
            contains(3005, 3004));
        assertThat(auditedIds(service.getEncountersByAuditUser(butch, null, null, null, null, null, null, 2, 2)),
            contains(3003));
    }

    @Test
    public void shouldRefuseASearchThatNamesNeitherUserNorProvider() {
        assertThrows(APIException.class, () -> search(null, null, null, null, null, september(1), null));
        assertThrows(APIException.class,
            () -> service.getCountOfEncountersByAuditUser(null, null, null, null, null, null, null));
    }
}
