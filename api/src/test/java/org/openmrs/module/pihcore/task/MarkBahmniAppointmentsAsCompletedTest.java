package org.openmrs.module.pihcore.task;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.module.appointments.model.AppointmentStatus;
import org.openmrs.module.appointments.service.AppointmentsService;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class MarkBahmniAppointmentsAsCompletedTest extends PihCoreContextSensitiveTest {

    @Autowired
    AppointmentsService appointmentsService;

    MarkBahmniAppointmentsAsCompleted task;

    @BeforeEach
    public void before() throws Exception {
        executeDataSet("bahmniAppointmentsTestDataset.xml");
        task = new MarkBahmniAppointmentsAsCompleted();
    }


    @Test
    public void shouldMarkAppointmentsAsCompleted() {
        task.run();
        assertThat(appointmentsService.getAppointmentByUuid("75504r42-3ca8-11e3-bf2b-0800271c1b77").getStatus(), is(AppointmentStatus.Completed)); // associated with completed visit, should be marked complete
        assertThat(appointmentsService.getAppointmentByUuid("83457034-f65a-4d90-8558-a26cce155789").getStatus(), is(AppointmentStatus.CheckedIn)); // associated with a visit, but visit is not completed, should still be checked in
        assertThat(appointmentsService.getAppointmentByUuid("cc230ca4-cc64-428f-93c6-5401eb036ffb").getStatus(), is(AppointmentStatus.Scheduled)); // associated with completed visit, but status is "Scheduled", so should not be changed
        assertThat(appointmentsService.getAppointmentByUuid("26b095a1-5f6a-4c6c-9d79-823c05ce1626").getStatus(), is(AppointmentStatus.CheckedIn)); // not associated with any encounters, so status should be not be changed
        assertThat(appointmentsService.getAppointmentByUuid("bd1861d7-22b7-44de-b8ef-15976245bc85").getStatus(), is(AppointmentStatus.CheckedIn)); // associated with an encounter, but that encounter does not have a visit, so status should be not be changed
        assertThat(appointmentsService.getAppointmentByUuid("8c33099b-377d-4a99-8a3e-3abb14fc45b7").getStatus(), is(AppointmentStatus.CheckedIn)); // with two encounters and two visits, one visit is completed but the other is not, so should not be changed to completed

    }
}

