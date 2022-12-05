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
package org.openmrs.module.pihcore.metadata;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.pihcore.ZlConfigConstants;

/**
 * Convenience methods for working with metadata
 */
public class Metadata {

    /**
     * @return the installed Object represented by the given descriptor
     */
    public static EncounterType lookupEncounterType(String uuid) {
        return Context.getEncounterService().getEncounterTypeByUuid(uuid);
    }

    public static PersonAttributeType getMothersFirstNameAttributeType() {
        return Context.getPersonService().getPersonAttributeTypeByUuid(PihEmrConfigConstants.PERSONATTRIBUTETYPE_MOTHERS_FIRST_NAME_UUID);
    }

    public static PersonAttributeType getPhoneNumberAttributeType() {
        return Context.getPersonService().getPersonAttributeTypeByUuid(PihEmrConfigConstants.PERSONATTRIBUTETYPE_TELEPHONE_NUMBER_UUID);
    }

    public static PersonAttributeType getUnknownPatientAttributeType() {
        return Context.getPersonService().getPersonAttributeTypeByUuid(PihEmrConfigConstants.PERSONATTRIBUTETYPE_UNKNOWN_PATIENT_UUID);
    }

    public static PatientIdentifierType getNifIdentifierType() {
        return Context.getPatientService().getPatientIdentifierTypeByUuid(ZlConfigConstants.PATIENTIDENTIFIERTYPE_NIF_UUID);
    }

    public static PatientIdentifierType getCinIdentifierType() {
        return Context.getPatientService().getPatientIdentifierTypeByUuid(ZlConfigConstants.PATIENTIDENTIFIERTYPE_CIN_UUID);
    }

    public static PatientIdentifierType getBiometricsReferenceNumberIdentifierType() {
        return Context.getPatientService().getPatientIdentifierTypeByUuid(ZlConfigConstants.PATIENTIDENTIFIERTYPE_BIOMETRICSREFERENCECODE_UUID);
    }

    /**
     * @return the Concept that matches the passed uuid, name or source:code mapping
     */
    public static Concept getConcept(String lookup) {
        Concept c = Context.getConceptService().getConceptByUuid(lookup);
        if (c == null) {
            try {
                String[] split = lookup.split("\\:");
                if (split.length == 2) {
                    c = Context.getConceptService().getConceptByMapping(split[1], split[0]);
                }
            }
            catch (Exception e) {}
        }
        if (c == null) {
            c = Context.getConceptService().getConceptByName(lookup);
        }
        if (c == null) {
            throw new IllegalArgumentException("Unable to find Concept using key: " + lookup);
        }
        return c;
    }
}
