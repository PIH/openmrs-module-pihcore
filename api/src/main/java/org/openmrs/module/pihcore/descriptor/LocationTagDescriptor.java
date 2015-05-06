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

import org.openmrs.LocationTag;

/**
 * Encapsulates the information needed to create a new LocationTag
 */
public abstract class LocationTagDescriptor extends MetadataDescriptor<LocationTag> {

	// For now, we don't need anything extra from MetadataDescriptor, but this is here for forwards compatibility

    /**
     * @see Descriptor#getDescribedType()
     */
    @Override
    public Class<LocationTag> getDescribedType() {
        return LocationTag.class;
    }
}