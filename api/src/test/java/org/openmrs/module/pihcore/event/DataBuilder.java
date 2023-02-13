package org.openmrs.module.pihcore.event;

import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.openmrs.module.dbevent.Database;

import java.util.Date;
import java.util.UUID;

public class DataBuilder {

    private final Database db;
    private final Date dateCreated;

    public DataBuilder(Database db, Date dateCreated) {
        this.db = db;
        this.dateCreated = dateCreated;
    }

    public Integer insertPerson(String gender, Date birthdate) {
        String uuid = uuid();
        db.executeUpdate(
                "insert into person (uuid, gender, birthdate, creator, date_created) values (?,?,?,?,?)",
                uuid, gender, birthdate, 1, dateCreated
        );
        return db.executeQuery("select person_id from person where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public Integer insertPatient(String gender, Date birthdate) {
        Integer pId = insertPerson(gender, birthdate);
        db.executeUpdate("insert into patient (patient_id, creator, date_created) values (?,?,?)", pId, 1, dateCreated);
        return pId;
    }

    public Integer insertPersonName(Integer patientId, String givenName, String familyName) {
        String uuid = uuid();
        db.executeUpdate(
                "insert into person_name (person_id, uuid, given_name, family_name, preferred, creator, date_created) values (?,?,?,?,?,?,?)",
                patientId, uuid, givenName, familyName, 1, 1, dateCreated
        );
        return db.executeQuery("select person_name_id from person_name where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public Integer insertPersonAddress(Integer patientId, String address) {
        String uuid = uuid();
        db.executeUpdate(
                "insert into person_address (person_id, uuid, address1, preferred, creator, date_created) values (?,?,?,?,?,?)",
                patientId, uuid, address, 1, 1, dateCreated
        );
        return db.executeQuery("select person_address_id from person_address where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public Integer insertPersonAttribute(Integer patientId, Integer type, String attributeValue) {
        String uuid = uuid();
        db.executeUpdate(
                "insert into person_attribute (person_id, uuid, person_attribute_type_id, value, creator, date_created) values (?,?,?,?,?,?)",
                patientId, uuid, type, attributeValue, 1, dateCreated
        );
        return db.executeQuery("select person_attribute_id from person_attribute where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public Integer insertRelationshipToPersonB(Integer patientId, Integer type, Integer person) {
        String uuid = uuid();
        db.executeUpdate(
                "insert into relationship (uuid, relationship, person_a, person_b, creator, date_created) values (?,?,?,?,?,?)",
                uuid, type, patientId, person, 1, dateCreated
        );
        return db.executeQuery("select relationship_id from relationship where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public Integer insertRelationshipToPersonA(Integer patientId, Integer type, Integer person) {
        String uuid = uuid();
        db.executeUpdate(
                "insert into relationship (uuid, relationship, person_a, person_b, creator, date_created) values (?,?,?,?,?,?)",
                uuid, type, person, patientId, 1, dateCreated
        );
        return db.executeQuery("select relationship_id from relationship where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public Integer insertPersonMergeLogAsWinner(Integer patientId, Integer loser) {
        String uuid = uuid();
        db.executeUpdate(
                "insert into person_merge_log (uuid, winner_person_id, loser_person_id, merged_data, creator, date_created, voided) values (?,?,?,?,?,?,?)",
                uuid, patientId, loser, "Merged As Winner", 1, dateCreated, 0
        );
        return db.executeQuery("select person_merge_log_id from person_merge_log where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public Integer insertPersonMergeLogAsLoser(Integer patientId, Integer winner) {
        String uuid = uuid();
        db.executeUpdate(
                "insert into person_merge_log (uuid, winner_person_id, loser_person_id, merged_data, creator, date_created, voided) values (?,?,?,?,?,?,?)",
                uuid, winner, patientId, "Merged As Loser", 1, dateCreated, 0
        );
        return db.executeQuery("select person_merge_log_id from person_merge_log where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public Integer insertPatientIdentifier(Integer patientId, Integer type, String identifier, Integer location) {
        String uuid = uuid();
        db.executeUpdate(
                "insert into patient_identifier (patient_id, uuid, identifier_type, identifier, location_id, preferred, creator, date_created) values (?,?,?,?,?,?,?,?)",
                patientId, uuid, type, identifier, location, 1, 1, dateCreated
        );
        return db.executeQuery("select patient_identifier_id from patient_identifier where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public Integer insertAllergy(Integer patientId, String allergen, String type) {
        String uuid = uuid();
        db.executeUpdate(
                "insert into allergy (patient_id, uuid, coded_allergen, allergen_type, creator, date_created) values (?,?,?,?,?,?)",
                patientId, uuid, conceptId(allergen), type, 1, dateCreated
        );
        return db.executeQuery("select allergy_id from allergy where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public Integer insertAllergyReaction(Integer allergyId, String reaction) {
        String uuid = uuid();
        db.executeUpdate(
                "insert into allergy_reaction (allergy_id, uuid, reaction_concept_id) values (?,?,?)",
                allergyId, uuid, conceptId( reaction)
        );
        return db.executeQuery("select allergy_reaction_id from allergy_reaction where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public Integer insertCondition(Integer patientId, String nonCoded, String type) {
        String uuid = uuid();
        db.executeUpdate(
                "insert into conditions (patient_id, uuid, condition_non_coded, clinical_status, creator, date_created) values (?,?,?,?,?,?)",
                patientId, uuid, nonCoded, type, 1, dateCreated
        );
        return db.executeQuery("select condition_id from conditions where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public Integer insertPatientProgram(Integer patientId, String programName, Date dateEnrolled) {
        String uuid = uuid();
        Integer programId = db.executeQuery("select program_id from program where name = ?", new ScalarHandler<>(), programName);
        db.executeUpdate(
                "insert into patient_program (patient_id, uuid, program_id, date_enrolled, creator, date_created) values (?,?,?,?,?,?)",
                patientId, uuid, programId, dateEnrolled, 1, dateCreated
        );
        return db.executeQuery("select patient_program_id from patient_program where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public Integer insertPatientState(Integer patientProgramId, String stateUuid, Date startDate) {
        String uuid = uuid();
        Integer stateId = db.executeQuery("select program_workflow_state_id from program_workflow_state where uuid = ?", new ScalarHandler<>(), stateUuid);
        db.executeUpdate(
                "insert into patient_state (patient_program_id, uuid, state, start_date, creator, date_created) values (?,?,?,?,?,?)",
                patientProgramId, uuid, stateId, startDate, 1, dateCreated
        );
        return db.executeQuery("select patient_state_id from patient_state where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public Integer insertPatientProgramAttribute(Integer patientProgramId, Integer type, String value) {
        String uuid = uuid();
         db.executeUpdate(
                "insert into patient_program_attribute (patient_program_id, uuid, attribute_type_id, value_reference, creator, date_created) values (?,?,?,?,?,?)",
                patientProgramId, uuid, type, value, 1, dateCreated
        );
        return db.executeQuery("select patient_program_attribute_id from patient_program_attribute where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public Integer insertVisit(Integer patientId, Integer type, Date visitDate, Integer location) {
        String uuid = uuid();
        db.executeUpdate(
                "insert into visit (patient_id, uuid, visit_type_id, date_started, location_id, creator, date_created) values (?,?,?,?,?,?,?)",
                patientId, uuid, type, visitDate, location, 1, dateCreated
        );
        return db.executeQuery("select visit_id from visit where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public Integer insertVisitAttribute(Integer visitId, Integer type, String value) {
        String uuid = uuid();
        db.executeUpdate(
                "insert into visit_attribute (visit_id, uuid, attribute_type_id, value_reference, creator, date_created) values (?,?,?,?,?,?)",
                visitId, uuid, type, value, 1, dateCreated
        );
        return db.executeQuery("select visit_attribute_id from visit_attribute where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public Integer insertEncounter(Integer patientId, Integer type, Date encounterDate, Integer location) {
        String uuid = uuid();
        db.executeUpdate(
                "insert into encounter (patient_id, uuid, encounter_type, encounter_datetime, location_id, creator, date_created) values (?,?,?,?,?,?,?)",
                patientId, uuid, type, encounterDate, location, 1, dateCreated
        );
        return db.executeQuery("select encounter_id from encounter where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public Integer insertEncounterProvider(Integer encounterId, Integer providerId, Integer roleId) {
        String uuid = uuid();
        db.executeUpdate(
                "insert into encounter_provider (encounter_id, uuid, provider_id, encounter_role_id, creator, date_created) values (?,?,?,?,?,?)",
                encounterId, uuid, providerId, roleId, 1, dateCreated
        );
        return db.executeQuery("select encounter_provider_id from encounter_provider where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public Integer insertEncounterDiagnosis(Integer encounterId, String codedDiagnosis, String certainty) {
        String uuid = uuid();
        db.executeUpdate(
                "insert into encounter_diagnosis (patient_id, encounter_id, uuid, diagnosis_coded, certainty, creator, date_created) values (?,?,?,?,?,?,?)",
                patientForEncounter(encounterId), encounterId, uuid, conceptId( codedDiagnosis), certainty, 1, dateCreated
        );
        return db.executeQuery("select diagnosis_id from encounter_diagnosis where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public Integer insertDiagnosisAttribute(Integer encounterDiagnosisId, Integer type, String value) {
        String uuid = uuid();
        db.executeUpdate(
                "insert into diagnosis_attribute (diagnosis_id, uuid, attribute_type_id, value_reference, creator, date_created) values (?,?,?,?,?,?)",
                encounterDiagnosisId, uuid, type, value, 1, dateCreated
        );
        return db.executeQuery("select diagnosis_attribute_id from diagnosis_attribute where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public Integer insertObs(Integer encounterId, Date obsDatetime, Integer location, String concept, Double valueNumeric) {
        String uuid = uuid();
        db.executeUpdate(
                "insert into obs (person_id, uuid, encounter_id, obs_datetime, concept_id, value_numeric, location_id, status, creator, date_created) values (?,?,?,?,?,?,?,?,?,?)",
                patientForEncounter(encounterId), uuid, encounterId, obsDatetime, conceptId( concept), valueNumeric, location, "FINAL", 1, dateCreated
        );
        return db.executeQuery("select obs_id from obs where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public Integer insertNote(Integer patientId, Integer encounterId, Integer obsId, String note) {
        String uuid = uuid();
        Integer maxNoteId = db.executeQuery("select max(note_id) from note", new ScalarHandler<>());
        Integer noteId = (maxNoteId == null ? 1 : maxNoteId + 1);
        db.executeUpdate(
                "insert into note (note_id, uuid, patient_id, encounter_id, obs_id, text, creator, date_created) values (?,?,?,?,?,?,?,?)",
                noteId, uuid, patientId, encounterId, obsId, note, 1, dateCreated
        );
        return noteId;
    }

    public Integer insertOrder(Integer encounterId, Integer type, String orderable, Date dateActivated) {
        String uuid = uuid();
        db.executeUpdate(
                "insert into orders (uuid, patient_id, encounter_id, order_type_id, order_action, concept_id, date_activated, order_number, care_setting, orderer, creator, date_created) values (?,?,?,?,?,?,?,?,?,?,?,?)",
                uuid, patientForEncounter(encounterId), encounterId, 2, "NEW", conceptId( orderable), dateActivated, uuid, 1, 1, 1, dateCreated
        );
        return db.executeQuery("select order_id from orders where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public Integer insertDrugOrder(Integer orderId, Integer drugInventoryId) {
        db.executeUpdate("insert into drug_order (order_id, drug_inventory_id) values (?, ?)", orderId, drugInventoryId);
        return orderId;
    }

    public Integer insertReferralOrder(Integer orderId) {
        db.executeUpdate("insert into referral_order (order_id) values (?)", orderId);
        return orderId;
    }

    public Integer insertTestOrder(Integer orderId) {
        db.executeUpdate("insert into test_order (order_id) values (?)", orderId);
        return orderId;
    }

    public Integer insertRadiologyOrder(Integer orderId) {
        db.executeUpdate("insert into emr_radiology_order (order_id) values (?)", orderId);
        return orderId;
    }

    public Integer insertOrderAttribute(Integer orderId, Integer type, String value) {
        String uuid = uuid();
        db.executeUpdate(
                "insert into order_attribute (order_id, uuid, attribute_type_id, value_reference, creator, date_created) values (?,?,?,?,?,?)",
                orderId, uuid, type, value, 1, dateCreated
        );
        return db.executeQuery("select order_attribute_id from order_attribute where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public Integer insertOrderGroup(Integer encounterId) {
        String uuid = uuid();
        db.executeUpdate(
                "insert into order_group (uuid, patient_id, encounter_id, creator, date_created) values (?,?,?,?,?)",
                uuid, patientForEncounter(encounterId), encounterId, 1, dateCreated
        );
        return db.executeQuery("select order_group_id from order_group where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public Integer insertOrderGroupAttribute(Integer orderGroupId, Integer type, String value) {
        String uuid = uuid();
        db.executeUpdate(
                "insert into order_group_attribute (order_group_id, uuid, attribute_type_id, value_reference, creator, date_created) values (?,?,?,?,?,?)",
                orderGroupId, uuid, type, value, 1, dateCreated
        );
        return db.executeQuery("select order_group_attribute_id from order_group_attribute where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public Integer insertCohort(String name) {
        String uuid = uuid();
        db.executeUpdate(
                "insert into cohort (uuid, name, creator, date_created) values (?,?,?,?)",
                uuid, "Test Cohort", 1, dateCreated
        );
        return db.executeQuery("select cohort_id from cohort where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public Integer insertCohortMember(Integer patientId, Integer cohortId) {
        String uuid = uuid();
        db.executeUpdate(
                "insert into cohort_member (cohort_id, patient_id, uuid, creator, date_created) values (?,?,?,?,?)",
                cohortId, patientId, uuid, 1, dateCreated
        );
        return db.executeQuery("select cohort_member_id from cohort_member where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public Integer insertAppointmentType(String name, Integer duration) {
        String uuid = uuid();
        db.executeUpdate(
                "insert into appointmentscheduling_appointment_type (uuid, name, duration, creator, date_created) values (?,?,?,?,?)",
                uuid, name, duration, 1, dateCreated
        );
        return db.executeQuery("select appointment_type_id from appointmentscheduling_appointment_type where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public Integer insertAppointmentBlock(Date startDate, Date endDate, Integer location) {
        String uuid = uuid();
        db.executeUpdate(
                "insert into appointmentscheduling_appointment_block (uuid, start_date, end_date, location_id, creator, date_created) values (?,?,?,?,?,?)",
                uuid, startDate, endDate, location, 1, dateCreated
        );
        return db.executeQuery("select appointment_block_id from appointmentscheduling_appointment_block where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public Integer insertAppointmentTimeSlot(Integer appointmentBlock, Date startDate, Date endDate) {
        String uuid = uuid();
        db.executeUpdate(
                "insert into appointmentscheduling_time_slot (uuid, appointment_block_id, start_date, end_date, creator, date_created) values (?,?,?,?,?,?)",
                uuid, appointmentBlock, startDate, endDate, 1, dateCreated
        );
        return db.executeQuery("select time_slot_id from appointmentscheduling_time_slot where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public Integer insertAppointment(Integer patientId, Integer slotId, Integer typeId, String status) {
        String uuid = uuid();
        db.executeUpdate(
                "insert into appointmentscheduling_appointment (uuid, patient_id, time_slot_id, appointment_type_id, status, creator, date_created) values (?,?,?,?,?,?,?)",
                uuid, patientId, slotId, typeId, status, 1, dateCreated
        );
        return db.executeQuery("select appointment_id from appointmentscheduling_appointment where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public Integer insertAppointmentStatusHistory(Integer appointmentId, String status, Date startDate, Date endDate) {
        String uuid = uuid();
        db.executeUpdate(
                "insert into appointmentscheduling_appointment_status_history (appointment_id, status, start_date, end_date) values (?,?,?,?)",
                appointmentId, status, startDate, endDate
        );
        return db.executeQuery("select max(appointment_status_history_id) from appointmentscheduling_appointment_status_history where appointment_id = ?", new ScalarHandler<>(), appointmentId);
    }

    public Integer insertAppointmentRequest(Integer patientId, Integer typeId, Date requestedOn, String status) {
        String uuid = uuid();
        db.executeUpdate(
                "insert into appointmentscheduling_appointment_request (patient_id, uuid, appointment_type_id, status, requested_on, creator, date_created) values (?,?,?,?,?,?,?)",
                patientId, uuid, typeId, status, requestedOn, 1, dateCreated
        );
        return db.executeQuery("select appointment_request_id from appointmentscheduling_appointment_request where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public Integer insertFhirDiagnosticReport(Integer patientId, Integer encounterId, String status) {
        String uuid = uuid();
        db.executeUpdate(
                "insert into fhir_diagnostic_report (uuid, subject_id, encounter_id, status, creator, date_created) values (?,?,?,?,?,?)",
                uuid, patientId, encounterId, status, 1, dateCreated
        );
        return db.executeQuery("select diagnostic_report_id from fhir_diagnostic_report where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public void insertFhirDiagnosticReportPerformer(Integer diagnosticReportId, Integer providerId) {
        db.executeUpdate(
                "insert into fhir_diagnostic_report_performers (diagnostic_report_id, provider_id) values (?,?)",
                diagnosticReportId, providerId
        );
    }

    public void insertFhirDiagnosticReportResults(Integer diagnosticReportId, Integer obsId) {
        db.executeUpdate(
                "insert into fhir_diagnostic_report_results (diagnostic_report_id, obs_id) values (?,?)",
                diagnosticReportId, obsId
        );
    }

    public Integer insertConceptProposal(Integer encounterId, Integer obsId, String originalText) {
        String uuid = uuid();
        db.executeUpdate(
                "insert into concept_proposal (uuid, encounter_id, obs_id, original_text, creator, date_created) values (?,?,?,?,?,?)",
                uuid, encounterId, obsId, originalText, 1, dateCreated
        );
        return db.executeQuery("select concept_proposal_id from concept_proposal where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public void insertConceptProposalTagMap(Integer conceptProposalId, Integer conceptNameTagId) {
        db.executeUpdate(
                "insert into concept_proposal_tag_map (concept_proposal_id, concept_name_tag_id) values (?,?)",
                conceptProposalId, conceptNameTagId
        );
    }

    public Integer insertNamePhonetic(Integer personNameId, int field, String renderedString) {
        db.executeUpdate(
                "insert into name_phonetics (person_name_id, field, rendered_string, renderer_class_name) values (?,?,?,?)",
                personNameId, field, renderedString, "org.openmrs.module.namephonetics.phoneticsalgorithm.DoubleMetaphoneAlternate"
        );
        return db.executeQuery("select name_phonetics_id from name_phonetics where person_name_id = ? and field = ?",
                new ScalarHandler<>(), personNameId, field);
    }

    public Integer insertAddressHierarcyAddressToEntryMap(Integer addressId, Integer entryId) {
        String uuid = uuid();
        db.executeUpdate(
                "insert into address_hierarchy_address_to_entry_map (uuid, address_id, entry_id) values (?,?,?)",
                uuid, addressId, entryId
        );
        return db.executeQuery("select address_to_entry_map_id from address_hierarchy_address_to_entry_map where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public Integer insertAttributeType(String domain, String name) {
        String uuid = uuid();
        String table = domain + "_attribute_type";
        String id = table + "_id";
        db.executeUpdate(
                "insert into " + table + " (uuid, name, min_occurs, creator, date_created) values (?,?,?,?,?)",
                uuid, name, 0, 1, dateCreated
        );
        return db.executeQuery("select " + id + " from " + table + " where uuid = ?", new ScalarHandler<>(), uuid);
    }

    public Integer insertConceptNameTag(String tag) {
        String uuid = uuid();
        db.executeUpdate(
                "insert into concept_name_tag (uuid, tag, creator, date_created) values (?,?,?,?)",
                uuid, tag, 1, dateCreated
        );
        return db.executeQuery("select concept_name_tag_id from concept_name_tag where uuid = ?", new ScalarHandler<>(), uuid);

    }

    public void updateTable(String table, String column, Object value, String idColumn, Integer idValue) {
        db.executeUpdate("update " + table + " set " + column + " = ? where " + idColumn + " = ?", value, idValue);
    }

    private static String uuid() {
        return UUID.randomUUID().toString();
    }
    
    private Integer patientForEncounter(Integer encounterId) {
        return db.executeQuery("select patient_id from encounter where encounter_id = ?", new ScalarHandler<>(), encounterId);
    }

    private Integer conceptId(String name) {
        return db.executeQuery("select concept_id from concept_name where name = ?", new ScalarHandler<>(), name);
    }

    public Date getDateCreated() {
        return dateCreated;
    }
}
