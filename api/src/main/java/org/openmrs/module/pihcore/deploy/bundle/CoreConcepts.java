package org.openmrs.module.pihcore.deploy.bundle;

import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptMapType;
import org.openmrs.ConceptSource;
import org.openmrs.api.ConceptNameType;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.builder.ConceptBuilder;
import org.openmrs.module.metadatadeploy.builder.ConceptMapBuilder;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.conceptClass;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.conceptSource;

/**
 * Core concepts, used in *all* PIH deployments of OpenMRS going forwards
 */
@Component
public class CoreConcepts extends VersionedPihMetadataBundle {

    @Override
    public int getVersion() {
        return 1;
    }

    public static final class Concepts {
        public static final String UNKNOWN = "3cd6fac4-26fe-102b-80cb-0017a47871b2";
        public static final String YES = "3cd6f600-26fe-102b-80cb-0017a47871b2";
        public static final String NO = "3cd6f86c-26fe-102b-80cb-0017a47871b2";

        public static final String PAST_MEDICAL_HISTORY_CONSTRUCT = "1633AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        public static final String PAST_MEDICAL_HISTORY_FINDING = "1628AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        public static final String PAST_MEDICAL_HISTORY_PRESENCE = "1729AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        public static final String PAST_MEDICAL_HISTORY_COMMENT = "160221AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

        public static final String FAMILY_HISTORY_CONSTRUCT = "160593AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        public static final String FAMILY_HISTORY_DIAGNOSIS = "160592AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        public static final String FAMILY_HISTORY_RELATIONSHIP = "1560AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        public static final String FAMILY_HISTORY_PRESENCE = "1729AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"; // same as PAST_MEDICAL_HISTORY_PRESENCE
        public static final String FAMILY_HISTORY_COMMENT = "160618AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

    }

    public static final class ConceptSources {
        public static final String LOINC = "2889f378-f287-40a5-ac9c-ce77ee963ed7";
        public static final String CIEL = "21ADDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD";
        public static final String PIH = "fb9aaaf1-65e2-4c18-b53c-16b575f2f385";
        public static final String SNOMED_CT = "1ADDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD";
        public static final String EMRAPI_MODULE = "edd52713-8887-47b7-ba9e-6e1148824ca4";
        public static final String MDRTB_MODULE = "ddb6b595-0b85-4a80-9243-efe4ba404eef";
    }

    // these exist already, we don't create them
    public static final class ConceptDatatypes {
        public static final String N_A = "8d4a4c94-c2cc-11de-8d13-0010c6dffd0f";
        public static final String CODED = "8d4a48b6-c2cc-11de-8d13-0010c6dffd0f";
        public static final String TEXT = "8d4a4ab4-c2cc-11de-8d13-0010c6dffd0f";
    }

    public static final class ConceptClasses {
        public static final String MISC = "8d492774-c2cc-11de-8d13-0010c6dffd0f";
        public static final String DIAGNOSIS = "8d4918b0-c2cc-11de-8d13-0010c6dffd0f";
        public static final String QUESTION = "8d491e50-c2cc-11de-8d13-0010c6dffd0f";
        public static final String CONV_SET = "8d492594-c2cc-11de-8d13-0010c6dffd0f";
    }

    // these exist already, we don't create them
    public static final class ConceptMapTypes {
        public static final String SAME_AS = "35543629-7d8c-11e1-909d-c80aa9edcf4e";
    }

    @Override
    protected void installEveryTime() throws Exception {
        Map<String, String> properties = new LinkedHashMap<String, String>();
        properties.put("concept.unknown", Concepts.UNKNOWN); // see HtmlFormEntryConstants.GP_UNKNOWN_CONCEPT
        setGlobalProperties(properties);
    }

    @Override
    protected void installNewVersion() throws Exception {
        Locale locale_HAITI = new Locale("ht");
        ConceptDatatype notApplicable = MetadataUtils.existing(ConceptDatatype.class, ConceptDatatypes.N_A);
        ConceptDatatype text = MetadataUtils.existing(ConceptDatatype.class, ConceptDatatypes.TEXT);
        ConceptDatatype coded = MetadataUtils.existing(ConceptDatatype.class, ConceptDatatypes.CODED);

        ConceptClass misc = install(conceptClass("Misc", "Terms which don't fit other categories", ConceptClasses.MISC));
        ConceptClass diagnosis = install(conceptClass("Diagnosis", "Conclusion drawn through findings", ConceptClasses.DIAGNOSIS));
        ConceptClass question = install(conceptClass("Question", "Question (eg, patient history, SF36 items)", ConceptClasses.QUESTION));
        ConceptClass convSet = install(conceptClass("ConvSet", "Term to describe convenience sets", ConceptClasses.CONV_SET));

        ConceptSource pih = install(conceptSource("PIH", "Partners In Health concept dictionary using concept ids and preferred English names", "PIH", ConceptSources.PIH)); // TODO remove the hl7Code
        ConceptSource ciel = install(conceptSource("CIEL", "Columbia International eHealth Laboratory concept ID", null, ConceptSources.CIEL));
        ConceptSource snomedCt = install(conceptSource("SNOMED CT", "SNOMED Preferred mapping", null, ConceptSources.SNOMED_CT));
        ConceptSource loinc = install(conceptSource("LOINC", "A universal code system for identifying laboratory and clinical observations.", null, ConceptSources.LOINC));
        ConceptSource emrapi = install(conceptSource("org.openmrs.module.emrapi", "Source used to tag concepts used in the emr-api module", null, ConceptSources.EMRAPI_MODULE));
        ConceptSource mdrtb = install(conceptSource("org.openmrs.module.mdrtb", "Concepts used with the MDR-TB module", null, ConceptSources.MDRTB_MODULE));

        ConceptMapType sameAs = MetadataUtils.existing(ConceptMapType.class, ConceptMapTypes.SAME_AS);

        Concept yes = install(new ConceptBuilder("3cd6f600-26fe-102b-80cb-0017a47871b2")
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

        Concept no = install(new ConceptBuilder("3cd6f86c-26fe-102b-80cb-0017a47871b2")
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

        Concept unknown = install(new ConceptBuilder("3cd6fac4-26fe-102b-80cb-0017a47871b2")
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

        Concept pmhWhich = install(new ConceptBuilder(Concepts.PAST_MEDICAL_HISTORY_FINDING)
                .datatype(coded)
                .conceptClass(diagnosis)
                        // in CIEL this is called PAST MEDICAL HISTORY ADDED
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

        Concept famHxRelationship = install(new ConceptBuilder(Concepts.FAMILY_HISTORY_RELATIONSHIP)
                .datatype(coded)
                .conceptClass(misc)
                .name("1825BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Family member", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers() // TODO
                .mapping(new ConceptMapBuilder("171802ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "1560").build())
                .mapping(new ConceptMapBuilder("132750ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(snomedCt, "303071001").build())
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
