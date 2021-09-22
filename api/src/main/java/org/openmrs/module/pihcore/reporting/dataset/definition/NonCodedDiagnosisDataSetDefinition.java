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

package org.openmrs.module.pihcore.reporting.dataset.definition;

import org.openmrs.Provider;
import org.openmrs.module.reporting.dataset.definition.BaseDataSetDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

import java.util.Date;

/**
 * Searches for Obs that represent non-coded diagnoses in a given date range
 */
public class NonCodedDiagnosisDataSetDefinition extends BaseDataSetDefinition {

    public static final long serialVersionUID = 1L;

    @ConfigurationProperty(group = "when")
    private Date fromDate;

    @ConfigurationProperty(group = "when")
    private Date toDate;

    @ConfigurationProperty(group = "what")
    private String nonCoded;

    @ConfigurationProperty(group = "who")
    private Provider provider;

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

    public String getNonCoded() {
        return nonCoded;
    }

    public void setNonCoded(String nonCoded) {
        this.nonCoded = nonCoded;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }
}
