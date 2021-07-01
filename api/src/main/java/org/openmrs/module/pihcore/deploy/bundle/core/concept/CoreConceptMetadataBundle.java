package org.openmrs.module.pihcore.deploy.bundle.core.concept;

import org.openmrs.ConceptDatatype;
import org.openmrs.module.metadatadeploy.bundle.VersionedMetadataBundle;
import org.springframework.stereotype.Component;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.conceptClass;

@Component
public class CoreConceptMetadataBundle extends VersionedMetadataBundle {

    /* Update this file (and version) with great caution
     *  This metadata is in the PIH concepts server and in mds packages
     * */
    @Override
    public int getVersion() {
        return 8;
    }

    // These Constants should be removed once we are no longer creating concepts in code.  If we still need to refer
    // to them at that point, we should use constants setup in openmrs-config-pihemr
    public static final class ConceptSources {
        public static final String LOINC = "2889f378-f287-40a5-ac9c-ce77ee963ed7";
        public static final String CIEL = "21ADDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD";
        public static final String PIH = "fb9aaaf1-65e2-4c18-b53c-16b575f2f385";
        public static final String PIH_MALAWI = "12ADDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD";
        public static final String SNOMED_CT = "1ADDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD";
        public static final String SNOMED_NP = "2ADDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD";
        public static final String SNOMED_MVP = "14ADDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD";
        public static final String RX_NORM = "4ADDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD";
        public static final String IMO_PROCEDURE_IT = "25ADDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD";
        public static final String IMO_PROBLEM_IT = "24ADDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD";
        public static final String EMRAPI_MODULE = "edd52713-8887-47b7-ba9e-6e1148824ca4";
        public static final String MDRTB_MODULE = "ddb6b595-0b85-4a80-9243-efe4ba404eef";
        public static final String AMPATH = "13ADDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD";
        public static final String ICD10 = "3f65bd34-26fe-102b-80cb-0017a47871b2";
        public static final String ICD11 = "39ADDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD";
        public static final String HUM_REPORTS_MODULE = "947a1410-1987-4399-8017-c1ea70f242d1";
    }

    // these exist already, we don't create them
    public static final class ConceptDatatypes {
        public static final String N_A = ConceptDatatype.N_A_UUID;
        public static final String CODED = ConceptDatatype.CODED_UUID;
        public static final String BOOLEAN = ConceptDatatype.BOOLEAN_UUID;
        public static final String TEXT = ConceptDatatype.TEXT_UUID;
        public static final String NUMERIC = ConceptDatatype.NUMERIC_UUID;
        public static final String DATETIME = ConceptDatatype.DATETIME_UUID;
        public static final String DATE = ConceptDatatype.DATE_UUID;
        public static final String TIME = ConceptDatatype.TIME_UUID;
    }

    public static final class ConceptClasses {
        public static final String MISC = "8d492774-c2cc-11de-8d13-0010c6dffd0f";
        public static final String DIAGNOSIS = "8d4918b0-c2cc-11de-8d13-0010c6dffd0f";
        public static final String SYMPTOM = "8d492954-c2cc-11de-8d13-0010c6dffd0f";
        public static final String QUESTION = "8d491e50-c2cc-11de-8d13-0010c6dffd0f";
        public static final String CONV_SET = "8d492594-c2cc-11de-8d13-0010c6dffd0f";
        public static final String MED_SET = "8d4923b4-c2cc-11de-8d13-0010c6dffd0f";
        public static final String FINDING = "8d491a9a-c2cc-11de-8d13-0010c6dffd0f";
        public static final String DRUG = "8d490dfc-c2cc-11de-8d13-0010c6dffd0f";
        public static final String TEST = "8d4907b2-c2cc-11de-8d13-0010c6dffd0f";
        public static final String PROCEDURE = "8d490bf4-c2cc-11de-8d13-0010c6dffd0f";
        public static final String FREQUENCY = "8e071bfe-520c-44c0-a89b-538e9129b42a";
    }

    // these exist already, we don't create them
    public static final class ConceptMapTypes {
        public static final String SAME_AS = "35543629-7d8c-11e1-909d-c80aa9edcf4e";
        public static final String NARROWER_THAN = "43ac5109-7d8c-11e1-909d-c80aa9edcf4e";
        public static final String BROADER_THAN = "4b9d9421-7d8c-11e1-909d-c80aa9edcf4e";
    }

    @Override
    protected void installEveryTime() throws Exception {
        // nothing
    }

    @Override
    protected void installNewVersion() throws Exception {
        install(conceptClass("Misc", "Terms which don't fit other categories", ConceptClasses.MISC));
        install(conceptClass("Diagnosis", "Conclusion drawn through findings", ConceptClasses.DIAGNOSIS));
        install(conceptClass("Symptom", "Patient-reported observation", ConceptClasses.SYMPTOM));
        install(conceptClass("Question", "Question (eg, patient history, SF36 items)", ConceptClasses.QUESTION));
        install(conceptClass("ConvSet", "Term to describe convenience sets", ConceptClasses.CONV_SET));
        install(conceptClass("MedSet", "Term to describe medication sets", ConceptClasses.MED_SET));
        install(conceptClass("Finding", "Practitioner observation/finding", ConceptClasses.FINDING));
        install(conceptClass("Drug", "Drug", ConceptClasses.DRUG));
        install(conceptClass("Test", "Acq. during patient encounter (vitals, labs, etc.)", ConceptClasses.TEST));
        install(conceptClass("Procedure", "Describes a clinical procedure", ConceptClasses.PROCEDURE));
        install(conceptClass("Frequency", "A concept used for capturing frequency information such as for medication ordering.", ConceptClasses.FREQUENCY));
    }

}
