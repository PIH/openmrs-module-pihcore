package org.openmrs.module.pihcore.deploy.bundle.core.concept;

import org.openmrs.api.ConceptNameType;
import org.openmrs.module.metadatadeploy.builder.ConceptBuilder;
import org.openmrs.module.metadatadeploy.builder.ConceptMapBuilder;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.VersionedPihConceptBundle;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A small number of very common concepts
 */
@Component
@Requires(CoreConceptMetadataBundle.class)
public class CommonConcepts extends VersionedPihConceptBundle {

    @Override
    public int getVersion() {
        return 3;
    }

    public static final class Concepts {
        public static final String UNKNOWN = "3cd6fac4-26fe-102b-80cb-0017a47871b2";
        public static final String YES = "3cd6f600-26fe-102b-80cb-0017a47871b2";
        public static final String NO = "3cd6f86c-26fe-102b-80cb-0017a47871b2";
        public static final String OTHER_NON_CODED = "3cee7fb4-26fe-102b-80cb-0017a47871b2";
        public static final String FREE_TEXT_GENERAL = "160632AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        public static final String NONE = "3cd743f8-26fe-102b-80cb-0017a47871b2";
        public static final String NOT_APPLICABLE = "3cd7b72a-26fe-102b-80cb-0017a47871b2";
    }

    @Override
    protected void installEveryTime() throws Exception {
        Map<String, String> properties = new LinkedHashMap<String, String>();
        properties.put("concept.unknown", Concepts.UNKNOWN); // see HtmlFormEntryConstants.GP_UNKNOWN_CONCEPT
        setGlobalProperties(properties);
    }

    @Override
    protected void installNewVersion() throws Exception {
        install(new ConceptBuilder(Concepts.YES)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e15161e-26fe-102b-80cb-0017a47871b2", "Yes", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("0b931af0-15f5-102d-96e4-000c29c2a5d7", "Oui", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("541f1720-0abe-49dd-b4f6-7de87ce05917", "Wi", locale_HAITI, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("ece437ba-07fe-102c-b5fa-0017a47871b2", "Generic answer to a question.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("7568887a-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "YES").build())
                .mapping(new ConceptMapBuilder("b20d114a-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1065").build())
                .build());

        install(new ConceptBuilder(Concepts.NO)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e151786-26fe-102b-80cb-0017a47871b2", "No", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("743d14ca-dd31-4ed7-8bfb-b2e4c98a2609", "Non", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("5bcc9a17-0a17-4075-84d4-f09882cfaa18", "Non", locale_HAITI, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("d07abafd-ea95-4edd-9c84-ee85592fc91f", "Generic answer to question.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b20d12bc-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1066").build())
                .mapping(new ConceptMapBuilder("aefacf53-8e8e-4ed0-9d32-cbd1d14c6639")
                        .type(sameAs).ensureTerm(pih, "NO").build())
                .build());

        install(new ConceptBuilder(Concepts.UNKNOWN)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e1518f8-26fe-102b-80cb-0017a47871b2", "Unknown", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("0b8bdd6c-15f5-102d-96e4-000c29c2a5d7", "DON'T KNOW", Locale.ENGLISH, null)
                .name("0b8bde66-15f5-102d-96e4-000c29c2a5d7", "DO NOT KNOW", Locale.ENGLISH, null)
                .name("0b8bdf56-15f5-102d-96e4-000c29c2a5d7", "Inconnu", Locale.FRENCH, null) // locale-preferred
                .description("ece43b8e-07fe-102c-b5fa-0017a47871b2", "Generic answer to a question.", Locale.ENGLISH)
                .description("5a0076f0-1fc0-4c91-8d92-0541eab81216", "réponse générique à la question", Locale.FRENCH)
                .mapping(new ConceptMapBuilder("48d452b4-2448-4b4a-ab17-5993cd2aa765")
                        .type(sameAs).ensureTerm(pih, "unknown").build())
                .mapping(new ConceptMapBuilder("b20d1438-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1067").build())
                .mapping(new ConceptMapBuilder("aef53e25-42b3-4e15-b072-773408c42fcf")
                        .type(sameAs).ensureTerm(emrapi, "Unknown Cause of Death").build())
                .mapping(new ConceptMapBuilder("fe4792ee-5b68-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(mdrtb, "UNKNOWN").build())
                .build());

        install(new ConceptBuilder(Concepts.OTHER_NON_CODED)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("1724c34c-6f89-4bd0-8bf7-ec83c3ba53fb", "Other", Locale.ENGLISH, null) // locale-preferred
                .name("0b6f94ea-15f5-102d-96e4-000c29c2a5d7", "Other non-coded", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("110860BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Otro no codificado", locale_SPANISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("af406c44-9ddb-4808-8caa-8361f7f95028", "Autre", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("14a06afe-a240-4d52-8eda-a762857aabe8", "Autre, non spécifiée", Locale.FRENCH, null)
                .name("105802BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Autre et non codé", Locale.FRENCH, null)
                .name("abc2ce9a-2a12-46b5-9bd7-cee4d9f61b08", "Lòt", locale_HAITI, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("110113BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Nyinginezo", locale_SWAHILI, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("0b40b634-15f5-102d-96e4-000c29c2a5d7", "A generic answer which is a coded response to a question.  If appropriate, there might be a corresponding question which contains this non-coded response (ie. Deliver Location Non-Coded where datatype=text)", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b299b5b4-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "5622").build())
                .mapping(new ConceptMapBuilder("75aa9616-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "OTHER NON-CODED").build())
                .mapping(new ConceptMapBuilder("137878ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(pih, "OTHER").build())
                .mapping(new ConceptMapBuilder("6fb13061-8e19-432e-8678-5aa415cf0320")
                        .type(sameAs).ensureTerm(pihMalawi, "6408").build())
                .mapping(new ConceptMapBuilder("04ef5e6c-3062-432a-ad7f-0581a6cac76d")
                        .type(sameAs).ensureTerm(ciel, "5622").build())
                .mapping(new ConceptMapBuilder("a567d7c3-7943-41e4-80d5-6090aedcb6b6")
                        .type(sameAs).ensureTerm(mdrtb, "OTHER").build())
                .mapping(new ConceptMapBuilder("141670ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(snomedMvp, "56221000105001").build())
                .mapping(new ConceptMapBuilder("137766ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(narrowerThan).ensureTerm(snomedNp, "74964007").build())
                .mapping(new ConceptMapBuilder("4f37b2ff-7456-4db7-a8de-1d312f586ef2")
                        .type(sameAs).ensureTerm(ampath, "5622").build())
                .build());


        install(new ConceptBuilder(Concepts.FREE_TEXT_GENERAL)
                .datatype(text)
                .conceptClass(question)
                .name("109086BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Free text general", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("109085BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Freetext general", Locale.ENGLISH, null)
                .description("16778FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", "This is a freetext field for general use.  It should be tied to another concept as a child.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("98a91a12-3095-4a11-9d90-a112201f6d59")
                        .type(sameAs).ensureTerm(ampath, "1915").build())
                .mapping(new ConceptMapBuilder("567ec656-4791-4423-8fc3-ffd215eb2eb8")
                        .type(sameAs).ensureTerm(pih, "GENERAL FREE TEXT").build())
                .mapping(new ConceptMapBuilder("217755ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "160632").build())
                .mapping(new ConceptMapBuilder("e65c0819-271d-4289-85a8-20d22b10d08a")
                        .type(sameAs).ensureTerm(pih, "9721").build())
                .build());

        install(new ConceptBuilder(Concepts.NONE)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e15729e-26fe-102b-80cb-0017a47871b2", "None", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("3e157424-26fe-102b-80cb-0017a47871b2", "Aucun", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("ece4993a-07fe-102c-b5fa-0017a47871b2", "Generic descriptive answer.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("7568a490-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "NONE").build())
                .mapping(new ConceptMapBuilder("b20d518c-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1107").build())
                .mapping(new ConceptMapBuilder("430dd060-0371-102d-b0e3-001ec94a0cc1")
                        .type(sameAs).ensureTerm(mdrtb, "NONE").build())
                .build());

        install(new ConceptBuilder(Concepts.NOT_APPLICABLE)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e16182a-26fe-102b-80cb-0017a47871b2", "Not applicable", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("0b8e7ce8-15f5-102d-96e4-000c29c2a5d7", "Sans objet", Locale.FRENCH, null) // locale-preferred
                .name("f5dfdb38-d5db-102d-ad2a-000c29c2a5d7", "Pas valable", Locale.FRENCH, null)
                .name("0b8e7bee-15f5-102d-96e4-000c29c2a5d7", "Ne pas applicable", Locale.FRENCH, null)
                .description("ece55cc6-07fe-102c-b5fa-0017a47871b2", "Generic response to a question.  The question doesn't directly apply to the person.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b20f276e-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1175").build())
                .build());
    }

}
