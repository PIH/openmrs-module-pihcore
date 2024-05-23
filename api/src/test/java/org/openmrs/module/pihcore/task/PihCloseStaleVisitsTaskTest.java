package org.openmrs.module.pihcore.task;

import org.apache.commons.lang.time.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.VisitService;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.concept.EmrConceptService;
import org.openmrs.module.emrapi.disposition.Disposition;
import org.openmrs.module.emrapi.disposition.DispositionDescriptor;
import org.openmrs.module.emrapi.disposition.DispositionService;
import org.openmrs.module.emrapi.test.ContextSensitiveMetadataTestUtils;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.metadatamapping.MetadataSource;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.queue.api.QueueEntryService;
import org.openmrs.module.queue.api.QueueService;
import org.openmrs.module.queue.model.QueueEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.jupiter.api.Assertions.*;

public class PihCloseStaleVisitsTaskTest extends PihCoreContextSensitiveTest {

    private static final String ED_TRIAGE_ENCOUNTER_TYPE_UUID = "74cef0a6-2801-11e6-b67b-9e71128cae77";

    @Autowired
    private AdtService adtService;

    @Autowired
    private EmrApiProperties emrApiProperties;

    @Autowired
    ConceptService conceptService;

    @Autowired
    DispositionService dispositionService;

    @Autowired
    EmrConceptService emrConceptService;

    @Autowired
    protected MetadataMappingService metadataMappingService;

    @Autowired
    protected VisitService visitService;

    @Autowired
    protected PatientService patientService;

    @Autowired
    protected EncounterService encounterService;

    @Autowired
    protected LocationService locationService;

    @Autowired
    @Qualifier("queue.QueueEntryService")
    protected QueueEntryService queueEntryService;

    @Autowired
    @Qualifier("queue.QueueService")
    protected QueueService queueService;

    @Autowired
    protected FormService formService;

    PihCloseStaleVisitsTask task;

    @BeforeEach
    public void setUp() throws Exception {
        executeDataSet("closeStaleVisitsTestDataset.xml");
        executeDataSet("queueTestDataset.xml");
        createEmrApiMappingSource(metadataMappingService);
        loadFromInitializer(Domain.ENCOUNTER_TYPES, "encounterTypes.csv");
        loadFromInitializer(Domain.ENCOUNTER_ROLES, "encounterRoles.csv");
        loadFromInitializer(Domain.VISIT_TYPES, "visitTypes.csv");
        loadFromInitializer(Domain.METADATA_TERM_MAPPINGS, "metadataMappings.csv");
        task = new PihCloseStaleVisitsTask();
    }

    protected Config getConfig() {
        ConfigDescriptor d = new ConfigDescriptor();
        d.setCountry(ConfigDescriptor.Country.HAITI);
        d.setSite("MIREBALAIS");
        return new Config(d);
    }

    protected void createEmrApiMappingSource(MetadataMappingService metadataMappingService) {
        MetadataSource source = new MetadataSource();
        source.setName(EmrApiConstants.EMR_METADATA_SOURCE_NAME);
        metadataMappingService.saveMetadataSource(source);
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

        task.run();

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
        visit.setDateCreated(DateUtils.addHours(new Date(), -30));  // because recently-created visits won't be closed
        visit.setStartDatetime(DateUtils.addDays(new Date(), -1)); // visit started yesterday
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
        dispositionObs.setValueCoded(emrConceptService.getConcept(EmrApiConstants.EMR_CONCEPT_SOURCE_NAME + ":ED Observation"));  // this fake code is set in ContextSensitiveMetadataTestUtils
        dispositionObsGroup.addGroupMember(dispositionObs);

        encounter.addObs(dispositionObsGroup);
        encounterService.saveEncounter(encounter);

        visit.addEncounter(encounter);
        visitService.saveVisit(visit);

        VisitDomainWrapper activeVisit = adtService.getActiveVisit(patient, location);

        // sanity check
        assertNotNull(activeVisit);

        task.run();

        activeVisit = adtService.getActiveVisit(patient, location);
        assertNotNull(activeVisit);
    }

    @Test
    public void test_shouldNotCloseVisitIfMostRecentDispositionOfTypeAdmit() throws Exception {

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
        visit.setDateCreated(DateUtils.addHours(new Date(), -30));  // because recently-created visits won't be closed
        visit.setStartDatetime(DateUtils.addDays(new Date(), -1)); // visit started yesterday
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

        task.run();

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
        visit.setDateCreated(DateUtils.addHours(new Date(), -30));  // because recently-created visits won't be closed
        visit.setStartDatetime(DateUtils.addDays(new Date(), -1));  // visit started yesterday
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

        task.run();

        activeVisit = adtService.getActiveVisit(patient, location);
        assertNotNull(activeVisit);
    }

    @Test
    public void test_shouldCloseVisitIfDoesNotHaveEncounterWithinExpirationRange() throws Exception {

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
        visit.setDateCreated(DateUtils.addHours(new Date(), -30));  // because recently-created visits won't be closed
        visit.setStartDatetime(DateUtils.addDays(new Date(), -1));  // visit started yesterday
        visit.setPatient(patient);
        visit.setLocation(location);
        visit.setVisitType(emrApiProperties.getAtFacilityVisitType());

        // create an encounter earlier than the expire range
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

        task.run();

        activeVisit = adtService.getActiveVisit(patient, location);
        assertNull(activeVisit);

    }


    @Test
    public void test_ED_shouldKeepVisitOpenIfItHasCheckinAtEmergencyLocationAndEncounterWithin7Days() throws Exception {

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
        visit.setDateCreated(DateUtils.addHours(new Date(), -30));  // because recently-created visits won't be closed
        visit.setStartDatetime(DateUtils.addDays(new Date(), -60));  // visit started 60 days ago (because ED visit logic extends to 30 days we need to start this long aga)
        visit.setPatient(patient);
        visit.setLocation(location);
        visit.setVisitType(emrApiProperties.getAtFacilityVisitType());

        // create a check-in at Ijans Location
        Encounter checkIn = new Encounter();
        checkIn.setPatient(patient);
        checkIn.setEncounterType(emrApiProperties.getCheckInEncounterType());
        checkIn.setEncounterDatetime(visit.getStartDatetime());
        checkIn.setLocation(locationService.getLocation("Ijans"));  // we've added this to the test dataset
        encounterService.saveEncounter(checkIn);

        // create a another encounter within 7 days
        Encounter consult = new Encounter();
        consult.setPatient(patient);
        consult.setEncounterType(emrApiProperties.getVisitNoteEncounterType());
        consult.setEncounterDatetime(DateUtils.addDays(new Date(), -6));
        encounterService.saveEncounter(consult);

        visit.addEncounter(checkIn);
        visit.addEncounter(consult);
        visitService.saveVisit(visit);

        VisitDomainWrapper activeVisit = adtService.getActiveVisit(patient, location);

        // sanity check
        assertNotNull(activeVisit);

        task.run();

        activeVisit = adtService.getActiveVisit(patient, location);
        assertNotNull(activeVisit);
    }

    @Test
    public void test_ED_shouldKeepVisitOpenIfItHasEDTriageAndAnyEncounterWithinThePast7Days() throws Exception {

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
        visit.setDateCreated(DateUtils.addHours(new Date(), -30));  // because recently-created visits won't be closed
        visit.setStartDatetime(DateUtils.addDays(new Date(), -60));  // visit started 60 days ago (because ED visit logic extends to 30 days we need to start this long aga)
        visit.setPatient(patient);
        visit.setLocation(location);
        visit.setVisitType(emrApiProperties.getAtFacilityVisitType());

        // create ED Triage encounter
        Encounter edTriage = new Encounter();
        edTriage.setPatient(patient);
        edTriage.setEncounterType(encounterService.getEncounterTypeByUuid(ED_TRIAGE_ENCOUNTER_TYPE_UUID));
        edTriage.setEncounterDatetime(visit.getStartDatetime());
        encounterService.saveEncounter(edTriage);

        // create a another encounter within 7 days
        Encounter consult = new Encounter();
        consult.setPatient(patient);
        consult.setEncounterType(emrApiProperties.getVisitNoteEncounterType());
        consult.setEncounterDatetime(DateUtils.addDays(new Date(), -6));
        encounterService.saveEncounter(consult);

        visit.addEncounter(edTriage);
        visit.addEncounter(consult);
        visitService.saveVisit(visit);

        VisitDomainWrapper activeVisit = adtService.getActiveVisit(patient, location);

        // sanity check
        assertNotNull(activeVisit);

        task.run();

        activeVisit = adtService.getActiveVisit(patient, location);
        assertNotNull(activeVisit);
    }
    @Test
    public void test_ED_shouldNotKeepVisitOpenIfItHasCheckinAtEmergencyLocationButNoEncounterWithinSevenDays() throws Exception {

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
        visit.setDateCreated(DateUtils.addHours(new Date(), -30));  // because recently-created visits won't be closed
        visit.setStartDatetime(DateUtils.addDays(new Date(), -60));  // visit started 60 days ago (because ED visit logic extends to 30 days we need to start this long aga)
        visit.setPatient(patient);
        visit.setLocation(location);
        visit.setVisitType(emrApiProperties.getAtFacilityVisitType());

        // create a check-in at Ijans Location
        Encounter checkIn = new Encounter();
        checkIn.setPatient(patient);
        checkIn.setEncounterType(emrApiProperties.getCheckInEncounterType());
        checkIn.setEncounterDatetime(visit.getStartDatetime());
        checkIn.setLocation(locationService.getLocation("Ijans"));  // we've added this to the test dataset
        encounterService.saveEncounter(checkIn);

        // create another encounter, but more than 7 days ago
        Encounter consult = new Encounter();
        consult.setPatient(patient);
        consult.setEncounterType(emrApiProperties.getVisitNoteEncounterType());
        consult.setEncounterDatetime(DateUtils.addDays(new Date(), -8));
        encounterService.saveEncounter(consult);

        visit.addEncounter(checkIn);
        visit.addEncounter(consult);
        visitService.saveVisit(visit);

        VisitDomainWrapper activeVisit = adtService.getActiveVisit(patient, location);

        // sanity check
        assertNotNull(activeVisit);

        task.run();

        activeVisit = adtService.getActiveVisit(patient, location);
        assertNull(activeVisit);
    }

    @Test
    public void test_ED_shouldKeepVisitOpenIfItHasEDTriageAndMostRecentDispositionKeepsVisitOpenAndHasAnyEncounterWithinThePastThirtyDays() throws Exception {

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
        visit.setDateCreated(DateUtils.addHours(new Date(), -30));  // because recently-created visits won't be closed
        visit.setStartDatetime(DateUtils.addDays(new Date(), -60)); // visit started 60 days ago (because ED visit logic extends to 30 days we need to start this long aga)
        visit.setPatient(patient);
        visit.setLocation(location);
        visit.setVisitType(emrApiProperties.getAtFacilityVisitType());

        // create ED Triage encounterdmit to hospital
        Encounter edTriage = new Encounter();
        edTriage.setPatient(patient);
        edTriage.setEncounterType(encounterService.getEncounterTypeByUuid(ED_TRIAGE_ENCOUNTER_TYPE_UUID));
        edTriage.setEncounterDatetime(visit.getStartDatetime());
        encounterService.saveEncounter(edTriage);

        // create an encounter with a disposition obs
        Encounter encounter = new Encounter();
        encounter.setPatient(patient);
        encounter.setEncounterType(encounterService.getEncounterType(1));
        encounter.setEncounterDatetime(visit.getStartDatetime());

        Obs dispositionObsGroup = new Obs();
        dispositionObsGroup.setConcept(dispositionService.getDispositionDescriptor().getDispositionSetConcept());
        Obs dispositionObs = new Obs();
        dispositionObs.setConcept(dispositionService.getDispositionDescriptor().getDispositionConcept());
        dispositionObs.setValueCoded(emrConceptService.getConcept(EmrApiConstants.EMR_CONCEPT_SOURCE_NAME + ":ED Observation"));  // this fake code is set in ContextSensitiveMetadataTestUtils and this disposition keeps visits open
        dispositionObsGroup.addGroupMember(dispositionObs);

        encounter.addObs(dispositionObsGroup);
        encounterService.saveEncounter(encounter);

        visit.addEncounter(encounter);
        visitService.saveVisit(visit);

        // create a another encounter within 30 days
        Encounter consult = new Encounter();
        consult.setPatient(patient);
        consult.setEncounterType(emrApiProperties.getVisitNoteEncounterType());
        consult.setEncounterDatetime(DateUtils.addDays(new Date(), -28)); // twenty-eight days ago
        encounterService.saveEncounter(consult);

        visit.addEncounter(edTriage);
        visit.addEncounter(consult);
        visitService.saveVisit(visit);

        VisitDomainWrapper activeVisit = adtService.getActiveVisit(patient, location);

        // sanity check
        assertNotNull(activeVisit);

        task.run();

        activeVisit = adtService.getActiveVisit(patient, location);
        assertNotNull(activeVisit);
    }


    @Test
    public void test_ED_shouldNotKeepVisitOpenIfItHasEDTriageEvenIfMostRecentDispositionKeepsVisitOpenButNoEncounterWithinThePastThirtyDays() throws Exception {

        DispositionDescriptor descriptor=  ContextSensitiveMetadataTestUtils.setupDispositionDescriptor(conceptService, dispositionService);
        ContextSensitiveMetadataTestUtils.setupAdmissionDecisionConcept(conceptService, emrApiProperties);
        ContextSensitiveMetadataTestUtils.setupSupportsVisitLocationTag(locationService);

        List<Disposition> dispositions = dispositionService.getDispositions();

        Patient patient = patientService.getPatient(7);    // patient already has one visit in test dataset

        // need to tag the unknown location so we don't run into an error when testing against the existing visits in the test dataset
        Location unknownLocation = locationService.getLocation(1);
        unknownLocation.addTag(emrApiProperties.getSupportsVisitsLocationTag());
        locationService.saveLocation(unknownLocation);

        Location location = locationService.getLocation(2);
        location.addTag(emrApiProperties.getSupportsVisitsLocationTag());
        locationService.saveLocation(location);

        Visit visit = new Visit();
        visit.setDateCreated(DateUtils.addHours(new Date(), -30));  // because recently-created visits won't be closed
        visit.setStartDatetime(DateUtils.addDays(new Date(), -60));  // visit started 60 days ago (because ED visit logic extends to 30 days we need to start this long aga)
        visit.setPatient(patient);
        visit.setLocation(location);
        visit.setVisitType(emrApiProperties.getAtFacilityVisitType());

        // create ED Triage encounterdmit to hospital
        Encounter edTriage = new Encounter();
        edTriage.setPatient(patient);
        edTriage.setEncounterType(encounterService.getEncounterTypeByUuid(ED_TRIAGE_ENCOUNTER_TYPE_UUID));
        edTriage.setEncounterDatetime(visit.getStartDatetime());
        encounterService.saveEncounter(edTriage);

        // create an encounter with a disposition obs
        Encounter encounter = new Encounter();
        encounter.setPatient(patient);
        encounter.setEncounterType(encounterService.getEncounterType(1));
        encounter.setEncounterDatetime(visit.getStartDatetime());

        Obs dispositionObsGroup = new Obs();
        dispositionObsGroup.setConcept(dispositionService.getDispositionDescriptor().getDispositionSetConcept());
        Obs dispositionObs = new Obs();
        dispositionObs.setConcept(dispositionService.getDispositionDescriptor().getDispositionConcept());
        dispositionObs.setValueCoded(emrConceptService.getConcept(EmrApiConstants.EMR_CONCEPT_SOURCE_NAME + ":ED Observation"));   // this fake code is set in ContextSensitiveMetadataTestUtils and this disposition keeps visits open
        dispositionObsGroup.addGroupMember(dispositionObs);

        encounter.addObs(dispositionObsGroup);
        encounterService.saveEncounter(encounter);

        visitService.saveVisit(visit);

        // create a another encounter but 32 days ago
        Encounter consult = new Encounter();
        consult.setPatient(patient);
        consult.setEncounterType(emrApiProperties.getVisitNoteEncounterType());
        consult.setEncounterDatetime(DateUtils.addDays(new Date(), -32)); // twenty-eight days ago
        encounterService.saveEncounter(consult);

        visit.addEncounter(edTriage);
        visit.addEncounter(encounter);
        visit.addEncounter(consult);
        visitService.saveVisit(visit);

        VisitDomainWrapper activeVisit = adtService.getActiveVisit(patient, location);

        // sanity check
        assertNotNull(activeVisit);

        task.run();

        activeVisit = adtService.getActiveVisit(patient, location);
        assertNull(activeVisit);
    }

    @Test
    public void test_ED_shouldNotKeepVisitOpenIfItHasEDTriageAndEncounterWithinFortyEightHoursButMostRecentDispositionIsDischarge() throws Exception {

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
        visit.setDateCreated(DateUtils.addHours(new Date(), -30));  // because recently-created visits won't be closed
        visit.setStartDatetime(DateUtils.addHours(new Date(), -60));  // visit started 60 days ago (because ED visit logic extends to 30 days we need to start this long aga)
        visit.setPatient(patient);
        visit.setLocation(location);
        visit.setVisitType(emrApiProperties.getAtFacilityVisitType());

        // create ED Triage encounter
        Encounter edTriage = new Encounter();
        edTriage.setPatient(patient);
        edTriage.setEncounterType(encounterService.getEncounterTypeByUuid(ED_TRIAGE_ENCOUNTER_TYPE_UUID));
        edTriage.setEncounterDatetime(visit.getStartDatetime());
        encounterService.saveEncounter(edTriage);

        // create another encounter within 48 hours
        Encounter consult = new Encounter();
        consult.setPatient(patient);
        consult.setEncounterType(emrApiProperties.getVisitNoteEncounterType());
        consult.setEncounterDatetime(DateUtils.addHours(new Date(), -47));
        encounterService.saveEncounter(consult);

        // create an encounter with a disposition obs of "discharge"
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

        task.run();

        activeVisit = adtService.getActiveVisit(patient, location);
        assertNull(activeVisit);
    }


    @Test
    public void test_shouldCloseVisitIfItHasADischargeEncounterEvenIfDateCreatedAndDateChangedAreWithin12Hours() throws Exception {

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
        visit.setDateCreated(new Date());
        visit.setDateChanged(new Date());
        visit.setStartDatetime(DateUtils.addHours(new Date(), -12));  // twelve hourse aga
        visit.setPatient(patient);
        visit.setLocation(location);
        visit.setVisitType(emrApiProperties.getAtFacilityVisitType());

        // create an exit from care encounter
        Encounter exitFromCareEncounter = new Encounter();
        exitFromCareEncounter.setPatient(patient);
        exitFromCareEncounter.setEncounterType(emrApiProperties.getExitFromInpatientEncounterType());
        exitFromCareEncounter.setEncounterDatetime(visit.getStartDatetime());
        encounterService.saveEncounter(exitFromCareEncounter);

        visit.addEncounter(exitFromCareEncounter);
        visitService.saveVisit(visit);

        VisitDomainWrapper activeVisit = adtService.getActiveVisit(patient, location);

        // sanity check
        assertNotNull(activeVisit);

        task.run();

        activeVisit = adtService.getActiveVisit(patient, location);
        assertNull(activeVisit);
    }


    @Test
    public void test_shouldNotCloseVisitIfDateCreatedWithin12Hours() throws Exception {

        ContextSensitiveMetadataTestUtils.setupDispositionDescriptor(conceptService, dispositionService);
        ContextSensitiveMetadataTestUtils.setupAdmissionDecisionConcept(conceptService, emrApiProperties);
        ContextSensitiveMetadataTestUtils.setupSupportsVisitLocationTag(locationService);

        Location location = locationService.getLocation(1);
        location.addTag(emrApiProperties.getSupportsVisitsLocationTag());
        locationService.saveLocation(location);

        // set the date created on this visit to now
        Visit visit = visitService.getVisit(1);
        visit.setDateCreated(new Date());

        // sanity check
        assertThat(visitService.getVisit(1).getStopDatetime(), nullValue());

        task.run();

        // this visit would have been closed, except that we updated the date created
        assertThat(visitService.getVisit(1).getStopDatetime(), nullValue());

    }

    @Test
    public void test_shouldNotCloseVisitIfDateChangedWithin12Hours() throws Exception {

        ContextSensitiveMetadataTestUtils.setupDispositionDescriptor(conceptService, dispositionService);
        ContextSensitiveMetadataTestUtils.setupAdmissionDecisionConcept(conceptService, emrApiProperties);
        ContextSensitiveMetadataTestUtils.setupSupportsVisitLocationTag(locationService);

        Location location = locationService.getLocation(1);
        location.addTag(emrApiProperties.getSupportsVisitsLocationTag());
        locationService.saveLocation(location);

        // set the date changed on this visit to now
        Visit visit = visitService.getVisit(1);
        visit.setDateChanged(new Date());

        // sanity check
        assertThat(visitService.getVisit(1).getStopDatetime(), nullValue());

        task.run();

        // this visit would have been closed, except that we updated the date changed
        assertThat(visitService.getVisit(1).getStopDatetime(), nullValue());

    }

    @Test
    public void getLatestDateWithinVisit_shouldReturnVisitStartDateIfNoEncountersOrQueueEntries() throws Exception {
        Visit visit = visitService.getVisit(1);
        assertThat(visit.getStopDatetime(), nullValue());
        assertEquals(visit.getEncounters().size(), 0);
        Date latestDate = task.getLatestDateWithinVisit(visit);
        assertEquals(latestDate, visit.getStartDatetime());
    }

    @Test
    public void getLatestDateWithinVisit_shouldReturnEncounterDateIfLatest() throws Exception {
        Visit visit = visitService.getVisit(1);
        Encounter e1 = new Encounter();
        e1.setEncounterDatetime(DateUtils.addHours(visit.getStartDatetime(), 3));
        visit.addEncounter(e1);
        Encounter e2 = new Encounter();
        e2.setEncounterDatetime(DateUtils.addHours(visit.getStartDatetime(), 1));
        visit.addEncounter(e2);
        assertEquals(visit.getEncounters().size(), 2);
        Date latestDate = task.getLatestDateWithinVisit(visit);
        assertEquals(latestDate, e1.getEncounterDatetime());
    }

    @Test
    public void getLatestDateWithinVisit_shouldReturnQueueEntryStartDatesIfLatest() throws Exception {
        Visit visit = visitService.getVisit(1);
        QueueEntry qe1 = new QueueEntry();
        qe1.setQueue(queueService.getQueueById(1).get());
        qe1.setPatient(visit.getPatient());
        qe1.setVisit(visit);
        qe1.setPriority(conceptService.getConcept(1001));
        qe1.setStatus(conceptService.getConcept(3001));
        qe1.setStartedAt(DateUtils.addHours(visit.getStartDatetime(), 1));
        queueEntryService.saveQueueEntry(qe1);

        Date latestDate = task.getLatestDateWithinVisit(visit);
        assertEquals(latestDate, qe1.getStartedAt());
    }

    @Test
    public void getLatestDateWithinVisit_shouldReturnQueueEntryEndDatesIfLatest() throws Exception {
        Visit visit = visitService.getVisit(1);
        QueueEntry qe1 = new QueueEntry();
        qe1.setQueue(queueService.getQueueById(1).get());
        qe1.setPatient(visit.getPatient());
        qe1.setVisit(visit);
        qe1.setPriority(conceptService.getConcept(1001));
        qe1.setStatus(conceptService.getConcept(3001));
        qe1.setStartedAt(DateUtils.addHours(visit.getStartDatetime(), 1));
        qe1.setEndedAt(DateUtils.addHours(visit.getStartDatetime(), 2));
        queueEntryService.saveQueueEntry(qe1);

        Date latestDate = task.getLatestDateWithinVisit(visit);
        assertEquals(latestDate, qe1.getEndedAt());
    }
}
