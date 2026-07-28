package org.openmrs.module.pihcore.rest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.search.openmrs1_8.PatientByIdentifierSearchHandler1_8;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.PatientIdentifier;


import java.util.List;

@Component
public class PihPatientSearchHandler extends PatientByIdentifierSearchHandler1_8 {

	protected final Log log = LogFactory.getLog(getClass());

	public static final String SEARCH_ID = "pihPatientSearch";

	private final PatientService patientService;
	
	private final ProgramWorkflowService programWorkflowService;

	public PihPatientSearchHandler(@Autowired PatientService patientService,ProgramWorkflowService programWorkflowService) {
		this.patientService = patientService;
		this.programWorkflowService = programWorkflowService;
	}

	/**
	 * If multiple search handlers are found for a particular resource and the same supported parameters, the REST
	 * module will return the one whose id = "default".  We configure that here to ensure this search handler is used.
	 */
	@Override
	public SearchConfig getSearchConfig() {
		SearchConfig p = super.getSearchConfig();
		return new SearchConfig(SEARCH_ID, p.getSupportedResource(), p.getSupportedOpenmrsVersions(), p.getSearchQueries());
	}

	/**
	 * This has the same logic as the superclass, with the addition of searching by insurance number and phone number
	 * There are 2 possible parameters that will be passed to this:
	 *   - identifier = search with no white-space
	 *   - q = search with white-space
	 * This handles both the same way currently
	 */
	@Override
	public PageableResult search(RequestContext context) throws ResponseException {
		String searchString = context.getRequest().getParameter("identifier");
		if (StringUtils.isBlank(searchString)) {
			searchString = context.getRequest().getParameter("q");
		}
		if (StringUtils.isNotBlank(searchString)) {
			log.trace("Searching patients by: " + searchString);

			// The core patient search matches on identifier, name, and attributes based on core configuration(s)
			List<Patient> patients = patientService.getPatients(null, searchString, null, true);
			log.trace("Found " + patients.size() + " patients from core patient search");

			// Retrieve the HIV program from the ProgramWorkflowService
			Program hivProgram = programWorkflowService.getProgramByUuid(PihEmrConfigConstants.PROGRAM_HIV_UUID);
			for(Patient patient : patients){
				 // Ensure that the patient object is not null and the HIV program exists
				if (patient !=null && hivProgram !=null) {
					// Retrieve all patient programs for this patient that are linked to the HIV program
					List<PatientProgram> patientPrograms = programWorkflowService.getPatientPrograms(patient, hivProgram, null, null, null, null, false);
					if (patientPrograms != null) {
						for (PatientProgram pp : patientPrograms) {
									for (PatientIdentifier pid : patient.getIdentifiers()) {
										// Set the location of the identifier to match the program enrollment's location
										pid.setLocation(pp.getLocation());
									}
						}
					}
				}
			}

			if (!patients.isEmpty()) {
				return new NeedsPaging<>(patients, context);
			}
		}

		log.trace("No patients found that match the search criteria");
		return new EmptySearchResult();
	}
	
}