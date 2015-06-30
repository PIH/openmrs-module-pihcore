package org.openmrs.module.pihcore.deploy.bundle.core.concept;

import org.openmrs.Concept;
import org.openmrs.api.ConceptNameType;
import org.openmrs.module.metadatadeploy.builder.ConceptBuilder;
import org.openmrs.module.metadatadeploy.builder.ConceptMapBuilder;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.VersionedPihConceptBundle;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@Requires({CoreConceptMetadataBundle.class})
public class AllergyConcepts extends VersionedPihConceptBundle {

    public static final class Concepts {
        public static final String ALLERGENS_FOOD_SET = "4c887216-04c1-11e5-8418-1697f925ec7b";
        public static final String ALLERGENS_DRUG_SET = "4c886e88-04c1-11e5-8418-1697f925ec7b";
        public static final String ALLERGENS_ENVIRONMENT_SET = "4c887806-04c1-11e5-8418-1697f925ec7b";
        public static final String ALLERGY_REACTIONS_SET = "4c8865b4-04c1-11e5-8418-1697f925ec7b";
        public static final String ALLERGY_SEVERITY_MILD = "3cdef9ae-26fe-102b-80cb-0017a47871b2";
        public static final String ALLERGY_SEVERITY_MODERATE = "3cdef832-26fe-102b-80cb-0017a47871b2";
        public static final String ALLERGY_SEVERITY_SEVERE = "3cdefc92-26fe-102b-80cb-0017a47871b2";
    }

    // ** WE ARE NO LONGER INSTALLING THIS BUNDLE, ALL CONCEPTS ARE INSTALLED VIA AN ALLERGIES MDS BUNDLE ** //
    // ** NOTE THAT WE ARE STILL USING THE ABOVE UUIDS TO SET GLOBAL PROPERTIES, SO THE CONCEPTS IN THE BUNDLE ** //
    // ** MUST HAVE UUIDS THAT MATCH THE ABOVE UUIDS ** //

    @Override
    public int getVersion() { return 2;  }

    @Override
    protected void installNewVersion() throws Exception {

        Concept mentalStatusChange = install(new ConceptBuilder("121677AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
        .datatype(notApplicable)
        .conceptClass(diagnosis)
        .name("127084BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Mental status change", Locale.ENGLISH, null) // locale-preferred
        .name("21808BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Altered Mental Status", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
        .name("80786BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Estado mental alterado", locale_SPANISH, null) // locale-preferred
        .mapping(new ConceptMapBuilder("95967ABBBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                .type(narrowerThan).ensureTerm(icd10who, "F99").build())
                .mapping(new ConceptMapBuilder("266979ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(imoProblemIT, "72276").build())
                .mapping(new ConceptMapBuilder("70242ABBBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(snomedCt, "419284004").build())
                .mapping(new ConceptMapBuilder("185768ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "121677").build())
                .build());

        Concept angioedema = install(new ConceptBuilder("148787AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(diagnosis)
                .name("48406BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Angioedema", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("108627BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Angio-edema", Locale.ENGLISH, null)
                .name("57589BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "angioedema", locale_SPANISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("15145FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", "Recurring attacks of transient edema suddenly appearing in areas of the skin or mucous membranes and occasionally of the viscera, often associated with dermatographism, urticaria, erythema, and purpura.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("275558ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(pih, "1298").build())
                .mapping(new ConceptMapBuilder("275431ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(icd10who, "T78.3").build())
                .mapping(new ConceptMapBuilder("276324ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(imoProblemIT, "38248").build())
                .mapping(new ConceptMapBuilder("46884ABBBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(snomedCt, "41291007").build())
                .mapping(new ConceptMapBuilder("276265ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "148787").build())
                .build());

        Concept bronchospasm = install(new ConceptBuilder("108AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(symptom)
                .name("123BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Bronchospasm", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("107FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", "A contraction of smooth muscle in the walls of the bronchi and bronchioles, causing narrowing of the lumen.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("272108ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(imoProblemIT, "522535").build())
                .mapping(new ConceptMapBuilder("133655ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(snomedCt, "4386001").build())
                .mapping(new ConceptMapBuilder("171007ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "108").build())
                .mapping(new ConceptMapBuilder("133970ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ampath, "108").build())
                .build());

        Concept arrhythmia = install(new ConceptBuilder("120148AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(diagnosis)
                .name("100604BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Arrhythmia", Locale.ENGLISH, null) // locale-preferred
                .name("126137BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Cardiac arrhythmia", Locale.ENGLISH, null)
                .name("20357BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Cardiac Dysrhythmia", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("81689BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Trastorno de conducción cardíaca", locale_SPANISH, null) // locale-preferred
                .description("5924FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", "Any variation from the normal rhythm or rate of the heart beat.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("275573ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(pih, "3310").build())
                .mapping(new ConceptMapBuilder("71169ABBBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(snomedCt, "698247007").build())
                .mapping(new ConceptMapBuilder("276636ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(imoProblemIT, "38620").build())
                .mapping(new ConceptMapBuilder("275675ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ampath, "1530").build())
                .mapping(new ConceptMapBuilder("276126ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "120148").build())
                .mapping(new ConceptMapBuilder("275497ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(icd10who, "I49.9").build())
                .build());

        install(new ConceptBuilder(Concepts.ALLERGY_REACTIONS_SET)
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("4c886be0-04c1-11e5-8418-1697f925ec7b", "PIH Core allergic reactions", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("4c886d52-04c1-11e5-8418-1697f925ec7b", "A convenience set to allow for easy selection of common allergic reactions", Locale.ENGLISH)
                .setMembers(arrhythmia, bronchospasm, angioedema, mentalStatusChange)
                .build());

        // Drug allergy
        Concept morphine = install(new ConceptBuilder("80106AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("6837BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Morphine", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("94904BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "MST Continus", Locale.ENGLISH, null)
                .name("97722BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Roxanol", Locale.ENGLISH, null)
                .name("97433BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Rescudose", Locale.ENGLISH, null)
                .name("91169BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Duromorph", Locale.ENGLISH, null)
                .name("95276BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Nepenthe", Locale.ENGLISH, null)
                .name("97723BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Roxanol UD", Locale.ENGLISH, null)
                .name("97256BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Rapi-Ject", Locale.ENGLISH, null)
                .name("94254BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "M-Eslon", Locale.ENGLISH, null)
                .name("97602BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "RMS", Locale.ENGLISH, null)
                .name("90413BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Demerol", Locale.ENGLISH, null)
                .name("94900BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "MS Contin", Locale.ENGLISH, null)
                .name("98025BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Sevredol", Locale.ENGLISH, null)
                .name("94963BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "MXL brand of morphine sulfate", Locale.ENGLISH, null)
                .name("97724BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Roxanol-T", Locale.ENGLISH, null)
                .name("94873BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Morcap SR", Locale.ENGLISH, null)
                .name("95796BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Oramorph", Locale.ENGLISH, null)
                .name("88383BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Avinza", Locale.ENGLISH, null)
                .name("100439BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Zomorph", Locale.ENGLISH, null)
                .name("91133BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Duramorph PF", Locale.ENGLISH, null)
                .name("94902BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "MSIR", Locale.ENGLISH, null)
                .name("90466BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Depodur", Locale.ENGLISH, null)
                .name("93576BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Kadian", Locale.ENGLISH, null)
                .name("95797BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Oramorph SR", Locale.ENGLISH, null)
                .name("88282BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Astramorph PF", Locale.ENGLISH, null)
                .name("95707BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "OMS", Locale.ENGLISH, null)
                .name("93316BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Infumorph", Locale.ENGLISH, null)
                .name("94901BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "MS/S", Locale.ENGLISH, null)
                .name("95708BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "OMS brand of Morphine Sulfate", Locale.ENGLISH, null)
                .mapping(new ConceptMapBuilder("130406ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(rxNorm, "7052").build())
                .mapping(new ConceptMapBuilder("138961ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(snomedCt, "73572009").build())
                .mapping(new ConceptMapBuilder("175991ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "80106").build())
                .mapping(new ConceptMapBuilder("145370ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(narrowerThan).ensureTerm(snomedNp, "373529000").build())
                .build());

        install(new ConceptBuilder(Concepts.ALLERGENS_DRUG_SET)
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("4c886fbe-04c1-11e5-8418-1697f925ec7b", "PIH Core drug allergies", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("4c8870f4-04c1-11e5-8418-1697f925ec7b", "A convenience set to allow for easy selection of common drug allergies", Locale.ENGLISH)
                .setMembers(morphine)
                .build());

        // Food allergy
        Concept peanuts = install(new ConceptBuilder("162172AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("124965BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Peanuts", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("17166FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", "A food often causing allergic reactions", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("237195ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "162172").build())
                .mapping(new ConceptMapBuilder("237194ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(snomedCt, "256349002").build())
                .build());

        install(new ConceptBuilder(Concepts.ALLERGENS_FOOD_SET)
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("4c887338-04c1-11e5-8418-1697f925ec7b", "PIH Core food allergens", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("4c8876bc-04c1-11e5-8418-1697f925ec7b", "A convenience set to allow for easy selection of common food allergens", Locale.ENGLISH)
                .setMembers(peanuts)
                .build());

        // Environment allergen
        Concept beeSting = install(new ConceptBuilder("162536AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("127083BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Bee stings", Locale.ENGLISH, null) // locale-preferred
                .name("126208BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Sting of bee", Locale.ENGLISH, null)
                .name("126138BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Bee venom", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("277720ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "162536").build())
                .mapping(new ConceptMapBuilder("277719ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(snomedCt, "288328004").build())
                .build());

        install(new ConceptBuilder(Concepts.ALLERGENS_ENVIRONMENT_SET)
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("4c887928-04c1-11e5-8418-1697f925ec7b", "PIH Core environmental allergens", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("4c887a4a-04c1-11e5-8418-1697f925ec7b", "A convenience set to allow for easy selection of common environmental allergens", Locale.ENGLISH)
                .setMembers(beeSting)
                .build());

        // Levels of severity
        Concept fatal = install(new ConceptBuilder("25b286a2-584b-4bbb-bb8b-558db9684c7a")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("718228c4-7824-40f6-93a8-9f7e439c01e7", "Fatal", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("cb8ec00c-df19-435f-926f-302ef54c9ff6", "Fatal", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .mapping(new ConceptMapBuilder("0f2780f0-a543-102e-a00e-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "FATAL").build())
                .mapping(new ConceptMapBuilder("278367ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(snomedCt, "399166001").build())
                .mapping(new ConceptMapBuilder("278368ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "162819").build())
                .mapping(new ConceptMapBuilder("cf1bfb76-a542-102e-a00e-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "7059").build())
                .build());

        Concept severe = install(new ConceptBuilder(Concepts.ALLERGY_SEVERITY_SEVERE)
                .datatype(notApplicable)
                .conceptClass(finding)
                .name("0b6342d0-15f5-102d-96e4-000c29c2a5d7", "Severe", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("93b806dc-07d4-102c-b5fa-0017a47871b2", "Severe", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("cc2e71b8-1cba-4ef4-9cd6-97e2efa93f0c", "Sévère", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("96a55fca-c66b-4bda-9f10-fb0ec126994d", "Grav", locale_HAITI, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("16229FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", "General qualifier value for the severity assesment", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("171742ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "1500").build())
                .mapping(new ConceptMapBuilder("7578d19e-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "SEVERE").build())
                .mapping(new ConceptMapBuilder("135122ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ampath, "1745").build())
                .mapping(new ConceptMapBuilder("132651ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(snomedCt, "24484000").build())
                .mapping(new ConceptMapBuilder("b21e7624-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1903").build())
                .build());

        Concept mild = install(new ConceptBuilder(Concepts.ALLERGY_SEVERITY_MILD)
                .datatype(notApplicable)
                .conceptClass(finding)
                .name("0b6340dc-15f5-102d-96e4-000c29c2a5d7", "Mild", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("93b7febc-07d4-102c-b5fa-0017a47871b2", "Mild", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("caac4020-4fc6-4584-83e0-668177b00a77", "Bénin", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("16227FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", "General qualifier value", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("171740ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "1498").build())
                .mapping(new ConceptMapBuilder("7578ce42-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "MILD").build())
                .mapping(new ConceptMapBuilder("135120ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ampath, "1743").build())
                .mapping(new ConceptMapBuilder("132650ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(snomedCt, "255604002").build())
                .mapping(new ConceptMapBuilder("b21e72fa-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1901").build())
                .build());

        Concept moderate = install(new ConceptBuilder(Concepts.ALLERGY_SEVERITY_MODERATE)
                .datatype(notApplicable)
                .conceptClass(finding)
                .name("0b633fa6-15f5-102d-96e4-000c29c2a5d7", "Moderate", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("93b7fa98-07d4-102c-b5fa-0017a47871b2", "Moderate", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("14d2181c-0275-42c8-90e4-f6bc914becad", "Modéré", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("d2fac9a8-b710-4ed7-9f3b-466a48ecaca5", "Modere", locale_HAITI, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("16228FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", "General qualifier value of the severity", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b21e7142-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "1900").build())
                .mapping(new ConceptMapBuilder("135121ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ampath, "1744").build())
                .mapping(new ConceptMapBuilder("171741ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "1499").build())
                .mapping(new ConceptMapBuilder("133261ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(snomedCt, "6736007").build())
                .mapping(new ConceptMapBuilder("7578ccbc-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "MODERATE").build())
                .build());

        Concept lifeThreatening = install(new ConceptBuilder("162693AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("126526BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Life threatening severity", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .mapping(new ConceptMapBuilder("278076ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(snomedCt, "442452003").build())
                .mapping(new ConceptMapBuilder("278077ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "162693").build())
                .build());

        install(new ConceptBuilder("162760AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(coded)
                .conceptClass(question)
                .name("126624BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Severity of adverse reaction", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .mapping(new ConceptMapBuilder("278208ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "162760").build())
                .mapping(new ConceptMapBuilder("278369ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(narrowerThan).ensureTerm(snomedNp, "405162009").build())
                .answers(lifeThreatening, moderate, mild, severe)
                .build());
    }

}

