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
 * Encapsulates the information needed to create a new PersonAttributeType
 */
public abstract class PersonAttributeTypeDescriptor extends MetadataDescriptor {

    /**
     * @return the format, defaulting to String
     */
    public Class<?> format() {
        return String.class;
    }

    /**
     * @return the foreign key, defaulting to null
     */
    public Integer foreignKey() {
        return null;
    }

    /**
     * @return the searchable flag, defaulting to false
     */
    public boolean searchable() {
        return false;
    }

    /**
     * @return the sortWeight for this attribute type
     */
    public abstract double sortWeight();
}