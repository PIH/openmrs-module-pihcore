package org.openmrs.module.pihcore.setup;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.util.OpenmrsConstants;

/**
 * Sets the concept.true and concept.false global properties when they are missing.
 * <p>
 * OpenMRS core needs them to save Boolean obs (Obs.setValueBoolean stores the concept they name as the coded value).
 * Only a core liquibase migration from 1.x sets them, and databases created on OpenMRS 2.4 or later never run it.
 * See https://talk.openmrs.org/t/the-concept-false/42622, TRUNK-6232 and TRUNK-6233.
 * <p>
 * We use the PIH Yes and No concepts. An existing value is never changed.
 */
public class BooleanConceptsSetup {

    protected static final Log log = LogFactory.getLog(BooleanConceptsSetup.class);

    public static final String PIH_SOURCE = "PIH";

    public static final String TRUE_CONCEPT_CODE = "YES";

    public static final String FALSE_CONCEPT_CODE = "NO";

    public static void setupBooleanConceptsIfMissing(AdministrationService administrationService, ConceptService conceptService) {
        setIfMissing(administrationService, conceptService, OpenmrsConstants.GLOBAL_PROPERTY_TRUE_CONCEPT, TRUE_CONCEPT_CODE);
        setIfMissing(administrationService, conceptService, OpenmrsConstants.GLOBAL_PROPERTY_FALSE_CONCEPT, FALSE_CONCEPT_CODE);
    }

    private static void setIfMissing(AdministrationService administrationService, ConceptService conceptService,
                                     String property, String code) {
        if (StringUtils.isNotBlank(administrationService.getGlobalProperty(property))) {
            return;
        }
        Concept concept = conceptService.getConceptByMapping(code, PIH_SOURCE);
        if (concept == null) {
            log.warn("Global property " + property + " is not set, and there is no concept mapped to " + PIH_SOURCE
                    + ":" + code + " to set it to; Boolean obs can't be saved until it is set");
            return;
        }
        GlobalProperty gp = administrationService.getGlobalPropertyObject(property);
        if (gp == null) {
            gp = new GlobalProperty(property);
        }
        gp.setPropertyValue(concept.getConceptId().toString());
        administrationService.saveGlobalProperty(gp);
        log.warn("Set global property " + property + " to " + concept.getConceptId() + " (" + PIH_SOURCE + ":" + code + ")");
    }
}
