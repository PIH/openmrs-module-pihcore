/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.pihcore.service;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.Program;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.OrderDAO;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.adt.InpatientAdmission;
import org.openmrs.module.emrapi.adt.InpatientAdmissionSearchCriteria;
import org.openmrs.module.emrapi.adt.InpatientRequest;
import org.openmrs.module.emrapi.adt.InpatientRequestSearchCriteria;
import org.openmrs.module.emrapi.disposition.DispositionType;
import org.openmrs.module.pihcore.PihCoreConstants;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.model.Vaccination;
import org.openmrs.parameter.EncounterSearchCriteria;
import org.openmrs.parameter.EncounterSearchCriteriaBuilder;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Service for PIH Core
 */
public class PihCoreServiceImpl extends BaseOpenmrsService implements PihCoreService, ApplicationContextAware {
	
	protected final Log log = LogFactory.getLog(this.getClass());

    private ApplicationContext applicationContext;

    @Getter @Setter
	private PihCoreDAO dao;

    @Setter
    private OrderDAO orderDAO;

    @Setter
    private ConceptService conceptService;

    @Setter
    private ObsService obsService;

    @Setter
    private PatientService patientService;

    @Setter
    private PersonService personService;

    @Setter
    private ProgramWorkflowService programWorkflowService;

    @Setter
    private Config config;

    /**
     * @see OrderService#getNextOrderNumberSeedSequenceValue()
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public synchronized Long getNextRadiologyOrderNumberSeedSequenceValue() {
        return dao.getNextRadiologyOrderNumberSeedSequenceValue();
    }

    /**
     * @see OrderDAO#saveOrder(Order)
     */
    @Transactional
    public Order saveOrder(Order order) {
        return orderDAO.saveOrder(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InpatientAdmission> getStaleInpatientAdmissions(Date admittedOnOrBefore, int mostRecentEncounterThresholdInDays) {

        List<InpatientAdmission> staleInpatientAdmissions = new ArrayList<>();

        AdtService adtService = Context.getService(AdtService.class);

        List<InpatientAdmission> inpatientAdmissions = adtService.getInpatientAdmissions(new InpatientAdmissionSearchCriteria());

        for (InpatientAdmission inpatientAdmission : inpatientAdmissions) {
            Encounter admissionEncounter = inpatientAdmission.getFirstAdmissionOrTransferEncounter();
            if (admissionEncounter != null) {
                if (admissionEncounter.getEncounterDatetime().before(new DateTime(admittedOnOrBefore).plusDays(1).withTimeAtStartOfDay().toDate())) {
                    Duration timeSinceLastEncounter = new Duration(new DateTime(inpatientAdmission.getLatestEncounter().getEncounterDatetime()), new DateTime());
                    if (timeSinceLastEncounter.getStandardDays() >= mostRecentEncounterThresholdInDays) {
                        staleInpatientAdmissions.add(inpatientAdmission);
                    }
                }
            }
        }
        return staleInpatientAdmissions;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InpatientRequest> getStaleAdmissionRequests(Date admissionRequestOnOrBefore, int mostRecentEncounterThresholdInDays) {

        List<InpatientRequest> staleInpatientRequests = new ArrayList<>();
        AdtService adtService = Context.getService(AdtService.class);

        InpatientRequestSearchCriteria searchCriteria = new InpatientRequestSearchCriteria();
        searchCriteria.addDispositionType(DispositionType.ADMIT);

        List<InpatientRequest> inpatientRequests = adtService.getInpatientRequests(searchCriteria);

        for (InpatientRequest inpatientRequest : inpatientRequests) {
            Encounter dispositionEncounter = inpatientRequest.getDispositionEncounter();
            if (dispositionEncounter != null) {
                if (dispositionEncounter.getEncounterDatetime().before(new DateTime(admissionRequestOnOrBefore).plusDays(1).withTimeAtStartOfDay().toDate())) {
                    Duration timeSinceLastEncounter = new Duration(new DateTime(inpatientRequest.getLatestEncounter().getEncounterDatetime()), new DateTime());
                    if (timeSinceLastEncounter.getStandardDays() >= mostRecentEncounterThresholdInDays) {
                        staleInpatientRequests.add(inpatientRequest);
                    }
                }
            }

        }
        return staleInpatientRequests;
    }

    @Override
    public List<Vaccination> getVaccinations(Patient patient) {
        List<Vaccination> ret = new ArrayList<>();
        Concept vaccinationConcept = conceptService.getConceptByUuid(PihCoreConstants.VACCINATION_TYPE_CONCEPT_UUID);
        List<Obs> vaccinationList = obsService.getObservationsByPersonAndConcept(patient, vaccinationConcept);
        for (Obs obs : vaccinationList) {
            Vaccination vaccination = new Vaccination();
            vaccination.setVaccinationObs(obs);
            if (obs.getObsGroup() != null) {
                vaccination.setGroupObs(obs.getObsGroup());
                for (Obs sibling : obs.getObsGroup().getGroupMembers()) {
                    if (BooleanUtils.isNotTrue(sibling.getVoided())) {
                        String siblingConcept = sibling.getConcept().getUuid();
                        if (siblingConcept.equalsIgnoreCase(PihCoreConstants.VACCINATION_NUM_CONCEPT_UUID)) {
                            vaccination.setSequenceNumberObs(sibling);
                        } else if (siblingConcept.equalsIgnoreCase(PihCoreConstants.VACCINATION_DATE_CONCEPT_UUID)) {
                            vaccination.setDateObs(sibling);
                        }
                    }
                }
            }
            ret.add(vaccination);
        }
        ret.sort(Comparator.comparing(Vaccination::getEffectiveDate).reversed());
        return ret;
    }

    public void updateHealthCenter(Patient patient) {
        // specific Haiti-based logic for Health Centers
        if (config.isHaiti()) {
            PersonAttributeType healthCenter = personService.getPersonAttributeTypeByUuid(PihEmrConfigConstants.PERSONATTRIBUTETYPE_HEALTH_CENTER_UUID);
            // Retrieve the HIV program from the ProgramWorkflowService
            Program hivProgram = programWorkflowService.getProgramByUuid(PihEmrConfigConstants.PROGRAM_HIV_UUID);
            if (patient != null && hivProgram != null && healthCenter != null) {
                // Retrieve all patient programs for this patient that are linked to the HIV program
                List<PatientProgram> patientPrograms = programWorkflowService.getPatientPrograms(patient, hivProgram, null, null, null, null, false);
                if (!patientPrograms.isEmpty()) {
                    // Get the most recent HIV program for the patient by selecting the one with the latest enrollment date
                    PatientProgram mostRecentProgram = patientPrograms.stream()
                        .max(Comparator.comparing(PatientProgram::getDateEnrolled))
                        .orElse(null);
                    // per documentation, the "addAttribute" function will create the attribute if needed, or otherwise override any existing attribute value
                    patient.addAttribute(new PersonAttribute(healthCenter,mostRecentProgram.getLocation().getId().toString()));
                    patientService.savePatient(patient);
                    
                } else {
                
                    EncounterService encounterService= Context.getService(EncounterService.class);
                    EncounterType encounterType = encounterService.getEncounterTypeByUuid(PihEmrConfigConstants.ENCOUNTERTYPE_PATIENT_REGISTRATION_UUID);
                    List<EncounterType> encounterTypes = new ArrayList<EncounterType>();
                    encounterTypes.add(encounterType);
                    EncounterSearchCriteria encounterSearchCriteria = new EncounterSearchCriteriaBuilder()
                        .setPatient(patient)
                        .setEncounterTypes(encounterTypes)
                        .setIncludeVoided(false)
                        .createEncounterSearchCriteria();
                    List<Encounter> encounters = encounterService.getEncounters(encounterSearchCriteria);
                    if(!encounters.isEmpty()){
                    Encounter mostRecentEncounter = encounters.stream()
                            .max(Comparator.comparing(Encounter::getEncounterDatetime))
                            .orElse(null);
                    patient.addAttribute(new PersonAttribute(healthCenter, mostRecentEncounter.getLocation().getId().toString()));
                    patientService.savePatient(patient);
                    
                    }  
                }
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
