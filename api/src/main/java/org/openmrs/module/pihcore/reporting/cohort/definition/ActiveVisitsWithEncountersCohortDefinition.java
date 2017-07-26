package org.openmrs.module.pihcore.reporting.cohort.definition;


import org.openmrs.Location;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

import java.util.Date;
import java.util.List;

@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class ActiveVisitsWithEncountersCohortDefinition extends BaseCohortDefinition{

    public static final long serialVersionUID = 1L;

    @ConfigurationProperty
    private Date effectiveDate;

    @ConfigurationProperty(group="which")
    private TimeQualifier whichEncounter = TimeQualifier.ANY;

    @ConfigurationProperty(group="which")
    private List<Location> locationList;

    @ConfigurationProperty(group = "when")
    private Boolean active;

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public TimeQualifier getWhichEncounter() {
        return whichEncounter;
    }

    public void setWhichEncounter(TimeQualifier whichEncounter) {
        this.whichEncounter = whichEncounter;
    }

    public List<Location> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<Location> locationList) {
        this.locationList = locationList;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean isActive() {
        return active;
    }
}
