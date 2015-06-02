package org.openmrs.module.pihcore.merge;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.User;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.metadata.core.EncounterTypes;
import org.openmrs.module.pihcore.metadata.core.PersonAttributeTypes;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest( { Context.class } )
public class PihPatientMergeActionsTest {

    private PihPatientMergeActions pihPatientMergeActions;

    private PersonService personService;

    private EncounterService encounterService;

    private PersonAttributeType phoneNumber = new PersonAttributeType(1);

    private PersonAttributeType mothersName = new PersonAttributeType(2);

    private EncounterType registration = new EncounterType(1);

    private User user = new User(1);

    @Before
    public void setup() {

        mockStatic(Context.class);
        when(Context.getAuthenticatedUser()).thenReturn(user);

        personService = mock(PersonService.class);
        when(personService.getPersonAttributeTypeByUuid(PersonAttributeTypes.TELEPHONE_NUMBER.uuid())).thenReturn(phoneNumber);
        when(personService.getPersonAttributeTypeByUuid(PersonAttributeTypes.MOTHERS_FIRST_NAME.uuid())).thenReturn(mothersName);

        encounterService = mock(EncounterService.class);
        when(encounterService.getEncounterTypeByUuid(EncounterTypes.PATIENT_REGISTRATION.uuid())).thenReturn(registration);

        pihPatientMergeActions = new PihPatientMergeActions();
        pihPatientMergeActions.setPersonService(personService);
        pihPatientMergeActions.setEncounterService(encounterService);
    }

    @Test
    public void shouldRemoveNonPreferredPhoneNumberAndMothersNameIfPresentOnPreferredPatient() {

        Patient preferred = new Patient();
        Patient nonPreferred = new Patient();

        PersonAttribute preferredPhoneNumber = new PersonAttribute(phoneNumber, "preferred");
        PersonAttribute preferredMothersName = new PersonAttribute(mothersName, "preferred");
        preferred.addAttribute(preferredPhoneNumber);
        preferred.addAttribute(preferredMothersName);

        PersonAttribute nonPreferredPhoneNumber = new PersonAttribute(phoneNumber, "non-preferred");
        PersonAttribute nonPreferredMothersName = new PersonAttribute(mothersName, "non-preferred");
        nonPreferred.addAttribute(nonPreferredPhoneNumber);
        nonPreferred.addAttribute(nonPreferredMothersName);

        //sanity check
        assertThat(preferred.getActiveAttributes().size(), is(2));
        assertThat(nonPreferred.getActiveAttributes().size(), is(2));

        pihPatientMergeActions.beforeMergingPatients(preferred, nonPreferred);

        assertThat(preferred.getActiveAttributes().size(), is(2));
        assertThat(nonPreferred.getActiveIdentifiers().size(), is(0));

    }

    @Test
    public void shouldNotRemoveNonPreferredPhoneNumberAndMothersNameIfNotPresentOnPreferredPatient() {

        Patient preferred = new Patient();
        Patient nonPreferred = new Patient();

        PersonAttribute nonPreferredPhoneNumber = new PersonAttribute(phoneNumber, "non-preferred");
        PersonAttribute nonPreferredMothersName = new PersonAttribute(mothersName, "non-preferred");
        nonPreferred.addAttribute(nonPreferredPhoneNumber);
        nonPreferred.addAttribute(nonPreferredMothersName);

        //sanity check
        assertThat(preferred.getActiveAttributes().size(), is(0));
        assertThat(nonPreferred.getActiveAttributes().size(), is(2));

        pihPatientMergeActions.beforeMergingPatients(preferred, nonPreferred);

        assertThat(preferred.getActiveAttributes().size(), is(0));
        assertThat(nonPreferred.getActiveAttributes().size(), is(2));

    }

    @Test
    public void shouldRemoveNonPreferredPhoneNumberButNotMothersName() {

        Patient preferred = new Patient();
        Patient nonPreferred = new Patient();

        PersonAttribute preferredPhoneNumber = new PersonAttribute(phoneNumber, "preferred");
        preferred.addAttribute(preferredPhoneNumber);

        PersonAttribute nonPreferredPhoneNumber = new PersonAttribute(phoneNumber, "non-preferred");
        PersonAttribute nonPreferredMothersName = new PersonAttribute(mothersName, "non-preferred");
        nonPreferred.addAttribute(nonPreferredPhoneNumber);
        nonPreferred.addAttribute(nonPreferredMothersName);

        //sanity check
        assertThat(preferred.getActiveAttributes().size(), is(1));
        assertThat(nonPreferred.getActiveAttributes().size(), is(2));

        pihPatientMergeActions.beforeMergingPatients(preferred, nonPreferred);

        assertThat(preferred.getActiveAttributes().size(), is(1));
        assertThat(nonPreferred.getActiveAttributes().size(), is(1));
        assertThat(nonPreferred.getActiveAttributes(), contains(nonPreferredMothersName));

    }

    @Test
    public void shouldNotFailIfNonPreferredPatientDoesNotHaveAttributes() {

        Patient preferred = new Patient();
        Patient nonPreferred = new Patient();

        PersonAttribute preferredPhoneNumber = new PersonAttribute(phoneNumber, "preferred");
        PersonAttribute preferredMothersName = new PersonAttribute(mothersName, "preferred");
        preferred.addAttribute(preferredPhoneNumber);
        preferred.addAttribute(preferredMothersName);

        pihPatientMergeActions.beforeMergingPatients(preferred, nonPreferred);

        assertThat(preferred.getAttributes().size(), is(2));
        assertThat(nonPreferred.getAttributes().size(), is(0));

    }

    @Test
    public void shouldVoidMostRecentRegistrationEncountersOnNonPreferredPatientIfMoreRecentThanPreferred() {
        Patient preferred = new Patient(1);
        Patient nonPreferred = new Patient(2);

        Encounter preferredEncounter1 = createEncounter(1, new DateTime(2012, 9, 10, 0, 0, 0).toDate());
        Encounter preferredEncounter2 = createEncounter(2, new DateTime(2012, 10, 10, 0, 0, 0).toDate());

        Encounter nonPreferredEncounter1 = createEncounter(3, new DateTime(2012, 11, 10, 0, 0, 0).toDate());
        Encounter nonPreferredEncounter2 = createEncounter(4, new DateTime(2012, 12, 10, 0, 0, 0).toDate());

        when(encounterService.getEncounters(preferred, null, null, null, null,
                Collections.singleton(registration), null, null, null, false))
                .thenReturn(Arrays.asList(preferredEncounter1, preferredEncounter2));

        when(encounterService.getEncounters(nonPreferred, null, null, null, null,
                Collections.singleton(registration), null, null, null, false))
                .thenReturn(Arrays.asList(nonPreferredEncounter1, nonPreferredEncounter2));

        pihPatientMergeActions.beforeMergingPatients(preferred, nonPreferred);

        verify(encounterService, never()).voidEncounter(eq(preferredEncounter1), anyString());
        verify(encounterService, never()).voidEncounter(eq(preferredEncounter2), anyString());
        verify(encounterService).voidEncounter(eq(nonPreferredEncounter1), anyString());
        verify(encounterService).voidEncounter(eq(nonPreferredEncounter2), anyString());
    }

    @Test
    public void shouldNotVoidMostRecentRegistrationEncounterOnNonPreferredPatientIfNotMoreRecentThanPreferred() {
        Patient preferred = new Patient(1);
        Patient nonPreferred = new Patient(2);

        Encounter preferredEncounter1 = createEncounter(1, new DateTime(2012, 10, 10, 0, 0, 0).toDate());
        Encounter preferredEncounter2 = createEncounter(2, new DateTime(2012, 11, 10, 0, 0, 0).toDate());

        Encounter nonPreferredEncounter1 = createEncounter(3, new DateTime(2012, 1, 10, 0, 0, 0).toDate());
        Encounter nonPreferredEncounter2 = createEncounter(4, new DateTime(2012, 2, 10, 0, 0, 0).toDate());

        when(encounterService.getEncounters(preferred, null, null, null, null,
                Collections.singleton(registration), null, null, null, false))
                .thenReturn(Arrays.asList(preferredEncounter1, preferredEncounter2));

        when(encounterService.getEncounters(nonPreferred, null, null, null, null,
                Collections.singleton(registration), null, null, null, false))
                .thenReturn(Arrays.asList(nonPreferredEncounter1, nonPreferredEncounter2));

        pihPatientMergeActions.beforeMergingPatients(preferred, nonPreferred);

        verify(encounterService, never()).voidEncounter(eq(preferredEncounter1), anyString());
        verify(encounterService, never()).voidEncounter(eq(preferredEncounter2), anyString());
        verify(encounterService, never()).voidEncounter(eq(nonPreferredEncounter1), anyString());
        verify(encounterService, never()).voidEncounter(eq(nonPreferredEncounter2), anyString());
    }

    @Test
    public void shouldNotFailIfNoEncounters() {

        Patient preferred = new Patient(1);
        Patient nonPreferred = new Patient(2);

        when(encounterService.getEncounters(preferred, null, null, null, null,
                Collections.singleton(registration), null, null, null, false))
                .thenReturn(null);

        when(encounterService.getEncounters(nonPreferred, null, null, null, null,
                Collections.singleton(registration), null, null, null, false))
                .thenReturn(Collections.singletonList(new Encounter()));

        pihPatientMergeActions.beforeMergingPatients(preferred, nonPreferred);

        // just make sure no NPE
    }


    private Encounter createEncounter(int id, Date date) {
        Encounter encounter = new Encounter(id);
        encounter.setEncounterDatetime(date);
        return encounter;
    }

}
