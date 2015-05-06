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

import org.openmrs.PatientIdentifierType;
import org.openmrs.patient.IdentifierValidator;

/**
 * Encapsulates the information needed to create a new PatientIdentifierType
 */
public abstract class PatientIdentifierTypeDescriptor extends MetadataDescriptor<PatientIdentifierType> {

    /**
     * @return the format, defaulting to null
     */
    public String format() {
        return null;
    }

    /**
     * @return the format description, defaulting to null
     */
    public String formatDescription() {
        return null;
    }

    /**
     * @return the locationBehavior of this location attribute type, defaulting to NOT_USED
     */
    public PatientIdentifierType.LocationBehavior locationBehavior() {
        return PatientIdentifierType.LocationBehavior.NOT_USED;
    }

    /**
     * @return the validator for this location attribute type. defaults to null
     */
    public Class<? extends IdentifierValidator> validator() {
        return null;
    }

    /**
     * @return the required flag for this location attribute type. defaults to false
     */
    public boolean required() {
        return false;
    }

    /**
     * @see Descriptor#getDescribedType()
     */
    @Override
    public Class<PatientIdentifierType> getDescribedType() {
        return PatientIdentifierType.class;
    }
}