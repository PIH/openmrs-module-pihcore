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

package org.openmrs.module.pihcore.descriptor;

/**
 * Encapsulates the minimum information needed to create OpenmrsMetadata
 */
public abstract class MetadataDescriptor implements Descriptor {

	/**
	 * @return the name of the metadata
	 */
	public abstract String name();

	/**
	 * @return the description of the metadata
	 */
	public abstract String description();

	/**
	 * @return whether or not this metadata is retired
	 */
	public boolean retired() {
		return false;
	}

}