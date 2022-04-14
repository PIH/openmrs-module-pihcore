/*
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

package org.openmrs.module.pihcore.radiology;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonName;
import org.openmrs.Visit;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.radiologyapp.RadiologyConstants;
import org.openmrs.module.radiologyapp.RadiologyOrder;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

// ignoring Mirth tests for now as there is no current good way to test this
// once we get a good production pipeline set up, we may want to re-enable this

// NOTE: the mirebalais.properties hardcoded in this file are bamboo-specific
// these tests will only pass on bamboo

@SkipBaseSetup
@Disabled
public class MirthTest extends BaseModuleContextSensitiveTest {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@AfterAll
	public static void tearDown() {
		runtimeProperties = null;
	}
	
	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}
	
	@Override
	public String getWebappName() {
		return "mirebalais";
	}
	
	@Autowired
	@Qualifier("patientService")
	private PatientService patientService;
	
	@Autowired
	@Qualifier("encounterService")
	private EncounterService encounterService;
	
	@Autowired
	@Qualifier("orderService")
	private OrderService orderService;
	
	@Autowired
	@Qualifier("visitService")
	private VisitService visitService;
	
	@Autowired
	@Qualifier("adminService")
	private AdministrationService administrationService;
	
	@Autowired
	@Qualifier("locationService")
	private LocationService locationService;
	
	@Autowired
	@Qualifier("adtService")
	private AdtService adtService;
	
	@Autowired
	@Qualifier("conceptService")
	private ConceptService conceptService;
	
	@Autowired
	@Qualifier("emrApiProperties")
	private EmrApiProperties emrApiProperties;

	@Test
	@DirtiesContext
	public void testMirthChannelIntegration() throws Exception {
		
		authenticate();

        // test that when we create a new patient, a new patient message is created
		// if the test patient already exists, delete it and any existing orders
		if (patientService.getPatients("2ADMMN").size() > 0) {
			Patient patient = patientService.getPatients("2ADMMN").get(0);
			
			for (Order order : orderService.getAllOrdersByPatient(patient)) {
				orderService.purgeOrder(order);
			}
			
			for (Encounter encounter : encounterService.getEncountersByPatient(patient)) {
				encounterService.purgeEncounter(encounter);
			}
			
			for (Visit visit : visitService.getVisitsByPatient(patient)) {
				visitService.purgeVisit(visit);
			}
			
			patientService.purgePatient(patient);
		}
		
		// TODO: eventually we should make sure all the necessary fields are included here
		// first create and save a patient
		Patient patient = new Patient();
		patient.setGender("M");
		
		Calendar birthdate = Calendar.getInstance();
		birthdate.set(2000, 2, 23);
		patient.setBirthdate(birthdate.getTime());
		
		PersonName name = new PersonName();
		name.setFamilyName("Test Patient");
		name.setGivenName("Mirth Integration");
		patient.addName(name);
		
		PatientIdentifier identifier = new PatientIdentifier();
		identifier.setIdentifierType(emrApiProperties.getPrimaryIdentifierType());
		identifier.setIdentifier("2ADMMN");
		identifier.setPreferred(true);
		identifier.setLocation(locationService.getLocation("Unknown Location"));
		patient.addIdentifier(identifier);
		
		// save the patient
		patientService.savePatient(patient);

		/**
		 *  Commenting this out because we are not currently sending ADT information to APCS
		String result = listenForResults();

		System.out.println(result);

		// make sure the appropriate message has been delivered
		TestUtils.assertContains("MSH|^~\\&|||||||ADT^A01||P|2.3", result);
		TestUtils.assertContains("PID|||2ADMMN||Test Patient^Mirth Integration||200003230000|M", result);

		// now resave the patient and verify that a patient updated message is sent
		// save the patient to trigger an update event
		patientService.savePatient(patient);

		result = listenForResults();

		System.out.println(result);

		TestUtils.assertContains("MSH|^~\\&|||||||ADT^A08||P|2.3", result);
		TestUtils.assertContains("PID|||2ADMMN||Test Patient^Mirth Integration||200003230000|M", result);

		 */
		
		// TODO: eventually we should make sure all the necessary fields are concluded here
		// TODO: specifically: sending facility, device location, universal service id, universal service id text, and modality
		
		// ensure that there is a visit for the patient (so that the encounter visit handlers doesn't bomb)
		adtService.ensureActiveVisit(patient, locationService.getLocation("Mirebalais Hospital"));
		
		// now create and save the order for this patient
		RadiologyOrder order = new RadiologyOrder();
		order.setOrderType(orderService.getOrderTypeByUuid(administrationService
		        .getGlobalProperty(RadiologyConstants.GP_RADIOLOGY_TEST_ORDER_TYPE)));
		order.setPatient(patient);
		order.setConcept(conceptService.getConceptByUuid("fc6de1c0-1a36-11e2-a310-aa00f871a3e1")); // chest x-ray, one view
		order.setAccessionNumber("ACCESSION NUMBER");
		order.setDateActivated(new SimpleDateFormat("MM-dd-yyyy").parse("09-09-2012"));
		order.setUrgency(Order.Urgency.STAT);
		order.setClinicalHistory("Patient fell off horse");
		order.setExamLocation(locationService.getLocation("Mirebalais Hospital"));
		
		Encounter encounter = new Encounter();
		encounter.setPatient(patient);
		encounter.setEncounterDatetime(new Date());
		encounter.setLocation(locationService.getLocation("Mirebalais Hospital"));
		encounter.setEncounterType(encounterService.getEncounterType(1));
		encounter.addOrder(order);
		encounter.addProvider(emrApiProperties.getOrderingProviderEncounterRole(), Context.getProviderService().getProvider(1));
		encounterService.saveEncounter(encounter);

        // TODO: I've changed the configuration so that this sends the message directly to the PACS test server
        // TODO: since we aren't getting messages send back from PACS yet, there is no good way to test this

		/*String result = listenForResults();
		
		TestUtils.assertContains("MSH|^~\\&||Mirebalais|||||ORM^O01||P|2.3", result);
		TestUtils.assertContains("PID|||2ADMMN||Test Patient^Mirth Integration||200003230000|M", result);
		TestUtils.assertContains("PV1|||Mirebalais Hospital|||||^User^Super", result);
		TestUtils.assertContains("ORC|NW", result);
		TestUtils
		        .assertContains(
		            "OBR|||ACCESSION NUMBER|36554-4^X-ray of chest, 1 view|||||||||||||||CR||||||||^^^^^STAT||||^Patient fell off horse|||||201209090000",
		            result);*/
	}
	
	private String listenForResults() throws IOException {
		
		ServerSocket listener = new ServerSocket(6660); // TODO: store this port in a global property?
		listener.setSoTimeout(20000); // don't wait more than 20 seconds for an incoming connection
		
		Socket mirthConnection = listener.accept();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(mirthConnection.getInputStream()));
		
		StringBuilder sb = new StringBuilder();
		String line;
		
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		
		// TODO: need an acknowledgement?
		
		mirthConnection.close();
		listener.close();
		
		return sb.toString();
	}
}
