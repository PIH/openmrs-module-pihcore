package org.openmrs.module.pihcore.task;

import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Location;
import org.openmrs.Visit;
import org.openmrs.api.LocationService;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.metadatamapping.MetadataSource;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.openmrs.module.appointmentscheduling.Appointment.AppointmentStatus;

public class MarkAppointmentAsMissedOrCompletedTaskTest extends PihCoreContextSensitiveTest {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private EmrApiProperties emrApiProperties;

    @Autowired
    private TestDataManager testDataManager;

    @Autowired
    private MetadataMappingService metadataMappingService;

    @BeforeEach
    public void before() throws Exception {
        executeDataSet("appointmentTestDataset.xml");
        createEmrApiMappingSource(metadataMappingService);
        loadFromInitializer(Domain.ENCOUNTER_ROLES, "encounterRoles.csv");
        loadFromInitializer(Domain.ENCOUNTER_TYPES, "encounterTypes.csv");
        loadFromInitializer(Domain.VISIT_TYPES, "visitTypes.csv");
        loadFromInitializer(Domain.METADATA_TERM_MAPPINGS, "metadataMappings.csv");
    }

    protected void createEmrApiMappingSource(MetadataMappingService metadataMappingService) {
        MetadataSource source = new MetadataSource();
        source.setName(EmrApiConstants.EMR_METADATA_SOURCE_NAME);
        metadataMappingService.saveMetadataSource(source);
    }

    @Test
    public void shouldMarkPastScheduledAppointmentsAsMissed() {

        new MarkAppointmentsAsMissedOrCompletedTask().run();

        assertThat(appointmentService.getAppointment(1).getStatus(), is(AppointmentStatus.MISSED));
        assertThat(appointmentService.getAppointment(2).getStatus(), is(AppointmentStatus.MISSED));

        // status of other appointments should not be changed
        assertThat(appointmentService.getAppointment(3).getStatus(), is(AppointmentStatus.COMPLETED));
        assertThat(appointmentService.getAppointment(4).getStatus(), is(AppointmentStatus.INCONSULTATION));
        assertThat(appointmentService.getAppointment(5).getStatus(), is(AppointmentStatus.CANCELLED));
        assertThat(appointmentService.getAppointment(6).getStatus(), is(AppointmentStatus.CANCELLED_AND_NEEDS_RESCHEDULE));
        assertThat(appointmentService.getAppointment(7).getStatus(), is(AppointmentStatus.WALKIN));
        assertThat(appointmentService.getAppointment(8).getStatus(), is(AppointmentStatus.WAITING));

    }

    @Test
    public void shouldNotMarkFutureAppointmentsAsMissedOrCompleted() {

        new MarkAppointmentsAsMissedOrCompletedTask().run();

        assertThat(appointmentService.getAppointment(9).getStatus(), is(AppointmentStatus.SCHEDULED));
        assertThat(appointmentService.getAppointment(10).getStatus(), is(AppointmentStatus.WAITING));

    }

    @Test
    public void shouldMarkActiveAppointmentsAsCompleteIfConsultAsPartOfVisit() {

        new MarkAppointmentsAsMissedOrCompletedTask().run();

        // sanity check: status isn't changed by default, since there are no consultations associated with this visit
        assertThat(appointmentService.getAppointment(4).getStatus(), is(AppointmentStatus.INCONSULTATION));
        assertThat(appointmentService.getAppointment(7).getStatus(), is(AppointmentStatus.WALKIN));
        assertThat(appointmentService.getAppointment(8).getStatus(), is(AppointmentStatus.WAITING));

        Location location1 = locationService.getLocation(1);
        Location location3 = locationService.getLocation(3);  // location of the appointments, as defined in test dataset

        // now add a visit to one appointment, and a visit with a consult to the other
        Appointment appt4 = appointmentService.getAppointment(4);
        Visit visit4 = testDataManager.visit()
                .patient(appt4.getPatient())
                .visitType(1)
                .started(new DateTime(2005, 1, 1, 0, 0, 0).toDate())
                .save();
        appt4.setVisit(visit4);
        appointmentService.saveAppointment(appt4);

        Appointment appt7 = appointmentService.getAppointment(7);
        Visit visit7 = testDataManager.visit()
                .patient(appt7.getPatient())
                .visitType(1)
                .started(new DateTime(2005, 1, 1, 0, 0, 0).toDate())
                .encounter(testDataManager.encounter()
                        .patient(appt7.getPatient())
                        .encounterDatetime(new DateTime(2005, 1, 1, 0, 0, 0).toDate())
                        .encounterType(getConsultationEncounterType())
                        .location(location1)
                        .save())
                .save();
        appt7.setVisit(visit7);
        appointmentService.saveAppointment(appt7);

        Appointment appt8 = appointmentService.getAppointment(8);
        Visit visit8 = testDataManager.visit()
                .patient(appt8.getPatient())
                .visitType(1)
                .started(new DateTime(2005, 1, 1, 0, 0, 0).toDate())
                .encounter(testDataManager.encounter()
                        .patient(appt8.getPatient())
                        .encounterDatetime(new DateTime(2005, 1, 1, 0, 0, 0).toDate())
                        .encounterType(emrApiProperties.getVisitNoteEncounterType())
                        .location(location3)
                        .save())
                .save();
        appt8.setVisit(visit8);
        appointmentService.saveAppointment(appt8);

        // run the task again
        new MarkAppointmentsAsMissedOrCompletedTask().run();

        // should not be changed because associated visit did not have consult
        assertThat(appointmentService.getAppointment(4).getStatus(), is(AppointmentStatus.INCONSULTATION));

        // should not be changed since associated visit did not have consult at location
        assertThat(appointmentService.getAppointment(7).getStatus(), is(AppointmentStatus.WALKIN));

        // should be changed to COMPLETED since associated visit had consult at location
        assertThat(appointmentService.getAppointment(8).getStatus(), is(AppointmentStatus.COMPLETED));

    }

}
