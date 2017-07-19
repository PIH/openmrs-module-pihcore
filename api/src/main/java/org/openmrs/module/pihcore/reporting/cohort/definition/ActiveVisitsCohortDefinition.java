package org.openmrs.module.pihcore.reporting.cohort.definition;


import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

import java.util.Date;

@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class ActiveVisitsCohortDefinition extends BaseCohortDefinition{

    public static final long serialVersionUID = 1L;

    @ConfigurationProperty
    private Date effectiveDate;

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
}
