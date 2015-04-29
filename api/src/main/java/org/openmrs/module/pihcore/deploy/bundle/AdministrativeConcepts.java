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
 * Misc concepts that refer to administration and bookkeeping-related things
 */
@Component
@Requires({CoreConceptMetadataBundle.class, CommonConcepts.class, AnswerConcepts.class})
public class AdministrativeConcepts extends VersionedPihConceptBundle {

    @Override
    public int getVersion() {
        return 2;
    }

    public static final class Concepts {
        public static final String ID_CARD_PRINTING_REQUESTED = "0dc64225-e9f6-11e4-a8a3-54ee7513a7ff";
    }

    @Override
    protected void installNewVersion() throws Exception {
        Concept freeTextGeneral = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.FREE_TEXT_GENERAL);
        Concept nurse = MetadataUtils.existing(Concept.class, AnswerConcepts.Concepts.NURSE);
        Concept doctor = MetadataUtils.existing(Concept.class, AnswerConcepts.Concepts.DOCTOR);
        Concept communityHealthWorker = MetadataUtils.existing(Concept.class, AnswerConcepts.Concepts.COMMUNITY_HEALTH_WORKER);
        Concept otherNonCoded = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.OTHER_NON_CODED);
        Concept yes = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.YES);
        Concept no = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.NO);

        Concept titleWhoCompletedForm = install(new ConceptBuilder("162577AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(coded)
                .conceptClass(question)
                .name("126197BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Title of who completed form", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("126196BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Form completed by", Locale.ENGLISH, null)
                .mapping(new ConceptMapBuilder("19191dac-4035-4b8f-9f4b-9330e01d3d9c")
                        .type(sameAs).ensureTerm(pih, "9720").build())
                .mapping(new ConceptMapBuilder("56235ed7-80f2-4965-bfb1-b935d45c0f1a")
                        .type(sameAs).ensureTerm(pih, "TITLE WHO COMPLETED FORM").build())
                .mapping(new ConceptMapBuilder("277833ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "162577").build())
                .answers(nurse, doctor, communityHealthWorker, otherNonCoded)
                .build());

        install(new ConceptBuilder("162581AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("126203BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Title of who completed form detail", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .mapping(new ConceptMapBuilder("33edea2b-a1a1-48f5-9055-8582cd72a931")
                        .type(sameAs).ensureTerm(pih, "9722").build())
                .mapping(new ConceptMapBuilder("277841ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "162581").build())
                .setMembers(
                        titleWhoCompletedForm,
                        freeTextGeneral)
                .build());

        // TODO: Concept named PrintingIDCardStatus on zanmi / lacolline / mirebalais exists. It wasn't really preferred to use this for a few reasons:
        // * It does not have a consistent UUID between servers (it is set up at runtime at first use, and UUID is not a constant)
        // * It does not have the appropriate data type, and stores values as value_text true/false rather than as coded values
        // * We should migrate the existing functionality to use this concept
        install(new ConceptBuilder(Concepts.ID_CARD_PRINTING_REQUESTED)
                .datatype(coded)
                .conceptClass(question)
                .name("3fe8bbf9-e9f6-11e4-a8a3-54ee7513a7ff", "ID Card Printing Requested", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .answers(yes, no)
                .mapping(new ConceptMapBuilder("df6b97bc-e9f6-11e4-a8a3-54ee7513a7ff").type(sameAs).ensureTerm(pih, "ID Card Printing Requested").build())
                .build());
    }
}
