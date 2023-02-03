package org.openmrs.module.pihcore.event;

import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;
import org.openmrs.module.dbevent.Database;
import org.openmrs.module.dbevent.DbEvent;
import org.openmrs.module.dbevent.DbEventLog;
import org.openmrs.module.dbevent.DbEventSource;
import org.openmrs.module.dbevent.DbEventStatus;
import org.openmrs.module.dbevent.EventContext;
import org.openmrs.module.dbevent.test.TestEventContext;
import org.openmrs.module.pihcore.setup.DbEventSetup;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    private DbEvent lastEvent = null;

    @Test
    public void shouldTrackPatientChanges() throws Exception {
        Properties mysqlProperties = getMysqlProperties();
        if (mysqlProperties.isEmpty()) {
            System.out.println("***** SKIPPING " + getClass().getName() + ".  To execute, pass mysql connection properties in");
            return;
        }
        EventContext ctx = new TestEventContext(mysqlProperties);
        Database db = ctx.getDatabase();
        DbEventSource eventSource = DbEventSetup.getEventSource(ctx);

        try {
            eventSource.start();

            // Test initial snapshot
            waitForSnapshotToComplete(eventSource);
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

            String personUuid = uuid();
            String otherPersonUuid = uuid();
            Date now = new Date();
            Date visitDate = date("2022-07-13");

            db.executeUpdate(
                    "insert into person (uuid, gender, birthdate, creator, date_created) values (?,?,?,?,?)",
                    personUuid, "M", date("1985-02-17"), 1, now
            );
            db.executeUpdate(
                    "insert into person (uuid, gender, birthdate, creator, date_created) values (?,?,?,?,?)",
                    otherPersonUuid, "F", date("1988-09-02"), 1, now
            );
            waitForNextEvent(eventSource);

            Integer pId = db.executeQuery("select person_id from person where uuid = ?", new ScalarHandler<>(), personUuid);
            Integer otherPersonId = db.executeQuery("select person_id from person where uuid = ?", new ScalarHandler<>(), otherPersonUuid);

            // Test streaming inserts

            testUpdate(eventSource, db, "patient", pId,
                    "insert into patient (patient_id, creator, date_created) values (?,?,?)",
                    pId, 1, now
            );

            testUpdate(eventSource, db, "person_name", pId,
                    "insert into person_name (person_id, uuid, given_name, family_name, preferred, creator, date_created) values (?,?,?,?,?,?,?)",
                    pId, uuid(), "TestFirst", "TestLast", 1, 1, now
            );

            testUpdate(eventSource, db, "person_address", pId,
                    "insert into person_address (person_id, uuid, address1, preferred, creator, date_created) values (?,?,?,?,?,?)",
                    pId, uuid(), "My Home Address", 1, 1, now
            );

            testUpdate(eventSource, db, "person_attribute", pId,
                    "insert into person_attribute (person_id, uuid, person_attribute_type_id, value, creator, date_created) values (?,?,?,?,?,?)",
                    pId, uuid(), 8, "5555-4433", 1, now
            );

            testUpdate(eventSource, db, "relationship", pId,
                    "insert into relationship (uuid, relationship, person_a, person_b, creator, date_created) values (?,?,?,?,?,?)",
                    uuid(), 6, pId, otherPersonId, 1, now
            );

            testUpdate(eventSource, db, "relationship", pId,
                    "insert into relationship (uuid, relationship, person_a, person_b, creator, date_created) values (?,?,?,?,?,?)",
                    uuid(), 6, otherPersonId, pId, 1, now
            );

            testUpdate(eventSource, db, "patient_identifier", pId,
                    "insert into patient_identifier (patient_id, uuid, identifier_type, identifier, location_id, preferred, creator, date_created) values (?,?,?,?,?,?,?,?)",
                    pId, uuid(), 4, "ABC123", 1, 1, 1, now
            );

            testUpdate(eventSource, db, "allergy", pId,
                    "insert into allergy (patient_id, uuid, coded_allergen, allergen_type, creator, date_created) values (?,?,?,?,?,?)",
                    pId, uuid(), conceptId(db,"Penicillin"), "DRUG", 1, now
            );

            Integer allergyId = db.executeQuery("select allergy_id from allergy where patient_id = ?", new ScalarHandler<>(), pId);

            testUpdate(eventSource, db, "allergy_reaction", pId,
                    "insert into allergy_reaction (allergy_id, uuid, reaction_concept_id) values (?,?,?)",
                    allergyId, uuid(), conceptId(db, "Unknown")
            );

            testUpdate(eventSource, db, "conditions", pId,
                    "insert into conditions (patient_id, uuid, condition_non_coded, clinical_status, creator, date_created) values (?,?,?,?,?,?)",
                    pId, uuid(), "Confused", "ACTIVE", 1, now
            );

            Integer programId = db.executeQuery("select program_id from program where name = 'HIV'", new ScalarHandler<>());
            Integer stateId = db.executeQuery("select program_workflow_state_id from program_workflow_state where uuid = '872c529c-a5a4-47c7-9584-bd15fa5bb0a9'", new ScalarHandler<>());

            testUpdate(eventSource, db, "patient_program", pId,
                    "insert into patient_program (patient_id, uuid, program_id, date_enrolled, creator, date_created) values (?,?,?,?,?,?)",
                    pId, uuid(), programId, visitDate, 1, now
            );

            Integer ppId = db.executeQuery("select patient_program_id from patient_program where patient_id = ?", new ScalarHandler<>(), pId);

            testUpdate(eventSource, db, "patient_state", pId,
                    "insert into patient_state (patient_program_id, uuid, state, start_date, creator, date_created) values (?,?,?,?,?,?)",
                    ppId, uuid(), stateId, visitDate, 1, now
            );

            db.executeUpdate(
                    "insert ignore into program_attribute_type (program_attribute_type_id, uuid, name, min_occurs, creator, date_created) values (?,?,?,?,?,?)",
                    1, uuid(), "Comments", 0, 1, now
            );

            testUpdate(eventSource, db, "patient_program_attribute", pId,
                    "insert into patient_program_attribute (patient_program_id, uuid, attribute_type_id, value_reference, creator, date_created) values (?,?,?,?,?,?)",
                    ppId, uuid(), 1, "Extra Program Info", 1, now
            );

            testUpdate(eventSource, db, "visit", pId,
                    "insert into visit (patient_id, uuid, visit_type_id, date_started, location_id, creator, date_created) values (?,?,?,?,?,?,?)",
                    pId, uuid(), 1, visitDate, 1, 1, now
            );

            Integer visitId = db.executeQuery("select visit_id from visit where patient_id = ?", new ScalarHandler<>(), pId);

            db.executeUpdate(
                    "insert ignore into visit_attribute_type (visit_attribute_type_id, uuid, name, min_occurs, creator, date_created) values (?,?,?,?,?,?)",
                    1, uuid(), "Comments", 0, 1, now
            );

            testUpdate(eventSource, db, "visit_attribute", pId,
                    "insert into visit_attribute (visit_id, uuid, attribute_type_id, value_reference, creator, date_created) values (?,?,?,?,?,?)",
                    visitId, uuid(), 1, "Last Minute Visit", 1, now
            );

            testUpdate(eventSource, db, "encounter", pId,
                    "insert into encounter (patient_id, uuid, encounter_type, encounter_datetime, location_id, creator, date_created) values (?,?,?,?,?,?,?)",
                    pId, uuid(), 2, visitDate, 1, 1, now
            );

            Integer encId = db.executeQuery("select encounter_id from encounter where patient_id = ?", new ScalarHandler<>(), pId);

            testUpdate(eventSource, db, "encounter_provider", pId,
                    "insert into encounter_provider (encounter_id, uuid, provider_id, encounter_role_id, creator, date_created) values (?,?,?,?,?,?)",
                    encId, uuid(), 1, 6, 1, now
            );

            testUpdate(eventSource, db, "encounter_diagnosis", pId,
                    "insert into encounter_diagnosis (patient_id, encounter_id, uuid, diagnosis_coded, certainty, creator, date_created) values (?,?,?,?,?,?,?)",
                    pId, encId, uuid(), conceptId(db, "Pneumonia"), "PROVISIONAL", 1, now
            );

            Integer diagnosisId = db.executeQuery("select diagnosis_id from encounter_diagnosis where encounter_id = ?", new ScalarHandler<>(), encId);

            db.executeUpdate(
                    "insert ignore into diagnosis_attribute_type (diagnosis_attribute_type_id, uuid, name, min_occurs, creator, date_created) values (?,?,?,?,?,?)",
                    1, uuid(), "Comments", 0, 1, now
            );

            /*
            TODO: Investigate
            testUpdate(eventSource, db, "diagnosis_attribute", pId,
                    "insert into diagnosis_attribute (diagnosis_id, uuid, attribute_type_id, value_reference, creator, date_created) values (?,?,?,?,?,?)",
                    diagnosisId, uuid(), 1, "Severe", 1, now
            );
             */

            testUpdate(eventSource, db, "obs", pId,
                    "insert into obs (person_id, uuid, encounter_id, obs_datetime, concept_id, value_numeric, location_id, status, creator, date_created) values (?,?,?,?,?,?,?,?,?,?)",
                    pId, uuid(), encId, visitDate, conceptId(db, "Weight (kg)"), 80, 1, "FINAL", 1, now
            );

            Integer obsId = db.executeQuery("select obs_id from obs where encounter_id = ?", new ScalarHandler<>(), encId);

            Integer maxNoteId = db.executeQuery("select max(note_id) from note", new ScalarHandler<>());
            Integer nextNoteId = (maxNoteId == null ? 1 : maxNoteId + 1);
            testUpdate(eventSource, db, "note", pId,
                    "insert into note (note_id, uuid, patient_id, text, creator, date_created) values (?,?,?,?,?,?)",
                    nextNoteId++, uuid(), pId, "Patient Note", 1, now
            );
            testUpdate(eventSource, db, "note", pId,
                    "insert into note (note_id, uuid, encounter_id, text, creator, date_created) values (?,?,?,?,?,?)",
                    nextNoteId++, uuid(), encId, "Encounter Note", 1, now
            );
            /*
            TODO: Need to join on obs_id
            testUpdate(eventSource, db, "note", pId,
                    "insert into note (note_id, uuid, obs_id, text, creator, date_created) values (?,?,?,?,?,?)",
                    nextNoteId++, uuid(), obsId, "Obs Note", 1, now
            );
             */

            testUpdate(eventSource, db, "orders", pId,
                    "insert into orders (uuid, patient_id, encounter_id, order_type_id, order_action, concept_id, date_activated, order_number, care_setting, orderer, creator, date_created) values (?,?,?,?,?,?,?,?,?,?,?,?)",
                    uuid(), pId, encId, 2, "NEW", conceptId(db, "Aspirin"), visitDate, uuid(), 1, 1, 1, now
            );
            testUpdate(eventSource, db, "orders", pId,
                    "insert into orders (uuid, patient_id, encounter_id, order_type_id, order_action, concept_id, date_activated, order_number, care_setting, orderer, creator, date_created) values (?,?,?,?,?,?,?,?,?,?,?,?)",
                    uuid(), pId, encId, 4, "NEW", conceptId(db, "Biopsy"), visitDate, uuid(), 1, 1, 1, now
            );
            testUpdate(eventSource, db, "orders", pId,
                    "insert into orders (uuid, patient_id, encounter_id, order_type_id, order_action, concept_id, date_activated, order_number, care_setting, orderer, creator, date_created) values (?,?,?,?,?,?,?,?,?,?,?,?)",
                    uuid(), pId, encId, 5, "NEW", conceptId(db, "Chest, 2 views (X-ray)"), visitDate, uuid(), 1, 1, 1, now
            );

            Integer drugOrderId = db.executeQuery("select order_id from orders where order_type_id = 2 and patient_id = ?", new ScalarHandler<>(), pId);
            Integer referralOrderId = db.executeQuery("select order_id from orders where order_type_id = 4 and patient_id = ?", new ScalarHandler<>(), pId);
            Integer radiologyOrderId = db.executeQuery("select order_id from orders where order_type_id = 5 and patient_id = ?", new ScalarHandler<>(), pId);

            testUpdate(eventSource, db, "drug_order", pId, "insert into drug_order (order_id, drug_inventory_id) values (?, ?)", drugOrderId, 1);
            testUpdate(eventSource, db, "referral_order", pId, "insert into referral_order (order_id) values (?)", referralOrderId);
            testUpdate(eventSource, db, "test_order", pId, "insert into test_order (order_id) values (?)", radiologyOrderId);
            testUpdate(eventSource, db, "emr_radiology_order", pId, "insert into emr_radiology_order (order_id) values (?)", radiologyOrderId);

            db.executeUpdate(
                    "insert ignore order_attribute_type (order_attribute_type_id, uuid, name, min_occurs, creator, date_created) values (?,?,?,?,?,?)",
                    1, uuid(), "Comments", 0, 1, now
            );

            testUpdate(eventSource, db, "order_attribute", pId,
                    "insert into order_attribute (order_id, uuid, attribute_type_id, value_reference, creator, date_created) values (?,?,?,?,?,?)",
                    drugOrderId, uuid(), 1, "Additional comment", 1, now
            );

            testUpdate(eventSource, db, "order_group", pId,
                    "insert into order_group (uuid, patient_id, encounter_id, creator, date_created) values (?,?,?,?,?)",
                    uuid(), pId, encId, 1, now
            );

            Integer orderGroupId = db.executeQuery("select order_group_id from order_group where patient_id = ?", new ScalarHandler<>(), pId);

            db.executeUpdate(
                    "insert ignore order_group_attribute_type (order_group_attribute_type_id, uuid, name, min_occurs, creator, date_created) values (?,?,?,?,?,?)",
                    1, uuid(), "Comments", 0, 1, now
            );

            /*
            TODO
            testUpdate(eventSource, db, "order_group_attribute", pId,
                    "insert into order_group_attribute (order_group_id, uuid, attribute_type_id, value_reference, creator, date_created) values (?,?,?,?,?,?)",
                    orderGroupId, uuid(), 1, "Additional comment", 1, now
            );

             */

            /*
            TODO Remaining:
                "appointmentscheduling_appointment"
                "appointmentscheduling_appointment_request"
                "appointmentscheduling_appointment_status_history"
                "paperrecord_paper_record"
                "paperrecord_paper_record_merge_request"
                "paperrecord_paper_record_request",
                "cohort_member"
                "address_hierarchy_address_to_entry_map"
                "fhir_diagnostic_report"
                "fhir_diagnostic_report_performers"
                "fhir_diagnostic_report_results"
                "person_merge_log"
                "logic_rule_token"
                "logic_rule_token_tag"
                "name_phonetics"
                "concept_proposal"
                "concept_proposal_tag_map"

             Additional test to confirm remaining tables are explicitly not handled, and fail if any unknown tables found
             */


            // Test streaming updates

            testUpdate(eventSource, db, "person", pId, "update person set gender = 'F' where person_id = ?", pId);
            testUpdate(eventSource, db, "patient", pId, "update patient set allergy_status = 'None' where patient_id = ?", pId);
            testUpdate(eventSource, db, "person_name", pId, "update person_name set middle_name = 'Q' where person_id = ?", pId);
            testUpdate(eventSource, db, "person_address", pId, "update person_address set address1 = '11 Main Street' where person_id = ?", pId);
            testUpdate(eventSource, db, "person_attribute", pId, "update person_attribute set value = '1234-5678' where person_id = ?", pId);
            testUpdate(eventSource, db, "relationship", pId, "update relationship set relationship = 2 where person_a = ?", pId);
            testUpdate(eventSource, db, "relationship", pId, "update relationship set relationship = 2 where person_b = ?", pId);
            testUpdate(eventSource, db, "patient_identifier", pId, "update patient_identifier set identifier = 'XYZ456' where patient_id = ?", pId);
            testUpdate(eventSource, db, "allergy", pId, "update allergy set comments = 'Mild' where patient_id = ?", pId);
            testUpdate(eventSource, db, "allergy_reaction", pId, "update allergy_reaction set reaction_concept_id = ? where allergy_id = ?", conceptId(db, "Hives"), allergyId);
            testUpdate(eventSource, db, "conditions", pId, "update conditions set condition_non_coded = 'Very confused' where patient_id = ?", pId);
            testUpdate(eventSource, db, "patient_program", pId, "update patient_program set location_id = 1 where patient_program_id = ?", ppId);
            testUpdate(eventSource, db, "patient_state", pId, "update patient_state set end_date = ? where patient_program_id = ?", visitDate, ppId);
            testUpdate(eventSource, db, "patient_program_attribute", pId, "update patient_program_attribute set value_reference = 'Extra Info' where patient_program_id = ?", ppId);
            testUpdate(eventSource, db, "visit", pId, "update visit set date_stopped = ? where patient_id = ?", date("2022-07-15"), pId);
            testUpdate(eventSource, db, "visit_attribute", pId, "update visit_attribute set value_reference = 'Upcoming' where visit_id = ?", visitId);
            testUpdate(eventSource, db, "encounter", pId, "update encounter set location_id = 42 where encounter_id = ?", encId);
            testUpdate(eventSource, db, "encounter_provider", pId, "update encounter_provider set encounter_role_id = 3 where encounter_id = ?", encId);
            testUpdate(eventSource, db, "encounter_diagnosis", pId, "update encounter_diagnosis set certainty = 'CONFIRMED' where diagnosis_id = ?", diagnosisId);
            /*
            TODO: Investigate
            testUpdate(eventSource, db, "diagnosis_attribute", pId, "update diagnosis_attribute set value_reference = 'MODERATE' where diagnosis_id = ?", diagnosisId);
            */
            testUpdate(eventSource, db, "obs", pId, "update obs set value_numeric = 85 where obs_id = ?", obsId);
            testUpdate(eventSource, db, "note", pId, "update note set text = 'Updated note' where patient_id = ?", pId);
            testUpdate(eventSource, db, "note", pId, "update note set text = 'Updated note' where encounter_id = ?", encId);
            // TODO: testUpdate(eventSource, db, "note", pId, "update note set text = 'Updated note' where obs-id = ?", obsId);
            testUpdate(eventSource, db, "orders", pId, "update orders set instructions = 'As directed' where order_id = ?", drugOrderId);
            testUpdate(eventSource, db, "drug_order", pId, "update drug_order set as_needed = 1 where order_id = ?", drugOrderId);
            testUpdate(eventSource, db, "referral_order", pId, "update referral_order set clinical_history = 'Never' where order_id = ?", referralOrderId);
            testUpdate(eventSource, db, "test_order", pId, "update test_order set clinical_history = 'None' where order_id = ?", radiologyOrderId);
            testUpdate(eventSource, db, "emr_radiology_order", pId, "update emr_radiology_order set exam_location = 1 where order_id = ?", radiologyOrderId);
            testUpdate(eventSource, db, "order_attribute", pId, "update order_attribute set value_reference = 'Modified comment' where order_id = ?", drugOrderId);
            testUpdate(eventSource, db, "order_group", pId, "update order_group set order_group_reason = ? where order_group_id = ?", conceptId(db, "Asthma"), orderGroupId);
            // TODO: testUpdate(eventSource, db, "order_group_attribute", pId, "update order_group_attribute set value_reference = 'Modified comment' where order_group_id = ?", orderGroupId);
        }
        finally {
            eventSource.stop();
        }
    }

    public void testUpdate(DbEventSource eventSource, Database db, String table, Integer patientId, String statement, Object... args) throws Exception {
        db.executeUpdate(statement, args);
        DbEventStatus status = waitForNextEvent(eventSource);
        List<Map<String, Object>>results = db.executeQuery("select * from dbevent_patient where patient_id = ?", new MapListHandler(), patientId);
        assertThat(results.size(), equalTo(1));
        assertTrue(status.isProcessed());
        assertThat(status.getEvent().getTable(), equalTo(table));
        assertThat(results.get(0).get("patient_id"), equalTo(patientId));
        assertThat(results.get(0).get("last_updated"), equalTo(localDateTime(status.getEvent().getTimestamp())));
        assertFalse((Boolean)results.get(0).get("deleted"));
    }

    protected void waitForSnapshotToComplete(DbEventSource eventSource) throws Exception {
        PatientUpdateEventConsumer consumer = (PatientUpdateEventConsumer) eventSource.getEventConsumer();
        while (!consumer.isSnapshotInitialized()) {
            TimeUnit.SECONDS.sleep(1);
        }
    }

    protected DbEventStatus waitForNextEvent(DbEventSource eventSource) throws Exception {
        DbEventStatus status = DbEventLog.getLatestEventStatus(eventSource.getConfig().getSourceName());
        while (status == null || (status.getEvent().equals(lastEvent) || !status.isProcessed())) {
            TimeUnit.MILLISECONDS.sleep(100);
            status = DbEventLog.getLatestEventStatus(eventSource.getConfig().getSourceName());
        }
        lastEvent = status.getEvent();
        return status;
    }

    private LocalDateTime localDateTime(String datetime) throws Exception {
        Date lastUpdatedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(datetime);
        return localDateTime(lastUpdatedDate.getTime());
    }

    private LocalDateTime localDateTime(long millis) {
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private Date date(String dateStr) throws Exception {
        return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
    }

    private String uuid() {
        return UUID.randomUUID().toString();
    }

    private Integer conceptId(Database db, String name) {
        return db.executeQuery("select concept_id from concept_name where name = ?", new ScalarHandler<>(), name);
    }

    protected Properties getMysqlProperties() throws Exception {
        Properties p = new Properties();
        String propertiesFile = System.getProperty("MYSQL_PROPERTIES_FILE");
        if (StringUtils.isNotBlank(propertiesFile)) {
            try (InputStream is = Files.newInputStream(Paths.get(propertiesFile))) {
                p.load(is);
            }
        }
        return p;
    }
}
