package org.openmrs.module.pihcore.task;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.VisitService;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.concept.EmrConceptService;
import org.openmrs.module.emrapi.disposition.DispositionService;
import org.openmrs.module.emrapi.test.ContextSensitiveMetadataTestUtils;
import org.openmrs.module.emrapi.visit.EmrVisitService;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.module.pihcore.metadata.haiti.mirebalais.MirebalaisLocations;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class PihCloseStaleVisitsTaskTest extends BaseModuleContextSensitiveTest {

    private static final String ED_TRIAGE_ENCOUNTER_TYPE_UUID = "74cef0a6-2801-11e6-b67b-9e71128cae77";

    @Autowired
    private AdtService adtService;

    @Autowired
    private EmrApiProperties emrApiProperties;

    @Autowired
    LocationService locationService;

    @Autowired
    PatientService patientService;

    @Autowired
    VisitService visitService;

    @Autowired
    EmrVisitService emrVisitService;

    @Autowired
    ConceptService conceptService;

    @Autowired
    DispositionService dispositionService;

    @Autowired
    EncounterService encounterService;

    @Autowired
    EmrConceptService emrConceptService;

    @Before
    public void setUp() throws Exception {
        executeDataSet("closeStaleVisitsTestDataset.xml");
    }

    @Test
    public void test_shouldCloseActiveVisits() throws Exception {

        ContextSensitiveMetadataTestUtils.setupDispositionDescriptor(conceptService, dispositionService);
        ContextSensitiveMetadataTestUtils.setupAdmissionDecisionConcept(conceptService, emrApiProperties);
        ContextSensitiveMetadataTestUtils.setupSupportsVisitLocationTag(locationService);

        Location location = locationService.getLocation(1);
        location.addTag(emrApiProperties.getSupportsVisitsLocationTag());
        locationService.saveLocation(location);

        // sanity check--visits in the standard test data set that are open
        assertThat(visitService.getVisit(1).getStopDatetime(), nullValue());
        assertThat(visitService.getVisit(2).getStopDatetime(), nullValue());
        assertThat(visitService.getVisit(3).getStopDatetime(), nullValue());
        assertThat(visitService.getVisit(4).getStopDatetime(), nullValue());
        assertThat(visitService.getVisit(5).getStopDatetime(), nullValue());
        assertThat(visitService.getVisit(6).getStopDatetime(), nullValue());

        new PihCloseStaleVisitsTask().execute();

        // only visits that have a location that is tagged "Visit Location" will be closed
        assertThat(visitService.getVisit(1).getStopDatetime(), notNullValue());
        assertThat(visitService.getVisit(2).getStopDatetime(), nullValue());
        assertThat(visitService.getVisit(3).getStopDatetime(), nullValue());
        assertThat(visitService.getVisit(4).getStopDatetime(), nullValue());
        assertThat(visitService.getVisit(5).getStopDatetime(), nullValue());

        // should ignore voided visits
        assertThat(visitService.getVisit(6).getStopDatetime(), nullValue());
    }


    @Test
    public void test_shouldNotCloseVisitIfMostRecentDispositionKeepsVisitOpen() throws Exception {

        ContextSensitiveMetadataTestUtils.setupDispositionDescriptor(conceptService, dispositionService);
        ContextSensitiveMetadataTestUtils.setupAdmissionDecisionConcept(conceptService, emrApiProperties);
        ContextSensitiveMetadataTestUtils.setupSupportsVisitLocationTag(locationService);

        Patient patient = patientService.getPatient(7);    // patient already has one visit in test dataset

        // need o tag the unknown location so we don't run into an error when testing against the existing visits in the test dataset
        Location unknownLocation = locationService.getLocation(1);
        unknownLocation.addTag(emrApiProperties.getSupportsVisitsLocationTag());
        locationService.saveLocation(unknownLocation);

        Location location = locationService.getLocation(2);
        location.addTag(emrApiProperties.getSupportsVisitsLocationTag());
        locationService.saveLocation(location);

        Visit visit = new Visit();
        visit.setStartDatetime(DateUtils.addHours(new Date(), -14));
        visit.setPatient(patient);
        visit.setLocation(location);
        visit.setVisitType(emrApiProperties.getAtFacilityVisitType());

        // create an encounter with a disposition obs
        Encounter encounter = new Encounter();
        encounter.setPatient(patient);
        encounter.setEncounterType(encounterService.getEncounterType(1));
        encounter.setEncounterDatetime(visit.getStartDatetime());

        Obs dispositionObsGroup = new Obs();
        dispositionObsGroup.setConcept(dispositionService.getDispositionDescriptor().getDispositionSetConcept());
        Obs dispositionObs = new Obs();
        dispositionObs.setConcept(dispositionService.getDispositionDescriptor().getDispositionConcept());
        dispositionObs.setValueCoded(emrConceptService.getConcept(EmrApiConstants.EMR_CONCEPT_SOURCE_NAME + ":Admit to hospital"));  // this fake code is set in ContextSensitiveMetadataTestUtils
        dispositionObsGroup.addGroupMember(dispositionObs);

        encounter.addObs(dispositionObsGroup);
        encounterService.saveEncounter(encounter);

        visit.addEncounter(encounter);
        visitService.saveVisit(visit);

        VisitDomainWrapper activeVisit = adtService.getActiveVisit(patient, location);

        // sanity check
        assertNotNull(activeVisit);

        new PihCloseStaleVisitsTask().execute();

        activeVisit = adtService.getActiveVisit(patient, location);
        assertNotNull(activeVisit);
    }

    @Test
    public void test_shouldNotCloseVisitIfHasEncounterWithinExpirationRange() throws Exception {

        ContextSensitiveMetadataTestUtils.setupDispositionDescriptor(conceptService, dispositionService);
        ContextSensitiveMetadataTestUtils.setupAdmissionDecisionConcept(conceptService, emrApiProperties);
        ContextSensitiveMetadataTestUtils.setupSupportsVisitLocationTag(locationService);

        Patient patient = patientService.getPatient(7);    // patient already has one visit in test dataset

        // need o tag the unknown location so we don't run into an error when testing against the existing visits in the test dataset
        Location unknownLocation = locationService.getLocation(1);
        unknownLocation.addTag(emrApiProperties.getSupportsVisitsLocationTag());
        locationService.saveLocation(unknownLocation);

        Location location = locationService.getLocation(2);
        location.addTag(emrApiProperties.getSupportsVisitsLocationTag());
        locationService.saveLocation(location);

        Visit visit = new Visit();
        visit.setStartDatetime(DateUtils.addHours(new Date(), -14));
        visit.setPatient(patient);
        visit.setLocation(location);
        visit.setVisitType(emrApiProperties.getAtFacilityVisitType());

        // create an encounter within the expiration range
        Encounter encounter = new Encounter();
        encounter.setPatient(patient);
        encounter.setEncounterType(encounterService.getEncounterType(1));
        encounter.setEncounterDatetime(DateUtils.addHours(new Date(), -(emrApiProperties.getVisitExpireHours() - 1)));  // expire hours - 1
        encounterService.saveEncounter(encounter);

        visit.addEncounter(encounter);
        visitService.saveVisit(visit);

        VisitDomainWrapper activeVisit = adtService.getActiveVisit(patient, location);

        // sanity check
        assertNotNull(activeVisit);

        new PihCloseStaleVisitsTask().execute();

        activeVisit = adtService.getActiveVisit(patient, location);
        assertNotNull(activeVisit);
    }

    @Test
    public void test_shouldCloseVisitIfDoesNotEncounterWithinExpirationRange() throws Exception {

        ContextSensitiveMetadataTestUtils.setupDispositionDescriptor(conceptService, dispositionService);
        ContextSensitiveMetadataTestUtils.setupAdmissionDecisionConcept(conceptService, emrApiProperties);
        ContextSensitiveMetadataTestUtils.setupSupportsVisitLocationTag(locationService);

        Patient patient = patientService.getPatient(7);    // patient already has one visit in test dataset

        // need o tag the unknown location so we don't run into an error when testing against the existing visits in the test dataset
        Location unknownLocation = locationService.getLocation(1);
        unknownLocation.addTag(emrApiProperties.getSupportsVisitsLocationTag());
        locationService.saveLocation(unknownLocation);

        Location location = locationService.getLocation(2);
        location.addTag(emrApiProperties.getSupportsVisitsLocationTag());
        locationService.saveLocation(location);

        Visit visit = new Visit();
        visit.setStartDatetime(DateUtils.addHours(new Date(), -14));
        visit.setPatient(patient);
        visit.setLocation(location);
        visit.setVisitType(emrApiProperties.getAtFacilityVisitType());

        // create an encounter within the expiration range
        Encounter encounter = new Encounter();
        encounter.setPatient(patient);
        encounter.setEncounterType(encounterService.getEncounterType(1));
        encounter.setEncounterDatetime(DateUtils.addHours(new Date(), -(emrApiProperties.getVisitExpireHours() + 1)));  // expire hours + 1
        encounterService.saveEncounter(encounter);

        visit.addEncounter(encounter);
        visitService.saveVisit(visit);

        VisitDomainWrapper activeVisit = adtService.getActiveVisit(patient, location);

        // sanity check
        assertNotNull(activeVisit);

        new PihCloseStaleVisitsTask().execute();

        activeVisit = adtService.getActiveVisit(patient, location);
        assertNull(activeVisit);


    }


    @Test
    public void test_shouldKeepVisitOpenIfItHasCheckinAtEmergencyLocationAndEncounterWithinFortyEightHours() throws Exception {

        ContextSensitiveMetadataTestUtils.setupDispositionDescriptor(conceptService, dispositionService);
        ContextSensitiveMetadataTestUtils.setupAdmissionDecisionConcept(conceptService, emrApiProperties);
        ContextSensitiveMetadataTestUtils.setupSupportsVisitLocationTag(locationService);

        Patient patient = patientService.getPatient(7);    // patient already has one visit in test dataset

        // need to tag the unknown location so we don't run into an error when testing against the existing visits in the test dataset
        Location unknownLocation = locationService.getLocation(1);
        unknownLocation.addTag(emrApiProperties.getSupportsVisitsLocationTag());
        locationService.saveLocation(unknownLocation);

        Location location = locationService.getLocation(2);
        location.addTag(emrApiProperties.getSupportsVisitsLocationTag());
        locationService.saveLocation(location);

        Visit visit = new Visit();
        visit.setStartDatetime(DateUtils.addHours(new Date(), -60));
        visit.setPatient(patient);
        visit.setLocation(location);
        visit.setVisitType(emrApiProperties.getAtFacilityVisitType());

        // create a check-in at Ijans Location
        Encounter checkIn = new Encounter();
        checkIn.setPatient(patient);
        checkIn.setEncounterType(emrApiProperties.getCheckInEncounterType());
        checkIn.setEncounterDatetime(visit.getStartDatetime());
        checkIn.setLocation(locationService.getLocationByUuid(MirebalaisLocations.EMERGENCY.uuid()));  // we've added this to the test dataset
        encounterService.saveEncounter(checkIn);

        // create a another encounter within 48 hours
        Encounter consult = new Encounter();
        consult.setPatient(patient);
        consult.setEncounterType(emrApiProperties.getVisitNoteEncounterType());
        consult.setEncounterDatetime(DateUtils.addHours(new Date(), -47));
        encounterService.saveEncounter(consult);

        visit.addEncounter(checkIn);
        visit.addEncounter(consult);
        visitService.saveVisit(visit);

        VisitDomainWrapper activeVisit = adtService.getActiveVisit(patient, location);

        // sanity check
        assertNotNull(activeVisit);

        new PihCloseStaleVisitsTask().execute();

        activeVisit = adtService.getActiveVisit(patient, location);
        assertNotNull(activeVisit);
    }

    @Test
    public void test_shouldNotKeepVisitOpenIfItHasCheckinAtEmergencyLocationButNoEncounterWithinFortyEightHours() throws Exception {

        ContextSensitiveMetadataTestUtils.setupDispositionDescriptor(conceptService, dispositionService);
        ContextSensitiveMetadataTestUtils.setupAdmissionDecisionConcept(conceptService, emrApiProperties);
        ContextSensitiveMetadataTestUtils.setupSupportsVisitLocationTag(locationService);

        Patient patient = patientService.getPatient(7);    // patient already has one visit in test dataset

        // need to tag the unknown location so we don't run into an error when testing against the existing visits in the test dataset
        Location unknownLocation = locationService.getLocation(1);
        unknownLocation.addTag(emrApiProperties.getSupportsVisitsLocationTag());
        locationService.saveLocation(unknownLocation);

        Location location = locationService.getLocation(2);
        location.addTag(emrApiProperties.getSupportsVisitsLocationTag());
        locationService.saveLocation(location);

        Visit visit = new Visit();
        visit.setStartDatetime(DateUtils.addHours(new Date(), -60));
        visit.setPatient(patient);
        visit.setLocation(location);
        visit.setVisitType(emrApiProperties.getAtFacilityVisitType());

        // create a check-in at Ijans Location
        Encounter checkIn = new Encounter();
        checkIn.setPatient(patient);
        checkIn.setEncounterType(emrApiProperties.getCheckInEncounterType());
        checkIn.setEncounterDatetime(visit.getStartDatetime());
        checkIn.setLocation(locationService.getLocationByUuid(MirebalaisLocations.EMERGENCY.uuid()));  // we've added this to the test dataset
        encounterService.saveEncounter(checkIn);

        // create another encounter, but not within 48 hours
        Encounter consult = new Encounter();
        consult.setPatient(patient);
        consult.setEncounterType(emrApiProperties.getVisitNoteEncounterType());
        consult.setEncounterDatetime(DateUtils.addHours(new Date(), -49));
        encounterService.saveEncounter(consult);

        visit.addEncounter(consult);
        visitService.saveVisit(visit);

        VisitDomainWrapper activeVisit = adtService.getActiveVisit(patient, location);

        // sanity check
        assertNotNull(activeVisit);

        new PihCloseStaleVisitsTask().execute();

        activeVisit = adtService.getActiveVisit(patient, location);
        assertNull(activeVisit);
    }

    @Test
    public void test_shouldKeepVisitOpenIfItHasEDTriageAndEncounterWithinFortyEightHours() throws Exception {

        ContextSensitiveMetadataTestUtils.setupDispositionDescriptor(conceptService, dispositionService);
        ContextSensitiveMetadataTestUtils.setupAdmissionDecisionConcept(conceptService, emrApiProperties);
        ContextSensitiveMetadataTestUtils.setupSupportsVisitLocationTag(locationService);

        Patient patient = patientService.getPatient(7);    // patient already has one visit in test dataset

        // need to tag the unknown location so we don't run into an error when testing against the existing visits in the test dataset
        Location unknownLocation = locationService.getLocation(1);
        unknownLocation.addTag(emrApiProperties.getSupportsVisitsLocationTag());
        locationService.saveLocation(unknownLocation);

        Location location = locationService.getLocation(2);
        location.addTag(emrApiProperties.getSupportsVisitsLocationTag());
        locationService.saveLocation(location);

        Visit visit = new Visit();
        visit.setStartDatetime(DateUtils.addHours(new Date(), -60));
        visit.setPatient(patient);
        visit.setLocation(location);
        visit.setVisitType(emrApiProperties.getAtFacilityVisitType());

        // create ED Triage encounterdmit to hospital
        Encounter edTriage = new Encounter();
        edTriage.setPatient(patient);
        edTriage.setEncounterType(encounterService.getEncounterTypeByUuid(ED_TRIAGE_ENCOUNTER_TYPE_UUID));
        edTriage.setEncounterDatetime(visit.getStartDatetime());
        encounterService.saveEncounter(edTriage);

        // create a another encounter within 48 hours
        Encounter consult = new Encounter();
        consult.setPatient(patient);
        consult.setEncounterType(emrApiProperties.getVisitNoteEncounterType());
        consult.setEncounterDatetime(DateUtils.addHours(new Date(), -47));
        encounterService.saveEncounter(consult);

        visit.addEncounter(edTriage);
        visit.addEncounter(consult);
        visitService.saveVisit(visit);

        VisitDomainWrapper activeVisit = adtService.getActiveVisit(patient, location);

        // sanity check
        assertNotNull(activeVisit);

        new PihCloseStaleVisitsTask().execute();

        activeVisit = adtService.getActiveVisit(patient, location);
        assertNotNull(activeVisit);
    }

    @Test
    public void test_shouldNotKeepVisitOpenIfItHasEDTriageAndEncounterWithinFortyEightHoursButAlsoDischargeEncounter() throws Exception {

        ContextSensitiveMetadataTestUtils.setupDispositionDescriptor(conceptService, dispositionService);
        ContextSensitiveMetadataTestUtils.setupAdmissionDecisionConcept(conceptService, emrApiProperties);
        ContextSensitiveMetadataTestUtils.setupSupportsVisitLocationTag(locationService);

        Patient patient = patientService.getPatient(7);    // patient already has one visit in test dataset

        // need to tag the unknown location so we don't run into an error when testing against the existing visits in the test dataset
        Location unknownLocation = locationService.getLocation(1);
        unknownLocation.addTag(emrApiProperties.getSupportsVisitsLocationTag());
        locationService.saveLocation(unknownLocation);

        Location location = locationService.getLocation(2);
        location.addTag(emrApiProperties.getSupportsVisitsLocationTag());
        locationService.saveLocation(location);

        Visit visit = new Visit();
        visit.setStartDatetime(DateUtils.addHours(new Date(), -60));
        visit.setPatient(patient);
        visit.setLocation(location);
        visit.setVisitType(emrApiProperties.getAtFacilityVisitType());

        // create ED Triage encounter
        Encounter edTriage = new Encounter();
        edTriage.setPatient(patient);
        edTriage.setEncounterType(encounterService.getEncounterTypeByUuid(ED_TRIAGE_ENCOUNTER_TYPE_UUID));
        edTriage.setEncounterDatetime(visit.getStartDatetime());
        encounterService.saveEncounter(edTriage);

        // create a another encounter within 48 hours
        Encounter consult = new Encounter();
        consult.setPatient(patient);
        consult.setEncounterType(emrApiProperties.getVisitNoteEncounterType());
        consult.setEncounterDatetime(DateUtils.addHours(new Date(), -47));
        encounterService.saveEncounter(consult);

        // create an encounter with a disposition obs of "discharge
        Encounter encounterWithDisposition = new Encounter();
        encounterWithDisposition.setPatient(patient);
        encounterWithDisposition.setEncounterType(encounterService.getEncounterType(1));
        encounterWithDisposition.setEncounterDatetime(visit.getStartDatetime());

        Obs dispositionObsGroup = new Obs();
        dispositionObsGroup.setConcept(dispositionService.getDispositionDescriptor().getDispositionSetConcept());
        Obs dispositionObs = new Obs();
        dispositionObs.setConcept(dispositionService.getDispositionDescriptor().getDispositionConcept());
        dispositionObs.setValueCoded(emrConceptService.getConcept(EmrApiConstants.EMR_CONCEPT_SOURCE_NAME + ":Discharged"));  // this fake code is set in ContextSensitiveMetadataTestUtils
        dispositionObsGroup.addGroupMember(dispositionObs);

        encounterWithDisposition.addObs(dispositionObsGroup);
        encounterService.saveEncounter(encounterWithDisposition);

        visit.addEncounter(edTriage);
        visit.addEncounter(consult);
        visit.addEncounter(encounterWithDisposition);
        visitService.saveVisit(visit);

        VisitDomainWrapper activeVisit = adtService.getActiveVisit(patient, location);

        // sanity check
        assertNotNull(activeVisit);

        new PihCloseStaleVisitsTask().execute();

        activeVisit = adtService.getActiveVisit(patient, location);
        assertNull(activeVisit);
    }


}
