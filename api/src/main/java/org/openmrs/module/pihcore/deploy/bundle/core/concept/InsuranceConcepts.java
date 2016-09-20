package org.openmrs.module.pihcore.deploy.bundle.core.concept;

import org.openmrs.Concept;
import org.openmrs.api.ConceptNameType;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.builder.ConceptBuilder;
import org.openmrs.module.metadatadeploy.builder.ConceptMapBuilder;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.VersionedPihConceptBundle;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Created by ball on 8/8/16.
 */
@Component
@Requires({CoreConceptMetadataBundle.class, CommonConcepts.class})
public class InsuranceConcepts extends VersionedPihConceptBundle {

    @Override
    public int getVersion() {
        return 2;
    }


    public static final class Concepts {
        public static final String HAITI_INSURANCE_COMPANY_NAME= "c9131281-e086-41a1-a8cb-4cce9fdefe05";
        public static final String INSURANCE_POLICY_NUMBER= "762fde47-e001-43bd-a808-154452d24cb4";
        public static final String OTHER_INSURANCE_COMPANY_NAME= "ce581a9c-433f-43f4-8a03-58cc83af5e3d";
    }

    @Override
    protected void installNewVersion() throws Exception {

        Concept unknown = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.UNKNOWN);
        Concept other = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.OTHER_NON_CODED);
        Concept none = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.NONE);


        Concept insuranceNumber = install(new ConceptBuilder(Concepts.INSURANCE_POLICY_NUMBER)
                .datatype(text)
                .conceptClass(question)
                .name("efd4653e-9a0f-4d60-bd41-19795c7ffce9", "Insurance policy number", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("92f5bdd1-6a09-43ed-a83f-e750474ed562", "Numéro d'assurance", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("8d2bf20b-5cb9-405a-84cb-6c3e31fc21d9", "Nimewo asirans", locale_HAITI, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("5b859e0f-57e1-45b6-a2af-5cee07e414e9", "Policy number for insurance", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("4b4fd997-3b3e-4ee2-8a38-cc3f5cf10e20")
                        .type(sameAs).ensureTerm(pih, "Insurance policy number").build())
                .mapping(new ConceptMapBuilder("19e0f0c4-dd0e-4002-a501-368d042d3d73")
                        .type(sameAs).ensureTerm(pih, "10737").build())
                .build());

        Concept cigna = install(new ConceptBuilder("e45d8d56-a6bd-4b46-9939-3f6ae24bbba5")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("d8b1954e-aabf-426e-8f39-932f6899bfed", "CIGNA (Vanbreda)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("5aa133dd-29dd-421d-b2c4-ce4c44d2481f", "CIGNA (Vanbreda)", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("c972ca93-3f9a-4ec3-8828-46c78a84cb4b", "CIGNA (Vanbreda)", locale_HAITI, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("0350f7c1-0377-4fb4-884f-53f742645065", "Haitian insurance", locale_HAITI)
                .mapping(new ConceptMapBuilder("65a15b60-0d4a-4cc1-8325-4f70e3e75158")
                        .type(sameAs).ensureTerm(pih, "CIGNA (Vanbreda)").build())
                .mapping(new ConceptMapBuilder("842d0137-3cda-4d5e-ba56-3db54b1c5cc9")
                        .type(sameAs).ensureTerm(pih, "10736").build())
                .build());

        Concept gmcHenner = install(new ConceptBuilder("aa6e61f7-dafd-4f5d-a013-97cbeab9c14f")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("57a38db5-5468-44c4-af20-82f5f84130fd", "GMC Henner", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("1d2be8c6-9205-49ed-935f-a18b8f7e54fc", "GMC Henner", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("7368fe1b-05ea-4c38-9606-67d853a60d6e", "GMC Henner", locale_HAITI, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("70ef0782-68b5-4bd7-b0bb-3e0b9451d0e6", "Insurance company", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("460302b5-2731-40ab-b525-b71bc3c3532b")
                        .type(sameAs).ensureTerm(pih, "10735").build())
                .mapping(new ConceptMapBuilder("c31ec091-7e3b-41ae-ba5a-c4d00c404f17")
                        .type(sameAs).ensureTerm(pih, "GMC Henner").build())
                .build());

        Concept uniAssurances = install(new ConceptBuilder("fbb45a9d-25be-41ca-8fba-a64d96f2867a")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("c0cf8282-39e0-46a2-aca2-767c589c0d77", "UniAssurances", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("1a40d23f-4f48-4392-9a7f-7709b2a0e891", "UniAssurances", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("adb79f84-6c91-4234-a8fd-a0202767a92c", "UniAssurances", locale_HAITI, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("d424f821-3539-4f79-875d-2a1504230ef5", "Haitian insurance company", locale_HAITI)
                .mapping(new ConceptMapBuilder("a962b737-4a78-4a27-b3f6-5ac2494f3d72")
                        .type(sameAs).ensureTerm(pih, "10734").build())
                .mapping(new ConceptMapBuilder("ccdf1c5d-f36e-48a2-8113-f13facbb9e3c")
                        .type(sameAs).ensureTerm(pih, "UniAssurances").build())
                .build());

        Concept sogebankAssurance = install(new ConceptBuilder("bfa263ac-cf3d-4250-a6d4-180ce918b0ef")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("003d636f-4eb8-4895-8a23-60db0510db81", "Sogebank Assurance", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("335f7137-da23-41c9-851b-734c1412ce2e", "Sogebank Assurance", Locale.FRENCH, null) // locale-preferred
                .name("b5fdb5a1-d5b6-45c6-923f-808c571fc8af", "Société générale haïtienne de Banque", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)
                .name("a7a88d46-ab0e-4df0-b1cf-c497c96db78b", "Sogebank Assurance", locale_HAITI, null) // locale-preferred
                .name("9af9a671-4ca6-4cb1-b21b-f0261612b81a", "Société générale haïtienne de Banque", locale_HAITI, ConceptNameType.FULLY_SPECIFIED)
                .description("ab749613-15b0-4110-9d92-f7d991f5cfcb", "Haitian insurance company", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("f0b4cb9c-1a73-4df9-9bf0-de08d6f7e4f1")
                        .type(sameAs).ensureTerm(pih, "10733").build())
                .mapping(new ConceptMapBuilder("23ddc03d-1fd7-451f-b7bd-4442f076642d")
                        .type(sameAs).ensureTerm(pih, "Sogebank Assurance").build())
                .build());

        Concept conam = install(new ConceptBuilder("939e2288-a9df-4f63-8b24-95674d04bd33")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("255165d4-8646-4b4f-a362-0148fb107944", "CONAM", Locale.ENGLISH, null) // locale-preferred
                .name("4cda186d-c11c-4c9d-a6b8-9f7dd3808cc8", "Coordination Nationale Maladie", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("bf36623c-9c5f-4c8d-9207-76665ffa7f7f", "CONAM", Locale.FRENCH, null) // locale-preferred
                .name("69e12f35-8ec4-490e-9bef-55c55061cb8c", "Coordination Nationale Maladie", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)
                .name("34645aaa-109b-4bf1-9c78-7cfdb2d0c884", "CONAM", locale_HAITI, null) // locale-preferred
                .name("102b89f6-3a3e-4c72-953f-9d826abe8c31", "Coordination Nationale Maladie", locale_HAITI, ConceptNameType.FULLY_SPECIFIED)
                .description("b952d4ba-ae41-41ba-8bf1-752b3dd2b96e", "Haiti insurance company", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("f5af26fd-7bc4-4c74-a3b1-f2121d752a0c")
                        .type(sameAs).ensureTerm(pih, "10732").build())
                .mapping(new ConceptMapBuilder("179e0d4f-ce72-44fe-a236-089590c62e44")
                        .type(sameAs).ensureTerm(pih, "CONAM").build())
                .build());

        Concept cah = install(new ConceptBuilder("acf84e71-a141-4d3a-a33d-8aa8694ce03b")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("58408ad2-905e-4e13-8e97-f61085baa89e", "CAH", Locale.ENGLISH, null) // locale-preferred
                .name("cd8735e9-93bc-4c43-8087-9560ea91ac34", "Compagnie d’assurance d’Haïti", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("d64ce12b-6eb6-4c5d-8c45-0484b00dfb41", "CAH", Locale.FRENCH, null) // locale-preferred
                .name("c020de03-bc1f-4f60-ae46-b2a0ecc6d463", "Compagnie d’assurance d’Haïti", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)
                .name("736a7b0d-0544-4828-a9f7-98c06b0e1d06", "CAH", locale_HAITI, null) // locale-preferred
                .name("cda4e0e9-f0d7-45a4-8d1a-af9832c7aad9", "Compagnie d’assurance d’Haïti", locale_HAITI, ConceptNameType.FULLY_SPECIFIED)
                .description("2d98725f-7fde-4847-b234-6942ee55baa6", "Insurance company in Haiti", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("8b6103b6-419c-4512-9430-61f9b6929d7f")
                        .type(sameAs).ensureTerm(pih, "10731").build())
                .mapping(new ConceptMapBuilder("02a8eff2-24eb-4266-8eec-c901b7eed04c")
                        .type(sameAs).ensureTerm(pih, "CAH").build())
                .build());

        Concept inassa = install(new ConceptBuilder("8e027388-697b-4a82-9249-7f76bd7b00d8")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("96f118d2-f5df-4dbe-908f-0abd0661eb74", "INASSA", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("926305e0-dd7e-439d-96c2-ffa9cda206dc", "INASSA", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("6aa6e224-75a9-4cad-8a31-cd549d8ca043", "INASSA", locale_HAITI, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("841caf86-9af3-4d2b-810b-0a5fae4b35ca", "Haitian insurance company", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("0def79ed-eaba-4fea-a459-b30eea7226a2")
                        .type(sameAs).ensureTerm(pih, "INASSA").build())
                .mapping(new ConceptMapBuilder("d17cac75-2f7a-47e5-bcee-8a9a1b17c5c0")
                        .type(sameAs).ensureTerm(pih, "10730").build())
                .build());

        Concept aic = install(new ConceptBuilder("4ee3cbfa-1e09-4d5e-bcd1-d7529a34774b")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("47dd45ea-c049-44fb-9869-e11afe505f41", "AIC", Locale.ENGLISH, null) // locale-preferred
                .name("5e469492-243d-4c55-87eb-f11d8d653694", "Alternative Insurance Company", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("c1576739-a764-4a14-b9b5-bba66304a72c", "AIC", Locale.FRENCH, null) // locale-preferred
                .name("285085cb-1ee7-434d-a4c8-8e755b95c812", "Alternative Insurance Company", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)
                .name("4f7d9907-395c-4dd6-ab41-c0a970fd1da9", "AIC", locale_HAITI, null) // locale-preferred
                .name("4c1b5cf7-a611-4e47-9d90-4e9ad8960ed4", "Alternative Insurance Company", locale_HAITI, ConceptNameType.FULLY_SPECIFIED)
                .description("81bad248-9a7c-4103-844a-b0ec00b6f3b8", "Haitian insurance company", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("4a926977-24ef-45fd-b314-c366e8eb5f7a")
                        .type(sameAs).ensureTerm(pih, "AIC").build())
                .mapping(new ConceptMapBuilder("d0f37631-dfd9-42cb-92cf-d8858cbb595b")
                        .type(sameAs).ensureTerm(pih, "10729").build())
                .build());

        Concept haitiInsuranceCo = install(new ConceptBuilder(Concepts.HAITI_INSURANCE_COMPANY_NAME)
                .datatype(coded)
                .conceptClass(question)
                .name("be6a0015-3fc1-4559-8c31-24a5126e0537", "Haiti insurance company name", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("6c174385-836d-481f-9753-4a6ccbe26086", "The name of various insurance companies in Haiti.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b942bae4-3e9d-490e-89e7-7a36309d603a")
                        .type(sameAs).ensureTerm(pih, "Haiti insurance company name").build())
                .mapping(new ConceptMapBuilder("18eb55fa-080b-406a-a09e-ed1378b19f1a")
                        .type(sameAs).ensureTerm(pih, "10728").build())
                .answers(
                        aic,inassa,cah,conam,sogebankAssurance,uniAssurances,gmcHenner,cigna,
                        none,other,unknown)
                .build());

        Concept insuranceCoOther = install(new ConceptBuilder(Concepts.OTHER_INSURANCE_COMPANY_NAME)
                .datatype(text)
                .conceptClass(question)
                .name("ddd3d35b-0b7a-4d33-8ffe-dce09cb978aa", "Insurance company name (text)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("9b49feaa-cc1b-47d9-aab9-207606317bac", "Insurance company name, non-coded", Locale.ENGLISH, null)
                .description("96e8ab41-b110-42c8-9fce-4968a35a3cea", "The insurance company name (non coded, free text)", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("667fbdc3-9ba9-471c-b9a6-d30e3d4b58f0")
                        .type(sameAs).ensureTerm(pih, "Insurance company name (text)").build())
                .mapping(new ConceptMapBuilder("740b2a1b-0aee-4f7f-ae76-a338545a800c")
                        .type(sameAs).ensureTerm(pih, "10740").build())
                .build());

        install(new ConceptBuilder("729cf278-2a3e-4f0c-87ff-3cbe25bf2bc9")
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("abf48113-0bc7-4ae6-9b9f-7f7e09cbc69c", "Insurance construct", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .mapping(new ConceptMapBuilder("bc1585aa-7341-4704-9734-51b27b230153")
                        .type(sameAs).ensureTerm(pih, "10738").build())
                .mapping(new ConceptMapBuilder("f43ad6e1-a34f-4ec2-9e6f-b778e9c6e383")
                        .type(sameAs).ensureTerm(pih, "Insurance construct").build())
                .setMembers(haitiInsuranceCo, insuranceCoOther, insuranceNumber)
                .build());
    }

}
