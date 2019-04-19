package org.openmrs.module.pihcore.encounter;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.module.emrapi.adt.AdtServiceImpl;
import org.openmrs.module.emrapi.adt.EmrApiVisitAssignmentHandler;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.mockito.Mockito.mock;

public class PihEncounterValidatorTest {

    private PihEncounterValidator validator = new PihEncounterValidator();

    @Before
    public void setup() {
        validator.setAdtService(new AdtServiceImpl());
        validator.setVisitAssignmentHandler(mock(EmrApiVisitAssignmentHandler.class));
    }

    @Test
    public void testShouldRemoveVisitIfNotSuitable() throws Exception {

        Patient patient = new Patient();
        Location location = new Location();

        Visit notSuitable = new Visit();
        notSuitable.setPatient(patient);
        notSuitable.setStartDatetime(DateUtils.addDays(new Date(), -7));
        notSuitable.setStopDatetime(DateUtils.addDays(new Date(), -6));
        notSuitable.setLocation(location);

        Encounter encounter = new Encounter();
        encounter.setId(1);
        encounter.setPatient(patient);
        encounter.setLocation(location);
        encounter.setEncounterDatetime(new Date());

        notSuitable.addEncounter(encounter); // we attempt to assign a non-suitable visit to the encounter

        validator.validate(encounter, null);

        Assert.assertNull(encounter.getVisit());
        Assert.assertThat(notSuitable.getEncounters(), not(contains(encounter)));
    }

    @Test
    public void testShouldNotRemoveVisitIfSuitable() throws Exception {

        Patient patient = new Patient();
        Location location = new Location();

        Visit suitable = new Visit();
        suitable.setPatient(patient);
        suitable.setStartDatetime(DateUtils.addDays(new Date(), -1));
        suitable.setLocation(location);

        Encounter encounter = new Encounter();
        encounter.setId(1);
        encounter.setPatient(patient);
        encounter.setLocation(location);
        encounter.setEncounterDatetime(new Date());

        suitable.addEncounter(encounter);

        validator.validate(encounter, null);

        Assert.assertThat(encounter.getVisit(), is(suitable));
        Assert.assertThat(suitable.getEncounters(), contains(encounter));
    }



}
