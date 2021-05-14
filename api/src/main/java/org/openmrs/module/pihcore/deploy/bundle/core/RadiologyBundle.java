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

package org.openmrs.module.pihcore.deploy.bundle.core;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.pihcore.metadata.core.EncounterTypes;
import org.springframework.stereotype.Component;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.encounterRole;

/**
 * Radiology metadata bundle
 */
@Component
public class RadiologyBundle extends AbstractMetadataBundle {

	public static final class EncounterRoles {
		public static final String RADIOLOGY_TECHNICIAN = "8f4d96e2-c97c-4285-9319-e56b9ba6029c";
		public static final String PRINCIPAL_RESULTS_INTERPRETER = "08f73be2-9452-44b5-801b-bdf7418c2f71";
	}

	/**
	 * @see AbstractMetadataBundle#install()
	 */
	@Override
	public void install() {

        // note that the radiology order type itself is installed as part of the OrderTypes bundle
		install(EncounterTypes.RADIOLOGY_ORDER);
		install(EncounterTypes.RADIOLOGY_REPORT);
		install(EncounterTypes.RADIOLOGY_STUDY);
		install(encounterRole("Radiology Technician", "Radiology Technician - person who performs radiology studies", EncounterRoles.RADIOLOGY_TECHNICIAN));
		install(encounterRole("Principal Results Interpreter", "Principal Results Interpreter - the provider responsible for interpreting the results of a radiology study", EncounterRoles.PRINCIPAL_RESULTS_INTERPRETER));

	}

}
