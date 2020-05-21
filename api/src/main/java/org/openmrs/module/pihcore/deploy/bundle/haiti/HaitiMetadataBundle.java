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
import org.openmrs.module.haiticore.metadata.bundles.HaitiEncounterTypeBundle;
import org.openmrs.module.haiticore.metadata.bundles.HaitiPatientIdentifierTypeBundle;
import org.openmrs.module.haiticore.metadata.bundles.HaitiPersonAttributeTypeBundle;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.core.PihCoreMetadataBundle;
import org.springframework.stereotype.Component;

;

/**
 * Core metadata bundle
 */
@Component
@Requires( { PihCoreMetadataBundle.class,
        // OrderEntryConcepts.class,  install this after the MDS packages
		// TestOrderConcepts.class,  I believe we are now installing these via MDS
 		HaitiPatientIdentifierTypeBundle.class,
 		HaitiEncounterTypeBundle.class,
		HaitiPersonAttributeTypeBundle.class,
        PihHaitiPatientIdentifierTypeBundle.class,
		PihHaitiLocationsBundle.class
} )
public class HaitiMetadataBundle extends AbstractMetadataBundle {

	protected Log log = LogFactory.getLog(getClass());

	/**
	 * @see AbstractMetadataBundle#install()
	 */
	@Override
	public void install() {

	}
}
