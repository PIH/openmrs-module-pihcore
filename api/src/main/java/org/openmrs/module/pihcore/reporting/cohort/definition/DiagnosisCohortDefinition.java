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

package org.openmrs.module.pihcore.reporting.cohort.definition;

import org.openmrs.Concept;
import org.openmrs.module.emrapi.diagnosis.Diagnosis;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

import java.util.Date;
import java.util.List;

/**
 * Searches for patients who have diagnoses, represented as Obs groups following best-practice arrangement of concepts
 * @see {@link org.openmrs.module.emrapi.diagnosis.Diagnosis}
 * @see {@link org.openmrs.module.emrapi.diagnosis.DiagnosisMetadata}
 */
public class DiagnosisCohortDefinition extends BaseCohortDefinition {

    public static final long serialVersionUID = 1L;

    @ConfigurationProperty(group = "which")
    private List<Concept> codedDiagnoses;

    @ConfigurationProperty(group = "which")
    private List<Concept> excludeCodedDiagnoses;

    @ConfigurationProperty(group = "which")
    boolean includeAllCodedDiagnoses = false;

    @ConfigurationProperty(group = "which")
    boolean includeAllNonCodedDiagnoses = false;

    @ConfigurationProperty(group = "which")
    private Diagnosis.Order diagnosisOrder;

    @ConfigurationProperty(group = "which")
    private Diagnosis.Certainty certainty;

    @ConfigurationProperty(group = "when")
    private Date onOrAfter;

    @ConfigurationProperty(group = "when")
    private Date onOrBefore;

    public List<Concept> getCodedDiagnoses() {
        return codedDiagnoses;
    }

    public void setCodedDiagnoses(List<Concept> codedDiagnoses) {
        this.codedDiagnoses = codedDiagnoses;
    }

    public List<Concept> getExcludeCodedDiagnoses() {
        return excludeCodedDiagnoses;
    }

    public void setExcludeCodedDiagnoses(List<Concept> excludeCodedDiagnoses) {
        this.excludeCodedDiagnoses = excludeCodedDiagnoses;
    }

    public boolean isIncludeAllCodedDiagnoses() {
        return includeAllCodedDiagnoses;
    }

    public void setIncludeAllCodedDiagnoses(boolean includeAllCodedDiagnoses) {
        this.includeAllCodedDiagnoses = includeAllCodedDiagnoses;
    }

    public boolean isIncludeAllNonCodedDiagnoses() {
        return includeAllNonCodedDiagnoses;
    }

    public void setIncludeAllNonCodedDiagnoses(boolean includeAllNonCodedDiagnoses) {
        this.includeAllNonCodedDiagnoses = includeAllNonCodedDiagnoses;
    }

    public Diagnosis.Order getDiagnosisOrder() {
        return diagnosisOrder;
    }

    public void setDiagnosisOrder(Diagnosis.Order diagnosisOrder) {
        this.diagnosisOrder = diagnosisOrder;
    }

    public Diagnosis.Certainty getCertainty() {
        return certainty;
    }

    public void setCertainty(Diagnosis.Certainty certainty) {
        this.certainty = certainty;
    }

    public Date getOnOrAfter() {
        return onOrAfter;
    }

    public void setOnOrAfter(Date onOrAfter) {
        this.onOrAfter = onOrAfter;
    }

    public Date getOnOrBefore() {
        return onOrBefore;
    }

    public void setOnOrBefore(Date onOrBefore) {
        this.onOrBefore = onOrBefore;
    }
}
