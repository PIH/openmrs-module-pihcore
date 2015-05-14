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
 * Socio-economic concepts, including questions and answers
 */
@Component
@Requires({CoreConceptMetadataBundle.class, CommonConcepts.class})
public class SocioEconomicConcepts extends VersionedPihConceptBundle {

    @Override
    public int getVersion() { return 9;  }

    public static final class Concepts {
        public static final String CIVIL_STATUS = "3cd6df26-26fe-102b-80cb-0017a47871b2";
        public static final String MAIN_ACTIVITY = "3cd97286-26fe-102b-80cb-0017a47871b2";
        public static final String MAIN_ACTIVITY_NON_CODED = "3ce41a38-26fe-102b-80cb-0017a47871b2";
        public static final String URBAN = "c50d9651-f76d-4b95-a6cd-6525608852e3";
        public static final String RURAL = "1b27978f-e175-464f-82a5-ac8fc4e7155c";
        public static final String PATIENT_CONTACTS_CONSTRUCT = "3cd9936a-26fe-102b-80cb-0017a47871b2";
        public static final String NAMES_AND_FIRSTNAMES_OF_CONTACT = "3cd997f2-26fe-102b-80cb-0017a47871b2";
        public static final String RELATIONSHIPS_OF_CONTACT = "3cd99a68-26fe-102b-80cb-0017a47871b2";
        public static final String TELEPHONE_NUMBER_OF_CONTACT = "276f8057-55a4-4b1c-8915-69ad090fcffb";
        public static final String ADDRESS_OF_CONTACT = "5190CC3E-83F0-4410-8660-B109086D9A5E";
        public static final String MARRIED = "3cee0aca-26fe-102b-80cb-0017a47871b2";
    }

    @Override
    protected void installNewVersion() throws Exception {
        Concept unknown = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.UNKNOWN);
        Concept otherNonCoded = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.OTHER_NON_CODED);

        Concept mainActivityNonCoded = install(new ConceptBuilder(Concepts.MAIN_ACTIVITY_NON_CODED)
                .datatype(text)
                .conceptClass(question)
                .name("3e254494-26fe-102b-80cb-0017a47871b2", "Main activity, non-coded", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("0b9565bc-15f5-102d-96e4-000c29c2a5d7", "Occupation, non-coded", Locale.ENGLISH, null)
                .name("0b9564cc-15f5-102d-96e4-000c29c2a5d7", "Principal activity, non-coded", Locale.ENGLISH, null)
                .name("f696981e-d5db-102d-ad2a-000c29c2a5d7", "Activité principale, non codée", Locale.FRENCH, null) // locale-preferred
                .description("ecf2882e-07fe-102c-b5fa-0017a47871b2", "response if patient main activity is not one of the coded choices", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b2560486-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "2452").build())
                .mapping(new ConceptMapBuilder("75956b38-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "MAIN ACTIVITY, NON-CODED").build())
                .build());

        Concept urban = install(new ConceptBuilder(Concepts.URBAN)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("bf2be020-e02d-45f2-a9f0-3e3573e3a211", "Urban", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("8489dde6-d659-4d3f-ad6c-922e451e534f", "Urbaine", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("b08a5d68-348f-43c7-95e5-bda39a06a2c9", "An urban area is characterized by higher population density and vast human features in comparison to the areas surrounding it.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("2e56e31f-877e-45d8-9602-09237d0ba152")
                        .type(sameAs).ensureTerm(pih, "Urban").build())
                .mapping(new ConceptMapBuilder("0cbe6a6c-bb23-420e-9a40-c76747337042")
                        .type(sameAs).ensureTerm(pih, "9666").build())
                .build());

        Concept rural = install(new ConceptBuilder(Concepts.RURAL)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("ed83c7bf-b7bb-45d1-812c-d8ee37a080e3", "Rural", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("efc13014-58e3-4769-a3f5-f3488e30433b", "Rural", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("fbf39aa9-648b-455c-99d6-2c07cfbe15aa", "In general, a rural area is a geographic area that is located outside cities and towns.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("cd3fa7d7-60fd-41a6-98f9-0e110c77af97")
                        .type(sameAs).ensureTerm(pih, "Rural").build())
                .mapping(new ConceptMapBuilder("f683a2ce-8952-4e97-a5cf-de428e0917b1")
                        .type(sameAs).ensureTerm(pih, "9665").build())
                .build());

        install(new ConceptBuilder("6aeb49da-bfdf-4b49-8b68-d1e675f0dc2e")
                .datatype(coded)
                .conceptClass(question)
                .name("6d70ba3d-8464-46d9-89a1-dacff7ceb9bb", "Residential environment", Locale.ENGLISH, null) // locale-preferred
                .name("1f3d6109-b6c1-47ab-b207-27af20729596", "Classification of residential environment", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("d14a56cb-03f7-4ca4-8d6e-1d855b6c2ad5", "Is the residence urban or rural?", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("1f33049f-4dde-4ca9-8eab-5adf519102c8")
                        .type(sameAs).ensureTerm(pih, "9667").build())
                .mapping(new ConceptMapBuilder("6f94b002-47a3-4f4b-948e-ed2537f6c5e0")
                        .type(sameAs).ensureTerm(pih, "Classification of residential environment").build())
                .answers(rural, urban)
                .build());

        Concept celibate = install(new ConceptBuilder("60eca8ce-777b-4556-8ea3-f7fa5a439ab2")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("0b78117e-15f5-102d-96e4-000c29c2a5d7", "Celibate", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("f71e3f26-d5db-102d-ad2a-000c29c2a5d7", "Célibataire", Locale.FRENCH, null) // locale-preferred
                .description("ed282fec-07fe-102c-b5fa-0017a47871b2", "Answer which describes a type of civil status.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("75a090d0-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "CELIBATE").build())
                .mapping(new ConceptMapBuilder("b25f4aaa-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "3628").build())
                .build());

        Concept divorced = install(new ConceptBuilder("3cd6e55c-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e150a5c-26fe-102b-80cb-0017a47871b2", "Divorced", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("d1c68e19-cfac-4b72-98c0-17de59bf5b23", "Divorcé(e)", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("c87991b3-02fe-45ef-9325-477787cba57f", "Divòse", locale_HAITI, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("ece42a0e-07fe-102c-b5fa-0017a47871b2", "Answer which describes a type of civil status.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b20d0524-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1058").build())
                .mapping(new ConceptMapBuilder("75687e8e-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "DIVORCED").build())
                .build());

        Concept singleOrChild = install(new ConceptBuilder("df488243-d1d5-4b50-ae04-40b4ffdcf934")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("0b75ec14-15f5-102d-96e4-000c29c2a5d7", "Single or a child", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("0b7feb10-15f5-102d-96e4-000c29c2a5d7", "Single/Child", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("388cae77-22a6-48bc-bfbb-17908c8d61c0", "Célibataire ou un enfant", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("536981db-7436-4edc-9c9d-0eceb1461fda", "Selibatè oubyen timoun", locale_HAITI, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("ed244d64-07fe-102c-b5fa-0017a47871b2", "Civil status of patient.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("759eda88-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "SINGLE OR A CHILD").build())
                .mapping(new ConceptMapBuilder("b25d1d48-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "3347").build())
                .build());

        Concept neverMarried = install(new ConceptBuilder("3cd6e3d6-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e1508f4-26fe-102b-80cb-0017a47871b2", "Never married", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("f5c8e202-d5db-102d-ad2a-000c29c2a5d7", "Jamais marié", Locale.FRENCH, null) // locale-preferred
                .description("ece428b0-07fe-102c-b5fa-0017a47871b2", "Answer which describes a type of civil status.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("75687cb8-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "NEVER MARRIED").build())
                .mapping(new ConceptMapBuilder("b20d03a8-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1057").build())
                .build());

        Concept widowed = install(new ConceptBuilder("3cd6e6f6-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e150bce-26fe-102b-80cb-0017a47871b2", "Widowed", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("95692394-07d4-102c-b5fa-0017a47871b2", "Spouse died", Locale.ENGLISH, null)
                .name("faae1a8e-07c1-4c37-8527-75dc7901d2ac", "Veuf(ve)", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("bf337879-0fd6-4bbb-bb4d-8b4cf68a8e52", "Veuve", Locale.FRENCH, null)
                .name("ab6ed0a7-f146-476f-b6f7-866b44402dce", "Veuf", Locale.FRENCH, null)
                .name("1dd94bf3-0962-4ca6-8a66-0fd1dccd0887", "Vèf", locale_HAITI, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("ece42bf8-07fe-102c-b5fa-0017a47871b2", "Answer which describes a type of civil status.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("7568800a-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "WIDOWED").build())
                .mapping(new ConceptMapBuilder("b20d06aa-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1059").build())
                .build());

        Concept separated = install(new ConceptBuilder("3cd6e246-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e150782-26fe-102b-80cb-0017a47871b2", "Separated", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("5c572211-edfc-457c-b922-8ee8a5293bf8", "Séparé(e)", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("4e1dd8d7-22ca-4fbc-9163-c1f6cbb328e6", "Separe", locale_HAITI, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("ece42752-07fe-102c-b5fa-0017a47871b2", "Answer which describes a type of civil status.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b20d0236-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1056").build())
                .mapping(new ConceptMapBuilder("7568786c-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "SEPARATED").build())
                .build());

        Concept partnerInPrison = install(new ConceptBuilder("0e3d6c1e-3dee-4510-9132-36c8f136e41e")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("0b75eb1a-15f5-102d-96e4-000c29c2a5d7", "Partner in prison", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("f6fe72fe-d5db-102d-ad2a-000c29c2a5d7", "Partenaire en prison", Locale.FRENCH, null) // locale-preferred
                .description("ed244c06-07fe-102c-b5fa-0017a47871b2", "Patient's partner is in prison.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("759ed92a-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "PARTNER IN PRISON").build())
                .mapping(new ConceptMapBuilder("b25d1bb8-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "3346").build())
                .build());

        Concept married = install(new ConceptBuilder(Concepts.MARRIED)
                .datatype(notApplicable)
                .conceptClass(finding)
                .name("3e3ffcb2-26fe-102b-80cb-0017a47871b2", "Married", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("90c02968-5164-4e97-ab01-db80aa9e0d56", "Marié(e)", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("cca6224b-3f11-4f7a-b9c8-ac1b784e6c77", "Marye", locale_HAITI, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("ed18a7d4-07fe-102c-b5fa-0017a47871b2", "Wedded to another person.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("75a9a0ee-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "MARRIED").build())
                .mapping(new ConceptMapBuilder("b295eca4-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "5555").build())
                .build());

        Concept livingWithPartner = install(new ConceptBuilder("3cd6e96c-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e150eb2-26fe-102b-80cb-0017a47871b2", "Living with partner", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("b3a05960-796f-4565-bd44-7dc6331d2d51", "Placé(e)", Locale.FRENCH, null) // locale-preferred
                .name("c7510c67-cc7a-4cc5-85fc-12c98cb44b00", "Cohabitant avec le partenaire", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)
                .name("14a425c9-30dc-4cfb-a23f-23b2e8fdcdda", "Concubine", Locale.FRENCH, null)
                .name("aeb288a7-21af-4800-b972-196adcdd3f14", "Plase", locale_HAITI, null) // locale-preferred
                .name("80341f66-14da-4a9a-a03f-f8a0ef3e52c4", "Ap viv ak patnè", locale_HAITI, ConceptNameType.FULLY_SPECIFIED)
                .name("6843ccc2-a24e-4671-aa07-4f89dfd49345", "Konkibinaj", locale_HAITI, null)
                .description("ece42e00-07fe-102c-b5fa-0017a47871b2", "Answer which describes a type of civil status.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b20d081c-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1060").build())
                .mapping(new ConceptMapBuilder("75688186-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "LIVING WITH PARTNER").build())
                .build());

        install(new ConceptBuilder(Concepts.CIVIL_STATUS)
                .datatype(coded)
                .conceptClass(question)
                .name("3e15049e-26fe-102b-80cb-0017a47871b2", "Civil status", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("0b8ec5fe-15f5-102d-96e4-000c29c2a5d7", "CIV_STAT", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("f5c82c18-d5db-102d-ad2a-000c29c2a5d7", "État civil", Locale.FRENCH, null) // locale-preferred
                .description("ece42496-07fe-102c-b5fa-0017a47871b2", "More detailed description of the following encounter form question:  \"Are you currently married or living with a partner?\"", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b20cff3e-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1054").build())
                .mapping(new ConceptMapBuilder("6b1d3ea7-267d-4077-8b01-522489ca5a22")
                        .type(sameAs).ensureTerm(pih, "CIVIL STATUS").build())
                .mapping(new ConceptMapBuilder("db9aa56a-d4be-11e4-9dcf-b36e1005e77b")
                        .type(sameAs).ensureTerm(ciel, "1054").build())
                .answers(separated, neverMarried, divorced, widowed, livingWithPartner, married, partnerInPrison, singleOrChild, celibate, unknown, otherNonCoded)
                .build());

        uninstall(MetadataUtils.possible(Concept.class, "94807725-b1cf-437e-b439-c5885440121e"), "No longer used");
        uninstall(MetadataUtils.possible(Concept.class, "3cd9786c-26fe-102b-80cb-0017a47871b2"), "No longer used");

        Concept housework = install(new ConceptBuilder("3cda14e8-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e19c6d2-26fe-102b-80cb-0017a47871b2", "Housework", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("0b8eb1cc-15f5-102d-96e4-000c29c2a5d7", "Housewife", Locale.ENGLISH, null)
                .name("0b8eb0dc-15f5-102d-96e4-000c29c2a5d7", "Domestic", Locale.ENGLISH, null)
                .name("0b8eb2c6-15f5-102d-96e4-000c29c2a5d7", "Ménage", Locale.FRENCH, null) // locale-preferred
                .description("ece86506-07fe-102c-b5fa-0017a47871b2", "A main activity", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b211809a-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1404").build())
                .build());

        Concept schoolAgeButNotInSchool = install(new ConceptBuilder("1ed2c51d-7d4d-44df-9257-48eaf61a2f7a")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("0b75e840-15f5-102d-96e4-000c29c2a5d7", "School age but not in school", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("f6fe25b0-d5db-102d-ad2a-000c29c2a5d7", "Enfants d'âge scolaire, mais pas à l'école", Locale.FRENCH, null) // locale-preferred
                .description("ed2447f6-07fe-102c-b5fa-0017a47871b2", "Educational status of a patient.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b25d171c-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "3343").build())
                .mapping(new ConceptMapBuilder("759ed4fc-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "SCHOOL AGE BUT NOT IN SCHOOL").build())
                .build());

        Concept commerce = install(new ConceptBuilder("3cd9740c-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e17c01c-26fe-102b-80cb-0017a47871b2", "Commerce", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("0b8e937c-15f5-102d-96e4-000c29c2a5d7", "Commerçant", Locale.FRENCH, null) // locale-preferred
                .description("ece769da-07fe-102c-b5fa-0017a47871b2", "Main activity", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b21086fe-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1305").build())
                .mapping(new ConceptMapBuilder("1f575f52-3942-4430-87a6-35841a024aed")
                        .type(sameAs).ensureTerm(pih, "COMMERCE").build())
                .build());

        Concept houseworkAndFieldwork = install(new ConceptBuilder("3ce40fca-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e252a68-26fe-102b-80cb-0017a47871b2", "Housework/Fieldwork", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("f695e1ee-d5db-102d-ad2a-000c29c2a5d7", "Ménage/Travail de terrain", Locale.FRENCH, null) // locale-preferred
                .description("ecf278fc-07fe-102c-b5fa-0017a47871b2", "It's very common (especially in Lesotho) for patients to respond to the main activity question with both housework and fieldwork", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("75955cd8-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "HOUSEWORK/FIELDWORK").build())
                .mapping(new ConceptMapBuilder("b255fa68-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "2445").build())
                .build());

        Concept tooSickToWork = install(new ConceptBuilder("a2c0bdcf-f4ba-4e10-9af4-d9ad7dcc2ca4")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("0b75ea2a-15f5-102d-96e4-000c29c2a5d7", "Too sick to work", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("f6fe57ce-d5db-102d-ad2a-000c29c2a5d7", "Trop malade pour travailler", Locale.FRENCH, null) // locale-preferred
                .description("ed244aa8-07fe-102c-b5fa-0017a47871b2", "Patient is too sick to work or maintain job.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("759ed7cc-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "TOO SICK TO WORK").build())
                .mapping(new ConceptMapBuilder("b25d1a32-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "3345").build())
                .build());

        Concept factoryWorker = install(new ConceptBuilder("3cdcbe78-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e1d53f6-26fe-102b-80cb-0017a47871b2", "Factory worker", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("f6326b1e-d5db-102d-ad2a-000c29c2a5d7", "Ouvrier", Locale.FRENCH, null) // locale-preferred
                .description("ecec0c9c-07fe-102c-b5fa-0017a47871b2", "Factory profession or activity", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b214b972-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1683").build())
                .mapping(new ConceptMapBuilder("75776d2c-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "FACTORY WORKER").build())
                .build());

        Concept smallChild = install(new ConceptBuilder("96fa6038-30b0-4305-a4ce-0a392a0081c6")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("6c1b8567-e4e7-4e89-92e0-4558a1a8d869", "Small child (less than 5 years old)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("09e8a2db-2d82-4117-9d0e-e9cda15ca9a6", "Child younger than school age", Locale.ENGLISH, null)
                .name("f7823332-d5db-102d-ad2a-000c29c2a5d7", "Petit enfant (moins de 5 ans)", Locale.FRENCH, null) // locale-preferred
                .description("66757468-e47a-4006-af51-ab55bf187b80", "Small child (under 5)", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("75ad18dc-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "SMALL CHILD (LESS THAN AGE 5)").build())
                .mapping(new ConceptMapBuilder("b29d5a8e-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "6138").build())
                .build());

        Concept professional = install(new ConceptBuilder("907716b4-1852-404a-9ee1-beff998f9461")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("0b76dd22-15f5-102d-96e4-000c29c2a5d7", "Professionel", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("f7039220-d5db-102d-ad2a-000c29c2a5d7", "Professionnel", Locale.FRENCH, null) // locale-preferred
                .description("ed24d0b8-07fe-102c-b5fa-0017a47871b2", "Employment type. Examples are nurse, doctor, teacher, lawyer, or any position outside of agriculture. ", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("759f2222-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "PROFESSIONAL").build())
                .mapping(new ConceptMapBuilder("b25d6690-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "3396").build())
                .build());

        Concept brewer = install(new ConceptBuilder("3cdcc2ec-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e1d5838-26fe-102b-80cb-0017a47871b2", "Brewer", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("f632b696-d5db-102d-ad2a-000c29c2a5d7", "Brasseur", Locale.FRENCH, null) // locale-preferred
                .description("ecec12a0-07fe-102c-b5fa-0017a47871b2", "Brewery profession or activity", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("75777182-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "BREWER").build())
                .mapping(new ConceptMapBuilder("b214be18-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1686").build())
                .build());

        Concept teacher = install(new ConceptBuilder("d192f132-999a-4666-8725-028f9fcb8df4")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("15cddbff-47a3-4bde-8c49-fafa448af031", "Teacher", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("9a35a067-c03a-45da-93b5-5979e8e363bf", "Professor", Locale.ENGLISH, null)
                .name("d774632c-d77a-4181-aa08-c171f0e2afd1", "Professeur", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("84998026-5282-4b20-9f7c-a320448740ca", "Professeur d'école", Locale.FRENCH, null)
                .description("65f4f023-905a-491f-ab52-c136274da38c", "Main activity", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("e1f4236c-ef7c-4be3-be0e-42a6813ce4fe")
                        .type(sameAs).ensureTerm(pih, "7098").build())
                .mapping(new ConceptMapBuilder("ad56aac5-34a9-484f-927b-a04737a9aadd")
                        .type(sameAs).ensureTerm(pih, "Teacher").build())
                .build());

        Concept retired = install(new ConceptBuilder("3ce4113c-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e252bd0-26fe-102b-80cb-0017a47871b2", "Retired", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("f695fd3c-d5db-102d-ad2a-000c29c2a5d7", "Retraité", Locale.FRENCH, null) // locale-preferred
                .description("ecf27a5a-07fe-102c-b5fa-0017a47871b2", "A main activity for someone who has retired from work", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("75955e54-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "RETIRED").build())
                .mapping(new ConceptMapBuilder("b255fbda-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "2446").build())
                .build());

        Concept farmer = install(new ConceptBuilder("3cd9757e-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e17c26a-26fe-102b-80cb-0017a47871b2", "Farmer", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("f5ff6d22-d5db-102d-ad2a-000c29c2a5d7", "Fermier", Locale.FRENCH, null) // locale-preferred
                .name("3e17c4cc-26fe-102b-80cb-0017a47871b2", "Cultivateur", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)
                .description("ece76be2-07fe-102c-b5fa-0017a47871b2", "Main activity", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("75715ef0-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "FARMER").build())
                .mapping(new ConceptMapBuilder("b2108898-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1306").build())
                .build());

        Concept employed = install(new ConceptBuilder("b866af07-d583-4d0c-9861-f00e90677042")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("0b76dc32-15f5-102d-96e4-000c29c2a5d7", "Employed", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("f70378e4-d5db-102d-ad2a-000c29c2a5d7", "Employé", Locale.FRENCH, null) // locale-preferred
                .description("ed24cec4-07fe-102c-b5fa-0017a47871b2", "Patient currently employed.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("759f20c4-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "EMPLOYED").build())
                .mapping(new ConceptMapBuilder("b25d6514-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "3395").build())
                .build());

        Concept shopOwner = install(new ConceptBuilder("3cdcc468-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e1d59be-26fe-102b-80cb-0017a47871b2", "Shop owner", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("f632cfbe-d5db-102d-ad2a-000c29c2a5d7", "Propriétaire de la boutique", Locale.FRENCH, null) // locale-preferred
                .description("ecec149e-07fe-102c-b5fa-0017a47871b2", "Shop owner profession or activity", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b214bf94-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1687").build())
                .mapping(new ConceptMapBuilder("757772ea-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "SHOP OWNER").build())
                .build());

        Concept manualLaborer = install(new ConceptBuilder("3cd995ae-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e181328-26fe-102b-80cb-0017a47871b2", "Manual laborer", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("f6023714-d5db-102d-ad2a-000c29c2a5d7", "Main-d'œuvre", Locale.FRENCH, null) // locale-preferred
                .description("ece7a468-07fe-102c-b5fa-0017a47871b2", "An occupation or main activity", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b210a81e-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1326").build())
                .mapping(new ConceptMapBuilder("7571791c-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "MANUAL LABORER").build())
                .build());

        Concept driver = install(new ConceptBuilder("f15d07f0-be62-44d4-89ef-0e17e0aa480e")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("0b75e93a-15f5-102d-96e4-000c29c2a5d7", "Driver", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("0b829374-15f5-102d-96e4-000c29c2a5d7", "Chauffeur", Locale.ENGLISH, null)
                .name("f6fe3eba-d5db-102d-ad2a-000c29c2a5d7", "Chauffeur", Locale.FRENCH, null) // locale-preferred
                .description("ed244954-07fe-102c-b5fa-0017a47871b2", "Employment Activity ", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b25d18a2-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "3344").build())
                .mapping(new ConceptMapBuilder("759ed664-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "DRIVER").build())
                .build());

        Concept student = install(new ConceptBuilder("3cd976f0-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e17c724-26fe-102b-80cb-0017a47871b2", "Student", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("3e17c97c-26fe-102b-80cb-0017a47871b2", "Étudiant", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)  // locale-preferred
                .description("ece76fc0-07fe-102c-b5fa-0017a47871b2", "Main activity", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("75716076-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "STUDENT").build())
                .mapping(new ConceptMapBuilder("b2108a1e-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1307").build())
                .build());

        Concept miner = install(new ConceptBuilder("3cdcbd06-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e1d5284-26fe-102b-80cb-0017a47871b2", "Miner", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("f63251e2-d5db-102d-ad2a-000c29c2a5d7", "Mineur", Locale.FRENCH, null) // locale-preferred
                .description("ecec0aa8-07fe-102c-b5fa-0017a47871b2", "Miner profession or activity", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("75776bce-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "MINER").build())
                .mapping(new ConceptMapBuilder("b214b7e2-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1682").build())
                .build());

        Concept unemployed = install(new ConceptBuilder("3cd979de-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e17d098-26fe-102b-80cb-0017a47871b2", "Unemployed", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("f5ffd816-d5db-102d-ad2a-000c29c2a5d7", "Sans emploi", Locale.FRENCH, null) // locale-preferred
                .name("3e17d318-26fe-102b-80cb-0017a47871b2", "Chômage", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)
                .description("ece77768-07fe-102c-b5fa-0017a47871b2", "Main activity", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("75716364-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "UNEMPLOYED").build())
                .mapping(new ConceptMapBuilder("b2108d2a-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1309").build())
                .build());

        Concept fruitOrVegetableSeller = install(new ConceptBuilder("3cdcbff4-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e1d555e-26fe-102b-80cb-0017a47871b2", "Fruit or vegetable vendor", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("9563f9b4-07d4-102c-b5fa-0017a47871b2", "Produce seller", Locale.ENGLISH, null)
                .name("f632845a-d5db-102d-ad2a-000c29c2a5d7", "Vendeur de fruits ou de légumes", Locale.FRENCH, null) // locale-preferred
                .description("ecec0e9a-07fe-102c-b5fa-0017a47871b2", "Fruit/Veggie seller profession or activity", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("75776e8a-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "FRUIT OR VEGETABLE SELLER").build())
                .mapping(new ConceptMapBuilder("b214baee-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1684").build())
                .build());

        Concept shepherd = install(new ConceptBuilder("3cdcc170-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e1d56d0-26fe-102b-80cb-0017a47871b2", "Shepherd", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("f6329d64-d5db-102d-ad2a-000c29c2a5d7", "Berger", Locale.FRENCH, null) // locale-preferred
                .description("ecec1098-07fe-102c-b5fa-0017a47871b2", "Shepherd profession or activity", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("75777024-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "SHEPHERD").build())
                .mapping(new ConceptMapBuilder("b214bc60-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1685").build())
                .build());

        Concept fisherman = install(new ConceptBuilder("159674AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("107067BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Fisherman", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("b543fde8-54a7-4a8d-ae75-c792708fc731", "Pêcheur", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("16315FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", "occupation", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("216817ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "159674").build())
                .mapping(new ConceptMapBuilder("136442ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(snomedCt, "106400003").build())
                .mapping(new ConceptMapBuilder("136443ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(narrowerThan).ensureTerm(ampath, "6401").build())
                .build());

        Concept marketVendor = install(new ConceptBuilder("162945AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("a1272096-c77b-4e9e-9516-930f31ffd199", "Market vendor", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("75f8b7d4-e8a1-42e3-84a1-cb0ff968e693", "Vendeur de marché", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .mapping(new ConceptMapBuilder("a062c2e8-f589-11e4-b9b2-1697f925ec7b")
                        .type(sameAs).ensureTerm(ciel, "162945").build())
                .build());

        Concept civilServant = install(new ConceptBuilder("162944AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("5f085cf9-1819-499e-b809-f9f790b7f589", "Civil servant", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("9cae05a1-be90-4703-aca6-6488815bf107", "Fonctionnaire", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .mapping(new ConceptMapBuilder("a062c7a2-f589-11e4-b9b2-1697f925ec7b")
                        .type(sameAs).ensureTerm(ciel, "162944").build())
                .build());

        Concept healthCareWorker = install(new ConceptBuilder("3cdbc91e-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e1b1fdc-26fe-102b-80cb-0017a47871b2", "Health care worker", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("367d3538-9f67-45b2-ab98-34107ce442e3", "Membre du service de santé", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("3dd47072-ffe8-47c8-aa5a-7db571be4e20", "Travailleur de soins de santé", Locale.FRENCH, null)
                .description("ecea574e-07fe-102c-b5fa-0017a47871b2", "A person that works to provide health care. This includes but is not limited to physicians, nurses, health promoters and DOT workers.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b2133e44-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1538").build())
                .mapping(new ConceptMapBuilder("75769a96-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "HEALTH CARE WORKER").build())
                .build());

        install(new ConceptBuilder(Concepts.MAIN_ACTIVITY)
                .datatype(coded)
                .conceptClass(question)
                .name("3e17bb4e-26fe-102b-80cb-0017a47871b2", "Main activity", Locale.ENGLISH, null) // locale-preferred
                .name("0b8ec22a-15f5-102d-96e4-000c29c2a5d7", "Job", Locale.ENGLISH, null)
                .name("0b9562d8-15f5-102d-96e4-000c29c2a5d7", "Principal activity", Locale.ENGLISH, null)
                .name("0b8ec130-15f5-102d-96e4-000c29c2a5d7", "Occupation", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("0b8ec40a-15f5-102d-96e4-000c29c2a5d7", "Activité principale", Locale.FRENCH, null) // locale-preferred
                .description("ece767dc-07fe-102c-b5fa-0017a47871b2", "The main activity or job the patient is engaged in", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("d311dd1a-b42f-4263-b230-ae9a9b41ab60")
                        .type(sameAs).ensureTerm(pih, "Occupation").build())
                .mapping(new ConceptMapBuilder("b210855a-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1304").build())
                .answers(farmer, student, unemployed, otherNonCoded, manualLaborer, shepherd, brewer, shopOwner,
                        fruitOrVegetableSeller, miner, factoryWorker, housework, houseworkAndFieldwork, retired, unknown,
                        tooSickToWork, schoolAgeButNotInSchool, driver, professional, smallChild, commerce, teacher,
                        employed, fisherman, marketVendor, civilServant, healthCareWorker)
                .build());

        // start religion question and answers
        // TODO add French and Kreyol translations, if need be

        Concept vodou = install(new ConceptBuilder("162930AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("0fb9b669-e4cf-4215-a4b7-05810077135f", "Voodoo", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .mapping(new ConceptMapBuilder("22adced2-1242-4088-83d3-18d6a0cc485e")
                        .type(sameAs).ensureTerm(pih, "Voodoo").build())
                .mapping(new ConceptMapBuilder("21b83615-c9cb-4b93-a835-db85f473a26c")
                        .type(sameAs).ensureTerm(ciel, "162930").build())
                .mapping(new ConceptMapBuilder("96576ffe-f61a-40e8-b9e1-9246fce7dd5f")
                        .type(sameAs).ensureTerm(snomedCt, "44399006").build())
                .build());

        Concept catholic = install(new ConceptBuilder("162931AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("d60f7743-8010-4366-b1a2-67419cb26449", "Catholic", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .mapping(new ConceptMapBuilder("9c053a48-19ee-4964-8240-c4ba0d163df7")
                        .type(sameAs).ensureTerm(pih, "Catholic").build())
                .mapping(new ConceptMapBuilder("eda8d7a6-08de-4214-ba8b-6feedfd914c1")
                        .type(sameAs).ensureTerm(ciel, "162931").build())
                .mapping(new ConceptMapBuilder("09f579a5-31fa-4282-accc-904ff7bc6f63")
                        .type(sameAs).ensureTerm(snomedCt, "276120001").build())
                .build());

        Concept baptist = install(new ConceptBuilder("1162932AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("acb83b74-09ac-41c5-9d77-9846bee4bd60", "Baptist", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .mapping(new ConceptMapBuilder("39b96e32-bfed-4ab2-bf31-07d6c0b8cff8")
                        .type(sameAs).ensureTerm(pih, "Baptist").build())
                .mapping(new ConceptMapBuilder("2b52c6ce-d16b-492d-a0fb-0782b277026f")
                        .type(sameAs).ensureTerm(ciel, "162932").build())
                .mapping(new ConceptMapBuilder("4252ad9e-b29e-4deb-bd12-ebdf4923727a")
                        .type(sameAs).ensureTerm(snomedCt, "32617004").build())
                .build());

        Concept islam = install(new ConceptBuilder("162933AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("27fd8d35-310f-4d8b-973c-3b973e6523d4", "Islam", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .mapping(new ConceptMapBuilder("a1bb3862-f245-4aaf-ae73-be56f186808f")
                        .type(sameAs).ensureTerm(pih, "Islam").build())
                .mapping(new ConceptMapBuilder("6c5a0151-b600-47e8-ab31-c4f46a009d3b")
                        .type(sameAs).ensureTerm(ciel, "162933").build())
                .mapping(new ConceptMapBuilder("3a42c0ca-0052-434d-bd66-3a31af455012")
                        .type(sameAs).ensureTerm(snomedCt, "271390004").build())
                .build());


        Concept pentecostal = install(new ConceptBuilder("162934AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("649229d4-fb09-4f73-9a47-89e9a99a11ed", "Pentecostal", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .mapping(new ConceptMapBuilder("3088f507-6835-4c3e-864d-d26b020009fe")
                        .type(sameAs).ensureTerm(pih, "Pentecostal").build())
                .mapping(new ConceptMapBuilder("3811e396-86e3-4fc6-a6f2-d1f24f4318b3")
                        .type(sameAs).ensureTerm(ciel, "162934").build())
                .mapping(new ConceptMapBuilder("dbee5d5f-4915-45d4-b858-00b64ce88731")
                        .type(sameAs).ensureTerm(snomedCt, "309862000").build())
                .build());

        Concept adventist = install(new ConceptBuilder("162935AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3e47ed11-8d82-466f-82d8-43643ff2923d", "Seventh Day Adventist", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .mapping(new ConceptMapBuilder("e7cc2548-e3ea-4892-bc9c-5ba27ba4be4e")
                        .type(sameAs).ensureTerm(pih, "Seventh Day Adventist").build())
                .mapping(new ConceptMapBuilder("b011c46f-0641-4fec-8e47-c5147daa9b92")
                        .type(sameAs).ensureTerm(ciel, "162935").build())
                .mapping(new ConceptMapBuilder("37950a05-3d5a-4375-a676-5d7b9fa48092")
                        .type(sameAs).ensureTerm(snomedCt, "298024007").build())
                .build());

        Concept jehovahsWitness = install(new ConceptBuilder("162936AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("58714a23-6ccf-4816-848b-376862ea86cf", "Jehovah's Witness", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .mapping(new ConceptMapBuilder("c30fc279-7197-4dcc-b2f0-3ed4e80b7f37")
                        .type(sameAs).ensureTerm(pih, "Jehovah's Witness").build())
                .mapping(new ConceptMapBuilder("d98888d2-b834-4b21-a48b-14d83a798cef")
                        .type(sameAs).ensureTerm(ciel, "162936").build())
                .mapping(new ConceptMapBuilder("66f53d55-44b1-4061-a8a1-82bf12f63fe0")
                        .type(sameAs).ensureTerm(snomedCt, "276118004").build())
                .build());

        install(new ConceptBuilder("162929AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(coded)
                .conceptClass(question)
                .name("1ad1706a-1400-428e-8b99-0d8b073e1a81", "Religion", Locale.ENGLISH, null) // locale-preferred
                .name("9050b9ef-96d4-484a-9ca3-a21451d305a8", "Religious affiliation", Locale.ENGLISH, null)
                .mapping(new ConceptMapBuilder("10281d23-9f19-487c-a0c3-adde06c6d46b")
                        .type(sameAs).ensureTerm(pih, "Religion").build())
                .mapping(new ConceptMapBuilder("668e0a10-841c-42a4-a394-c706319bb341")
                        .type(sameAs).ensureTerm(ciel, "162929").build())
                .mapping(new ConceptMapBuilder("47714808-2ffb-4ed9-986d-b0810c6ff4a0")
                        .type(sameAs).ensureTerm(snomedCt, "160538000").build())
                .answers(vodou, catholic, baptist, islam, pentecostal, adventist, jehovahsWitness, otherNonCoded)
                .build());

        // end religion question and answers

        Concept namesAndFirstnamesOfContact = install(new ConceptBuilder(Concepts.NAMES_AND_FIRSTNAMES_OF_CONTACT)
                .datatype(text)
                .conceptClass(question)
                .name("3e1817e2-26fe-102b-80cb-0017a47871b2", "The names of a contact of a patient", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("b210a990-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1327").build())
                .mapping(new ConceptMapBuilder("75717a7a-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "NAMES AND FIRSTNAMES OF CONTACT").build())
                .build());

        Concept relationshipsOfContact = install(new ConceptBuilder(Concepts.RELATIONSHIPS_OF_CONTACT)
                .datatype(text)
                .conceptClass(question)
                .name("3e181c9c-26fe-102b-80cb-0017a47871b2", "Relationships of contact to patient", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("b210ab02-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1328").build())
                .mapping(new ConceptMapBuilder("75717bec-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "RELATIONSHIPS OF CONTACT").build())
                .build());

        Concept telephoneNumberOfContact = install(new ConceptBuilder(Concepts.TELEPHONE_NUMBER_OF_CONTACT)
                .datatype(text)
                .conceptClass(question)
                .name("441d28cf-bbfc-407e-a007-efa91f303d5b", "The phone number for the patient's contact", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("b2adaa88-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "6194").build())
                .mapping(new ConceptMapBuilder("75ad601c-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "TELEPHONE NUMBER OF CONTACT").build())
                .build());

        Concept addressOfContact = install(new ConceptBuilder(Concepts.ADDRESS_OF_CONTACT)
                .datatype(text)
                .conceptClass(question)
                .name("A7D167A-1B39-4DE9-8BE7-F3980FBE366F", "The address for the patient's contact", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("18636437-061A-4DA8-87D7-F345F8E8C638")
                        .type(sameAs).ensureTerm(pih, "ADDRESS OF PATIENT CONTACT").build())
                .build());

        Concept patientContactsConstruct = install(new ConceptBuilder(Concepts.PATIENT_CONTACTS_CONSTRUCT)
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("3e1810d0-26fe-102b-80cb-0017a47871b2", "Questions on contacts of the patient", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .setMembers(
                        namesAndFirstnamesOfContact,
                        relationshipsOfContact,
                        telephoneNumberOfContact,
                        addressOfContact)
                .mapping(new ConceptMapBuilder("b210a6ac-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1325").build())
                .mapping(new ConceptMapBuilder("757177b4-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "PATIENT CONTACTS CONSTRUCT").build())
                .build());
    }

}
