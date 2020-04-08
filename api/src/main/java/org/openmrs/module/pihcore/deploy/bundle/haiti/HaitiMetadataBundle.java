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

package org.openmrs.module.pihcore.deploy.bundle.haiti;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.EncounterType;
import org.openmrs.GlobalProperty;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.haiticore.metadata.bundles.HaitiAddressBundle;
import org.openmrs.module.haiticore.metadata.bundles.HaitiEncounterTypeBundle;
import org.openmrs.module.haiticore.metadata.bundles.HaitiPatientIdentifierTypeBundle;
import org.openmrs.module.haiticore.metadata.bundles.HaitiPersonAttributeTypeBundle;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.core.PihCoreMetadataBundle;
import org.openmrs.module.pihcore.metadata.mexico.MexicoEncounterTypes;
import org.openmrs.module.pihcore.metadata.sierraLeone.SierraLeoneEncounterTypes;
import org.springframework.stereotype.Component;

/**
 * Core metadata bundle
 */
@Component
@Requires( { PihCoreMetadataBundle.class,
 		HaitiPatientIdentifierTypeBundle.class,
 		HaitiEncounterTypeBundle.class,
		HaitiPersonAttributeTypeBundle.class,
        PihHaitiPatientIdentifierTypeBundle.class,
		PihHaitiLocationsBundle.class,
		HaitiAddressBundle.class
} )
public class HaitiMetadataBundle extends AbstractMetadataBundle {

	protected Log log = LogFactory.getLog(getClass());

	/**
	 * @see AbstractMetadataBundle#install()
	 */
	@Override
	public void install() {
		uninstall(possible(GlobalProperty.class, EmrApiConstants.PRIMARY_IDENTIFIER_TYPE), "replaced by metadata mapping");

		// uninstall Mexico and Sierra Leone encounter types that were originally installed globally
		uninstall(possible(EncounterType.class, MexicoEncounterTypes.MEXICO_CONSULT.uuid()), "now installed only in Mexico");
		uninstall(possible(EncounterType.class, SierraLeoneEncounterTypes.SIERRA_LEONE_OUTPATIENT_INITIAL.uuid()), "now installed only in Sierra Leone");
		uninstall(possible(EncounterType.class, SierraLeoneEncounterTypes.SIERRA_LEONE_OUTPATIENT_FOLLOWUP.uuid()), "now installed only in Sierra Leone");
	}
}
