package org.openmrs.module.pihcore.reporting;

import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptName;
import org.openmrs.ConceptSource;
import org.openmrs.module.metadatadeploy.builder.ConceptMapBuilder;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.VersionedPihConceptBundle;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.CoreConceptMetadataBundle;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.UUID;

/**
 * These are placeholder versions of concepts that are actually imported via MDS packages, that we need to have
 * available in unit tests to be able to test forms (and we don't load the big concepts MDS package during unit tests)
 */
@Component
@Requires(CoreConceptMetadataBundle.class)
public class MockConcepts extends VersionedPihConceptBundle {

    @Override
    public int getVersion() {
        return 2;
    }

    @Override
    protected void installNewVersion() throws Exception {
        install(question("Reason for visit", coded, pih, "REASON FOR VISIT"));
        install(answer("Malnutrition program", pih, "MALNUTRITION PROGRAM"));

        install(question("Chief complaint", text, ciel, "160531"));
    }

    private Concept baseConcept(String name) {
        Concept concept = new Concept();
        concept.addName(new ConceptName(name, Locale.ENGLISH));
        concept.addConceptMapping(new ConceptMapBuilder(uuid()).type(sameAs).ensureTerm(pih, name).build());
        return concept;
    }

    private Concept question(String name, ConceptDatatype datatype, ConceptSource source, String mapping) {
        Concept concept = baseConcept(name);
        concept.setDatatype(datatype);
        concept.setConceptClass(question);
        concept.addConceptMapping(new ConceptMapBuilder(uuid()).type(sameAs).ensureTerm(source, mapping).build());
        return concept;
    }

    private Concept answer(String name, ConceptSource source, String mapping) {
        Concept concept = baseConcept(name);
        concept.setDatatype(notApplicable);
        concept.setConceptClass(misc);
        concept.addConceptMapping(new ConceptMapBuilder(uuid()).type(sameAs).ensureTerm(source, mapping).build());
        return concept;
    }

    private Concept finding(String name, ConceptDatatype datatype, ConceptSource source, String mapping) {
        Concept concept = baseConcept(name);
        concept.setDatatype(datatype);
        concept.setConceptClass(finding);
        concept.addConceptMapping(new ConceptMapBuilder(uuid()).type(sameAs).ensureTerm(source, mapping).build());
        return concept;
    }

    private Concept diagnosis(String name) {
        Concept concept = baseConcept(name);
        concept.setDatatype(notApplicable);
        concept.setConceptClass(diagnosis);
        return concept;
    }

    private Concept diagnosis(String name, ConceptSource source, String mapping) {
        Concept concept = diagnosis(name);
        concept.addConceptMapping(new ConceptMapBuilder(uuid()).type(sameAs).ensureTerm(source, mapping).build());
        return concept;
    }

    private String uuid() {
        return UUID.randomUUID().toString();
    }
}
