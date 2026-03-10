package org.openmrs.module.pihcore.listener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.module.idgen.IdgenModuleActivator;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;
import org.openmrs.module.pihcore.ZlConfigConstants;

import javax.jms.MapMessage;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GeneratePrEPIdentifierListenerTest extends PihCoreContextSensitiveTest {

    private GeneratePrEPIdentifierListenerTask listenerTask;
    private PatientService patientService;
    private ProgramWorkflowService programWorkflowService;
    private PatientIdentifierType prepIdentifierType;
    private Program prepProgram;
    private Program otherProgram;
    private Location location;

    @BeforeEach
    public void setUp() {
        new IdgenModuleActivator().started();
        executeDataSet("generatePrEPIdentifierListenerTestDataset.xml");

        patientService = Context.getPatientService();
        programWorkflowService = Context.getProgramWorkflowService();

        prepIdentifierType = patientService.getPatientIdentifierTypeByUuid(ZlConfigConstants.PATIENTIDENTIFIERTYPE_PREPCODE_UUID);
        prepProgram = programWorkflowService.getProgramByUuid(ZlConfigConstants.PROGRAM_PREP_UUID);
        otherProgram = programWorkflowService.getProgramByUuid("550e8400-e29b-41d4-a716-446655440000");
        location = Context.getLocationService().getLocationByUuid("123e4567-e89b-12d3-a456-426614174000");
    }

    @Test
    public void onMessage_shouldGeneratePrEPIdentifierWhenPatientEnrolledInPrEPProgram() throws Exception {
        // load patient from standart test dataset
        Patient patient = patientService.getPatient(2);

        // Enroll patient in PrEP program
        PatientProgram patientProgram = new PatientProgram();
        patientProgram.setPatient(patient);
        patientProgram.setProgram(prepProgram);
        patientProgram.setLocation(location);
        patientProgram.setDateEnrolled(new Date());
        patientProgram = programWorkflowService.savePatientProgram(patientProgram);

        // Verify patient doesn't have PrEP identifier yet
        assertNull(patient.getPatientIdentifier(prepIdentifierType));

        // Create mock message
        MapMessage message = mock(MapMessage.class);
        when(message.getString("classname")).thenReturn("org.openmrs.PatientProgram");
        when(message.getString("uuid")).thenReturn(patientProgram.getUuid());
        when(message.getString("action")).thenReturn(Event.Action.CREATED.name());

        // Process message
        listenerTask = new GeneratePrEPIdentifierListenerTask(message);
        listenerTask.run();

        // Verify identifier was generated
        Context.flushSession();
        Context.clearSession();
        patient = patientService.getPatient(patient.getPatientId());
        PatientIdentifier prepIdentifier = patient.getPatientIdentifier(prepIdentifierType);
        assertNotNull(prepIdentifier);
        assertNotNull(prepIdentifier.getIdentifier());
        assertTrue(prepIdentifier.getIdentifier().startsWith("PREP"));
        assertEquals(location, prepIdentifier.getLocation());
    }

    @Test
    public void onMessage_shouldNotGeneratePrEPIdentifierWhenPatientAlreadyHasOne() throws Exception {
        // Create patient
        Patient patient = new Patient();
        patient.setGender("M");
        patient.addName(Context.getPersonService().parsePersonName("Jane Doe"));
        patientService.savePatient(patient);

        // Manually add PrEP identifier
        PatientIdentifier existingIdentifier = new PatientIdentifier("PREP000099", prepIdentifierType, location);
        patient.addIdentifier(existingIdentifier);
        patientService.savePatient(patient);

        // Enroll patient in PrEP program
        PatientProgram patientProgram = new PatientProgram();
        patientProgram.setPatient(patient);
        patientProgram.setProgram(prepProgram);
        patientProgram.setLocation(location);
        patientProgram.setDateEnrolled(new Date());
        patientProgram = programWorkflowService.savePatientProgram(patientProgram);

        // Create mock message
        MapMessage message = mock(MapMessage.class);
        when(message.getString("classname")).thenReturn("org.openmrs.PatientProgram");
        when(message.getString("uuid")).thenReturn(patientProgram.getUuid());
        when(message.getString("action")).thenReturn(Event.Action.CREATED.name());

        // Process message
        listenerTask = new GeneratePrEPIdentifierListenerTask(message);
        listenerTask.run();

        // Verify patient still has the original identifier
        Context.flushSession();
        Context.clearSession();
        patient = patientService.getPatient(patient.getPatientId());
        PatientIdentifier prepIdentifier = patient.getPatientIdentifier(prepIdentifierType);
        assertNotNull(prepIdentifier);
        assertEquals("PREP000099", prepIdentifier.getIdentifier());
    }

    @Test
    public void onMessage_shouldNotGeneratePrEPIdentifierWhenEnrolledInDifferentProgram() throws Exception {
        // Create patient
        Patient patient = new Patient();
        patient.setGender("M");
        patient.addName(Context.getPersonService().parsePersonName("Bob Smith"));
        patientService.savePatient(patient);

        // Enroll patient in different program
        PatientProgram patientProgram = new PatientProgram();
        patientProgram.setPatient(patient);
        patientProgram.setProgram(otherProgram);
        patientProgram.setLocation(location);
        patientProgram.setDateEnrolled(new Date());
        patientProgram = programWorkflowService.savePatientProgram(patientProgram);

        // Create mock message
        MapMessage message = mock(MapMessage.class);
        when(message.getString("classname")).thenReturn("org.openmrs.PatientProgram");
        when(message.getString("uuid")).thenReturn(patientProgram.getUuid());
        when(message.getString("action")).thenReturn(Event.Action.CREATED.name());

        // Process message
        listenerTask = new GeneratePrEPIdentifierListenerTask(message);
        listenerTask.run();

        // Verify no identifier was generated
        Context.flushSession();
        Context.clearSession();
        patient = patientService.getPatient(patient.getPatientId());
        assertNull(patient.getPatientIdentifier(prepIdentifierType));
    }

    @Test
    public void onMessage_shouldNotGeneratePrEPIdentifierWhenPatientProgramHasNoLocation() throws Exception {
        // Create patient
        Patient patient = new Patient();
        patient.setGender("M");
        patient.addName(Context.getPersonService().parsePersonName("Alice Johnson"));
        patientService.savePatient(patient);

        // Enroll patient in PrEP program without location
        PatientProgram patientProgram = new PatientProgram();
        patientProgram.setPatient(patient);
        patientProgram.setProgram(prepProgram);
        patientProgram.setLocation(null);  // No location
        patientProgram.setDateEnrolled(new Date());
        patientProgram = programWorkflowService.savePatientProgram(patientProgram);

        // Create mock message
        MapMessage message = mock(MapMessage.class);
        when(message.getString("classname")).thenReturn("org.openmrs.PatientProgram");
        when(message.getString("uuid")).thenReturn(patientProgram.getUuid());
        when(message.getString("action")).thenReturn(Event.Action.CREATED.name());

        // Process message
        listenerTask = new GeneratePrEPIdentifierListenerTask(message);
        listenerTask.run();

        // Verify no identifier was generated (location is required)
        Context.flushSession();
        Context.clearSession();
        patient = patientService.getPatient(patient.getPatientId());
        assertNull(patient.getPatientIdentifier(prepIdentifierType));
    }
}
