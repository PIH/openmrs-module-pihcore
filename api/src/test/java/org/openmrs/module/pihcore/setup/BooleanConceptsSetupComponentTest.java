package org.openmrs.module.pihcore.setup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptName;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.ConceptSource;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
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

    private Concept yes;

    private Concept no;

    @BeforeEach
    public void setUp() {
        // like a database first created on OpenMRS 2.4+: the PIH Yes and No concepts, but no concept.true / concept.false
        yes = pihConcept(BooleanConceptsSetup.TRUE_CONCEPT_CODE, "Yes");
        no = pihConcept(BooleanConceptsSetup.FALSE_CONCEPT_CODE, "No");
        removeGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_TRUE_CONCEPT);
        removeGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_FALSE_CONCEPT);
    }

    @Test
    public void shouldSetMissingTrueAndFalseConceptsToPihYesAndNo() {
        BooleanConceptsSetup.setupBooleanConceptsIfMissing(administrationService, conceptService);

        assertThat(administrationService.getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_TRUE_CONCEPT),
                is(yes.getConceptId().toString()));
        assertThat(administrationService.getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_FALSE_CONCEPT),
                is(no.getConceptId().toString()));
        assertThat(conceptService.getTrueConcept(), is(yes));
        assertThat(conceptService.getFalseConcept(), is(no));
    }

    @Test
    public void shouldSetBlankTrueAndFalseConcepts() {
        administrationService.saveGlobalProperty(new GlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_TRUE_CONCEPT, ""));
        administrationService.saveGlobalProperty(new GlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_FALSE_CONCEPT, " "));

        BooleanConceptsSetup.setupBooleanConceptsIfMissing(administrationService, conceptService);

        assertThat(administrationService.getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_TRUE_CONCEPT),
                is(yes.getConceptId().toString()));
        assertThat(administrationService.getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_FALSE_CONCEPT),
                is(no.getConceptId().toString()));
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
    public void shouldLeaveThemUnsetWhenYesAndNoAreMissing() {
        removePihMapping(yes, BooleanConceptsSetup.TRUE_CONCEPT_CODE);
        removePihMapping(no, BooleanConceptsSetup.FALSE_CONCEPT_CODE);

        BooleanConceptsSetup.setupBooleanConceptsIfMissing(administrationService, conceptService);

        assertThat(administrationService.getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_TRUE_CONCEPT), isEmptyOrNullString());
        assertThat(administrationService.getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_FALSE_CONCEPT), isEmptyOrNullString());
    }

    private Concept pihConcept(String code, String name) {
        Concept concept = conceptService.getConceptByMapping(code, BooleanConceptsSetup.PIH_SOURCE);
        if (concept == null) {
            ConceptSource pih = conceptService.getConceptSourceByName(BooleanConceptsSetup.PIH_SOURCE);
            if (pih == null) {
                pih = new ConceptSource();
                pih.setName(BooleanConceptsSetup.PIH_SOURCE);
                pih.setDescription(BooleanConceptsSetup.PIH_SOURCE);
                pih = conceptService.saveConceptSource(pih);
            }
            ConceptReferenceTerm term = conceptService.saveConceptReferenceTerm(new ConceptReferenceTerm(pih, code, null));
            concept = new Concept();
            concept.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
            concept.setDatatype(conceptService.getConceptDatatypeByName("N/A"));
            concept.setConceptClass(conceptService.getConceptClassByName("Misc"));
            concept.addConceptMapping(new ConceptMap(term, conceptService.getConceptMapTypeByName("SAME-AS")));
            concept = conceptService.saveConcept(concept);
        }
        return concept;
    }

    private void removePihMapping(Concept concept, String code) {
        for (ConceptMap map : new ArrayList<>(concept.getConceptMappings())) {
            ConceptReferenceTerm term = map.getConceptReferenceTerm();
            if (term.getCode().equals(code) && term.getConceptSource().getName().equals(BooleanConceptsSetup.PIH_SOURCE)) {
                concept.removeConceptMapping(map);
            }
        }
        conceptService.saveConcept(concept);
    }

    private void removeGlobalProperty(String property) {
        GlobalProperty gp = administrationService.getGlobalPropertyObject(property);
        if (gp != null) {
            administrationService.purgeGlobalProperty(gp);
        }
    }
}
