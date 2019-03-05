package org.openmrs.module.pihcore.deploy.bundle.core.concept;

import org.openmrs.Concept;
import org.openmrs.api.ConceptNameType;
import org.openmrs.module.metadatadeploy.builder.ConceptBuilder;
import org.openmrs.module.metadatadeploy.builder.ConceptMapBuilder;
import org.openmrs.module.metadatadeploy.builder.ConceptNumericBuilder;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.VersionedPihConceptBundle;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@Requires({CoreConceptMetadataBundle.class})
public class VaccinationConcepts extends VersionedPihConceptBundle{

    @Override
    public int getVersion() {
        return 5;
    }

    @Override
    protected void installNewVersion() throws Exception {
        Concept vaccinationGiven = install(new ConceptBuilder("2dc6c690-a5fe-4cc4-97cc-32c70200a2eb")
                .conceptClass(convSet)
                .datatype(coded)
                .name("1034BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Immunizations", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("107784BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Vaccinations", Locale.ENGLISH, null)
                .description("986FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", "Captures an immunization that has been administered to the patient, either before or during the encounter containing an observation of this concept.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("133795ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(snomedCt, "33879002").build())
                .mapping(new ConceptMapBuilder("171250ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "984").build())
                .build());

        Concept vaccinationDate = install(new ConceptBuilder("1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .conceptClass(finding)
                .datatype(date)
                .name("1565BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Vaccination date", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("107781BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Immunization date", Locale.ENGLISH, null)
                .name("86865BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Vaccination date", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("1566BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Date de vaccination", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)
                .name("1567BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "tarehe ya kupewa chanjo/Kinga", locale_SWAHILI, ConceptNameType.FULLY_SPECIFIED)
                .description("1379FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", "The full date of a vaccination", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("171662ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs) .ensureTerm(ciel, "1410").build())
                .build());

        Concept vaccinationSequenceNumber = install(new ConceptNumericBuilder("ef6b45b4-525e-4d74-bf81-a65a41f3feb9") // our copy of CIEL:1418
                .conceptClass(finding)
                .datatype(numeric)
                .lowAbsolute(0d)
                .precise(false)
                .name("1577BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Immunization sequence number", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("107783BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Vaccination sequence number", Locale.ENGLISH, null)
                .name("86873BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "immunization no", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("1578BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Numéro des vaccinations", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("171670ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "1418").build())
                .build());

        Concept vaccinationConstruct = install(new ConceptBuilder("74260088-9c83-41d5-b92b-03a41654daaf") // CIEL:1421, but with fewer answers
                .conceptClass(finding)
                .datatype(notApplicable)
                .name("1585BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Immunization history", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("107785BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "vaccination history", Locale.ENGLISH, null)
                .name("107786BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "vaccination hx", Locale.ENGLISH, null)
                .name("86876BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "immunization hx", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("1586BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Historique vaccinale", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)
                .name("110133BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Historia ya Chanjo", locale_SWAHILI, ConceptNameType.FULLY_SPECIFIED)
                .description("921f5876-a0fc-47a0-bd0f-ff818c5c1c9c", "Vaccations that this patient has already received (in the past or in the current visit)", Locale.ENGLISH)
                .setMembers(vaccinationGiven, vaccinationSequenceNumber, vaccinationDate)
                .mapping(new ConceptMapBuilder("136923ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(snomedCt, "425457005").build())
                .mapping(new ConceptMapBuilder("171673ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "1421").build())
                .build());

        install(new ConceptBuilder("3cd4e004-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(procedure)
                .name("93ee06ce-07d4-102c-b5fa-0017a47871b2", "BCG vaccine", Locale.ENGLISH, null) // locale-preferred
                .name("3e12ca08-26fe-102b-80cb-0017a47871b2", "BACILLE CAMILE-GUERIN VACCINATION", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("9388e7c6-07d4-102c-b5fa-0017a47871b2", "BCG", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("93edfd82-07d4-102c-b5fa-0017a47871b2", "BCG", Locale.ENGLISH, null)
                .name("f5b02afa-d5db-102d-ad2a-000c29c2a5d7", "VACCINATION DU BACILLE DE CALMETTE-GUÉRIN", Locale.FRENCH, null) // locale-preferred
                .description("ece17228-07fe-102c-b5fa-0017a47871b2", "Deactivated tuberculous agent as basis for tuberculosis vaccination.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b20a68dc-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "886").build())
                .mapping(new ConceptMapBuilder("753c8888-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "BACILLE CAMILE-GUERIN VACCINATION").build())
                .build());

        install(new ConceptBuilder("3cd42c36-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(procedure)
                .name("3e11e7dc-26fe-102b-80cb-0017a47871b2", "Oral polio vaccination", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("94b884e4-07d4-102c-b5fa-0017a47871b2", "ORAL POLIO VAX NO.3", Locale.ENGLISH, null)
                .name("95636e22-07d4-102c-b5fa-0017a47871b2", "POLIO VAX NO.0", Locale.ENGLISH, null)
                .name("95637ad4-07d4-102c-b5fa-0017a47871b2", "POLIO VAX NO.3", Locale.ENGLISH, null)
                .name("9563721e-07d4-102c-b5fa-0017a47871b2", "POLIO VAX NO.1", Locale.ENGLISH, null)
                .name("95637692-07d4-102c-b5fa-0017a47871b2", "POLIO VAX NO.2", Locale.ENGLISH, null)
                .name("94b877d8-07d4-102c-b5fa-0017a47871b2", "ORAL POLIO VAX NO.0", Locale.ENGLISH, null)
                .name("94b88048-07d4-102c-b5fa-0017a47871b2", "ORAL POLIO VAX NO.2", Locale.ENGLISH, null)
                .name("94b87c1a-07d4-102c-b5fa-0017a47871b2", "ORAL POLIO VAX NO.1", Locale.ENGLISH, null)
                .name("f5a682a2-d5db-102d-ad2a-000c29c2a5d7", "VACCINATION ORALE ANTIPOLIOMYÉLITE", Locale.FRENCH, null) // locale-preferred
                .description("ece065ae-07fe-102c-b5fa-0017a47871b2", "Vaccination given for polio.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("8ad98af3-e438-4b7a-b667-922854aad62e")
                        .type(sameAs).ensureTerm(pih, "ORAL POLIO VACCINATION").build())
                .mapping(new ConceptMapBuilder("b2094e98-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "783").build())
                .build());

        // from OCL
        install(new ConceptBuilder("1423AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("8ab170aa2be7433ab50e0e9ccd81369a", "PENTAVALENT PNEUMOVAX", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("878025f9e4d7428b9cbea96d8cf62558", "pneumovax", Locale.ENGLISH, ConceptNameType.SHORT)
                        //.name("104e8e5076b04fd18bf3fcd1117f9453", "IMMUNIZATION", Locale.ENGLISH, null)
                        //.name("6c8ebf9e72c640619055b51bb3bb9878", "VACCINATION", Locale.ENGLISH, null)
                .name("af7039b37b384238b51dca3446a506f5", "IMMUNIZATION, PENTAVALENT PNEUMOVAX", Locale.ENGLISH, null)
                .name("73a0cb5b858d4234a8073704233c5efe", "VACCINATION, PENTAVALENT PNEUMOVAX", Locale.ENGLISH, null)
                .mapping(new ConceptMapBuilder("553069d350d61b02e14f3c77")
                        .type(sameAs).ensureTerm(ciel, "1423").build())
                //.mapping(new ConceptMapBuilder("8d6fa852-31e2-3421-a83a-2b1e36f764ee")
                //        .type(narrowerThan).ensureTerm(HL-7-CVX, "109").build())
                .mapping(new ConceptMapBuilder("19e25e62-cf4e-324e-9c07-0c0d56de6274")
                        .type(sameAs).ensureTerm(snomedCt, "12866006").build())
                // TODO descriptions
                .build());

        // from OCL
        install(new ConceptBuilder("83531AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("9ea2957f48e645c48e95795db69130e1", "ROTAVIRUS VACCINE, LIVE", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("55306ca550d61b02e14f6cf7")
                        .type(sameAs).ensureTerm(ciel, "83531").build())
                .mapping(new ConceptMapBuilder("39edda7a-16fc-3e5a-ab41-38cb4874bed1")
                        .type(sameAs).ensureTerm(rxNorm, "253199").build())
                //.mapping(new ConceptMapBuilder("2ea39a0d-dc08-3c04-9e3d-a5c5aae1be05")
                //    .type(narrowerThan).ensureTerm(HL-7-CVX, "74").build())
                // TODO descriptions
                .build());

        // from OCL
        install(new ConceptBuilder("162586AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("406627b252a9401a86b143fa53f323f1", "Measles-Rubella vaccine", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("b0cb25bd523746998434cf4ad6751d2c", "rubeole-rougeole vaccine", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("5530b2fd50d61b02e150bc8d")
                        .type(sameAs).ensureTerm(ciel, "162586").build())
                // TODO descriptions
                .build());

        install(new ConceptBuilder("3ccc6b7c-26fe-102b-80cb-0017a47871b2")
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("956a4d6e-07d4-102c-b5fa-0017a47871b2", "TD BOOSTER", Locale.ENGLISH, null)
                .name("3e09b26a-26fe-102b-80cb-0017a47871b2", "DIPTHERIA TETANUS BOOSTER", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("f560f962-d5db-102d-ad2a-000c29c2a5d7", "INJECTION DE RAPPEL: DIPTHÉRIE TÉTANOS ", Locale.FRENCH, null) // locale-preferred
                .description("ecd66e50-07fe-102c-b5fa-0017a47871b2", "Vaccination against diptheria and tetanus (IM).", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b1c13504-4864-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "17").build())
                .mapping(new ConceptMapBuilder("737fedbe-4943-102e-96e9-000c29c2a5d7")
                        .type(sameAs).ensureTerm(pih, "DIPTHERIA TETANUS BOOSTER").build())
                .build());

        install(new ConceptBuilder("781AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("1ab00cd3-11bd-447c-a1c6-0fd896120cc3", "Diphtheria Tetanus and Pertussis vaccination", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("137802BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Vaccination contre la Diphtérie, tétanos et la coqueluche", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)
                .name("137803BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "VAKSINASYON Difteri, tetanòs ak koklich", locale_HAITI, ConceptNameType.FULLY_SPECIFIED)
                .name("7a62cdf4-621a-43d2-b676-273853973b4c", "DPT", Locale.ENGLISH, null)
                .name("93f5ae24-07d4-102c-b5fa-0017a47871b2", "DTAP", Locale.ENGLISH, null)
                .name("106355BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Diphtheria Tetanus and Pertussis immunization", Locale.ENGLISH, null)
                .name("0b7b71de-15f5-102d-96e4-000c29c2a5d7", "DTAP", Locale.ENGLISH, ConceptNameType.SHORT)
                .description("ece061bc-07fe-102c-b5fa-0017a47871b2", "Vaccination given for diphtheria, tetanus, and acellular pertussis infections", Locale.ENGLISH)
                .description("781FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", "Vaccination given for diphtheria, tetanus, and acellular pertussis infections", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("751237cc-4943-102e-96e9-000c29c2a5d7").type(sameAs).ensureTerm(pih, "DIPTHERIA TETANUS AND PERTUSSIS VACCINATION").build())
                .mapping(new ConceptMapBuilder("132452ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(snomedCt, "399014008").build())
                .mapping(new ConceptMapBuilder("171171ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "781").build())
                .mapping(new ConceptMapBuilder("132453ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(narrowerThan).ensureTerm(snomedNp, "421245007").build())
                .mapping(new ConceptMapBuilder("274393ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(imoProcedureIT, "1746307").build())
                .mapping(new ConceptMapBuilder("134475ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ampath, "781").build())
                .mapping(new ConceptMapBuilder("b2094ba0-4864-102e-96e9-000c29c2a5d7").type(sameAs).ensureTerm(pih, "781").build())
                .build());

        // how to handle "Others" for vaccinations
    }
}
