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
 * OpenMRS core needs them to save Boolean obs (Obs.setValueBoolean stores core's True or False concept as the coded
 * value). Only a core liquibase migration from 1.x sets them, and databases created on OpenMRS 2.4 or later never run
 * it, so they have core's True and False concepts but neither global property. See
 * https://talk.openmrs.org/t/the-concept-false/42622, TRUNK-6232 and TRUNK-6233.
 * <p>
 * An existing value is never changed.
 */
public class BooleanConceptsSetup {

    protected static final Log log = LogFactory.getLog(BooleanConceptsSetup.class);

    public static final String CORE_TRUE_CONCEPT_UUID = "cf82933b-3f3f-45e7-a5ab-5d31aaee3da3";

    public static final String CORE_FALSE_CONCEPT_UUID = "488b58ff-64f5-4f8a-8979-fa79940b1594";

    public static void setupBooleanConceptsIfMissing(AdministrationService administrationService, ConceptService conceptService) {
        setIfMissing(administrationService, conceptService, OpenmrsConstants.GLOBAL_PROPERTY_TRUE_CONCEPT, CORE_TRUE_CONCEPT_UUID);
        setIfMissing(administrationService, conceptService, OpenmrsConstants.GLOBAL_PROPERTY_FALSE_CONCEPT, CORE_FALSE_CONCEPT_UUID);
    }

    private static void setIfMissing(AdministrationService administrationService, ConceptService conceptService,
                                     String property, String conceptUuid) {
        if (StringUtils.isNotBlank(administrationService.getGlobalProperty(property))) {
            return;
        }
        Concept concept = conceptService.getConceptByUuid(conceptUuid);
        if (concept == null) {
            log.warn("Global property " + property + " is not set, and there is no concept with uuid " + conceptUuid
                    + " to set it to; Boolean obs can't be saved until it is set");
            return;
        }
        GlobalProperty gp = administrationService.getGlobalPropertyObject(property);
        if (gp == null) {
            gp = new GlobalProperty(property);
        }
        gp.setPropertyValue(concept.getConceptId().toString());
        administrationService.saveGlobalProperty(gp);
        log.info("Set global property " + property + " to " + concept.getConceptId() + " (concept " + conceptUuid + ")");
    }
}
