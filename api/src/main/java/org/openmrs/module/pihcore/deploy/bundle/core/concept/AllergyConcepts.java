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
    }

    @Override
    public int getVersion() { return 1;  }

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
    }
}

