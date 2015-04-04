package org.openmrs.module.pihcore.deploy.bundle;

import org.openmrs.Concept;
import org.openmrs.api.ConceptNameType;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.builder.ConceptBuilder;
import org.openmrs.module.metadatadeploy.builder.ConceptMapBuilder;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Concepts used in the clinical consultation
 */
@Component
@Requires({ CoreConceptMetadataBundle.class, CommonConcepts.class, AnswerConcepts.class })
public class ClinicalConsultationConcepts extends VersionedPihConceptBundle {

    @Override
    public int getVersion() {
        return 2;
    }

    // This class contains many concepts not enumerated here
    // (it's not worth the investment to add them all here if they aren't referenced in code)
    public static final class Concepts {
        public static final String PRESENTING_HISTORY = "3cd65c90-26fe-102b-80cb-0017a47871b2"; // PIH:PRESENTING HISTORY

        public static final String PAST_MEDICAL_HISTORY_CONSTRUCT = "1633AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        public static final String PAST_MEDICAL_HISTORY_FINDING = "1628AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        public static final String PAST_MEDICAL_HISTORY_PRESENCE = "1729AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        public static final String PAST_MEDICAL_HISTORY_COMMENT = "160221AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

        public static final String FAMILY_HISTORY_CONSTRUCT = "160593AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        public static final String FAMILY_HISTORY_DIAGNOSIS = "160592AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        public static final String FAMILY_HISTORY_RELATIONSHIP = "3ce18156-26fe-102b-80cb-0017a47871b2";
        public static final String FAMILY_HISTORY_PRESENCE = PAST_MEDICAL_HISTORY_PRESENCE;
        public static final String FAMILY_HISTORY_COMMENT = "160618AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

        public static final String SEXUALLY_ACTIVE = "3ce6c27e-26fe-102b-80cb-0017a47871b2";
    }

    @Override
    protected void installNewVersion() throws Exception {
        Concept yes = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.YES);
        Concept no = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.NO);
        Concept unknown = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.UNKNOWN);
        Concept other = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.OTHER_NON_CODED);
        Concept none = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.NONE);
        Concept notApplicableConcept = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.NOT_APPLICABLE);

        Concept father = MetadataUtils.existing(Concept.class, AnswerConcepts.Concepts.FATHER);
        Concept mother = MetadataUtils.existing(Concept.class, AnswerConcepts.Concepts.MOTHER);
        Concept brother = MetadataUtils.existing(Concept.class, AnswerConcepts.Concepts.BROTHER);
        Concept sister = MetadataUtils.existing(Concept.class, AnswerConcepts.Concepts.SISTER);
        Concept sibling = MetadataUtils.existing(Concept.class, AnswerConcepts.Concepts.SIBLING);
        Concept partnerOrSpouse = MetadataUtils.existing(Concept.class, AnswerConcepts.Concepts.PARTNER_OR_SPOUSE);
        Concept guardian = MetadataUtils.existing(Concept.class, AnswerConcepts.Concepts.GUARDIAN);
        Concept otherRelative = MetadataUtils.existing(Concept.class, AnswerConcepts.Concepts.OTHER_RELATIVE);

        install(new ConceptBuilder(Concepts.PRESENTING_HISTORY)
                .datatype(text)
                .conceptClass(question)
                .name("3e141cbe-26fe-102b-80cb-0017a47871b2", "Presenting history", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("3e141f8e-26fe-102b-80cb-0017a47871b2", "Anamnèse", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)
                .name("3e141f8e-26fe-102b-80cb-0017a47871b2", "Histoire de la maladie", Locale.FRENCH, null)
                .description("ece2fe5e-07fe-102c-b5fa-0017a47871b2", "History of the patients presenting complaint", Locale.ENGLISH)
                .description("ece30192-07fe-102c-b5fa-0017a47871b2", "Complaintes actuelles", Locale.FRENCH)
                .mapping(new ConceptMapBuilder("b20be720-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "974").build())
                .mapping(new ConceptMapBuilder("753e6658-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "PRESENTING HISTORY").build())
                .mapping(new ConceptMapBuilder("8373bf92-40f8-404a-bfad-34da8639a426")
                        .type(sameAs).ensureTerm(ciel, "1390").build())
                .build());

        // in CIEL this is called PAST MEDICAL HISTORY ADDED, but we give it a better name
        Concept pmhWhich = install(new ConceptBuilder(Concepts.PAST_MEDICAL_HISTORY_FINDING)
                .datatype(coded)
                .conceptClass(diagnosis)
                .name("1908BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Past medical history finding", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("1470FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", "Coded list of past medical history problems but not procedures which are coded under Past Surgical History.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("171868ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "1628").build())
                .build());

        Concept isSymptomPresent = install(new ConceptBuilder(Concepts.PAST_MEDICAL_HISTORY_PRESENCE)
                .datatype(coded)
                .conceptClass(diagnosis)
                .name("2009BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Sign/symptom present", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no, unknown)
                .mapping(new ConceptMapBuilder("171968ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "1729").build())
                .build());

        // TODO waiting for confirmation from Andy that this is appropriate as comments
        Concept pmhComment = install(new ConceptBuilder(Concepts.PAST_MEDICAL_HISTORY_COMMENT)
                .datatype(text)
                .conceptClass(question)
                .name("108123BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Past medical history added (text)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("217354ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "160221").build())
                .build());

        Concept pmhConstruct = install(new ConceptBuilder(Concepts.PAST_MEDICAL_HISTORY_CONSTRUCT)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("1913BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Past medical history", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("86916BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "PMH", Locale.ENGLISH, ConceptNameType.SHORT)
                .setMembers(pmhWhich, isSymptomPresent, pmhComment)
                .mapping(new ConceptMapBuilder("171873ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "1633").build())
                .build());


        Concept famHxDiagnosis = install(new ConceptBuilder(Concepts.FAMILY_HISTORY_DIAGNOSIS)
                .datatype(coded)
                .conceptClass(misc) // CIEL says Diagnosis, but this is wrong
                .name("109016BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Family history diagnosis", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("217715ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "160592").build())
                .mapping(new ConceptMapBuilder("217715ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(snomedCt, "416471007").build())
                .build());

        // CIEL:1560 (Family Member) is both a question and an answer. At PIH we have two existing concepts for this purpose.
        // This one is only used as a question, so we mark it as NARROWER-THAN CIEL:1560.
        // The answer concept is in the {@link AnswerConcepts} bundle
        // TODO decide whether to move this concept to the Socioeconomic bundle
        Concept famHxRelationship = install(new ConceptBuilder(Concepts.FAMILY_HISTORY_RELATIONSHIP)
                .datatype(coded)
                .conceptClass(question)
                .name("3e2256c6-26fe-102b-80cb-0017a47871b2", "RELATIONSHIP OF RELATIVE TO PATIENT", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("93bc0d72-07d4-102c-b5fa-0017a47871b2", "CONTACT_REL", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("f672a35a-d5db-102d-ad2a-000c29c2a5d7", "RELATION DU PARENT PAR RAPPORT AU PATIENT", Locale.FRENCH, null) // locale-preferred
                .description("ecf0bb66-07fe-102c-b5fa-0017a47871b2", "What is the relationship of this relative to this patient?", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b220b998-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "2172").build())
                .mapping(new ConceptMapBuilder("758701ce-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "RELATIONSHIP OF RELATIVE TO PATIENT").build())
                .mapping(new ConceptMapBuilder("e9335480-d593-11e4-9dcf-b36e1005e77b")
                        .type(narrowerThan).ensureTerm(ciel, "1560").build())
                .answers(father, mother, otherRelative, sibling, brother, sister, partnerOrSpouse, guardian)
                .build());

        Concept famHxComment = install(new ConceptBuilder(Concepts.FAMILY_HISTORY_COMMENT)
                        .datatype(text)
                        .conceptClass(question)
                        .name("109055BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Family history comment", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                        .mapping(new ConceptMapBuilder("217741ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                                .type(sameAs).ensureTerm(ciel, "160618").build())
                        .build()
        );

        Concept famHxConstruct = install(new ConceptBuilder(Concepts.FAMILY_HISTORY_CONSTRUCT)
                .datatype(notApplicable)
                .conceptClass(convSet) // CIEL says Finding but this is wrong
                .name("109017BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Patient's family history list", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("109019BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Family history list", Locale.ENGLISH, null)
                .setMembers(
                        famHxDiagnosis,
                        famHxRelationship,
                        isSymptomPresent,
                        famHxComment)
                .mapping(new ConceptMapBuilder("217716ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "160593").build())
                .mapping(new ConceptMapBuilder("144705ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(snomedCt, "57177007").build())
                .build());

        installSexualActivity(other, notApplicableConcept, none);

        installHospitalization();
    }

    private void installHospitalization() {
        // TODO decide how to code this and import PIH:HOSPITALIZATION CONSTRUCT (decide how to handle cascading dependencies on that construct)
        install(new ConceptBuilder("25a85083-15e1-4439-9bfa-8863588ff3c1")
                .datatype(text)
                .conceptClass(question)
                .name("e79d14c8-2737-4cf8-8f94-492dd906b04d", "Hospitalization comment", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("8761f8b8-1cac-4d89-b791-f21ba7e8a65f", "A text area for commentary about a hospitalization.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b2b26a14-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "6899").build())
                .mapping(new ConceptMapBuilder("75b22cb4-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "Hospitalization comment").build())
                .build());
    }

    private void installSexualActivity(Concept other, Concept notApplicableConcept, Concept none) {
        install(new ConceptBuilder(Concepts.SEXUALLY_ACTIVE)
                .datatype(booleanDatatype)
                .conceptClass(question)
                .name("3e38494a-26fe-102b-80cb-0017a47871b2", "SEXUALLY ACTIVE", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("9566bc8a-07d4-102c-b5fa-0017a47871b2", "SEXUAL ACTIVITY", Locale.ENGLISH, null)
                .name("f6b929d8-d5db-102d-ad2a-000c29c2a5d7", "SEXUELLEMENT ACTIF", Locale.FRENCH, null) // locale-preferred
                .name("3e384abc-26fe-102b-80cb-0017a47871b2", "ACTIF SEXUELLEMENT", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("b2583508-4864-102e-96e9-000c29c2a5d7")
                    .type(sameAs).ensureTerm(pih, "2730").build())
                .mapping(new ConceptMapBuilder("759aaf12-4943-102e-96e9-000c29c2a5d7")
                    .type(sameAs).ensureTerm(pih, "SEXUALLY ACTIVE").build())
                .build());

        Concept iud = install(new ConceptBuilder("3ceb4d4e-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e3d0ef8-26fe-102b-80cb-0017a47871b2", "Intrauterine device", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("94adecd2-07d4-102c-b5fa-0017a47871b2", "IUCD", Locale.ENGLISH, null)
                .name("94ae023a-07d4-102c-b5fa-0017a47871b2", "IUD", Locale.ENGLISH, null)
                .name("1c1163ae-3643-4bd4-8cf2-1d948fd6ffdf", "Dispositif intra-utérin", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("ed14ddde-07fe-102c-b5fa-0017a47871b2", "A device (as a spiral of plastic or a ring of stainless steel) inserted and left in the uterus to prevent effective conception.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("75a805c2-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "INTRAUTERINE DEVICE").build())
                .mapping(new ConceptMapBuilder("b293ae12-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "5275").build())
                .build());

        Concept norplant = install(new ConceptBuilder("3cdcf2e4-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("7dcf6221-8df5-43ca-a800-fa6c2a9c362e", "Levonorgestrel", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("73c4b9d8-2d3e-494e-b4ce-4493212a4be3", "Microlut", Locale.ENGLISH, null)
                .name("d8b4a143-ec63-412e-b9e0-e90842297b85", "Norplant system", Locale.ENGLISH, null)
                .name("1d91ee71-7b2c-4857-b28e-eb45f446d18f", "Norplant", Locale.ENGLISH, null)
                .name("7a09813f-6fbe-4067-9713-082575c6db13", "Plan B", Locale.ENGLISH, null)
                .name("f638c3a6-d5db-102d-ad2a-000c29c2a5d7", "IMPLANT CONTRACEPTIF SOUS-CUTANÉ", Locale.FRENCH, null) // locale-preferred
                .description("ecec44dc-07fe-102c-b5fa-0017a47871b2", "Norplant is 99% – 99.95% effective at preventing pregnancy, and is one of the most reliable, though not the most available, forms of birth control.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("d8f77ed9-da09-4dc7-9fb8-48d7ef3d65a6")
                        .type(sameAs).ensureTerm(ciel, "78796").build())
                .mapping(new ConceptMapBuilder("5b8cbd41-72e3-427b-b4ca-7d0c419bf5f1")
                        .type(sameAs).ensureTerm(rxNorm, "6373").build())
                .mapping(new ConceptMapBuilder("b214ee6a-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1718").build())
                .mapping(new ConceptMapBuilder("3a080495-6d56-4c3a-87db-8e90e44af37f")
                        .type(sameAs).ensureTerm(snomedCt, "109032009").build())
                .mapping(new ConceptMapBuilder("7577a260-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "NORPLANT").build())
                .build());

        Concept tubalLigation = install(new ConceptBuilder("3cdcf44c-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(procedure)
                .name("10f365da-77c7-4b8f-978f-d6fd70ca1a70", "Tubal ligation", Locale.ENGLISH, null) // locale-preferred
                .name("b618d501-3e46-4675-9d4f-9b6bab969778", "BTL", Locale.ENGLISH, null)
                .name("33bdfbec-b51b-4b1a-8cef-26ecab94fcc6", "Bilateral tubal ligation", Locale.ENGLISH, null)
                .name("7e556285-9204-4597-ac88-3e5d189a8599", "Ligation of fallopian tube", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("a0e32650-51c5-4eac-90af-a1ff035112f3", "Ligature des trompes", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("626b553b-bd87-4246-8485-b11a23f3f72c", "Interruption de la perméabilité des trompes utérines", Locale.FRENCH, null)
                .name("2e08cd66-97df-40c7-a931-f5ec63a7954b", "Ligature des trompes", locale_HAITI, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("e739c2b4-4da3-4051-a8af-4f05d8c6cc06", "Interruption de la perméabilité des trompes utérines", locale_HAITI, null)
                .name("0351252e-ac54-4647-be21-2b11c9efd33c", "Ligature", locale_HAITI, null)
                .description("ecec4720-07fe-102c-b5fa-0017a47871b2", "The surgical procedure in fallopian tube to prevent pregnancy, a method of female sterilization.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("f13a3fbf-039c-469f-9e2d-1636ac790c4b")
                        .type(sameAs).ensureTerm(ciel, "1472").build())
                .mapping(new ConceptMapBuilder("7577a3c8-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "TUBAL LIGATION").build())
                .mapping(new ConceptMapBuilder("5a6d1d4b-c337-4b7a-8a4d-a6d13d47c3ca")
                        .type(sameAs).ensureTerm(snomedCt, "77543007").build())
                .mapping(new ConceptMapBuilder("10b160ee-2af7-47cb-86da-e69a94bbac69")
                        .type(sameAs).ensureTerm(imoProblemIT, "674775").build())
                .mapping(new ConceptMapBuilder("b214efe6-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1719").build())
                .build());

        Concept vasectomy = install(new ConceptBuilder("3cdcf74e-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(procedure)
                .name("3e1ded66-26fe-102b-80cb-0017a47871b2", "Vasectomy", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("590b51b8-bced-4228-9ae3-5d7e4c8f289c", "Vasectomie", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("bed6486a-14cf-40da-b87d-da56368e55af", "Vasectomie", locale_HAITI, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("ecec49fa-07fe-102c-b5fa-0017a47871b2", "A method of male sterilization.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("7577a68e-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "VASECTOMY").build())
                .mapping(new ConceptMapBuilder("1e2d18d2-f79f-4ed0-9bb4-13510b3f77f5")
                        .type(sameAs).ensureTerm(snomedCt, "22523008").build())
                .mapping(new ConceptMapBuilder("b214f2ca-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1721").build())
                .build());

        Concept depoProvera = install(new ConceptBuilder("3cd5094e-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("93f47c52-07d4-102c-b5fa-0017a47871b2", "DEPO-PROVERA", Locale.ENGLISH, null)
                .name("3e12ebd2-26fe-102b-80cb-0017a47871b2", "MEDROXYPROGESTERONE ACETATE", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("93f48134-07d4-102c-b5fa-0017a47871b2", "DEPOPROVERA", Locale.ENGLISH, null)
                .name("f5b210fe-d5db-102d-ad2a-000c29c2a5d7", "ACÉTATE DE MÉDROXYPROGESTÉRONE", Locale.FRENCH, null) // locale-preferred
                .description("ece19d16-07fe-102c-b5fa-0017a47871b2", "A synthetic steroid progestational hormone C24H34O4 that is used especially in the treatment of amenorrhea and abnormal uterine bleeding, in conjunction with conjugated estrogens to relieve the symptoms of menopause and to prevent osteoporosis, and as an injectable contraceptive.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("753e08ac-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "MEDROXYPROGESTERONE ACETATE").build())
                .mapping(new ConceptMapBuilder("b20b077e-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "907").build())
                .build());

        Concept injectableContraceptives = install(new ConceptBuilder("3ceb5532-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(medSet)
                .name("3e3d14d4-26fe-102b-80cb-0017a47871b2", "INJECTABLE CONTRACEPTIVES", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("94acfcbe-07d4-102c-b5fa-0017a47871b2", "INJECTABLE HORMONES", Locale.ENGLISH, null)
                .name("f75c6936-d5db-102d-ad2a-000c29c2a5d7", "CONTRACEPTION INJECTABLE", Locale.FRENCH, null) // locale-preferred
                .description("ed14e5fe-07fe-102c-b5fa-0017a47871b2", "Injectable medicines which enable deliberate prevention of conception or impregnation.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b293b3ee-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "5279").build())
                .mapping(new ConceptMapBuilder("75a80b4e-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "INJECTABLE CONTRACEPTIVES").build())
                .setMembers(depoProvera)
                .build());

        Concept diaphragm = install(new ConceptBuilder("3ceb52ee-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e3d1362-26fe-102b-80cb-0017a47871b2", "DIAPHRAGM", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("93f0eede-07d4-102c-b5fa-0017a47871b2", "CERVICAL CAP", Locale.ENGLISH, null)
                .name("f75c360a-d5db-102d-ad2a-000c29c2a5d7", "DIAPHRAGME", Locale.FRENCH, null) // locale-preferred
                .description("ed14e3f6-07fe-102c-b5fa-0017a47871b2", "A molded cap, usually of thin rubber fitted over the uterine cervix to act as a mechanical contraceptive barrier.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b293b27c-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "5278").build())
                .mapping(new ConceptMapBuilder("75a809f0-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "DIAPHRAGM").build())
                .build());

        Concept femaleSterilization = install(new ConceptBuilder("3ceb4ee8-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(procedure)
                .name("3e3d106a-26fe-102b-80cb-0017a47871b2", "Female sterilization", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("eb6352dc-c830-4445-aeea-fcc3889c2110", "Stérilisation féminine", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("13274c1b-296c-4e77-b414-1affc41bdc0e", "Stérilisation féminine", locale_HAITI, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("ed14dfe6-07fe-102c-b5fa-0017a47871b2", "Permanent methods of family planning in which surgical procedure is done in female genital system to prevent pregnancy. Examples include tubal ligation, laparoscopic sterilization, salpingectomy and others", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("e6187cdf-c3fd-4049-bfce-a69ec38e648c")
                        .type(sameAs).ensureTerm(snomedCt, "60890002").build())
                .mapping(new ConceptMapBuilder("b293af8e-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "5276").build())
                .mapping(new ConceptMapBuilder("75a8072a-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "FEMALE STERILIZATION").build())
                .build());

        Concept condoms = install(new ConceptBuilder("3cce7a20-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("3e0ba5ac-26fe-102b-80cb-0017a47871b2", "CONDOMS", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("3e0ba7dc-26fe-102b-80cb-0017a47871b2", "PRESERVATIFS", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("ecd8bee4-07fe-102c-b5fa-0017a47871b2", "Methods for preventing pregnancy.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b1d9c59c-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "190").build())
                .mapping(new ConceptMapBuilder("50e1752c-3105-49a5-b0da-4d7926d45ecd")
                        .type(sameAs).ensureTerm(pih, "CONDOMS").build())
                .build());

        Concept oralContraception = install(new ConceptBuilder("3cd42786-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(medSet)
                .name("3e11e0a2-26fe-102b-80cb-0017a47871b2", "ORAL CONTRACEPTION", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("93ee52e6-07d4-102c-b5fa-0017a47871b2", "BIRTH CONTROL PILLS", Locale.ENGLISH, null)
                .name("f5a61cc2-d5db-102d-ad2a-000c29c2a5d7", "CONTRACEPTION ORALE", Locale.FRENCH, null) // locale-preferred
                .description("ece05fa0-07fe-102c-b5fa-0017a47871b2", "Female contraception method.  Ultimately will make a good MedSet.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b2094a24-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "780").build())
                .mapping(new ConceptMapBuilder("7512366e-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "ORAL CONTRACEPTION").build())
                .build());

        Concept naturalFamilyPlanning = install(new ConceptBuilder("3ceb5082-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e3d11dc-26fe-102b-80cb-0017a47871b2", "NATURAL FAMILY PLANNING", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("956506b0-07d4-102c-b5fa-0017a47871b2", "RHYTHM METHOD", Locale.ENGLISH, null)
                .name("f75c072a-d5db-102d-ad2a-000c29c2a5d7", "PLANIFICATION FAMILIALE NATURELLE", Locale.FRENCH, null) // locale-preferred
                .description("ed14e1e4-07fe-102c-b5fa-0017a47871b2", "A method of birth control that involves abstention from sexual intercourse during the period of ovulation which is determined through observation and measurement of bodily signs (as cervical mucus and body temperature).", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("75a80888-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "NATURAL FAMILY PLANNING").build())
                .mapping(new ConceptMapBuilder("b293b100-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "5277").build())
                .build());

        Concept abstinence = install(new ConceptBuilder("3cdcf5e6-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e1deafa-26fe-102b-80cb-0017a47871b2", "ABSTINENCE", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("f63907c6-d5db-102d-ad2a-000c29c2a5d7", "ABSTINENCE", Locale.FRENCH, null) // locale-preferred
                .description("ecec487e-07fe-102c-b5fa-0017a47871b2", "Method of preventing pregnancy (fill in)", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b214f158-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1720").build())
                .mapping(new ConceptMapBuilder("7577a526-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "ABSTINENCE").build())
                .build());

        Concept sterilization = install(new ConceptBuilder("3ce69f06-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(procedure)
                .name("3e381628-26fe-102b-80cb-0017a47871b2", "Sterilization", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("a385063c-968d-4d0d-98b4-6057ed3cd289", "Stérilisation", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("40cef535-d33c-44b2-98bc-6c9f9dd3fb2e", "Stérilizasyon", locale_HAITI, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("ecf52c8c-07fe-102c-b5fa-0017a47871b2", "Surgery to make person infertile.  This is used for procedure, but also used for diagnosis.  Not sure if this should be 2 concepts.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b2581abe-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "2712").build())
                .mapping(new ConceptMapBuilder("759a9388-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "STERILIZATION").build())
                .build());

        Concept hysterectomy = install(new ConceptBuilder("159837AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(procedure)
                .name("107314BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Hysterectomy", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("6a1b1a46-8008-4338-b8f5-89c879483ae7", "Hystérectomie", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("098475be-0055-4ca3-a255-78710f5ac755", "Hystérectomie", locale_HAITI, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("16408FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", "A surgical procedure in which uterus is removed.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("216979ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "159837").build())
                .mapping(new ConceptMapBuilder("136767ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(snomedCt, "236886002").build())
                .mapping(new ConceptMapBuilder("136768ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(narrowerThan).ensureTerm(ampath, "5276").build())
                .mapping(new ConceptMapBuilder("274526ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(imoProcedureIT, "667244").build())
                .build());

        install(new ConceptBuilder("3ccfbd0e-26fe-102b-80cb-0017a47871b2")
                .datatype(coded)
                .conceptClass(question)
                .name("3e0d6c2a-26fe-102b-80cb-0017a47871b2", "METHOD OF FAMILY PLANNING", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("94b6ceba-07d4-102c-b5fa-0017a47871b2", "METHOD OF BIRTH CONTROL", Locale.ENGLISH, null)
                .name("f58ebafa-d5db-102d-ad2a-000c29c2a5d7", "MÉTHODE DE PLANIFICATION FAMILIALE", Locale.FRENCH, null) // locale-preferred
                .mapping(new ConceptMapBuilder("b1e2124c-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "374").build())
                .mapping(new ConceptMapBuilder("748ee136-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "METHOD OF FAMILY PLANNING").build())
                .answers(
                        none,
                        notApplicableConcept,
                        abstinence,
                        condoms,
                        oralContraception,
                        injectableContraceptives,
                        diaphragm,
                        iud,
                        femaleSterilization,
                        tubalLigation,
                        vasectomy,
                        hysterectomy,
                        norplant,
                        depoProvera,
                        sterilization,
                        naturalFamilyPlanning,
                        other)
                .build());

        install(new ConceptBuilder("9dc4818d-049b-4a39-bf3c-69b12ea18f34")
                .datatype(text)
                .conceptClass(question)
                .name("0b6aea8a-15f5-102d-96e4-000c29c2a5d7", "OTHER FAMILY PLANNING METHOD, NON-CODED", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("f6d4bbf8-d5db-102d-ad2a-000c29c2a5d7", "AUTRE MÉTHODE DE PLANNIFICATION FAMILIALE, NON-CODÉ", Locale.FRENCH, null) // locale-preferred
                .description("ed1fe0bc-07fe-102c-b5fa-0017a47871b2", "The name of a method of family planning besides the coded answers.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("759c3a9e-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "OTHER FAMILY PLANNING METHOD, NON-CODED").build())
                .mapping(new ConceptMapBuilder("b25a31a0-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "2996").build())
                .build());
    }

}
