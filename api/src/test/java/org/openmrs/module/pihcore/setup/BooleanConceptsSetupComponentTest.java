package org.openmrs.module.pihcore.setup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;

public class BooleanConceptsSetupComponentTest extends PihCoreContextSensitiveTest {

    @Autowired
    @Qualifier("adminService")
    AdministrationService administrationService;

    @Autowired
    @Qualifier("conceptService")
    ConceptService conceptService;

    private Concept coreTrue;

    private Concept coreFalse;

    @BeforeEach
    public void setUp() {
        // what a database first created on OpenMRS 2.4+ looks like: core's True and False concepts, but no
        // concept.true / concept.false
        coreTrue = coreConcept(BooleanConceptsSetup.CORE_TRUE_CONCEPT_UUID, "True");
        coreFalse = coreConcept(BooleanConceptsSetup.CORE_FALSE_CONCEPT_UUID, "False");
        removeGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_TRUE_CONCEPT);
        removeGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_FALSE_CONCEPT);
    }

    @Test
    public void shouldSetMissingTrueAndFalseConceptsToCoresTrueAndFalse() {
        BooleanConceptsSetup.setupBooleanConceptsIfMissing(administrationService, conceptService);

        assertThat(administrationService.getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_TRUE_CONCEPT),
                is(coreTrue.getConceptId().toString()));
        assertThat(administrationService.getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_FALSE_CONCEPT),
                is(coreFalse.getConceptId().toString()));
        assertThat(conceptService.getTrueConcept(), is(coreTrue));
        assertThat(conceptService.getFalseConcept(), is(coreFalse));
    }

    @Test
    public void shouldSetBlankTrueAndFalseConcepts() {
        administrationService.saveGlobalProperty(new GlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_TRUE_CONCEPT, ""));
        administrationService.saveGlobalProperty(new GlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_FALSE_CONCEPT, " "));

        BooleanConceptsSetup.setupBooleanConceptsIfMissing(administrationService, conceptService);

        assertThat(administrationService.getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_TRUE_CONCEPT),
                is(coreTrue.getConceptId().toString()));
        assertThat(administrationService.getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_FALSE_CONCEPT),
                is(coreFalse.getConceptId().toString()));
    }

    @Test
    public void shouldNeverOverwriteExistingValues() {
        administrationService.saveGlobalProperty(new GlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_TRUE_CONCEPT, "5089"));
        administrationService.saveGlobalProperty(new GlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_FALSE_CONCEPT, "5090"));

        BooleanConceptsSetup.setupBooleanConceptsIfMissing(administrationService, conceptService);

        assertThat(administrationService.getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_TRUE_CONCEPT), is("5089"));
        assertThat(administrationService.getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_FALSE_CONCEPT), is("5090"));
    }

    @Test
    public void shouldLeaveThemUnsetWhenCoresConceptsAreMissing() {
        conceptService.purgeConcept(coreTrue);
        conceptService.purgeConcept(coreFalse);

        BooleanConceptsSetup.setupBooleanConceptsIfMissing(administrationService, conceptService);

        assertThat(administrationService.getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_TRUE_CONCEPT), isEmptyOrNullString());
        assertThat(administrationService.getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_FALSE_CONCEPT), isEmptyOrNullString());
    }

    private Concept coreConcept(String uuid, String name) {
        Concept concept = conceptService.getConceptByUuid(uuid);
        if (concept == null) {
            concept = new Concept();
            concept.setUuid(uuid);
            concept.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
            concept.setDatatype(conceptService.getConceptDatatypeByName("N/A"));
            concept.setConceptClass(conceptService.getConceptClassByName("Misc"));
            concept = conceptService.saveConcept(concept);
        }
        return concept;
    }

    private void removeGlobalProperty(String property) {
        GlobalProperty gp = administrationService.getGlobalPropertyObject(property);
        if (gp != null) {
            administrationService.purgeGlobalProperty(gp);
        }
    }
}
