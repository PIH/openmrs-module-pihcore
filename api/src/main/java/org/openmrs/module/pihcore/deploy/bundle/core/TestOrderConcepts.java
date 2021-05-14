package org.openmrs.module.pihcore.deploy.bundle.core;

import org.openmrs.api.ConceptNameType;
import org.openmrs.module.metadatadeploy.builder.ConceptMapBuilder;
import org.openmrs.module.metadatadeploy.builder.ConceptNumericBuilder;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.VersionedPihConceptBundle;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.CoreConceptMetadataBundle;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Deprecated
@Component
@Requires({CoreConceptMetadataBundle.class})
public class TestOrderConcepts extends VersionedPihConceptBundle {

    public static final class Concepts {
        public static final String HEMOGLOBIN = "3ccc7158-26fe-102b-80cb-0017a47871b2";
        public static final String HEMATOCRIT = "3cd69a98-26fe-102b-80cb-0017a47871b2";
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    protected void installNewVersion() throws Exception {
        install(new ConceptNumericBuilder(Concepts.HEMATOCRIT)
                .units("%")
                .precise(true)
                .hiNormal(50.0)
                .lowAbsolute(0.0)
                .lowNormal(35.0)
                .datatype(numeric)
                .conceptClass(test)
                .name("3e148d20-26fe-102b-80cb-0017a47871b2", "Hematocrit", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("94b8dc5a-07d4-102c-b5fa-0017a47871b2", "Packed cell volume", Locale.ENGLISH, null)
                .name("0b7ba816-15f5-102d-96e4-000c29c2a5d7", "Hct", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("93f3926a-07d4-102c-b5fa-0017a47871b2", "CRIT", Locale.ENGLISH, null)
                .name("0b851fb8-15f5-102d-96e4-000c29c2a5d7", "HCT", Locale.ENGLISH, null)
                .name("9562d11a-07d4-102c-b5fa-0017a47871b2", "PCV", Locale.ENGLISH, null)
                .name("e552c9d0-a77e-4d89-8936-522d9fa0f06c", "Hématocrite", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("ece3785c-07fe-102c-b5fa-0017a47871b2", "Percent of whole blood that is composed of red blood cells.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b20c2adc-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1015").build())
                .mapping(new ConceptMapBuilder("75683d52-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "HEMATOCRIT").build())
                .mapping(new ConceptMapBuilder("c8d1d90d-ac94-4a03-8515-664fcd1d9f89")
                        .type(sameAs).ensureTerm(loinc, "20570-8").build())
                .build());

        install(new ConceptNumericBuilder(Concepts.HEMOGLOBIN)
                .units("g/dl")
                .precise(true)
                .hiAbsolute(20.0)
                .lowAbsolute(2.0)
                .lowNormal(11.5)
                .datatype(numeric)
                .conceptClass(test)
                .name("3e09b800-26fe-102b-80cb-0017a47871b2", "Hemoglobin", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("0b853dd6-15f5-102d-96e4-000c29c2a5d7", "HGB", Locale.ENGLISH, null)
                .name("94aa1e22-07d4-102c-b5fa-0017a47871b2", "Hb", Locale.ENGLISH, null)
                .name("0b7b1982-15f5-102d-96e4-000c29c2a5d7", "Hb", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("c90bd06f-0bda-48b9-8968-0614743602e8", "Hémoglobine", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("ecd675d0-07fe-102c-b5fa-0017a47871b2", "The iron-containing respiratory pigment in red blood cells of vertebrates, consisting of about 6 percent heme and 94 percent globin.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b1c14288-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "21").build())
                .mapping(new ConceptMapBuilder("c3e2d313-086e-445f-9fe5-2e5715903a1e")
                        .type(sameAs).ensureTerm(loinc, "718-7").build())
                .mapping(new ConceptMapBuilder("73800682-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "HEMOGLOBIN").build())
                .build());
    }

}
