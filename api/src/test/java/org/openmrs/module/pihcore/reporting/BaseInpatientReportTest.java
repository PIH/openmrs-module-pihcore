package org.openmrs.module.pihcore.reporting;

import org.junit.Before;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.VisitService;
import org.openmrs.module.pihcore.deploy.bundle.core.VisitTypeBundle;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.pihcore.metadata.core.EncounterTypes;
import org.openmrs.module.pihcore.metadata.haiti.mirebalais.MirebalaisLocations;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;

@SkipBaseSetup
public abstract class BaseInpatientReportTest extends BaseReportTest {

    protected Patient patient1;
    protected Patient patient2;
    protected Patient patient3;
    protected Patient patient4;
    protected Patient patient5;
    protected Patient patient6;

    @Autowired
    private ConceptService conceptService;

    @Autowired
    private VisitService visitService;

    @Before
    public void setupDontOverrideBaseClassSetup() throws Exception {

        VisitType atFacility = visitService.getVisitTypeByUuid(VisitTypeBundle.VisitTypes.CLINIC_OR_HOSPITAL_VISIT);
        EncounterType checkIn = Metadata.lookup(EncounterTypes.CHECK_IN);
        EncounterType admission = Metadata.lookup(EncounterTypes.ADMISSION);
        EncounterType transfer = Metadata.lookup(EncounterTypes.TRANSFER);
        EncounterType exit = Metadata.lookup(EncounterTypes.EXIT_FROM_CARE);
        EncounterType consultation = Metadata.lookup(EncounterTypes.CONSULTATION);
        EncounterType postOpNote = Metadata.lookup(EncounterTypes.POST_OPERATIVE_NOTE);
        Location visitLocation = Metadata.lookup(MirebalaisLocations.MIREBALAIS_CDI_PARENT);
        Location outpatientClinic = Metadata.lookup(MirebalaisLocations.OUTPATIENT_CLINIC);
        Location womensInternalMedicine = Metadata.lookup(MirebalaisLocations.WOMENS_INTERNAL_MEDICINE);
        Location mensInternalMedicine = Metadata.lookup(MirebalaisLocations.MENS_INTERNAL_MEDICINE);
        Location surgicalWard = Metadata.lookup(MirebalaisLocations.SURGICAL_WARD);
        Location emergencyDepartment = Metadata.lookup(MirebalaisLocations.EMERGENCY);
        Concept dispositionConcept = conceptService.getConceptByUuid("c8b22b09-e2f2-4606-af7d-e52579996de3");

        // Already admitted at start of 3 Oct (Women's Internal Medicine)
        patient1 = data.randomPatient().save();
        Visit visit1 = data.visit().patient(patient1).visitType(atFacility)
                .started("2013-10-02 09:15:00").stopped("2013-10-14 04:30:00")
                .location(visitLocation).save();
        Encounter enc1a = data.encounter().visit(visit1).encounterType(checkIn)
                .location(outpatientClinic).encounterDatetime("2013-10-02 09:15:00").save();
        Encounter enc1b = data.encounter().visit(visit1).encounterType(admission)
                .location(womensInternalMedicine).encounterDatetime("2013-10-02 12:30:00").save();

        // Admitted and discharged the days before. Visit extends into 3 Oct, but they've already been discharged at that point. (I.e. not an inpatient on Oct 3.)
        patient2 = data.randomPatient().save();
        Visit visit2 = data.visit().patient(patient2).visitType(atFacility)
                .started("2013-10-01 17:30:00").stopped("2013-10-03 12:45:00")
                .location(visitLocation).save();
        data.encounter().visit(visit2).encounterType(checkIn)
                .location(outpatientClinic).encounterDatetime("2013-10-01 17:30:00").save();
        data.encounter().visit(visit2).encounterType(admission)
                .location(womensInternalMedicine).encounterDatetime("2013-10-01 18:30:00").save();
        data.encounter().visit(visit2).encounterType(exit)
                .location(womensInternalMedicine).encounterDatetime("2013-10-02 23:45:00").save();

        // Admitted during the day of 3 Oct (Men's Internal Medicine)
        patient3 = data.randomPatient().save();
        Visit visit3a = data.visit().patient(patient3).visitType(atFacility)
                .started("2013-10-03 12:34:00").stopped("2013-10-07 12:45:00")
                .location(visitLocation).save();
        data.encounter().visit(visit3a).encounterType(checkIn)
                .location(outpatientClinic).encounterDatetime("2013-10-03 12:34:00").save();
        data.encounter().visit(visit3a).encounterType(admission)
                .location(mensInternalMedicine).encounterDatetime("2013-10-03 13:57:00").save();
        Encounter encounter3a = data.encounter().visit(visit3a).encounterType(exit)
                .location(mensInternalMedicine).encounterDatetime("2013-10-07 12:45:00").save();
        data.obs().encounter(encounter3a).concept(dispositionConcept).value("Transfer out of hospital", "PIH").save();
        // This was a possible readmission, because the patient was admitted and left the previous day
        Visit visit3b = data.visit().patient(patient3).visitType(atFacility)
                .started("2013-10-02 12:34:00").stopped("2013-10-02 16:45:00")
                .location(visitLocation).save();
        data.encounter().visit(visit3b).encounterType(admission)
                .location(mensInternalMedicine).encounterDatetime("2013-10-02 12:34:00").save();
        Encounter encounter3b = data.encounter().visit(visit3b).encounterType(exit)
                .location(mensInternalMedicine).encounterDatetime("2013-10-02 16:45:00").save();
        data.obs().encounter(encounter3b).concept(dispositionConcept).value("Departed without medical discharge", "PIH").save();

        // Admitted earlier (Men's Internal Medicine), and discharged on 3 Oct
        patient4 = data.randomPatient().save();
        Visit visit4 = data.visit().patient(patient4).visitType(atFacility)
                .started("2013-10-01 12:34:00").stopped("2013-10-03 12:45:00")
                .location(visitLocation).save();
        data.encounter().visit(visit4).encounterType(checkIn)
                .location(outpatientClinic).encounterDatetime("2013-10-01 12:34:00").save();
        data.encounter().visit(visit4).encounterType(admission)
                .location(mensInternalMedicine).encounterDatetime("2013-10-01 13:57:00").save();
        data.encounter().visit(visit4).encounterType(exit)
                .location(mensInternalMedicine).encounterDatetime("2013-10-03 12:45:00").save();
        // consult with discharge
        Encounter encounter4a = data.encounter().visit(visit4).encounterType(consultation)
                .location(mensInternalMedicine).encounterDatetime("2013-10-03 12:15:00").save();
        data.obs().encounter(encounter4a).concept(dispositionConcept).value("DISCHARGED", "PIH").save();

        // Begins the day of 3 Oct at Women's Inpatient (was admitted earlier), and transferred to Surgical Ward during the day
        patient5 = data.randomPatient().save();
        Visit visit5 = data.visit().patient(patient5).visitType(atFacility)
                .started("2013-10-01 12:34:00").stopped("2013-10-13 12:45:00")
                .location(visitLocation).save();
        data.encounter().visit(visit5).encounterType(checkIn)
                .location(outpatientClinic).encounterDatetime("2013-10-01 12:34:00").save();
        data.encounter().visit(visit5).encounterType(admission)
                .location(womensInternalMedicine).encounterDatetime("2013-10-01 13:57:00").save();
        data.encounter().visit(visit5).encounterType(transfer)
                .location(surgicalWard).encounterDatetime("2013-10-03 12:45:00").save();

        //  Checks into ED during the day, transferred to surgery (with no admission), and has surgery
        patient6 = data.randomPatient().save();
        Visit visit6 = data.visit().patient(patient6).visitType(atFacility)
                .started("2013-10-03 10:05:00").stopped("2013-10-03 18:32:21")
                .location(visitLocation).save();
        data.encounter().visit(visit6).encounterType(checkIn)
                .location(emergencyDepartment).encounterDatetime("2013-10-03 10:05:00").save();
        data.encounter().visit(visit6).encounterType(transfer)
                .location(surgicalWard).encounterDatetime("2013-10-03 12:45:00").save();
        data.encounter().visit(visit6).encounterType(postOpNote)
                .location(surgicalWard).encounterDatetime("2013-10-03 14:53:00").save();
    }

}
