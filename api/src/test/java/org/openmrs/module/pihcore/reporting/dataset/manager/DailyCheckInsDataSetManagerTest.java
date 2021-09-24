package org.openmrs.module.pihcore.reporting.dataset.manager;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitType;
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

import java.util.Map;

import static org.junit.Assert.assertThat;
import static org.openmrs.module.pihcore.reporting.ReportingMatchers.isCohortWithExactlyIds;
import static org.openmrs.module.pihcore.reporting.ReportingMatchers.isCohortWithExactlyMembers;


@SkipBaseSetup
public class DailyCheckInsDataSetManagerTest extends BaseReportTest {

    @Autowired
    DailyCheckInsDataSetManager manager;

    @Autowired @Qualifier("reportingDataSetDefinitionService")
    DataSetDefinitionService dataSetDefinitionService;

    private Patient p2, p3, p4;

    @Before
    @Override
    public void setup() throws Exception {
        super.setup();
        VisitType atFacility = emrApiProperties.getAtFacilityVisitType();
        Location registrationDesk = locationService.getLocation("Biwo Resepsyon");
        Location outpatient = locationService.getLocation("Klinik Ekstèn");
        Location mirebalaisHospital = locationService.getLocation("Hôpital Universitaire de Mirebalais - Prensipal");
        EncounterType registration = getRegistrationEncounterType();
        EncounterType checkIn = getCheckInEncounterType();
        EncounterType consult = getConsultationEncounterType();

        // never registered or seen
        data.randomPatient().age(50).dateCreated("2013-10-01").save();

        // registered at Clinic Registration, checked in at Outpatient Clinic for a CLINICAL visit (and had a consult)
        p2 = data.randomPatient().age(50).save();
        Visit v1 = data.visit().patient(p2).visitType(atFacility).started("2013-10-01 09:30:00").stopped("2013-10-01 16:45:00").location(mirebalaisHospital).save();
        data.encounter().visit(v1).encounterType(registration).location(registrationDesk).encounterDatetime("2013-10-01 09:30:00").save();
        Encounter p2CheckIn = data.encounter().visit(v1).encounterType(checkIn).location(outpatient).encounterDatetime("2013-10-01 09:45:00").save();
        data.obs().encounter(p2CheckIn).concept("Type of HUM visit", "PIH").value("CLINICAL", "PIH").save();
        data.encounter().visit(v1).encounterType(consult).encounterDatetime("2013-10-01 10:45:00").location(outpatient).save();

        // checked in at Outpatient Clinic for a Pharmacy only visit
        p3 = data.randomPatient().age(50).save();
        data.encounter().patient(p3).encounterType(registration).location(registrationDesk).encounterDatetime("2013-01-01 09:30:00").save();
        Visit v2 = data.visit().patient(p3).visitType(atFacility).started("2013-10-01 10:30:00").stopped("2013-10-01 16:45:00").location(mirebalaisHospital).save();
        Encounter p3checkIn = data.encounter().visit(v2).encounterType(checkIn).location(outpatient).encounterDatetime("2013-10-01 10:45:00").save();
        data.obs().encounter(p3checkIn).concept("Type of HUM visit", "PIH").value("Pharmacy only", "PIH").save();

        // registered before and had a consult, then checked in again today for a CLINICAL visit (but no consult yet)
        p4 = data.randomPatient().age(50).save();
        data.encounter().patient(p4).encounterType(consult).encounterDatetime("2009-01-01").location(outpatient).save();
        Visit v3 = data.visit().patient(p4).visitType(atFacility).started("2013-10-01 14:30:00").location(mirebalaisHospital).save();
        Encounter p4CheckIn = data.encounter().visit(v3).encounterType(checkIn).location(outpatient).encounterDatetime("2013-10-01 14:30:00").save();
        data.obs().encounter(p4CheckIn).concept("Type of HUM visit", "PIH").value("CLINICAL", "PIH").save();
    }

    @Test
    public void testReport() throws Exception {
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("day", DateUtil.parseDate("2013-10-01", "yyyy-MM-dd"));

        DataSetDescriptor byLocationDescriptor = new DataSetDescriptor();
        byLocationDescriptor.setKey("byLocation");
        DataSetDefinition byLocationDsd = manager.constructDataSetDefinition(byLocationDescriptor, null);

        DataSet byLocation = dataSetDefinitionService.evaluate(byLocationDsd, context);

        for (DataSetRow row : byLocation) {
            if (row.getColumnValue("rowLabel").equals("Klinik Ekstèn")) {
                for (Map.Entry<String, Object> e : row.getColumnValuesByKey().entrySet()) {
                    if ("mirebalaisreports.dailyCheckInEncounters.CLINICAL_new".equals(e.getKey())) {
                        assertThat((Cohort) e.getValue(), isCohortWithExactlyMembers(p2));
                    }
                    else if ("mirebalaisreports.dailyCheckInEncounters.CLINICAL_return".equals(e.getKey())) {
                        assertThat((Cohort) e.getValue(), isCohortWithExactlyMembers(p4));
                    }
                    else if ("Pharmacy only".equals(e.getKey())) {
                        assertThat((Cohort) e.getValue(), isCohortWithExactlyMembers(p3));
                    }
                    else if (!"rowLabel".equals(e.getKey())) {
                        assertThat((Cohort) e.getValue(), isCohortWithExactlyIds());
                    }
                }
            }
            else {
                // everything else should be empty
                for (Map.Entry<String, Object> e : row.getColumnValuesByKey().entrySet()) {
                    if (!"rowLabel".equals(e.getKey())) {
                        assertThat((Cohort) e.getValue(), isCohortWithExactlyIds());
                    }
                }
            }
        }
    }

}
