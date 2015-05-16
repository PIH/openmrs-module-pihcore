package org.openmrs.module.pihcore.deploy.bundle;

import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.module.metadatadeploy.builder.ConceptMapBuilder;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.CoreConceptMetadataBundle;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.UUID;

/**
 * These are placeholder versions of concepts that we need to have available in unit tests, and we presume are actually
 * loaded from MDS packages in the distro. (We don't load the big concepts MDS packages during unit tests.)
 */
@Component
@Requires(CoreConceptMetadataBundle.class)
public class ConceptsFromMetadataSharing extends VersionedPihConceptBundle {

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    protected void installNewVersion() throws Exception {
        install(frequency(OrderEntryConcepts.Concepts.ONCE_A_DAY));
        install(frequency(OrderEntryConcepts.Concepts.TWICE_A_DAY));
        install(frequency(OrderEntryConcepts.Concepts.THREE_TIMES_A_DAY));
        install(frequency(OrderEntryConcepts.Concepts.FOUR_TIMES_A_DAY));
        install(frequency(OrderEntryConcepts.Concepts.FIVE_TIMES_A_DAY));
        install(frequency(OrderEntryConcepts.Concepts.SIX_TIMES_A_DAY));
        install(frequency(OrderEntryConcepts.Concepts.SEVEN_TIMES_A_DAY));
        install(frequency(OrderEntryConcepts.Concepts.EIGHT_TIMES_A_DAY));
        install(frequency(OrderEntryConcepts.Concepts.NINE_TIMES_A_DAY));
        install(frequency(OrderEntryConcepts.Concepts.EVERY_TWO_HOURS));
        install(frequency(OrderEntryConcepts.Concepts.EVERY_THREE_HOURS));
        install(frequency(OrderEntryConcepts.Concepts.EVERY_FOUR_HOURS));
        install(frequency(OrderEntryConcepts.Concepts.EVERY_SIX_HOURS));
        install(frequency(OrderEntryConcepts.Concepts.EVERY_TWELVE_HOURS));
        install(frequency(OrderEntryConcepts.Concepts.IN_THE_MORNING));
        install(frequency(OrderEntryConcepts.Concepts.AT_NIGHT));
        install(frequency(OrderEntryConcepts.Concepts.WHEN_REQUIRED));
        install(frequency(OrderEntryConcepts.Concepts.IMMEDIATELY));
    }

    private Concept frequency(String uuid) {
        Concept concept = baseConcept(uuid);
        concept.setDatatype(notApplicable);
        concept.setConceptClass(frequency);
        concept.setUuid(uuid);
        return concept;
    }

    private Concept baseConcept(String name) {
        Concept concept = new Concept();
        concept.addName(new ConceptName(name, Locale.ENGLISH));
        concept.addConceptMapping(new ConceptMapBuilder(uuid()).type(sameAs).ensureTerm(pih, name).build());
        return concept;
    }

    private String uuid() {
        return UUID.randomUUID().toString();
    }
}
