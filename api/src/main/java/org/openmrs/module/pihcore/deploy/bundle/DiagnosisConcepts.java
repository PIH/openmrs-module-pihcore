package org.openmrs.module.pihcore.deploy.bundle;

import org.openmrs.api.ConceptNameType;
import org.openmrs.module.metadatadeploy.builder.ConceptBuilder;
import org.openmrs.module.metadatadeploy.builder.ConceptMapBuilder;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Ultimately we need to find a way to harmonize all the diagnoses in the HUM_Clinical_Concepts MDS package with this
 * approach
 */
@Component
@Requires({CoreConceptMetadataBundle.class})
public class DiagnosisConcepts extends VersionedPihConceptBundle {

    @Override
    public int getVersion() {
        return 1;
    }

    public static final class Concepts {
        public static final String CARDIOPATHY = "6c07611c-a10e-4de7-be64-28cdf76144e9"; // PIH:CARDIOPATHY
    }

    @Override
    protected void installNewVersion() throws Exception {
        install(new ConceptBuilder(Concepts.CARDIOPATHY)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("a4a83322-db96-4689-8d64-891b4a87986a", "Cardiopathy", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("f28f6b59-18f2-4127-802f-b6f1f8d9c3c8", "A disease or disorder of the heart", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("0f2760de-a543-102e-a00e-000c29c2a5d7")
                        .type(sameAs)
                        .ensureTerm(pih, "7041")
                        .build())
                .mapping(new ConceptMapBuilder("cf1a37c8-a542-102e-a00e-000c29c2a5d7")
                        .type(sameAs)
                        .ensureTerm(pih, "CARDIOPATHY")
                        .build())
                .build());

    }

}
