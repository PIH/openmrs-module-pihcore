/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.pihcore.merge;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.messagesource.MessageSourceService;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PihValidationMergeActionsTest {

    PihValidationMergeActions actions;

    Patient patient1 = new Patient();

    Patient patient2 = new Patient();

    Program program = new Program();

    PatientProgram patientProgram1;

    PatientProgram patientProgram2;

    Date baseEnrollDate = date("2023-06-01");
    Date baseCompleteDate = date("2023-09-01");

    private ProgramWorkflowService programWorkflowService;

    private MessageSourceService messageSourceService;

    @BeforeEach
    public void setup() {
        programWorkflowService = mock(ProgramWorkflowService.class);
        messageSourceService = mock(MessageSourceService.class);
        actions = new PihValidationMergeActions(programWorkflowService, messageSourceService);

        patientProgram1 = new PatientProgram();
        patientProgram1.setPatient(patient1);
        patientProgram1.setProgram(program);
        patientProgram1.setDateEnrolled(baseEnrollDate);
        patientProgram1.setDateCompleted(baseCompleteDate);

        patientProgram2 = new PatientProgram();
        patientProgram2.setPatient(patient2);
        patientProgram2.setProgram(program);
        patientProgram2.setDateEnrolled(baseEnrollDate);
        patientProgram2.setDateCompleted(baseCompleteDate);

        when(programWorkflowService.getPatientPrograms(
                patient1, null, null, null, null, null, false)
        ).thenReturn(Collections.singletonList(patientProgram1));
        when(programWorkflowService.getPatientPrograms(
                patient2, null, null, null, null, null, false)
        ).thenReturn(Collections.singletonList(patientProgram2));
    }

    @Test
    public void test_patientsHaveOverlappingProgramEnrollments_shouldReturnTrueIfNotCompleted() {
        patientProgram1.setDateCompleted(null);
        patientProgram2.setDateCompleted(null);
        assertThat(ymd(patientProgram1.getDateEnrolled()), equalTo("2023-06-01"));
        assertThat(ymd(patientProgram2.getDateEnrolled()), equalTo("2023-06-01"));
        assertTrue(actions.patientsHaveOverlappingProgramEnrollments(programWorkflowService, patient1, patient2));
        patientProgram1.setDateEnrolled(DateUtils.addMonths(baseEnrollDate, 1));
        assertThat(ymd(patientProgram1.getDateEnrolled()), equalTo("2023-07-01"));
        assertThat(ymd(patientProgram2.getDateEnrolled()), equalTo("2023-06-01"));
        assertTrue(actions.patientsHaveOverlappingProgramEnrollments(programWorkflowService, patient1, patient2));
        patientProgram2.setDateEnrolled(DateUtils.addMonths(baseEnrollDate, 3));
        assertThat(ymd(patientProgram1.getDateEnrolled()), equalTo("2023-07-01"));
        assertThat(ymd(patientProgram2.getDateEnrolled()), equalTo("2023-09-01"));
        assertTrue(actions.patientsHaveOverlappingProgramEnrollments(programWorkflowService, patient1, patient2));
    }

    @Test
    public void test_patientsHaveOverlappingProgramEnrollments_shouldReturnTrueIfDatesOverlappingProgram1() {
        patientProgram1.setDateCompleted(null);
        assertThat(ymd(patientProgram1.getDateEnrolled()), equalTo("2023-06-01"));
        assertThat(patientProgram1.getDateCompleted(), nullValue());
        assertThat(ymd(patientProgram2.getDateEnrolled()), equalTo("2023-06-01"));
        assertThat(ymd(patientProgram2.getDateCompleted()), equalTo("2023-09-01"));
        assertTrue(actions.patientsHaveOverlappingProgramEnrollments(programWorkflowService, patient1, patient2));
        patientProgram1.setDateEnrolled(date("2023-09-01"));
        assertFalse(actions.patientsHaveOverlappingProgramEnrollments(programWorkflowService, patient1, patient2));
        patientProgram1.setDateEnrolled(date("2023-08-31"));
        assertTrue(actions.patientsHaveOverlappingProgramEnrollments(programWorkflowService, patient1, patient2));
        patientProgram1.setDateEnrolled(date("2023-03-01"));
        patientProgram1.setDateCompleted(date("2023-06-01"));
        assertFalse(actions.patientsHaveOverlappingProgramEnrollments(programWorkflowService, patient1, patient2));
        patientProgram1.setDateCompleted(date("2023-06-02"));
        assertTrue(actions.patientsHaveOverlappingProgramEnrollments(programWorkflowService, patient1, patient2));
        patientProgram1.setDateCompleted(date("2023-05-31"));
        assertFalse(actions.patientsHaveOverlappingProgramEnrollments(programWorkflowService, patient1, patient2));
    }

    @Test
    public void test_patientsHaveOverlappingProgramEnrollments_shouldReturnTrueIfDatesOverlappingProgram2() {
        patientProgram2.setDateCompleted(null);
        assertThat(ymd(patientProgram2.getDateEnrolled()), equalTo("2023-06-01"));
        assertThat(patientProgram2.getDateCompleted(), nullValue());
        assertThat(ymd(patientProgram1.getDateEnrolled()), equalTo("2023-06-01"));
        assertThat(ymd(patientProgram1.getDateCompleted()), equalTo("2023-09-01"));
        assertTrue(actions.patientsHaveOverlappingProgramEnrollments(programWorkflowService, patient1, patient2));
        patientProgram2.setDateEnrolled(date("2023-09-01"));
        assertFalse(actions.patientsHaveOverlappingProgramEnrollments(programWorkflowService, patient1, patient2));
        patientProgram2.setDateEnrolled(date("2023-08-31"));
        assertTrue(actions.patientsHaveOverlappingProgramEnrollments(programWorkflowService, patient1, patient2));
        patientProgram2.setDateEnrolled(date("2023-03-01"));
        patientProgram2.setDateCompleted(date("2023-06-01"));
        assertFalse(actions.patientsHaveOverlappingProgramEnrollments(programWorkflowService, patient1, patient2));
        patientProgram2.setDateCompleted(date("2023-06-02"));
        assertTrue(actions.patientsHaveOverlappingProgramEnrollments(programWorkflowService, patient1, patient2));
        patientProgram2.setDateCompleted(date("2023-05-31"));
        assertFalse(actions.patientsHaveOverlappingProgramEnrollments(programWorkflowService, patient1, patient2));
    }

    @Test
    public void test_patientsHaveOverlappingProgramEnrollments_shouldReturnFalseIfProgramsDiffer() {
        assertTrue(actions.patientsHaveOverlappingProgramEnrollments(programWorkflowService, patient1, patient2));
        patientProgram1.setProgram(new Program());
        assertFalse(actions.patientsHaveOverlappingProgramEnrollments(programWorkflowService, patient1, patient2));
    }

    Date date(String ymd) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(ymd);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    String ymd(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
}
