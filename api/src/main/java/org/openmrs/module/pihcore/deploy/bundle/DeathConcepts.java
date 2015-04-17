package org.openmrs.module.pihcore.deploy.bundle;

import org.openmrs.Concept;
import org.openmrs.api.ConceptNameType;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.builder.ConceptBuilder;
import org.openmrs.module.metadatadeploy.builder.ConceptMapBuilder;
import org.openmrs.module.metadatadeploy.builder.ConceptNumericBuilder;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@Requires({CoreConceptMetadataBundle.class, CommonConcepts.class, AnswerConcepts.class})
public class DeathConcepts extends VersionedPihConceptBundle {

    public static final String CAUSE_OF_DEATH_FROM_DEATH_CERTIFICATE = "1814AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

    @Override
    public int getVersion() {
        return 2;
    }

    @Override
    protected void installNewVersion() throws Exception {
        Concept yes = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.YES);
        Concept no = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.NO);
        Concept home = MetadataUtils.existing(Concept.class, AnswerConcepts.Concepts.HOME);
        Concept hospital = MetadataUtils.existing(Concept.class, AnswerConcepts.Concepts.HOSPITAL);
        Concept outsideOfInstitution = MetadataUtils.existing(Concept.class, AnswerConcepts.Concepts.OUTSIDE_OF_INSTITUTION);
        Concept otherNonCoded = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.OTHER_NON_CODED);
        Concept familyMember = MetadataUtils.existing(Concept.class, AnswerConcepts.Concepts.FAMILY_MEMBER);
        Concept police = MetadataUtils.existing(Concept.class, AnswerConcepts.Concepts.POLICE);
        Concept surgery = MetadataUtils.existing(Concept.class, AnswerConcepts.Concepts.SURGERY);

        install(new ConceptBuilder("e2d8ea09-3c19-4574-92f3-b3ea67187986")
                .datatype(text)
                .conceptClass(question)
                .name("9c194ad7-331b-410b-ac20-2a8dab166dd2", "Location of death at institution", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("6b1acb11-5fb0-4ea7-9532-6c81544b8eba", "Hospital service or outpatient clinic where the patient died. (A location.) \n\nAn observation of this concept implies that the patient died in a healthcare institution managed by this EMR system.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("2e9ce64f-2b2b-4648-b70b-3af895eb83a8")
                        .type(sameAs).ensureTerm(pih, "Location of death at institution").build())
                .mapping(new ConceptMapBuilder("50eebcc7-d330-4af9-b6b3-d61211e07978")
                        .type(sameAs).ensureTerm(pih, "9709").build())
                .build());

        install(new ConceptBuilder("e4310421-915a-4720-9d6a-42f5ca2ae975")
                .datatype(text)
                .conceptClass(question)
                .name("e17f482c-c814-4ae6-ae07-bd6d29d707a9", "Location of death, non-coded", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("afe40b97-be99-46a7-bd76-bedaa7f5a037", "Where did the patient die, if it cannot be represented by a coded answer to \"Location of death\"", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("85ba20be-383f-4539-b584-5790e76c0f1e")
                        .type(sameAs).ensureTerm(pih, "9708").build())
                .mapping(new ConceptMapBuilder("073d4569-c1d0-4aef-bd04-20060f85ff11")
                        .type(sameAs).ensureTerm(pih, "Location of death non coded").build())
                .build());

        install(new ConceptBuilder("ea2a816b-a8fc-437f-949d-391314f17445")
                .datatype(text)
                .conceptClass(question)
                .name("8a24c13d-a614-4251-85f9-be5526708ad1", "Condition contributing to the death but not related to the disease or morbid condition, non-coded", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("e374ee4d-5baa-48a5-b24b-ccc7020a2a26", "Condition contributing to the death, non-coded", Locale.ENGLISH, null)
                .description("a2d423b7-9da2-4af4-8f1e-e619c049e3df", "Condition contributing to the death but not related to the disease or morbid condition, as free-text", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("87ec0d78-048f-423a-8732-b04de82c796e")
                        .type(sameAs).ensureTerm(pih, "9723").build())
                .mapping(new ConceptMapBuilder("6ed991d3-e5ad-4b4f-9c6c-601a35c0412c")
                        .type(sameAs).ensureTerm(pih, "Condition contributing to the death non coded").build())
                .build());

        install(new ConceptBuilder("07501ef7-e073-4541-a9e5-a697d91e1370")
                .datatype(coded)
                .conceptClass(finding)
                .name("bc50cee2-8468-4b3b-8f0e-a5f95890df7c", "Location of death", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("717517a6-e488-457e-8320-6e42b816c0b2", "Lieu du décès", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("ca30ec30-292b-4b59-8c4d-5b0c10d87308", "Where did the patient die?", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("0bdb3d14-77b8-4d38-8a12-1b6ad26d5713")
                        .type(sameAs).ensureTerm(pih, "9669").build())
                .mapping(new ConceptMapBuilder("0225e69b-cc73-420a-b54d-93c1b7ce8921")
                        .type(sameAs).ensureTerm(pih, "Location of death").build())
                .answers(home, hospital, outsideOfInstitution, otherNonCoded)
                .build());

        install(new ConceptBuilder("59ded805-b258-43f1-bd89-9fcb7e51a375")
                .datatype(coded)
                .conceptClass(diagnosis)
                .name("6f43829f-b338-489a-af10-531992fd929f", "Final disease resulting in death", Locale.ENGLISH, null) // locale-preferred
                .name("2a41c308-2b82-40fe-9d73-c67efacd8a77", "Final disease or condition resulting in death", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("22562a24-8ec1-463e-9562-d6e5564a88cd", "Maladie ou ayant directement provoqué le décès", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .mapping(new ConceptMapBuilder("01142bc3-575e-42bf-95e1-c7147428e006")
                        .type(sameAs).ensureTerm(pih, "Final disease resulting in death").build())
                .mapping(new ConceptMapBuilder("1b953954-e0ad-4838-8d0f-4d595dfe5e60")
                        .type(sameAs).ensureTerm(pih, "9676").build())
                .build());

        install(new ConceptBuilder("c86df3dd-4e05-4aab-ba0d-3003d559cdbe")
                .datatype(coded)
                .conceptClass(question)
                .name("730f5e1b-9bb4-497a-aca3-553dc500d80b", "Maternal death", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("a51ac050-0309-4d2c-bb53-91fe6153be44", "Is the mother alive?", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("85eab777-6dba-41ee-89fe-001c74f71f65")
                        .type(sameAs).ensureTerm(pih, "Maternal death").build())
                .mapping(new ConceptMapBuilder("5e9327f2-a09a-4e59-9d53-a12df62317e1")
                        .type(sameAs).ensureTerm(pih, "9668").build())
                .answers(yes, no)
                .build());

        Concept diagnosisSequenceNumber = install(new ConceptNumericBuilder("1815AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .precise(false)
                .datatype(numeric)
                .conceptClass(misc)
                .name("2095BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "DIAGNOSIS SEQUENCE NUMBER", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("105907BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "NUMÉRO D'ORDRE DES DIAGNOSTICS", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("1625FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", "The number/order of a diagnosis on a list or death certificate.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("2c836f5d-2346-4c44-92c6-c3272120ac5c")
                        .type(sameAs).ensureTerm(pih, "9712").build())
                .mapping(new ConceptMapBuilder("b8a7f15d-82c0-4571-b4fb-cf1a577b84f7")
                        .type(sameAs).ensureTerm(ciel, "1815").build())
                .build());

        Concept causeOfDeathFromCertificate = install(new ConceptBuilder(CAUSE_OF_DEATH_FROM_DEATH_CERTIFICATE)
                .datatype(coded)
                .conceptClass(question)
                .name("2094BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "CAUSE OF DEATH FROM DEATH CERTIFICATE", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("105848BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "CAUSE DU DÉCÈS SUR CERTIFICAT DE DÉCÈS", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .mapping(new ConceptMapBuilder("2af0667b-3a89-4aac-a873-5a2900c2240b")
                        .type(sameAs).ensureTerm(pih, "9713").build())
                .mapping(new ConceptMapBuilder("cc260f30-e152-49a3-ac29-b85c4845e25b")
                        .type(sameAs).ensureTerm(ciel, "1814").build())
                .mapping(new ConceptMapBuilder("141593ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(snomedMvp, "18141000105007").build())
                .mapping(new ConceptMapBuilder("137578ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(narrowerThan).ensureTerm(snomedNp, "184305005").build())
                .build());

        Concept probableCauseOfDeath = install(new ConceptBuilder("160218AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(text)
                .conceptClass(question)
                .name("108120BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Probable cause of death (text)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .mapping(new ConceptMapBuilder("caa6e803-c410-47cc-846b-8f84eb0b400e")
                        .type(sameAs).ensureTerm(pih, "9715").build())
                .mapping(new ConceptMapBuilder("217351ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "160218").build())
                .mapping(new ConceptMapBuilder("144080ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(narrowerThan).ensureTerm(snomedNp, "184305005").build())
                .build());

        install(new ConceptBuilder("1816AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("2096BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "CAUSES OF DEATH FROM DEATH CERTIFICATE", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("105850BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "CAUSES DU DÉCÈS SUR CERTIFICAT DE DÉCÈS", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("1626FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", "Ordered list of causes of death from death certificate", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("5111c9c3-0894-4b9f-9c03-92f3d8da6156")
                        .type(sameAs).ensureTerm(pih, "9714").build())
                .mapping(new ConceptMapBuilder("70211dda-7985-4489-be51-e19c58e7ae88")
                        .type(sameAs).ensureTerm(ciel, "1816").build())
                .mapping(new ConceptMapBuilder("137579ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(narrowerThan).ensureTerm(snomedNp, "184305005").build())
                .mapping(new ConceptMapBuilder("141594ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(snomedMvp, "18161000105006").build())
                .setMembers(
                        diagnosisSequenceNumber,
                        causeOfDeathFromCertificate,
                        probableCauseOfDeath)
                .build());

        install(new ConceptBuilder("9cb08a8b-19f2-49df-85f1-57c66ea63832")
                .datatype(coded)
                .conceptClass(question)
                .name("11109d13-834c-4d94-9895-14d44d41e6ce", "Visited provider during illness", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("69d27c06-165e-4de3-a122-4e980d112749", "Visité un personnel de santé au cours de maladie", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("f2006f9f-2ea3-41ba-a2ca-c24150f0ff0d", "Did the person visit a provider during the sickness?", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("d9a47600-36bb-4644-817c-8593d7051ad7")
                        .type(sameAs).ensureTerm(pih, "9671").build())
                .mapping(new ConceptMapBuilder("b9509e02-4b12-41bd-9e36-824548b3e85c")
                        .type(sameAs).ensureTerm(pih, "Visited provider during illness").build())
                .answers(yes, no)
                .build());

        Concept mortician = install(new ConceptBuilder("0b725999-8fa0-40e0-b140-a5dfac05403c")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("111496ab-31cc-4e9a-8bdb-b6f629fe13a3", "Mortician", Locale.ENGLISH, null) // locale-preferred
                .name("473f645d-cb32-490d-b306-560774d54dd7", "Funeral home staff", Locale.ENGLISH, null)
                .name("78f45753-4148-43a1-b904-aa9ef5773a96", "Undertaker", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("a98c1ca2-fef3-4818-9f82-2e614daa35f2", "Funeral director", Locale.ENGLISH, null)
                .name("3c72ec96-42ac-46ae-a8e7-c36f1895af18", "Pompe funèbres", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .mapping(new ConceptMapBuilder("d64695ce-a749-4ff8-894f-b0aba79c8431")
                        .type(sameAs).ensureTerm(pih, "Undertaker").build())
                .mapping(new ConceptMapBuilder("d0f017dc-0f18-4941-8f41-bd5769f49237")
                        .type(sameAs).ensureTerm(pih, "9673").build())
                .build());

        install(new ConceptBuilder("5112fbab-db4a-4e67-aa96-be1bfbc0c2d1")
                .datatype(coded)
                .conceptClass(question)
                .name("db2b86a9-d43c-458c-a985-327a814a67d0", "Source of information", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("b25ec3ad-8fc1-482a-b79d-8f5fab3feccd", "Source d'information", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .mapping(new ConceptMapBuilder("048b6945-c09d-4715-8984-e00b6ed78cca")
                        .type(sameAs).ensureTerm(pih, "9672").build())
                .mapping(new ConceptMapBuilder("9ef74eb4-d9e7-4a05-84bc-d1918113841e")
                        .type(sameAs).ensureTerm(pih, "Source of information").build())
                .answers(familyMember, police, mortician, otherNonCoded)
                .build());

        install(new ConceptBuilder("cf68b8c0-5d4d-4835-a40d-bc30e0cf5d95")
                .datatype(text)
                .conceptClass(question)
                .name("61b214fb-888f-4fa0-9edb-44fd872309be", "Burial certificate number", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("9d8755e8-be55-4749-bafe-b67974cb01a3", "Burial permit", Locale.ENGLISH, null)
                .name("6cfb0bea-d4bf-4e81-be3f-1a83000cf26a", "Burial certificate", Locale.ENGLISH, null)
                .name("d6f2eeb0-cc9e-4c22-abef-f40ddffad5ce", "Permit d'inhumer", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .mapping(new ConceptMapBuilder("3be725fb-2988-4415-b71f-89b30dcb75bb")
                        .type(sameAs).ensureTerm(pih, "Burial certificate").build())
                .mapping(new ConceptMapBuilder("b13384e0-fa9b-46b7-a57e-af989a606f45")
                        .type(sameAs).ensureTerm(pih, "9680").build())
                .build());

        install(new ConceptBuilder("fa03343d-2143-4769-b421-a018717cd13b")
                .datatype(coded)
                .conceptClass(diagnosis)
                .name("c8f588cc-133c-4c62-a04b-c347d8f31d68", "Condition contributing to the death", Locale.ENGLISH, null) // locale-preferred
                .name("75a26b39-c8e0-4b65-83ef-9b1f2be7161d", "Condition contributing to the death but not related to the disease or morbid condition", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("b6f07641-b175-437f-adde-438547a78981", "Condition contributing to the death but not related to the disease or morbid condition", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("cac289be-04e3-4dff-9f4d-d2201945f42d")
                        .type(sameAs).ensureTerm(pih, "9678").build())
                .mapping(new ConceptMapBuilder("1526b421-e9ab-4493-9682-6a620bf2bfb6")
                        .type(sameAs).ensureTerm(pih, "Condition contributing to the death").build())
                .build());

        Concept autopsy = install(new ConceptBuilder("7198e4f4-8bf0-4816-92cb-569b62bc2fef")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("e767ddbb-53fa-44f8-97b7-ed47e1c97e61", "Autopsy", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("0f3edffb-88cd-40f1-b9ab-9b960e9067b3", "Obduction", Locale.ENGLISH, null)
                .name("76943508-c269-4d73-8158-3e34964a1a49", "Post-mortem examination", Locale.ENGLISH, null)
                .name("d4db623c-55f8-4f0f-99a5-296423f19808", "Autopsia cadaverum", Locale.ENGLISH, null)
                .name("cee5fea8-93b5-4da1-8fc7-5d3343be03a0", "Autopsie", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("206bea47-900d-4393-8804-b629a442bccb", "An autopsy is a highly specialized surgical procedure that consists of a thorough examination of a corpse to determine the cause and manner of death and to evaluate any disease or injury that may be present. It is usually performed by a specialized medical doctor called a pathologist.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("8197c3be-2399-423d-81a0-4dc05d926f5c")
                        .type(sameAs).ensureTerm(pih, "Autopsy").build())
                .mapping(new ConceptMapBuilder("1aaa2d94-250b-4f3b-b3ac-fce9af8a4cf0")
                        .type(sameAs).ensureTerm(pih, "9682").build())
                .build());

        install(new ConceptBuilder("b3f04355-1e38-477e-9664-feda30c0aedb")
                .datatype(coded)
                .conceptClass(question)
                .name("e028e91c-3498-45b7-a2e5-f0fc0731e821", "Method of confirming death", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("00f0132f-4ef3-4ba9-bb35-c396777d673f", "Moyen de confirmation du diagnostic", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("a18206ce-c93b-4f5e-9cb9-f2d85f27ee89", "How was the cause of death confirmed?", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("a9752950-23bd-442a-bd8e-5bba63f44464")
                        .type(sameAs).ensureTerm(pih, "Method of confirming death").build())
                .mapping(new ConceptMapBuilder("5d355151-550e-4207-aa0a-b4dff5d15e82")
                        .type(sameAs).ensureTerm(pih, "9681").build())
                .answers(autopsy, surgery)
                .build());

        install(new ConceptBuilder("eeb48585-432a-48be-af84-c2f2b8c5b541")
                .datatype(text)
                .conceptClass(question)
                .name("9ff10896-e6c3-4c7c-9249-b430c1747031", "Circumstances of death", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("08bbc806-32dd-44fe-8e75-62310a912ab2", "Description of death by non-clinician", Locale.ENGLISH, null)
                .name("e7607abd-c2e6-44b2-980e-004759df2c20", "Circonstances du décès", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("ee223551-4f65-4f79-aab2-cfca818f691d", "Description of death from a non-medical background", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("349807f4-57b6-4351-bb09-292f547bdfd5")
                        .type(sameAs).ensureTerm(pih, "Circumstances of death").build())
                .mapping(new ConceptMapBuilder("b8f0135c-4d68-41b9-b8c5-eff1096e4108")
                        .type(sameAs).ensureTerm(pih, "9675").build())
                .build());

        install(new ConceptBuilder("c983f4d2-7e8d-4336-ac56-f4bec4f4cfc9")
                .datatype(coded)
                .conceptClass(question)
                .name("d1bea751-68b8-4b7f-830d-567456eb26ed", "Underlying cause of death", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("25582932-056b-4739-abbd-341bec6d5f0d", "Cause sous-jacente de la mort", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("304e9546-d0f7-4330-b980-45c3d7a09b02", "Line 4 of the death certificate", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("9c522d02-d6ac-4b19-b845-60ad844cda89")
                        .type(sameAs).ensureTerm(pih, "9679").build())
                .mapping(new ConceptMapBuilder("9d64cd23-866d-49d5-b52b-70112776b331")
                        .type(sameAs).ensureTerm(pih, "Underlying cause of death").build())
                .build());

        install(new ConceptBuilder("12a8fbd6-8ac9-4bb1-ac25-b3c25661ba85")
                .datatype(coded)
                .conceptClass(diagnosis)
                .name("c4b4dc79-72e4-45bd-b1d6-4da66939bc9e", "Condition causing the final disease", Locale.ENGLISH, null) // locale-preferred
                .name("3e5204bb-fabd-4c96-9ac1-c57b8258d150", "Condition which caused the final disease or condition", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("b9ff3a12-8bc9-4c45-9575-2e48da83b573", "Affectations morbides ayant éventuellement conduit a l'état préciser l'affectation initiale étant indiquée en dernier lieu", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("68bb8153-da6d-4e19-a4ad-2169f8896857", "Conditions qui causent des maladies finale", Locale.FRENCH, null)
                .description("700f71f8-336d-4b08-b648-b8baecfd96cc", "The 2nd line on the death certificate", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("366d1dc5-3d60-492c-8823-3ca6c03d26fd")
                        .type(sameAs).ensureTerm(pih, "9677").build())
                .mapping(new ConceptMapBuilder("c500e3a2-2252-4efd-85c7-ce2ae1a792fa")
                        .type(sameAs).ensureTerm(pih, "Condition causing the final disease").build())
                .build());

        install(new ConceptNumericBuilder("6e656477-72e2-4ecf-a777-0c488c4a14e2")
                .units("days")
                .precise(false)
                .datatype(numeric)
                .conceptClass(question)
                .name("c2424b53-cc4b-415c-998a-1c5365c3af9d", "Duration of hospitalization during which patient died, in days", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .description("da7384c7-29fa-4806-af62-5a3681cfa34a", "If the patient died while hospitalized, then this concept represents the duration of that hospitalization, in days", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("ce3e74e7-53d8-4e73-88ab-8191ab5c1aa1")
                    .type(sameAs).ensureTerm(pih, "Duration of hospitalization when patient died").build())
                .mapping(new ConceptMapBuilder("8334471b-ba18-453a-8ce8-747911c01e06")
                    .type(sameAs).ensureTerm(pih, "9710").build())
                .build());

        // Update Death-related Global Properties
        setGlobalProperty("concept.causeOfDeath", causeOfDeathFromCertificate.getId().toString());
    }

}
