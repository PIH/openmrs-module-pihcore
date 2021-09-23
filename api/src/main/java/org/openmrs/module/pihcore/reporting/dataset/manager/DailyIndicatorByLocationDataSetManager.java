package org.openmrs.module.pihcore.reporting.dataset.manager;

import org.openmrs.Location;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.module.reporting.config.factory.DataSetFactory;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DailyIndicatorByLocationDataSetManager extends BaseDataSetManager {

    public List<Location> getLocations() {
        List<String> locationsToExclude = Arrays.asList(
                "8d6c993e-c2cc-11de-8d13-0010c6dffd0f",  // Unknown location
                "24bd1390-5959-11e4-8ed6-0800200c9a66",  // Hôpital Universitaire de Mirebalais - Prensipal
                "a084f714-a536-473b-94e6-ec317b152b43"   // Mirebalais
        );
        List<Location> ret = new ArrayList<>();
        for (Location l : locationService.getAllLocations(false)) {
            String uuid = l.getUuid();
            if (!locationsToExclude.contains(uuid)) {
                ret.add(l);
            }
        }
        return ret;
    }

    protected List<Map<String, Object>> getParameterOptions() {
        List<Map<String, Object>> options = new ArrayList<Map<String, Object>>();
        List<Location> locations = getLocations();
        for (Location location : locations) {
            Map<String, Object> option = new HashMap<String, Object>();
            option.put("location", location);
            options.add(option);
        }
        return options;
    }
}
