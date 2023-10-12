package org.openmrs.module.pihcore.event;

import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang.BooleanUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openmrs.module.dbevent.Database;
import org.openmrs.module.dbevent.DatabaseQuery;
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
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the PatientUpdateEventConsumer
 * Periodically, the underlying database used for this test should be updated to reflect the latest distribution,
 * to make sure all tables are being appropriately captured and tested.
 * To do this, create a new SDK instance, add in a single patient with data that covers as many domains as possible, and export
 * The resulting sql file should replace the one in resources/event/openmrs.sql
 */
@Disabled
public class PatientUpdateEventConsumerIT {

    private static final Logger log = LoggerFactory.getLogger(PatientUpdateEventConsumerIT.class);

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

    public static Properties getConnectionProperties() {
        Properties p = new Properties();
        p.setProperty("connection.username", "root");
        p.setProperty("connection.password", "test");
        p.setProperty("connection.url", "jdbc:mysql://" + container.getHost() + ":" + container.getMappedPort(3306) + "/openmrs");
        return p;
    }

    @Test
    public void shouldGetSnapshotStatements() {
        PatientUpdateEventConsumer consumer = (PatientUpdateEventConsumer) eventSource.getEventConsumer();
        Map<String, List<String>> snapshotStatements = consumer.getSnapshotStatements();
        int totalNum = 0;
        for (List<String> statements : snapshotStatements.values()) {
            totalNum += statements.size();
        }
        assertThat(totalNum, equalTo(41));
        assertSnapshot(snapshotStatements, "patient", 0, "date_created", "date_changed", "date_voided");
        assertSnapshot(snapshotStatements, "person", 0, "p.last_updated", "date_created", "date_changed", "date_voided");
        assertSnapshot(snapshotStatements, "allergy", 0, "p.last_updated", "date_created", "date_changed", "date_voided");
        assertSnapshot(snapshotStatements, "appointmentscheduling_appointment", 0, "p.last_updated", "date_created", "date_changed", "date_voided");
        assertSnapshot(snapshotStatements, "appointmentscheduling_appointment_request", 0, "p.last_updated", "date_created", "date_changed", "date_voided");
        assertSnapshot(snapshotStatements, "cohort_member", 0, "p.last_updated", "date_created", "date_voided"); // No date_changed
        assertSnapshot(snapshotStatements, "concept_proposal", 0, "encounter_id", "p.last_updated", "date_created", "date_changed");  // No date_voided
        assertSnapshot(snapshotStatements, "concept_proposal", 1, "obs_id", "p.last_updated", "date_created", "date_changed"); // No date_voided
        assertSnapshot(snapshotStatements, "conditions", 0, "p.last_updated", "date_created", "date_changed", "date_voided");
        assertSnapshot(snapshotStatements, "diagnosis_attribute", 0, "p.last_updated", "date_created", "date_changed", "date_voided");
        assertSnapshot(snapshotStatements, "encounter", 0, "p.last_updated", "date_created", "date_changed", "date_voided");
        assertSnapshot(snapshotStatements, "encounter_diagnosis", 0, "p.last_updated", "date_created", "date_changed", "date_voided");
        assertSnapshot(snapshotStatements, "encounter_provider", 0, "p.last_updated", "date_created", "date_changed", "date_voided");
        assertSnapshot(snapshotStatements, "fhir_diagnostic_report", 0, "subject_id", "p.last_updated", "date_created", "date_changed", "date_voided");
        assertSnapshot(snapshotStatements, "fhir_diagnostic_report", 1, "encounter_id", "p.last_updated", "date_created", "date_changed", "date_voided");
        assertSnapshot(snapshotStatements, "note", 0, "patient_id", "p.last_updated", "date_created", "date_changed"); // No date_voided
        assertSnapshot(snapshotStatements, "note", 1, "obs_id", "p.last_updated", "date_created", "date_changed"); // No date_voided
        assertSnapshot(snapshotStatements, "note", 2, "encounter_id", "p.last_updated", "date_created", "date_changed"); // No date_voided
        assertSnapshot(snapshotStatements, "obs", 0, "p.last_updated", "date_created", "date_voided"); // No date_changed
        assertSnapshot(snapshotStatements, "order_attribute", 0, "p.last_updated", "date_created", "date_changed", "date_voided");
        assertSnapshot(snapshotStatements, "order_group", 0, "p.last_updated", "date_created", "date_changed", "date_voided");
        assertSnapshot(snapshotStatements, "order_group_attribute", 0, "p.last_updated", "date_created", "date_changed", "date_voided");
        assertSnapshot(snapshotStatements, "orders", 0, "p.last_updated", "date_created", "date_voided"); // No date_changed
        assertSnapshot(snapshotStatements, "paperrecord_paper_record", 0, "p.last_updated", "date_created", "date_status_changed"); // Non-standard
        assertSnapshot(snapshotStatements, "paperrecord_paper_record_merge_request", 0,  "preferred_paper_record", "p.last_updated", "date_created"); // Non-standard
        assertSnapshot(snapshotStatements, "paperrecord_paper_record_merge_request", 1, "not_preferred_paper_record", "p.last_updated", "date_created"); // Non-standard
        assertSnapshot(snapshotStatements, "paperrecord_paper_record_request", 0, "assignee", "p.last_updated", "date_created", "date_status_changed"); // Non-standard
        assertSnapshot(snapshotStatements, "paperrecord_paper_record_request", 1, "patient_identifier_id", "p.last_updated", "date_created", "date_status_changed"); // Non-standard
        assertSnapshot(snapshotStatements, "patient_identifier", 0, "p.last_updated", "date_created", "date_changed", "date_voided");
        assertSnapshot(snapshotStatements, "patient_program", 0, "p.last_updated", "date_created", "date_changed", "date_voided");
        assertSnapshot(snapshotStatements, "patient_program_attribute", 0, "p.last_updated", "date_created", "date_changed", "date_voided");
        assertSnapshot(snapshotStatements, "patient_state", 0, "p.last_updated", "date_created", "date_changed", "date_voided");
        assertSnapshot(snapshotStatements, "person_address", 0, "p.last_updated", "date_created", "date_changed", "date_voided");
        assertSnapshot(snapshotStatements, "person_attribute", 0, "p.last_updated", "date_created", "date_changed", "date_voided");
        assertSnapshot(snapshotStatements, "person_merge_log", 0, "winner_person_id", "p.last_updated", "date_created", "date_changed", "date_voided");
        assertSnapshot(snapshotStatements, "person_merge_log", 1, "loser_person_id", "p.last_updated", "date_created", "date_changed", "date_voided");
        assertSnapshot(snapshotStatements, "person_name", 0, "p.last_updated", "date_created", "date_changed", "date_voided");
        assertSnapshot(snapshotStatements, "relationship", 0, "person_a", "p.last_updated", "date_created", "date_changed", "date_voided");
        assertSnapshot(snapshotStatements, "relationship", 1, "person_b", "p.last_updated", "date_created", "date_changed", "date_voided");
        assertSnapshot(snapshotStatements, "visit", 0, "p.last_updated", "date_created", "date_changed", "date_voided");
        assertSnapshot(snapshotStatements, "visit_attribute", 0, "p.last_updated", "date_created", "date_changed", "date_voided");
    }

    private void assertSnapshot(Map<String, List<String>> snapshots, String table, int index, String... textToMatch) {
        List<String> statements = snapshots.get(table);
        assertNotNull(statements);
        assertTrue(index < statements.size());
        String statement = statements.get(index);
        if (table.equals("patient")) {
            assertTrue(statement.contains("insert ignore into dbevent_patient"));
        }
        else {
            assertTrue(statement.contains("update dbevent_patient"));
            assertTrue(statement.contains("inner join " + table + " " + table));
        }
        for (String m : textToMatch) {
            assertTrue(statement.contains(m));
        }
    }

    @Test
    public void shouldGetStreamingQueries() {
        PatientUpdateEventConsumer consumer = (PatientUpdateEventConsumer) eventSource.getEventConsumer();
        Map<String, List<DatabaseQuery>> patientQueries = consumer.getPatientQueries();
        int totalNum = 0;
        for (List<DatabaseQuery> queries : patientQueries.values()) {
            totalNum += queries.size();
        }
        String basePatientIdQuery = "select p.patient_id from patient p where p.patient_id = ?";
        String patientIdColumn = "patient_id";
        String personIdColumn = "person_id";
        String encounterIdColumn = "encounter_id";
        String orderIdColumn = "order_id";

        assertThat(totalNum, equalTo(53));
        assertQuery(patientQueries, "address_hierarchy_address_to_entry_map", 0, "select p.patient_id from patient p inner join person_address person_address on person_address.person_id = p.patient_id where person_address.person_address_id = ?", "address_id");
        assertQuery(patientQueries, "allergy", 0, basePatientIdQuery, patientIdColumn);
        assertQuery(patientQueries, "allergy_reaction", 0, "select p.patient_id from patient p inner join allergy allergy on allergy.patient_id = p.patient_id where allergy.allergy_id = ?", "allergy_id");
        assertQuery(patientQueries, "appointmentscheduling_appointment", 0, basePatientIdQuery, patientIdColumn);
        assertQuery(patientQueries, "appointmentscheduling_appointment_request", 0, basePatientIdQuery, patientIdColumn);
        assertQuery(patientQueries, "appointmentscheduling_appointment_status_history", 0, "select p.patient_id from patient p inner join appointmentscheduling_appointment appointmentscheduling_appointment on appointmentscheduling_appointment.patient_id = p.patient_id where appointmentscheduling_appointment.appointment_id = ?", "appointment_id");
        assertQuery(patientQueries, "cohort_member", 0, basePatientIdQuery, patientIdColumn);
        assertQuery(patientQueries, "concept_proposal", 0, "select p.patient_id from patient p inner join encounter encounter on encounter.patient_id = p.patient_id where encounter.encounter_id = ?", encounterIdColumn);
        assertQuery(patientQueries, "concept_proposal", 1, "select p.patient_id from patient p inner join obs obs on obs.person_id = p.patient_id where obs.obs_id = ?", "obs_id");
        assertQuery(patientQueries, "concept_proposal_tag_map", 0, "select p.patient_id from patient p inner join encounter encounter on encounter.patient_id = p.patient_id inner join concept_proposal concept_proposal on concept_proposal.encounter_id = encounter.encounter_id where concept_proposal.concept_proposal_id = ?", "concept_proposal_id");
        assertQuery(patientQueries, "concept_proposal_tag_map", 1, "select p.patient_id from patient p inner join obs obs on obs.person_id = p.patient_id inner join concept_proposal concept_proposal on concept_proposal.obs_id = obs.obs_id where concept_proposal.concept_proposal_id = ?", "concept_proposal_id");
        assertQuery(patientQueries, "conditions", 0, basePatientIdQuery, patientIdColumn);
        assertQuery(patientQueries, "diagnosis_attribute", 0, "select p.patient_id from patient p inner join encounter_diagnosis encounter_diagnosis on encounter_diagnosis.patient_id = p.patient_id where encounter_diagnosis.diagnosis_id = ?", "diagnosis_id");
        assertQuery(patientQueries, "drug_order", 0, "select p.patient_id from patient p inner join orders orders on orders.patient_id = p.patient_id where orders.order_id = ?", orderIdColumn);
        assertQuery(patientQueries, "emr_radiology_order", 0, "select p.patient_id from patient p inner join orders orders on orders.patient_id = p.patient_id inner join test_order test_order on test_order.order_id = orders.order_id where test_order.order_id = ?", orderIdColumn);
        assertQuery(patientQueries, "encounter", 0, basePatientIdQuery, patientIdColumn);
        assertQuery(patientQueries, "encounter_diagnosis", 0, basePatientIdQuery, patientIdColumn);
        assertQuery(patientQueries, "encounter_provider", 0, "select p.patient_id from patient p inner join encounter encounter on encounter.patient_id = p.patient_id where encounter.encounter_id = ?", encounterIdColumn);
        assertQuery(patientQueries, "fhir_diagnostic_report", 0, basePatientIdQuery, "subject_id");
        assertQuery(patientQueries, "fhir_diagnostic_report", 1, "select p.patient_id from patient p inner join encounter encounter on encounter.patient_id = p.patient_id where encounter.encounter_id = ?", encounterIdColumn);
        assertQuery(patientQueries, "fhir_diagnostic_report_performers", 0, "select p.patient_id from patient p inner join fhir_diagnostic_report fhir_diagnostic_report on fhir_diagnostic_report.subject_id = p.patient_id where fhir_diagnostic_report.diagnostic_report_id = ?", "diagnostic_report_id");
        assertQuery(patientQueries, "fhir_diagnostic_report_performers", 1, "select p.patient_id from patient p inner join encounter encounter on encounter.patient_id = p.patient_id inner join fhir_diagnostic_report fhir_diagnostic_report on fhir_diagnostic_report.encounter_id = encounter.encounter_id where fhir_diagnostic_report.diagnostic_report_id = ?", "diagnostic_report_id");
        assertQuery(patientQueries, "fhir_diagnostic_report_results", 0, "select p.patient_id from patient p inner join obs obs on obs.person_id = p.patient_id where obs.obs_id = ?", "obs_id");
        assertQuery(patientQueries, "name_phonetics", 0, "select p.patient_id from patient p inner join person_name person_name on person_name.person_id = p.patient_id where person_name.person_name_id = ?", "person_name_id");
        assertQuery(patientQueries, "note", 0, basePatientIdQuery, patientIdColumn);
        assertQuery(patientQueries, "note", 1, "select p.patient_id from patient p inner join obs obs on obs.person_id = p.patient_id where obs.obs_id = ?", "obs_id");
        assertQuery(patientQueries, "note", 2, "select p.patient_id from patient p inner join encounter encounter on encounter.patient_id = p.patient_id where encounter.encounter_id = ?", encounterIdColumn);
        assertQuery(patientQueries, "obs", 0, basePatientIdQuery, personIdColumn);
        assertQuery(patientQueries, "order_attribute", 0, "select p.patient_id from patient p inner join orders orders on orders.patient_id = p.patient_id where orders.order_id = ?", orderIdColumn);
        assertQuery(patientQueries, "order_group", 0, basePatientIdQuery, patientIdColumn);
        assertQuery(patientQueries, "order_group_attribute", 0, "select p.patient_id from patient p inner join order_group order_group on order_group.patient_id = p.patient_id where order_group.order_group_id = ?", "order_group_id");
        assertQuery(patientQueries, "orders", 0, basePatientIdQuery, patientIdColumn);
        assertQuery(patientQueries, "paperrecord_paper_record", 0, "select p.patient_id from patient p inner join patient_identifier patient_identifier on patient_identifier.patient_id = p.patient_id where patient_identifier.patient_identifier_id = ?", "patient_identifier");
        assertQuery(patientQueries, "paperrecord_paper_record_merge_request", 0, "select p.patient_id from patient p inner join patient_identifier patient_identifier on patient_identifier.patient_id = p.patient_id inner join paperrecord_paper_record paperrecord_paper_record on paperrecord_paper_record.patient_identifier = patient_identifier.patient_identifier_id where paperrecord_paper_record.record_id = ?", "preferred_paper_record");
        assertQuery(patientQueries, "paperrecord_paper_record_merge_request", 1, "select p.patient_id from patient p inner join patient_identifier patient_identifier on patient_identifier.patient_id = p.patient_id inner join paperrecord_paper_record paperrecord_paper_record on paperrecord_paper_record.patient_identifier = patient_identifier.patient_identifier_id where paperrecord_paper_record.record_id = ?", "not_preferred_paper_record");
        assertQuery(patientQueries, "paperrecord_paper_record_request", 0, basePatientIdQuery, "assignee");
        assertQuery(patientQueries, "paperrecord_paper_record_request", 1, "select p.patient_id from patient p inner join patient_identifier patient_identifier on patient_identifier.patient_id = p.patient_id inner join paperrecord_paper_record paperrecord_paper_record on paperrecord_paper_record.patient_identifier = patient_identifier.patient_identifier_id where paperrecord_paper_record.record_id = ?", "paper_record");
        assertQuery(patientQueries, "patient_identifier", 0, basePatientIdQuery, patientIdColumn);
        assertQuery(patientQueries, "patient_program", 0, basePatientIdQuery, patientIdColumn);
        assertQuery(patientQueries, "patient_program_attribute", 0, "select p.patient_id from patient p inner join patient_program patient_program on patient_program.patient_id = p.patient_id where patient_program.patient_program_id = ?", "patient_program_id");
        assertQuery(patientQueries, "patient_state", 0, "select p.patient_id from patient p inner join patient_program patient_program on patient_program.patient_id = p.patient_id where patient_program.patient_program_id = ?", "patient_program_id");
        assertQuery(patientQueries, "person", 0, basePatientIdQuery, personIdColumn);
        assertQuery(patientQueries, "person_address", 0, basePatientIdQuery, personIdColumn);
        assertQuery(patientQueries, "person_attribute", 0, basePatientIdQuery, personIdColumn);
        assertQuery(patientQueries, "person_merge_log", 0, basePatientIdQuery, "winner_person_id");
        assertQuery(patientQueries, "person_merge_log", 1, basePatientIdQuery, "loser_person_id");
        assertQuery(patientQueries, "person_name", 0, basePatientIdQuery, personIdColumn);
        assertQuery(patientQueries, "referral_order", 0, "select p.patient_id from patient p inner join orders orders on orders.patient_id = p.patient_id where orders.order_id = ?", orderIdColumn);
        assertQuery(patientQueries, "relationship", 0, basePatientIdQuery, "person_a");
        assertQuery(patientQueries, "relationship", 1, basePatientIdQuery, "person_b");
        assertQuery(patientQueries, "test_order", 0, "select p.patient_id from patient p inner join orders orders on orders.patient_id = p.patient_id where orders.order_id = ?", orderIdColumn);
        assertQuery(patientQueries, "visit", 0, basePatientIdQuery, patientIdColumn);
        assertQuery(patientQueries, "visit_attribute", 0, "select p.patient_id from patient p inner join visit visit on visit.patient_id = p.patient_id where visit.visit_id = ?", "visit_id");
    }

    private void assertQuery(Map<String, List<DatabaseQuery>> queries, String table, int index, String expectedQuery, String expectedColumn) {
        List<DatabaseQuery> queriesForTable = queries.get(table);
        assertNotNull(queriesForTable);
        assertTrue(index < queriesForTable.size());
        DatabaseQuery query = queriesForTable.get(index);
        assertNotNull(query);
        assertThat(query.getSql().trim(), equalTo(expectedQuery.trim()));
        assertThat(query.getParameterNames().size(), equalTo(1));
        assertThat(query.getParameterNames().get(0), equalTo(expectedColumn));
    }

    @Test
    public void shouldTrackPatientChanges() throws Exception {

        eventSource.reset();
        eventSource.start();
        try {
            waitForSnapshotToComplete(eventSource);

            // Test initial snapshot
            List<Integer> voidedPatients = db.executeQuery("select patient_id from patient where voided = 1", new ColumnListHandler<>());
            List<Map<String, Object>> snapshotPatients = db.executeQuery("select * from dbevent_patient", new MapListHandler());
            System.out.println("Found " + snapshotPatients.size() + " patients to test in initial snapshot");
            assertFalse(snapshotPatients.isEmpty());
            for (Map<String, Object> row : snapshotPatients) {
                Integer patientId = (Integer) row.get("patient_id");
                LocalDateTime lastUpdated = (LocalDateTime) row.get("last_updated");
                Boolean deleted = (Boolean) row.get("deleted");
                assertNotNull(patientId);
                assertNotNull(lastUpdated);
                assertThat(voidedPatients.contains(patientId), equalTo(BooleanUtils.isTrue(deleted)));
            }
            System.out.println("Successfully tested " + snapshotPatients.size() + " patients to test in initial snapshot");

            Integer pId = data.insertPatient("M", date("1982-10-19"));
            assertLastEvent(pId, "patient", false);

            Integer personNameId = data.insertPersonName(pId, "TestFirst", "TestLast");
            assertLastEvent(pId, "person_name", false);

            Integer personAddressId = data.insertPersonAddress(pId, "My Home Address");
            assertLastEvent(pId, "person_address", false);

            data.insertPersonAttribute(pId, 8, "5555-4433");
            assertLastEvent(pId, "person_attribute", false);

            Integer otherPersonId = data.insertPerson("F", date("2022-01-06"));
            data.insertRelationshipToPersonB(pId, 6, otherPersonId);
            assertLastEvent(pId, "relationship", false);

            data.insertRelationshipToPersonA(pId, 6, otherPersonId);
            assertLastEvent(pId, "relationship", false);

            data.insertPersonMergeLogAsWinner(pId, otherPersonId);
            assertLastEvent(pId, "person_merge_log", false);

            data.insertPersonMergeLogAsLoser(pId, otherPersonId);
            assertLastEvent(pId, "person_merge_log", false);

            Integer patientIdentifierId = data.insertPatientIdentifier(pId, 4, "ABC123", 1);
            assertLastEvent(pId, "patient_identifier", false);

            Integer allergyId = data.insertAllergy(pId, "Penicillin", "DRUG");
            assertLastEvent(pId, "allergy", false);

            data.insertAllergyReaction(allergyId, "Unknown");
            assertLastEvent(pId, "allergy_reaction", false);

            data.insertCondition(pId, "Confused", "ACTIVE");
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

            Integer fhirDiagnosticReportWithEncounter = data.insertFhirDiagnosticReport(null, encounterId, "ENCOUNTER");
            assertLastEvent(pId, "fhir_diagnostic_report", false);

            Integer fhirDiagnosticReportWithObs = data.insertFhirDiagnosticReport(null, null, "OBS");
            Integer diagnosticObs = data.insertObs(encounterId, visitDate, 1, "Height (cm)", 100d);
            data.insertFhirDiagnosticReportResults(fhirDiagnosticReportWithObs, diagnosticObs);
            assertLastEvent(pId, "fhir_diagnostic_report_results", false);

            Integer conceptProposalId = data.insertConceptProposal(encounterId, null, "Proposal linked to encounter");
            assertLastEvent(pId, "concept_proposal", false);

            Integer tag1 = data.insertConceptNameTag("Tag 1");
            Integer tag2 = data.insertConceptNameTag("Tag 2");
            data.insertConceptProposalTagMap(conceptProposalId, tag1);
            assertLastEvent(pId, "concept_proposal_tag_map", false);

            Integer conceptProposalForObs = data.insertConceptProposal(null, obsId, "Proposal linked to obs");
            assertLastEvent(pId, "concept_proposal", false);

            Integer namePhoneticId = data.insertNamePhonetic(personNameId, 1, "PKS");
            assertLastEvent(pId, "name_phonetics", false);

            Integer addressHierarchyEntryId = data.insertAddressHierarcyAddressToEntryMap(personAddressId, 1);
            assertLastEvent(pId, "address_hierarchy_address_to_entry_map", false);

            Integer paperRecordId = data.insertPaperRecord(patientIdentifierId, 1);
            assertLastEvent(pId, "paperrecord_paper_record", false);

            Integer paperRecordRequestId = data.insertPaperRecordRequest(paperRecordId, 1);
            assertLastEvent(pId, "paperrecord_paper_record_request", false);

            Integer otherPaperRecordId = data.insertPaperRecord(patientIdentifierId, 1);
            data.insertPaperRecordMergeRequest(paperRecordId, otherPaperRecordId);
            assertLastEvent(pId, "paperrecord_paper_record_merge_request", false);

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
            testUpdate(pId, "allergy_reaction", "update allergy_reaction set reaction_concept_id = ? where allergy_id = ?", conceptId("Hives"), allergyId);
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
            testUpdate(pId, "order_group", "update order_group set order_group_reason = ? where order_group_id = ?", conceptId("Asthma"), orderGroupId);
            testUpdate(pId, "order_group_attribute", "update order_group_attribute set value_reference = 'Modified comment' where order_group_id = ?", orderGroupId);
            testUpdate(pId, "cohort_member", "update cohort_member set start_date = now() where patient_id = ?", pId);
            testUpdate(pId, "appointmentscheduling_appointment", "update appointmentscheduling_appointment set status = 'RESCHEDULED' where appointment_id = ?", appointmentId);
            testUpdate(pId, "appointmentscheduling_appointment_status_history", "update appointmentscheduling_appointment_status_history set start_date = ? where appointment_status_history_id = ?", visitDate, appointmentStatusId);
            testUpdate(pId, "appointmentscheduling_appointment_request", "update appointmentscheduling_appointment_request set status = 'ON HOLD' where appointment_request_id = ?", appointmentRequestId);
            testUpdate(pId, "fhir_diagnostic_report", "update fhir_diagnostic_report set status = 'UPDATED' where diagnostic_report_id = ?", fhirDiagnosticReportId);
            testUpdate(pId, "fhir_diagnostic_report_performers", "update fhir_diagnostic_report_performers set provider_id = 2 where diagnostic_report_id = ?", fhirDiagnosticReportId);
            testUpdate(pId, "fhir_diagnostic_report_results", "update fhir_diagnostic_report_results set obs_id = ? where diagnostic_report_id = ?", obsId, fhirDiagnosticReportWithObs);
            testUpdate(pId, "concept_proposal", "update concept_proposal set original_text = 'New original text' where concept_proposal_id = ?", conceptProposalId);
            testUpdate(pId, "concept_proposal_tag_map", "update concept_proposal_tag_map set concept_name_tag_id = ? where concept_proposal_id = ?", tag2, conceptProposalId);
            testUpdate(pId, "name_phonetics", "update name_phonetics set field = 2 where name_phonetics_id = ?", namePhoneticId);
            testUpdate(pId, "address_hierarchy_address_to_entry_map", "update address_hierarchy_address_to_entry_map set entry_id = 2 where address_to_entry_map_id = ?", addressHierarchyEntryId);
            testUpdate(pId, "paperrecord_paper_record", "update paperrecord_paper_record set status = 'ASSIGNED' where record_id = ?", paperRecordId);
            testUpdate(pId, "paperrecord_paper_record_request", "update paperrecord_paper_record_request set status = 'ASSIGNED' where request_id = ?", paperRecordRequestId);
            testUpdate(pId, "paperrecord_paper_record_merge_request", "update paperrecord_paper_record_merge_request set status = 'ASSIGNED' where preferred_paper_record = ?", paperRecordId);

            // Test streaming voids (reverse order from updates) and then deletes

            testVoidAndDelete(pId, "paperrecord_paper_record_merge_request", "preferred_paper_record", paperRecordId);
            testVoidAndDelete(pId, "paperrecord_paper_record_request", "request_id", paperRecordRequestId);
            testVoidAndDelete(pId, "paperrecord_paper_record", "patient_identifier", patientIdentifierId);
            testVoidAndDelete(pId, "address_hierarchy_address_to_entry_map", "address_to_entry_map_id", addressHierarchyEntryId);
            testVoidAndDelete(pId, "name_phonetics", "name_phonetics_id", namePhoneticId);
            testVoidAndDelete(pId, "concept_proposal_tag_map", "concept_proposal_id", conceptProposalId);
            testVoidAndDelete(pId, "concept_proposal", "concept_proposal_id", conceptProposalId);
            testVoidAndDelete(pId, "concept_proposal", "concept_proposal_id", conceptProposalForObs);
            testVoidAndDelete(pId, "fhir_diagnostic_report_results", "diagnostic_report_id", fhirDiagnosticReportWithObs);
            testVoidAndDelete(pId, "fhir_diagnostic_report_performers", "diagnostic_report_id", fhirDiagnosticReportId);
            testVoidAndDelete(pId, "fhir_diagnostic_report", "diagnostic_report_id", fhirDiagnosticReportId);
            testVoidAndDelete(pId, "fhir_diagnostic_report", "diagnostic_report_id", fhirDiagnosticReportWithEncounter);
            testVoidAndDelete(pId, "appointmentscheduling_appointment_request", "appointment_request_id", appointmentRequestId);
            testVoidAndDelete(pId, "appointmentscheduling_appointment_status_history", "appointment_status_history_id", appointmentStatusId);
            testVoidAndDelete(pId, "appointmentscheduling_appointment", "appointment_id", appointmentId);
            testVoidAndDelete(pId, "cohort_member", "patient_id", pId);
            testVoidAndDelete(pId, "order_group_attribute", "order_group_id", orderGroupId);
            testVoidAndDelete(pId, "order_group", "order_group_id", orderGroupId);
            testVoidAndDelete(pId, "order_attribute", "order_id", drugOrderId);
            testVoidAndDelete(pId, "emr_radiology_order", "order_id", radiologyOrderId);
            testVoidAndDelete(pId, "test_order", "order_id", radiologyOrderId);
            testVoidAndDelete(pId, "referral_order", "order_id", referralOrderId);
            testVoidAndDelete(pId, "drug_order", "order_id", drugOrderId);
            testVoidAndDelete(pId, "orders", "encounter_id", encounterId);
            testVoidAndDelete(pId, "note", "obs_id", obsId);
            testVoidAndDelete(pId, "note", "encounter_id", encounterId);
            testVoidAndDelete(pId, "note", "patient_id", pId);
            testVoidAndDelete(pId, "obs", "obs_id", obsId);
            testVoidAndDelete(pId, "obs", "obs_id", diagnosticObs);
            testVoidAndDelete(pId, "diagnosis_attribute", "diagnosis_id", diagnosisId);
            testVoidAndDelete(pId, "encounter_diagnosis", "diagnosis_id", diagnosisId);
            testVoidAndDelete(pId, "encounter_provider", "encounter_id", encounterId);
            testVoidAndDelete(pId, "encounter", "encounter_id", encounterId);
            testVoidAndDelete(pId, "visit_attribute", "visit_id", visitId);
            testVoidAndDelete(pId, "visit", "patient_id", pId);
            testVoidAndDelete(pId, "patient_program_attribute", "patient_program_id", patientProgramId);
            testVoidAndDelete(pId, "patient_state", "patient_program_id", patientProgramId);
            testVoidAndDelete(pId, "patient_program", "patient_program_id", patientProgramId);
            testVoidAndDelete(pId, "conditions", "patient_id", pId);
            testVoidAndDelete(pId, "allergy_reaction", "allergy_id", allergyId);
            testVoidAndDelete(pId, "allergy", "patient_id", pId);
            testVoidAndDelete(pId, "patient_identifier", "patient_id", pId);
            testVoidAndDelete(pId, "person_merge_log", "loser_person_id", pId);
            testVoidAndDelete(pId, "person_merge_log", "winner_person_id", pId);
            testVoidAndDelete(pId, "relationship", "person_b", pId);
            testVoidAndDelete(pId, "relationship", "person_a", pId);
            testVoidAndDelete(pId, "person_attribute", "person_id", pId);
            testVoidAndDelete(pId, "person_address", "person_id", pId);
            testVoidAndDelete(pId, "person_name", "person_id", pId);
            testVoidAndDelete(pId, "patient", "patient_id", pId);
        }
        finally {
            eventSource.stop();
            eventSource.reset();
        }
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

    public void testVoidAndDelete(Integer patientId, String table, String idColumn, Integer idValue) throws Exception{
        boolean expectedDeleted = table.equals("patient");
        if (db.getMetadata().getTable(table).getColumn("voided") != null) {
            db.executeUpdate("update " + table + " set voided = true where " + idColumn + " = ?", idValue);
            assertLastEvent(patientId, table, expectedDeleted);
        }
        db.executeUpdate("delete from " + table + " where " + idColumn + " = ?", idValue);

        assertLastEvent(patientId, table, expectedDeleted);
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

    private static Integer conceptId(String name) {
        return db.executeQuery("select concept_id from concept_name where name = ?", new ScalarHandler<>(), name);
    }
}
