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
 * Concepts used in the clinical consultation
 */
@Component
@Requires({ CoreConceptMetadataBundle.class, CommonConcepts.class, AnswerConcepts.class })
public class ClinicalConsultationConcepts extends VersionedPihConceptBundle {

    /* Update this file (and version) with great caution
     *  The most current concepts are on the PIH concepts server and in mds packages
     *  Changing the version will cause concepts to change -- included mappings which can break forms and report
     * */
    @Override
    public int getVersion() {
        return 8;
    }

    // This class contains many concepts not enumerated here
    // (it's not worth the investment to add them all here if they aren't referenced in code)
    public static final class Concepts {

        public static final String PAST_MEDICAL_HISTORY_CONSTRUCT = "1633AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        public static final String PAST_MEDICAL_HISTORY_FINDING = "1628AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        public static final String PAST_MEDICAL_HISTORY_PRESENCE = "1729AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        public static final String PAST_MEDICAL_HISTORY_COMMENT = "160221AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

        public static final String FAMILY_HISTORY_CONSTRUCT = "160593AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        public static final String FAMILY_HISTORY_DIAGNOSIS = "160592AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        public static final String FAMILY_HISTORY_RELATIONSHIP = "3ce18156-26fe-102b-80cb-0017a47871b2";
        public static final String FAMILY_HISTORY_PRESENCE = PAST_MEDICAL_HISTORY_PRESENCE;
        public static final String FAMILY_HISTORY_COMMENT = "160618AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
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

        // in CIEL this is called PAST MEDICAL HISTORY ADDED, but we give it a better name
        // .mapping(new ConceptMapBuilder("").type(narrowerThan).ensureTerm(pih, "10140").build())
        Concept pmhWhich = install(new ConceptBuilder(Concepts.PAST_MEDICAL_HISTORY_FINDING)
                .datatype(coded)
                .conceptClass(diagnosis)
                .name("1908BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Past medical history finding", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("1470FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", "Coded list of past medical history problems but not procedures which are coded under Past Surgical History.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("171868ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "1628").build())
                .build());

        // .mapping(new ConceptMapBuilder("").type(narrowerThan).ensureTerm(pih, "10141").build())
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
                .name("3e2256c6-26fe-102b-80cb-0017a47871b2", "Relationship of relative to patient", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("93bc0d72-07d4-102c-b5fa-0017a47871b2", "CONTACT_REL", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("0502ccd4-014b-4e2b-9734-76d17d46afc6", "Relation du rapport au patient", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
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

    }

}
