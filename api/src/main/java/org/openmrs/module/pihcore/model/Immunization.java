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
package org.openmrs.module.pihcore.model;

import lombok.Data;
import org.openmrs.Obs;

import java.util.Date;

@Data
public class Immunization {

    private Obs groupObs;
    private Obs immunizationObs;
    private Obs sequenceNumberObs;
    private Obs dateObs;

    public Date getEffectiveDate() {
        return dateObs != null ? dateObs.getValueDatetime() : immunizationObs.getObsDatetime();
    }
}
