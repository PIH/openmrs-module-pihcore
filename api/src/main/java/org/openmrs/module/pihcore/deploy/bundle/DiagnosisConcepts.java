package org.openmrs.module.pihcore.deploy.bundle;

import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * DO NOT USE THIS BUNDLE.
 *
 * Diagnoses should all come from MDS packages. This class still exists so that we can delete the one bad concept that
 * was created through it at some point.
 */
@Component
@Requires({CoreConceptMetadataBundle.class})
public class DiagnosisConcepts extends VersionedPihConceptBundle {

    public static final String BAD_CONCEPT_CARDIOPATHY = "6c07611c-a10e-4de7-be64-28cdf76144e9"; // incorrect new concept for PIH:BAD_CONCEPT_CARDIOPATHY that we will delete

    @Override
    public int getVersion() {
        return 2;
    }

    @Autowired
    private ConceptService conceptService;

    @Override
    protected void installNewVersion() throws Exception {
        Concept toDelete = conceptService.getConceptByUuid(BAD_CONCEPT_CARDIOPATHY);
        if (toDelete != null) {
            conceptService.purgeConcept(toDelete);
        }
    }

}
