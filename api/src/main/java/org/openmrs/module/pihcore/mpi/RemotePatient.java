package org.openmrs.module.pihcore.mpi;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.openmrs.Patient;

import java.util.Date;

public class RemotePatient {

    private Patient patient;
    private String remoteServer;
    private String remoteUuid;
    private Boolean localPatient;

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getRemoteServer() {
        return remoteServer;
    }

    public void setRemoteServer(String remoteServer) {
        this.remoteServer = remoteServer;
    }

    public String getRemoteUuid() {
        return remoteUuid;
    }

    public void setRemoteUuid(String remoteUuid) {
        this.remoteUuid = remoteUuid;
    }

    public Boolean getLocalPatient() {
        return localPatient;
    }

    public void setLocalPatient(Boolean localPatient) {
        this.localPatient = localPatient;
    }

    public Integer getAgeInMonths() {

        if (patient.getBirthdate() == null) {
            return null;
        }

        Date endDate = patient.isDead() ? patient.getDeathDate() : new Date();
        return Months.monthsBetween(new DateTime(patient.getBirthdate()), new DateTime(endDate)).getMonths();
    }

    public Integer getAgeInDays() {

        if (patient.getBirthdate() == null) {
            return null;
        }

        Date endDate = patient.isDead() ? patient.getDeathDate() : new Date();
        return Days.daysBetween(new DateTime(patient.getBirthdate()), new DateTime(endDate)).getDays();
    }
}
