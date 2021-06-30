package org.openmrs.module.pihcore.deploy.bundle.core.concept;

import org.openmrs.api.ConceptNameType;
import org.openmrs.module.metadatadeploy.builder.ConceptBuilder;
import org.openmrs.module.metadatadeploy.builder.ConceptMapBuilder;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.VersionedPihConceptBundle;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Includes miscellaneous answers to questions.
 * Note that a few very common answers (e.g. Yes, No, Other-non-coded) are defined in {@link CommonConcepts}
 */
@Component
@Requires({CoreConceptMetadataBundle.class})
public class AnswerConcepts extends VersionedPihConceptBundle {

    /* Update this file (and version) with great caution
    *  The most current concepts are on the PIH concepts server and in mds packages
    *  Changing the version will cause concepts to change -- included mappings which can break forms and report
    * */
    @Override
    public int getVersion() {
        return 7;
    }

    public static final class Concepts {
        public static final String NURSE = "1577AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        public static final String NURSE_CHW = "9a4b471e-8a9f-11e8-9a94-a6cf71072f73";
        public static final String DOCTOR = "1574AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        public static final String COMMUNITY_HEALTH_WORKER = "bf997029-a496-41a2-a7e7-7981e82d2dd0";

        public static final String FATHER = "3ce18444-26fe-102b-80cb-0017a47871b2";
        public static final String MOTHER = "3ce185ca-26fe-102b-80cb-0017a47871b2";
        public static final String BROTHER = "3ce18746-26fe-102b-80cb-0017a47871b2";
        public static final String SISTER = "3ce188f4-26fe-102b-80cb-0017a47871b2";
        public static final String SIBLING = "3ce18a70-26fe-102b-80cb-0017a47871b2";
        public static final String PARTNER_OR_SPOUSE = "3cee77da-26fe-102b-80cb-0017a47871b2";
        public static final String GUARDIAN = "3cde7c90-26fe-102b-80cb-0017a47871b2";
        public static final String OTHER_RELATIVE = "3ce18bec-26fe-102b-80cb-0017a47871b2";

        public static final String HOME = "987be8c8-6a5a-11e2-b6f9-aa00f871a3e1";
        public static final String HOSPITAL = "3ce0d472-26fe-102b-80cb-0017a47871b2";
        public static final String OUTSIDE_OF_INSTITUTION = "a49e7854-aed5-45eb-bd32-612f9938ef3d";
        public static final String FAMILY_MEMBER = "7e32130b-5be8-4766-bf03-f1b909934141";
        public static final String POLICE = "a1e44baf-c82f-4d4f-ac09-232da84c8a28";
        public static final String SURGERY = "a2bbe648-8b69-438a-9657-8148478cf951";
        public static final String INFECTIOUS_DISEASE = "160159AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        public static final String CANCER = "116031AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        public static final String UNNATURAL_DEATH = "166078AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    }

    @Override
    protected void installNewVersion() throws Exception {
        install(new ConceptBuilder(Concepts.NURSE)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("1848BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Nurse", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("1849BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Infirmière", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("d39b5604-13c7-445e-a2a9-1da1967cdd79", "Enfermera", locale_SPANISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("1437FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", "Trained nurse personnel", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("8f123093-5759-45aa-b96f-f7d66402f995")
                        .type(sameAs).ensureTerm(pih, "9718").build())
                .mapping(new ConceptMapBuilder("414cf5b2-4de1-4c00-ad0f-398aa5dcb84b")
                        .type(sameAs).ensureTerm(pih, "NURSE").build())
                .mapping(new ConceptMapBuilder("aad6252d-b190-4a03-8c84-7402a40fc63d")
                        .type(sameAs).ensureTerm(ciel, "1577").build())
                .mapping(new ConceptMapBuilder("136285ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(snomedCt, "106292003").build())
                .mapping(new ConceptMapBuilder("256d1c2a-4556-4e19-8954-995b841e694c")
                        .type(sameAs).ensureTerm(ampath, "6280").build())
                .build());

        install(new ConceptBuilder(Concepts.DOCTOR)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("107418BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Doctor", Locale.ENGLISH, null) // locale-preferred
                .name("1844BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "CLINICAL OFFICER/DOCTOR", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("107417BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Physician", Locale.ENGLISH, null)
                .name("611d795d-6b27-4d26-8c61-af4a78b8388b", "Médecin", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("bb0da6be-6515-48a2-ac06-a7fd4b6afd8d", "Doctor", locale_SPANISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("1435FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", "Physician, doctor or clinical officer", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("4d1beffe-760b-4440-9a1b-54dc27200232")
                        .type(sameAs).ensureTerm(pih, "DOCTOR").build())
                .mapping(new ConceptMapBuilder("022890d2-b180-4680-950f-23f3825f9c91")
                        .type(sameAs).ensureTerm(pih, "9719").build())
                .mapping(new ConceptMapBuilder("f086820a-fe21-47e4-9e3c-719f179e46ea")
                        .type(sameAs).ensureTerm(ciel, "1574").build())
                .mapping(new ConceptMapBuilder("136911ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(snomedCt, "309343006").build())
                .mapping(new ConceptMapBuilder("135312ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ampath, "5082").build())
                .mapping(new ConceptMapBuilder("135655ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ampath, "5506").build())
                .build());

        install(new ConceptBuilder(Concepts.COMMUNITY_HEALTH_WORKER)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("0b785ea4-15f5-102d-96e4-000c29c2a5d7", "Village Health Worker", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("1819BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Community Health Care Worker", Locale.ENGLISH, null)
                .name("126198BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "CCHA (Community and Clinical Health Assistant)", Locale.ENGLISH, null)
                .name("99967BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "VHW (Village Health Worker)", Locale.ENGLISH, null)
                .name("89434BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "CHEW (Community Health Extension Worker)", Locale.ENGLISH, null)
                .name("107416BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "CHO (Community Health Officer)", Locale.ENGLISH, null)
                .name("89567BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "CHW (Community Health Worker)", Locale.ENGLISH, null)
                .name("107415BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "CHN (Community Health Nurse)", Locale.ENGLISH, null)
                .name("7049249e-6ade-4743-a33d-fb3f6d4b33cd", "Accompagnateur", Locale.FRENCH, null) // locale-preferred
                .name("1820BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Officier de Santé du Village", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)
                .name("12745844-dfe3-43fe-88c0-6766cb81fefc", "Acompañante", locale_SPANISH, null) // locale-preferred
                .mapping(new ConceptMapBuilder("137476ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(narrowerThan).ensureTerm(snomedNp, "223366009").build())
                .mapping(new ConceptMapBuilder("3b9816c5-9bd3-441c-a453-5912baf4cfcc")
                        .type(sameAs).ensureTerm(ciel, "1555").build())
                .mapping(new ConceptMapBuilder("b25f6544-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "3645").build())
                .mapping(new ConceptMapBuilder("4c10c0b5-ca8e-4b5f-87a4-3142a269929e")
                        .type(sameAs).ensureTerm(ampath, "1862").build())
                .mapping(new ConceptMapBuilder("75a0ac8c-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "VILLAGE HEALTH WORKER").build())
                .mapping(new ConceptMapBuilder("141538ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(snomedMvp, "15551000105005").build())
                .build());

        install(new ConceptBuilder(Concepts.HOME)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("9880d702-6a5a-11e2-b6f9-aa00f871a3e1", "Home", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("44c1416d-4de3-4cb6-8ff2-af7b5cba366a", "Domicile", Locale.FRENCH, null) // locale-preferred
                .name("f358b82d-631b-49b4-be8e-577d345de149", "Sortie par la maison", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // TODO sketchy translation
                .name("cd429b94-7bb0-4edd-a746-f83f682729ee", "Lakay", locale_HAITI, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("5aca6668-db70-4a11-a186-04691d4bad12", "Casa", locale_SPANISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .mapping(new ConceptMapBuilder("988417fa-6a5a-11e2-b6f9-aa00f871a3e1")
                        .type(sameAs).ensureTerm(pih, "Home").build())
                .mapping(new ConceptMapBuilder("98843e1a-6a5a-11e2-b6f9-aa00f871a3e1")
                        .type(sameAs).ensureTerm(pih, "7889").build())
                .build());

        install(new ConceptBuilder(Concepts.HOSPITAL)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e215a0a-26fe-102b-80cb-0017a47871b2", "Hospital", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("dbd54ca7-747c-485e-b27b-91bdc50cf0bf", "Hôpital", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)
                .name("43c016d8-f384-4133-9d3a-6ed749bd5fcf", "Lopital", locale_HAITI, ConceptNameType.FULLY_SPECIFIED)
                .description("ecef53c0-07fe-102c-b5fa-0017a47871b2", "hospital", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("ffdb5db4-b5a6-4c19-95c6-926749046c56").type(sameAs).ensureTerm(snomedCt, "22232009").build())
                .mapping(new ConceptMapBuilder("136271ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(narrowerThan).ensureTerm(ampath, "6181").build())
                .mapping(new ConceptMapBuilder("75826862-4943-102e-96e9-000c29c2a5d7").type(sameAs).ensureTerm(pih, "HOSPITAL").build())
                .mapping(new ConceptMapBuilder("b21fb656-4864-102e-96e9-000c29c2a5d7").type(sameAs).ensureTerm(pih, "2070").build())
                .mapping(new ConceptMapBuilder("93056dee-85ce-48f8-81d0-52a9dcc440c5").type(sameAs).ensureTerm(ciel, "1589").build())
                .build());

        install(new ConceptBuilder(Concepts.OUTSIDE_OF_INSTITUTION)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("f3bbedb4-91b0-46de-a166-456a6502acda", "Outside of institution", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("d8311920-93a7-4ddf-aa4c-8211451f5d87", "Not in the hospital", Locale.ENGLISH, null)
                .name("da62bffc-ae71-4d3d-817a-05c08f5f119b", "Extra institutionnel", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("7538db0b-2046-4ee1-b8c2-2571e58ead7e", "Afuera de institución", locale_SPANISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("0b7bd483-895b-42c6-8b1b-e282a9329ba0", "Not within the hospital, health center, or institution", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("3e83d428-773c-4285-abf3-d52979752048")
                        .type(sameAs).ensureTerm(pih, "Outside of institution").build())
                .mapping(new ConceptMapBuilder("3001f390-94ca-49ba-88d3-3e4101fd29eb")
                        .type(sameAs).ensureTerm(pih, "9670").build())
                .build());

        install(new ConceptBuilder(Concepts.FAMILY_MEMBER)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("0b95af4a-15f5-102d-96e4-000c29c2a5d7", "Family member", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("0b95b044-15f5-102d-96e4-000c29c2a5d7", "FAMILY REPRESENTATIVE", Locale.ENGLISH, null)
                .name("0b95b134-15f5-102d-96e4-000c29c2a5d7", "PERSON RELATED TO PATIENT", Locale.ENGLISH, null)
                .name("0753fb31-7dcc-4d79-ae5e-129ee80cb071", "Membre de la famille", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("69566475-e2d7-4c93-8756-2da07ca672a4", "Manm nan fanmi an", locale_HAITI, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("229dde7e-60c8-4bad-a80f-4cd77fda4bc9", "A member of the family or someone who can represent the family", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("75af7b7c-4943-102e-96e9-000c29c2a5d7")
                    .type(sameAs).ensureTerm(pih, "FAMILY MEMBER").build())
                .mapping(new ConceptMapBuilder("b2af2854-4864-102e-96e9-000c29c2a5d7")
                    .type(sameAs).ensureTerm(pih, "6441").build())
                .mapping(new ConceptMapBuilder("1e90497e-d591-11e4-9dcf-b36e1005e77b")
                    .type(sameAs).ensureTerm(snomedCt, "303071001").build())
                .build());

        install(new ConceptBuilder(Concepts.POLICE)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("8281322d-5616-4cc4-8439-092a5388105d", "Police", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("993d6ce0-699a-4f16-8218-099b2effe4a9", "Police", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("54e40b3e-d1e8-4bfd-a82a-74db76a7fa53", "Policia", locale_SPANISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("54bc35fb-adaf-4d03-b471-fe9d286b8016", "A police force is a constituted body of persons empowered by the state to enforce the law, protect property, and limit civil disorder.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("e932f7a2-87d1-4caa-beda-5b66725a172b")
                        .type(sameAs).ensureTerm(pih, "Police").build())
                .mapping(new ConceptMapBuilder("9d91a9bd-90e9-411f-a996-0b007d96024f")
                        .type(sameAs).ensureTerm(pih, "9674").build())
                .build());

        install(new ConceptBuilder(Concepts.SURGERY)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("0b928a22-15f5-102d-96e4-000c29c2a5d7", "Surgery", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("0b940cd0-15f5-102d-96e4-000c29c2a5d7", "Chirurgie", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("41796964-a0bf-4482-aaf5-d421b4b43efe", "Cirugía", locale_SPANISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("ebff68ec-d039-4bc1-83da-2d99d1924972", "An act of performing surgery may be called a surgical procedure, operation, or simply surgery. In this context, the verb operate means performing surgery.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b2ae45e2-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "6298").build())
                .mapping(new ConceptMapBuilder("992f5fec-50ab-4db2-a9db-a7935ebe8289")
                        .type(sameAs).ensureTerm(pih, "SURGERY").build())
                .build());

        install(new ConceptBuilder(Concepts.FATHER)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e2259b4-26fe-102b-80cb-0017a47871b2", "Father", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("f672d7ee-d5db-102d-ad2a-000c29c2a5d7", "Père", Locale.FRENCH, null) // locale-preferred
                .name("8b9eabc4-3f52-4604-b7cc-8b922fb6a344", "Padre", locale_SPANISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("ecf0be2c-07fe-102c-b5fa-0017a47871b2", "A father to another person", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b220bc86-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "2174").build())
                .mapping(new ConceptMapBuilder("7587049e-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "FATHER").build())
                .mapping(new ConceptMapBuilder("dfcdf95e-d593-11e4-9dcf-b36e1005e77b")
                        .type(sameAs).ensureTerm(ciel, "971").build())
                .build());

        install(new ConceptBuilder(Concepts.MOTHER)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e225b1c-26fe-102b-80cb-0017a47871b2", "Mother", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("f673058e-d5db-102d-ad2a-000c29c2a5d7", "Mère", Locale.FRENCH, null) // locale-preferred
                .name("4b4ae320-ccb5-4b83-b73d-37ce192ded67", "Madre", locale_SPANISH, ConceptNameType.FULLY_SPECIFIED)
                .description("ecf0bf80-07fe-102c-b5fa-0017a47871b2", "A mother of another person", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("75870606-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "MOTHER").build())
                .mapping(new ConceptMapBuilder("b220be0c-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "2175").build())
                .mapping(new ConceptMapBuilder("e1d4e225-6760-46e7-8579-7390d0ec9bea")
                        .type(sameAs).ensureTerm(ciel, "970").build())
                .build());

        install(new ConceptBuilder(Concepts.OTHER_RELATIVE)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e2265d0-26fe-102b-80cb-0017a47871b2", "Other relative", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("ac1661a2-4b4b-4fc5-b683-6ceac5ab24c2", "Autre parent", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("ecf0ce30-07fe-102c-b5fa-0017a47871b2", "A relative (non-coded) to another person", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b220c3d4-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "2179").build())
                .mapping(new ConceptMapBuilder("75870bba-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "OTHER RELATIVE").build())
                .mapping(new ConceptMapBuilder("4c7d9bcf-0691-4f85-8dc6-9f936dbde00d")
                        .type(sameAs).ensureTerm(ciel, "5620").build())
                .build());

        install(new ConceptBuilder(Concepts.SIBLING)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e225f36-26fe-102b-80cb-0017a47871b2", "Sibling", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("57063624-1e75-4529-9731-f79134c27f01", "Soeur/Frère", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("c6f22eb7-7989-4315-be09-f5efc07cd72c", "Hermano", locale_SPANISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("ecf0cc5a-07fe-102c-b5fa-0017a47871b2", "A sibling to another person", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b220c262-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "2178").build())
                .mapping(new ConceptMapBuilder("75870a48-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "SIBLING").build())
                .mapping(new ConceptMapBuilder("676d75d2-5930-4b35-aca1-34d8bfe22314")
                        .type(sameAs).ensureTerm(ciel, "972").build())
                .build());

        install(new ConceptBuilder(Concepts.PARTNER_OR_SPOUSE)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e405a36-26fe-102b-80cb-0017a47871b2", "Partner or Spouse", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("78abf00f-360f-4fe1-92a8-2e33fa0c9fba", "Partenaire ou époux", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("424429be-d014-4738-8b6b-f74ab9e3baa2", "Esposo o Pareja", locale_SPANISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("ed191d5e-07fe-102c-b5fa-0017a47871b2", "General descriptive answer: a patient's partner or spouse.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("75aa8f18-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "PARTNER OR SPOUSE").build())
                .mapping(new ConceptMapBuilder("c4b3136a-2eb6-4536-865b-4842246aa015")
                        .type(sameAs).ensureTerm(ciel, "5617").build())
                .mapping(new ConceptMapBuilder("b299ae3e-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "5617").build())
                .build());

        // (This concept is both a question and an answer.)
        install(new ConceptBuilder(Concepts.GUARDIAN)
                .datatype(text)
                .conceptClass(question)
                .name("3e1ef350-26fe-102b-80cb-0017a47871b2", "Guardian relationship to child", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("93b6c704-07d4-102c-b5fa-0017a47871b2", "GUARDIAN RELATION", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("50fde515-7d03-4aea-820a-1208acb0fdcf", "Relation de tuteur à l'enfant", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("eceddad6-07fe-102c-b5fa-0017a47871b2", "IF GUARDIAN IS NOT THE CHILD'S PARENTS, WHAT IS HIS/HER RELATION TO THE CHILD?", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("757852a0-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "GUARDIAN RELATIONSHIP TO CHILD").build())
                .mapping(new ConceptMapBuilder("9d730eed-dae6-4800-9c0c-13cc7fe49bf2")
                        .type(sameAs).ensureTerm(ciel, "160639").build())
                .mapping(new ConceptMapBuilder("b21637a2-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1826").build())
                .build());

        install(new ConceptBuilder(Concepts.BROTHER)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e225c7a-26fe-102b-80cb-0017a47871b2", "Brother", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("f8400b03-3c37-4675-a3a4-027d67c7ad63", "Frère", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("ecf0c0e8-07fe-102c-b5fa-0017a47871b2", "A brother to another person", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("457a7d24-c13b-4ad9-af3f-b58da4153dfd")
                        .type(sameAs).ensureTerm(ciel, "160729").build())
                .mapping(new ConceptMapBuilder("b220bf7e-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "2176").build())
                .mapping(new ConceptMapBuilder("7587076e-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "BROTHER").build())
                .build());

        install(new ConceptBuilder(Concepts.SISTER)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e225dd8-26fe-102b-80cb-0017a47871b2", "Sister", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("338ad7f5-1d55-4c45-9c6c-3b7391d0127b", "Soeur", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("ecf0ca5c-07fe-102c-b5fa-0017a47871b2", "Sister to another person", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b220c0f0-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "2177").build())
                .mapping(new ConceptMapBuilder("3ddec89b-4770-4777-9d9b-7c15c10322a3")
                        .type(sameAs).ensureTerm(ciel, "160730").build())
                .mapping(new ConceptMapBuilder("758708e0-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "SISTER").build())
                .build());

        install(new ConceptBuilder(Concepts.NURSE_CHW)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("9a4b4976-8a9f-11e8-9a94-a6cf71072f73", "Nurse Accompagnateur", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("9a4b5470-8a9f-11e8-9a94-a6cf71072f73", "Infirmière Accompagnateur ", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("769d0929-6f01-447a-9253-37025bc6f593", "Acompañante Enfermera", locale_SPANISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("9a4b55d8-8a9f-11e8-9a94-a6cf71072f73", "Nurse accompagnateur", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("9a4b570e-8a9f-11e8-9a94-a6cf71072f73")
                        .type(sameAs).ensureTerm(pih, "Nurse CHW").build())
                .build());

        install(new ConceptBuilder(Concepts.INFECTIOUS_DISEASE)
                .datatype(notApplicable)
                .conceptClass(diagnosis)
                .name("919ebb0f-afc9-4a2b-b788-32077be85ed1", "Infectious or parasitic disease", Locale.ENGLISH, null)
                .name("fc56cfc8-8812-4996-8a4c-84176b0a6721", "Maladies infectueuses ou parasitaires", Locale.FRENCH, null)
                .name("108058BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "human immunodeficiency virus disease HIV resulting in infectious or parasitic disease", Locale.ENGLISH, null)
                .name("107977BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "HIV resulting in infectious or parasitic disease", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("3109103a-9b55-4a23-a72c-b6c7540dc20e", "VIH causant maladies infectueuses ou parasitaires", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("143868ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(narrowerThan).ensureTerm(icd10who, "B20.9").build())
                .mapping(new ConceptMapBuilder("217295ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "160159").build())
                .mapping(new ConceptMapBuilder("144021ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(narrowerThan).ensureTerm(snomedNp, "186708007").build())
                .build());

        install(new ConceptBuilder(Concepts.CANCER)
                .datatype(notApplicable)
                .conceptClass(diagnosis)
                .name("a22420c5-67fe-41ef-8668-aa946d0ac18a", "Neoplasm/cancer", Locale.ENGLISH, null)
                .name("124386BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Tumeur maligne", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)
                .name("89d7174d-a79e-4115-81cc-fd76df94754d", "Tumeur maligne", locale_HAITI, ConceptNameType.FULLY_SPECIFIED)
                .name("d382ffcc-41d0-4225-9f27-1019ff9ad059", "Cancer", Locale.ENGLISH, null)
                .name("16510BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Malignant Neoplasm", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("738eb18a-8d64-42bb-bfe4-8791de437e03", "Cancer", Locale.FRENCH, null)
                .description("4570FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", "New abnormal growth of tissue. Malignant neoplasms show a greater degree of anaplasia and have the properties of invasion and metastasis, compared to benign neoplasms.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("73680ABBBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(snomedCt, "363346000").build())
                .mapping(new ConceptMapBuilder("95030ABBBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(icd10who, "C80").build())
                .mapping(new ConceptMapBuilder("7aeb0705-d11a-42e0-8262-acf71158949c").type(sameAs).ensureTerm(ciel, "116030").build())
                .mapping(new ConceptMapBuilder("182672ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "116031").build())
                .build());

        install(new ConceptBuilder(Concepts.UNNATURAL_DEATH)
                .datatype(notApplicable)
                .conceptClass(diagnosis)
                .name("1e835e7a-f478-4a7a-94ce-e07076cd5f62", "Unnatural death", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("f1eba9c1-8b3f-440e-a106-c67f4bcd918d", "Décès non-naturel", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("3bbf1ad7-bfc1-4735-bf91-37a67ab352af").type(sameAs).ensureTerm(ciel, "166078").build())
                .mapping(new ConceptMapBuilder("63dccc8c-0d75-45e0-9c82-59c457fc785f").type(narrowerThan).ensureTerm(snomedNp, "419620001").build())
                .build());
    }

}
