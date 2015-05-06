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

import org.openmrs.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the information needed to create a new Location
 */
public abstract class LocationDescriptor extends MetadataDescriptor<Location> {

    /**
     * @return the descriptors for the parent location for this location . defaults to null
     */
    public LocationDescriptor parent() {
        return null;
    }

    /**
     * @return the descriptors for each attribute associated with this location. Defaults to an empty list
     */
    public List<LocationAttributeDescriptor> attributes() {
        return new ArrayList<LocationAttributeDescriptor>();
    }

    /**
     * @return the descriptors for each tag associated with this location. Defaults to an empty list
     */
    public List<LocationTagDescriptor> tags() {
        return new ArrayList<LocationTagDescriptor>();
    }

    /**
     * @see Descriptor#getDescribedType()
     */
    @Override
    public Class<Location> getDescribedType() {
        return Location.class;
    }
}