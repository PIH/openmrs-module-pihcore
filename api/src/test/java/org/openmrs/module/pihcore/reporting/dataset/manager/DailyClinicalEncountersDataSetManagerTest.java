package org.openmrs.module.pihcore.reporting.dataset.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Visit;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.pihcore.ZlConfigConstants;
import org.openmrs.module.pihcore.reporting.BaseReportTest;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.config.DataSetDescriptor;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.openmrs.module.pihcore.reporting.ReportingMatchers.isCohortWithExactlyMembers;

@SkipBaseSetup
public class DailyClinicalEncountersDataSetManagerTest extends BaseReportTest {

    @Autowired
    DailyClinicalEncountersDataSetManager manager;

    @Autowired @Qualifier("reportingDataSetDefinitionService")
    DataSetDefinitionService dataSetDefinitionService;

    @Autowired
    TestDataManager testData;

    private Patient p2, p3, p4;

    @BeforeEach
    public void setUp() throws Exception {
        EmrApiProperties eap = emrApiProperties;

        PatientIdentifierType zlemrId = patientService.getPatientIdentifierTypeByUuid(ZlConfigConstants.PATIENTIDENTIFIERTYPE_ZLEMRID_UUID);
        Location registrationDesk = locationService.getLocation("Biwo Resepsyon");
        Location outpatient = locationService.getLocation("Klinik Ekstèn");
        Location mirebalaisHospital = locationService.getLocation("Hôpital Universitaire de Mirebalais - Prensipal");
        EncounterType registration = getRegistrationEncounterType();
        EncounterType checkIn = getCheckInEncounterType();
        EncounterType vitals = getVitalsEncounterType();
        EncounterType consult = getConsultationEncounterType();

        // never registered or seen
        testData.patient().name("Mary", "Rodriguez").gender("F").birthdate("1946-05-26", false).dateCreated("2013-10-01").identifier(zlemrId, "Y2ARM5", mirebalaisHospital).save();

        // registered at Clinic Registration, checked in at Outpatient Clinic, had vitals and consultation
        p2 = testData.patient().name("Alice", "Smith").gender("F").birthdate("1975-01-02", false).dateCreated("2013-10-01").identifier(zlemrId, "Y2ATDN", mirebalaisHospital).save();
        Visit v1 = testData.visit().patient(p2).visitType(eap.getAtFacilityVisitType()).started("2013-10-01 09:30:00").stopped("2013-10-01 16:45:00").location(mirebalaisHospital).save();
        testData.encounter().visit(v1).encounterType(registration).location(registrationDesk).encounterDatetime("2013-10-01 09:30:00").save();
        Encounter p2CheckIn = testData.encounter().visit(v1).encounterType(checkIn).location(outpatient).encounterDatetime("2013-10-01 09:45:00").save();
        testData.obs().encounter(p2CheckIn).concept("Type of HUM visit", "PIH").value("CLINICAL", "PIH").save();
        testData.encounter().visit(v1).encounterType(vitals).location(outpatient).encounterDatetime("2013-10-01 10:00:00").save();
        testData.encounter().visit(v1).encounterType(consult).location(outpatient).encounterDatetime("2013-10-01 10:15:00").save();

        // registered long ago, checked in at Outpatient Clinic, had vitals but no consultation
        p3 = testData.patient().name("Gamma", "Helm").gender("M").birthdate("1985-01-01", false).dateCreated("2013-01-01").identifier(zlemrId, "Y2AVWK", mirebalaisHospital).save();
        testData.encounter().patient(p3).encounterType(registration).location(registrationDesk).encounterDatetime("2013-01-01 09:30:00").save();
        Visit v2 = testData.visit().patient(p3).visitType(eap.getAtFacilityVisitType()).started("2013-10-01 10:30:00").stopped("2013-10-01 16:45:00").location(mirebalaisHospital).save();
        Encounter p3checkIn = testData.encounter().visit(v2).encounterType(checkIn).location((outpatient)).encounterDatetime("2013-10-01 10:45:00").save();
        testData.obs().encounter(p3checkIn).concept("Type of HUM visit", "PIH").value("Pharmacy only", "PIH").save();
        testData.encounter().visit(v2).encounterType(vitals).location(outpatient).encounterDatetime("2013-10-01 11:00:00").save();

        // registered at Clinic Registration, checked in at Outpatient Clinic, skipped vitals but had consultation
        p4 = testData.patient().name("Johnson", "Vlissides").gender("M").birthdate("1965-01-02", false).dateCreated("2013-10-01").identifier(zlemrId, "2H5GGF", mirebalaisHospital).save();
        Visit v3 = testData.visit().patient(p4).visitType(eap.getAtFacilityVisitType()).started("2013-10-01 09:30:00").stopped("2013-10-01 16:45:00").location(mirebalaisHospital).save();
        testData.encounter().visit(v3).encounterType(registration).location(registrationDesk).encounterDatetime("2013-10-01 09:30:00").save();
        Encounter p4CheckIn = testData.encounter().visit(v3).encounterType(checkIn).location(outpatient).encounterDatetime("2013-10-01 09:45:00").save();
        testData.obs().encounter(p4CheckIn).concept("Type of HUM visit", "PIH").value("CLINICAL", "PIH").save();
        testData.encounter().visit(v3).encounterType(consult).location(outpatient).encounterDatetime("2013-10-01 10:15:00").save();
    }

    @Test
    public void testReport() throws Exception {
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("day", DateUtil.parseDate("2013-10-01", "yyyy-MM-dd"));

        DataSetDescriptor byLocationDescriptor = new DataSetDescriptor();
        byLocationDescriptor.setKey("byLocation");
        DataSetDefinition dataSetDefinition = manager.constructDataSetDefinition(byLocationDescriptor, null);

        DataSet byLocation = dataSetDefinitionService.evaluate(dataSetDefinition, context);

        for (DataSetRow row : byLocation) {
            if (row.getColumnValue("rowLabel").equals("Klinik Ekstèn")) {
                assertThat((Cohort) row.getColumnValue("clinicalCheckIns"), isCohortWithExactlyMembers(p2, p4));
                assertThat((Cohort) row.getColumnValue("vitals"), isCohortWithExactlyMembers(p2, p3));
                assertThat((Cohort) row.getColumnValue("consults"), isCohortWithExactlyMembers(p2, p4));
                assertThat((Cohort) row.getColumnValue("consultWithoutVitals"), isCohortWithExactlyMembers(p4));
            }
            else {
                assertThat((Cohort) row.getColumnValue("clinicalCheckIns"), isCohortWithExactlyMembers());
                assertThat((Cohort) row.getColumnValue("vitals"), isCohortWithExactlyMembers());
                assertThat((Cohort) row.getColumnValue("consults"), isCohortWithExactlyMembers());
                assertThat((Cohort) row.getColumnValue("consultWithoutVitals"), isCohortWithExactlyMembers());
            }
        }
    }

}
