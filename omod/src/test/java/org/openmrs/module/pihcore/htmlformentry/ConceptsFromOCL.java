package org.openmrs.module.pihcore.htmlformentry;

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
 * These are placeholder versions of concepts that are actually imported via OCL, that we need to have
 * available in unit tests to be able to test forms
 */
@Component("hfeConceptsFromOCL")
@Requires(CoreConceptMetadataBundle.class)
public class ConceptsFromOCL extends VersionedPihConceptBundle {

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    protected void installNewVersion() throws Exception {
        install(diagnosis("ASTHMA"));
        install(diagnosis("EPILEPSY"));
        install(diagnosis("HYPERTENSION"));
        install(diagnosis("SEXUALLY TRANSMITTED INFECTION"));
        install(diagnosis("WEIGHT LOSS"));
        install(diagnosis("MEASLES"));
        install(diagnosis("TUBERCULOSIS"));
        install(diagnosis("VARICELLA"));
        install(diagnosis("Diphtheria"));
        install(diagnosis("ACUTE RHEUMATIC FEVER"));
        install(diagnosis("DIABETES"));
        install(diagnosis("Traumatic Injury"));
        install(diagnosis("MALNUTRITION"));
        install(diagnosis("HEART DISEASE"));
        install(diagnosis("Premature birth of patient"));
        install(diagnosis("Cancer", ciel, "116031"));
        install(diagnosis("Hémoglobinopathie", ciel, "117635"));
        install(diagnosis("Malformations congénitales", ciel, "143849"));
        install(diagnosis("RAA"));
        install(question("Clinical Impression Comments", text, emrapi, "Consult Free Text Comments"));
        install(question("Current Medications", text, pih, "CURRENT MEDICATIONS"));
        install(question("Diagnostic Tests History", text, pih, "DIAGNOSTIC TESTS HISTORY"));
        install(question("Disposition comment", text, pih, "DISPOSITION COMMENTS"));
        install(question("Blood type", coded, pih, "BLOOD TYPING"));
        install(finding("Chief complaint", text, ciel, "160531"));
        install(question("Clinical comments", text, pih, "CLINICAL IMPRESSION COMMENTS"));
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
