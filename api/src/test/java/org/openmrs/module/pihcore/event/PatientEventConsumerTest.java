package org.openmrs.module.pihcore.event;

import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang.BooleanUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.module.dbevent.Database;
import org.openmrs.module.dbevent.DbEvent;
import org.openmrs.module.dbevent.DbEventLog;
import org.openmrs.module.dbevent.DbEventSource;
import org.openmrs.module.dbevent.DbEventStatus;
import org.openmrs.module.dbevent.EventContext;
import org.openmrs.module.dbevent.test.TestEventContext;
import org.openmrs.module.pihcore.setup.DbEventSetup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PatientEventConsumerTest {

    private static final Logger log = LoggerFactory.getLogger(PatientEventConsumerTest.class);

    static DbEvent lastEvent = null;
    static GenericContainer<?> container = null;
    static EventContext ctx = null;
    static Database db = null;
    static DbEventSource eventSource = null;
    static Date dateCreated = null;
    static Date dateChanged = null;
    static DataBuilder data = null;

    static Date now = new Date();
    static Date visitDate = date("2022-07-13");

    @BeforeAll
    public static void setupDb() throws Exception {
        container = new GenericContainer<>(new ImageFromDockerfile()
                .withFileFromClasspath("Dockerfile", "event/Dockerfile")
                .withFileFromClasspath("my.cnf", "event/my.cnf")
                .withFileFromClasspath("openmrs.sql", "event/openmrs.sql")
        ).withExposedPorts(3306).withLogConsumer(new Slf4jLogConsumer(log));
        container.start();
        ctx = new TestEventContext(getConnectionProperties());
        db = ctx.getDatabase();
        eventSource = DbEventSetup.getEventSource(ctx);
        dateCreated = datetime("2021-10-25 14:23:42.123");
        dateChanged = datetime("2022-07-19 08:12:33.789");
        data = new DataBuilder(db, dateCreated);
    }

    @AfterAll
    public static void teardown() {
        container.stop();
    }

    @BeforeEach
    public void startSource() throws Exception {
        eventSource.reset();
        eventSource.start();
        waitForSnapshotToComplete(eventSource);
    }

    @AfterEach
    public void stopSource() {
        eventSource.stop();
        eventSource.reset();
    }

    public static Properties getConnectionProperties() {
        Properties p = new Properties();
        p.setProperty("connection.username", "root");
        p.setProperty("connection.password", "test");
        p.setProperty("connection.url", "jdbc:mysql://" + container.getHost() + ":" + container.getMappedPort(3306) + "/openmrs");
        return p;
    }

    @Test
    public void shouldTrackPatientChanges() throws Exception {

        // Test initial snapshot
        List<Integer> voidedPatients = db.executeQuery("select patient_id from patient where voided = 1", new ColumnListHandler<>());
        List<Map<String, Object>> snapshotPatients = db.executeQuery("select * from dbevent_patient", new MapListHandler());
        System.out.println("Found " + snapshotPatients.size() + " patients to test in initial snapshot");
        assertFalse(snapshotPatients.isEmpty());
        for (Map<String, Object> row : snapshotPatients) {
            Integer patientId = (Integer)row.get("patient_id");
            LocalDateTime lastUpdated = (LocalDateTime) row.get("last_updated");
            Boolean deleted = (Boolean) row.get("deleted");
            assertNotNull(patientId);
            assertNotNull(lastUpdated);
            assertThat(voidedPatients.contains(patientId), equalTo(BooleanUtils.isTrue(deleted)));
        }
        System.out.println("Successfully tested " + snapshotPatients.size() + " patients to test in initial snapshot");

        Integer pId = data.insertPatient("M", date("1982-10-19"));
        assertLastEvent(pId, "patient", false);

        data.insertPersonName(pId, "TestFirst", "TestLast");
        assertLastEvent(pId, "person_name", false);

        data.insertPersonAddress(pId, "My Home Address");
        assertLastEvent(pId, "person_address", false);

        data.insertPersonAttribute(pId,8, "5555-4433");
        assertLastEvent(pId, "person_attribute", false);

        Integer otherPersonId = data.insertPerson("F", date("2022-01-06"));
        data.insertRelationshipToPersonB(pId,6, otherPersonId);
        assertLastEvent(pId, "relationship", false);

        data.insertRelationshipToPersonA(pId,6, otherPersonId);
        assertLastEvent(pId, "relationship", false);

        data.insertPersonMergeLogAsWinner(pId, otherPersonId);
        assertLastEvent(pId, "person_merge_log", false);

        data.insertPersonMergeLogAsLoser(pId, otherPersonId);
        assertLastEvent(pId, "person_merge_log", false);

        data.insertPatientIdentifier(pId,4,"ABC123", 1);
        assertLastEvent(pId, "patient_identifier", false);

        Integer allergyId = data.insertAllergy(pId,"Penicillin","DRUG");
        assertLastEvent(pId, "allergy", false);

        data.insertAllergyReaction(allergyId,"Unknown");
        assertLastEvent(pId, "allergy_reaction", false);

        data.insertCondition(pId,"Confused", "ACTIVE");
        assertLastEvent(pId, "conditions", false);

        Integer patientProgramId = data.insertPatientProgram(pId, "HIV", visitDate);
        assertLastEvent(pId, "patient_program", false);

        data.insertPatientState(patientProgramId, "872c529c-a5a4-47c7-9584-bd15fa5bb0a9", visitDate);
        assertLastEvent(pId, "patient_state", false);

        Integer programAttType = data.insertAttributeType("program", "Comments");
        data.insertPatientProgramAttribute(patientProgramId, programAttType, "Extra Program Info");
        assertLastEvent(pId, "patient_program_attribute", false);

        Integer visitId = data.insertVisit(pId, 1, visitDate, 1);
        assertLastEvent(pId, "visit", false);

        Integer visitAttType = data.insertAttributeType("visit", "Comments");
        data.insertVisitAttribute(visitId, visitAttType, "Last Minute Visit");
        assertLastEvent(pId, "visit_attribute", false);

        Integer encounterId = data.insertEncounter(pId, 2, visitDate, 1);
        assertLastEvent(pId, "encounter", false);

        data.insertEncounterProvider(encounterId, 1, 6);
        assertLastEvent(pId, "encounter_provider", false);

        Integer diagnosisId = data.insertEncounterDiagnosis(encounterId, "Pneumonia", "PROVISIONAL");
        assertLastEvent(pId, "encounter_diagnosis", false);

        Integer diagnosisAttType = data.insertAttributeType("diagnosis", "Comments");
        data.insertDiagnosisAttribute(diagnosisId, diagnosisAttType, "Severe");
        assertLastEvent(pId, "diagnosis_attribute", false);

        Integer obsId = data.insertObs(encounterId, visitDate, 1, "Weight (kg)", 80d);
        assertLastEvent(pId, "obs", false);

        data.insertNote(pId, null, null, "Patient Note");
        assertLastEvent(pId, "note", false);

        data.insertNote(null, encounterId, null, "Encounter Note");
        assertLastEvent(pId, "note", false);

        data.insertNote(null, null, obsId, "Obs Note");
        assertLastEvent(pId, "note", false);

        Integer drugOrderId = data.insertOrder(encounterId, 2, "Aspirin", visitDate);
        assertLastEvent(pId, "orders", false);

        data.insertDrugOrder(drugOrderId, 1);
        assertLastEvent(pId, "drug_order", false);

        Integer referralOrderId = data.insertOrder(encounterId, 4, "Biopsy", visitDate);
        assertLastEvent(pId, "orders", false);

        data.insertReferralOrder(referralOrderId);
        assertLastEvent(pId, "referral_order", false);

        Integer radiologyOrderId = data.insertOrder(encounterId, 5, "Chest, 2 views (X-ray)", visitDate);
        assertLastEvent(pId, "orders", false);

        data.insertTestOrder(radiologyOrderId);
        assertLastEvent(pId, "test_order", false);

        data.insertRadiologyOrder(radiologyOrderId);
        assertLastEvent(pId, "emr_radiology_order", false);

        Integer orderAttType = data.insertAttributeType("order", "Comments");
        data.insertOrderAttribute(drugOrderId, orderAttType, "Additional comment");
        assertLastEvent(pId, "order_attribute", false);

        Integer orderGroupId = data.insertOrderGroup(encounterId);
        assertLastEvent(pId, "order_group", false);

        Integer orderGroupAttType = data.insertAttributeType("order_group", "Comments");
        data.insertOrderGroupAttribute(orderGroupId, orderGroupAttType, "Additional comment");
        assertLastEvent(pId, "order_group_attribute", false);

        Integer cohortId = data.insertCohort("Test Cohort");
        data.insertCohortMember(pId, cohortId);
        assertLastEvent(pId, "cohort_member", false);

        Integer apptType = data.insertAppointmentType("Test Appointment", 60);
        Integer apptBlock = data.insertAppointmentBlock(visitDate, now, 1);
        Integer apptSlot = data.insertAppointmentTimeSlot(apptBlock, visitDate, now);

        Integer appointmentId = data.insertAppointment(pId, apptSlot, apptType, "SCHEDULED");
        assertLastEvent(pId, "appointmentscheduling_appointment", false);

        Integer appointmentStatusId = data.insertAppointmentStatusHistory(appointmentId, "CANCELLED", now, now);
        assertLastEvent(pId, "appointmentscheduling_appointment_status_history", false);

        Integer appointmentRequestId = data.insertAppointmentRequest(pId, apptType, now, "REQUESTED");
        assertLastEvent(pId, "appointmentscheduling_appointment_request", false);

        Integer fhirDiagnosticReportId = data.insertFhirDiagnosticReport(pId, null, "PATIENT");
        assertLastEvent(pId, "fhir_diagnostic_report", false);

        data.insertFhirDiagnosticReportPerformer(fhirDiagnosticReportId, 1);
        assertLastEvent(pId, "fhir_diagnostic_report_performers", false);

        data.insertFhirDiagnosticReport(null, encounterId, "ENCOUNTER");
        assertLastEvent(pId, "fhir_diagnostic_report", false);

        Integer fhirDiagnosticReportWithObs = data.insertFhirDiagnosticReport(null, null, "OBS");
        Integer diagnosticObs = data.insertObs(encounterId, visitDate, 1, "Height (cm)", 100d);
        data.insertFhirDiagnosticReportResults(fhirDiagnosticReportWithObs, diagnosticObs);
        assertLastEvent(pId, "fhir_diagnostic_report_results", false);

        // Test streaming updates

        testUpdate(pId, "person", "update person set gender = 'F' where person_id = ?", pId);
        testUpdate(pId, "patient", "update patient set allergy_status = 'None' where patient_id = ?", pId);
        testUpdate(pId, "person_name", "update person_name set middle_name = 'Q' where person_id = ?", pId);
        testUpdate(pId, "person_address", "update person_address set address1 = '11 Main Street' where person_id = ?", pId);
        testUpdate(pId, "person_attribute", "update person_attribute set value = '1234-5678' where person_id = ?", pId);
        testUpdate(pId, "relationship", "update relationship set relationship = 2 where person_a = ?", pId);
        testUpdate(pId, "relationship", "update relationship set relationship = 2 where person_b = ?", pId);
        testUpdate(pId, "person_merge_log", "update person_merge_log set merged_data = 'Updated winner' where winner_person_id = ?", pId);
        testUpdate(pId, "person_merge_log", "update person_merge_log set merged_data = 'Updated loser' where loser_person_id = ?", pId);
        testUpdate(pId, "patient_identifier", "update patient_identifier set identifier = 'XYZ456' where patient_id = ?", pId);
        testUpdate(pId, "allergy", "update allergy set comments = 'Mild' where patient_id = ?", pId);
        testUpdate(pId, "allergy_reaction", "update allergy_reaction set reaction_concept_id = ? where allergy_id = ?", conceptId( "Hives"), allergyId);
        testUpdate(pId, "conditions", "update conditions set condition_non_coded = 'Very confused' where patient_id = ?", pId);
        testUpdate(pId, "patient_program", "update patient_program set location_id = 1 where patient_program_id = ?", patientProgramId);
        testUpdate(pId, "patient_state", "update patient_state set end_date = ? where patient_program_id = ?", visitDate, patientProgramId);
        testUpdate(pId, "patient_program_attribute", "update patient_program_attribute set value_reference = 'Extra Info' where patient_program_id = ?", patientProgramId);
        testUpdate(pId, "visit", "update visit set date_stopped = ? where patient_id = ?", date("2022-07-15"), pId);
        testUpdate(pId, "visit_attribute", "update visit_attribute set value_reference = 'Upcoming' where visit_id = ?", visitId);
        testUpdate(pId, "encounter", "update encounter set location_id = 42 where encounter_id = ?", encounterId);
        testUpdate(pId, "encounter_provider", "update encounter_provider set encounter_role_id = 3 where encounter_id = ?", encounterId);
        testUpdate(pId, "encounter_diagnosis", "update encounter_diagnosis set certainty = 'CONFIRMED' where diagnosis_id = ?", diagnosisId);
        testUpdate(pId, "diagnosis_attribute", "update diagnosis_attribute set value_reference = 'MODERATE' where diagnosis_id = ?", diagnosisId);
        testUpdate(pId, "obs", "update obs set value_numeric = 85 where obs_id = ?", obsId);
        testUpdate(pId, "note", "update note set text = 'Updated note' where patient_id = ?", pId);
        testUpdate(pId, "note", "update note set text = 'Updated note' where encounter_id = ?", encounterId);
        testUpdate(pId, "note", "update note set text = 'Updated note' where obs_id = ?", obsId);
        testUpdate(pId, "orders", "update orders set instructions = 'As directed' where order_id = ?", drugOrderId);
        testUpdate(pId, "drug_order", "update drug_order set as_needed = 1 where order_id = ?", drugOrderId);
        testUpdate(pId, "referral_order", "update referral_order set clinical_history = 'Never' where order_id = ?", referralOrderId);
        testUpdate(pId, "test_order", "update test_order set clinical_history = 'None' where order_id = ?", radiologyOrderId);
        testUpdate(pId, "emr_radiology_order", "update emr_radiology_order set exam_location = 1 where order_id = ?", radiologyOrderId);
        testUpdate(pId, "order_attribute", "update order_attribute set value_reference = 'Modified comment' where order_id = ?", drugOrderId);
        testUpdate(pId, "order_group", "update order_group set order_group_reason = ? where order_group_id = ?", conceptId( "Asthma"), orderGroupId);
        testUpdate(pId, "order_group_attribute", "update order_group_attribute set value_reference = 'Modified comment' where order_group_id = ?", orderGroupId);
        testUpdate(pId, "cohort_member", "update cohort_member set start_date = now() where patient_id = ?", pId);
        testUpdate(pId, "appointmentscheduling_appointment", "update appointmentscheduling_appointment set status = 'RESCHEDULED' where appointment_id = ?", appointmentId);
        testUpdate(pId, "appointmentscheduling_appointment_status_history", "update appointmentscheduling_appointment_status_history set start_date = ? where appointment_status_history_id = ?", visitDate, appointmentStatusId);
        testUpdate(pId, "appointmentscheduling_appointment_request", "update appointmentscheduling_appointment_request set status = 'ON HOLD' where appointment_request_id = ?", appointmentRequestId);
        testUpdate(pId, "fhir_diagnostic_report", "update fhir_diagnostic_report set status = 'UPDATED' where diagnostic_report_id = ?", fhirDiagnosticReportId);
        testUpdate(pId, "fhir_diagnostic_report_performers", "update fhir_diagnostic_report_performers set provider_id = 2 where diagnostic_report_id = ?", fhirDiagnosticReportId);
        testUpdate(pId, "fhir_diagnostic_report_results", "update fhir_diagnostic_report_results set obs_id = ? where diagnostic_report_id = ?", obsId, fhirDiagnosticReportWithObs);
    }

    public void assertLastEvent(Integer patientId, String table, boolean expectedDeleted) throws Exception {
        DbEventStatus status = waitForNextEvent(eventSource);
        List<Map<String, Object>> results = db.executeQuery("select * from dbevent_patient where patient_id = ?", new MapListHandler(), patientId);
        assertThat(results.size(), equalTo(1));
        assertTrue(status.isProcessed());
        assertThat(status.getEvent().getTable(), equalTo(table));
        assertThat(results.get(0).get("patient_id"), equalTo(patientId));
        assertThat(results.get(0).get("last_updated"), equalTo(localDateTime(status.getEvent().getTimestamp())));
        assertThat(results.get(0).get("deleted"), equalTo(expectedDeleted));
    }

    public void testUpdate(Integer patientId, String table, String statement, Object... args) throws Exception {
        db.executeUpdate(statement, args);
        assertLastEvent(patientId, table, false);
    }

    protected static void waitForSnapshotToComplete(DbEventSource eventSource) throws Exception {
        PatientUpdateEventConsumer consumer = (PatientUpdateEventConsumer) eventSource.getEventConsumer();
        while (!consumer.isSnapshotInitialized()) {
            TimeUnit.SECONDS.sleep(1);
        }
    }

    protected static DbEventStatus waitForNextEvent(DbEventSource eventSource) throws Exception {
        DbEventStatus status = DbEventLog.getLatestEventStatus(eventSource.getConfig().getSourceName());
        int numIterations = 0;
        while (numIterations++ < 10 && (status == null || (status.getEvent().equals(lastEvent) || !status.isProcessed()))) {
            TimeUnit.MILLISECONDS.sleep(100);
            status = DbEventLog.getLatestEventStatus(eventSource.getConfig().getSourceName());
        }
        lastEvent = status.getEvent();
        return status;
    }

    private static LocalDateTime localDateTime(String datetime) throws Exception {
        Date lastUpdatedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(datetime);
        return localDateTime(lastUpdatedDate.getTime());
    }

    private static Date datetime(String datetime) throws Exception {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(datetime);
    }

    private static LocalDateTime localDateTime(long millis) {
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private static Date date(String dateStr) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String uuid() {
        return UUID.randomUUID().toString();
    }

    private static Integer conceptId(String name) {
        return db.executeQuery("select concept_id from concept_name where name = ?", new ScalarHandler<>(), name);
    }
}
