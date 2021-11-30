package org.openmrs.module.pihcore.fragment.controller.dashboardwidgets;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.parameter.EncounterSearchCriteriaBuilder;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EncountersFragmentController {

	private final Logger log = LoggerFactory.getLogger(getClass());

	public void controller(@SpringBean("patientService") PatientService patientService,
						   @SpringBean("encounterService") EncounterService encounterService,
						   @InjectBeans PatientDomainWrapper patientWrapper,
						   @FragmentParam("app") AppDescriptor app,
						   UiUtils ui,
						   FragmentConfiguration config,
						   FragmentModel model) {

		Object patientConfig = config.get("patient");
		if (patientConfig == null ) {
			patientConfig = config.get("patientId");
		}
		Patient patient = null;
		if (patientConfig != null) {
			if (patientConfig instanceof Patient) {
				patient = (Patient) patientConfig;
			}
			else if (patientConfig instanceof PatientDomainWrapper) {
				patient = ((PatientDomainWrapper) patientConfig).getPatient();
			}
			else if (patientConfig instanceof Integer) {
				patient = patientService.getPatient((Integer)patientConfig);
			}
			else if (patientConfig instanceof String) {
				patient = patientService.getPatientByUuid((String)patientConfig);
			}
		}
		if (patient == null) {
			throw new IllegalArgumentException("No patient found. Please pass patient into the configuration");
		}

		model.put("app", app);

		Map<EncounterType, String> encounterTypeToUrlMap = new HashMap<>();
		JsonNode node = app.getConfig().get("encounterTypes");
		if (node == null) {
			throw new IllegalStateException("Missing configuration encounterTypes on widget");
		}
		if (node instanceof ArrayNode) {
			ArrayNode arrayNode = (ArrayNode) node;
			for (Iterator<JsonNode> iter = arrayNode.getElements(); iter.hasNext();) {
				JsonNode encTypeNode = iter.next();
				try {
					EncounterType et = encounterService.getEncounterTypeByUuid(encTypeNode.get("encounterType").asText());
					String url = encTypeNode.get("url").asText();
					encounterTypeToUrlMap.put(et, url);
				}
				catch (Exception e) {
					throw new IllegalStateException("Invalid configuration for encounterType node", e);
				}
			}
		}
		else {
			throw new IllegalStateException("encounterTypes is expected to be an array of objects");
		}
		model.put("encounterTypeToUrlMap", encounterTypeToUrlMap);

		EncounterSearchCriteriaBuilder builder = new EncounterSearchCriteriaBuilder();
		builder.setPatient(patient);
		builder.setEncounterTypes(encounterTypeToUrlMap.keySet());
		List<Encounter> encounters = encounterService.getEncounters(builder.createEncounterSearchCriteria());
		encounters.sort((e1, e2) -> e2.getEncounterDatetime().compareTo(e1.getEncounterDatetime()));

		String maxConfig = getConfigValue(app, "maxToDisplay");
		if (StringUtils.isNotEmpty(maxConfig)) {
			Integer maxToDisplay = Integer.parseInt(maxConfig);
			if (maxToDisplay < encounters.size()) {
				encounters = encounters.subList(0, maxToDisplay);
			}
		}
		model.put("encounters", encounters);
	}

	private String getConfigValue(AppDescriptor app, String configValue) {
		JsonNode node = app.getConfig().get(configValue);
		if (node == null) {
			return "";
		}
		return node.asText();
	}
}
