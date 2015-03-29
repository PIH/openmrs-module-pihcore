package org.openmrs.module.pihcore.deploy.bundle;

import org.openmrs.ConceptDatatype;
import org.springframework.stereotype.Component;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.conceptClass;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.conceptSource;

@Component
public class CoreConceptMetadataBundle extends VersionedPihMetadataBundle {

    @Override
    public int getVersion() {
        return 1;
    }

    public static final class ConceptSources {
        public static final String LOINC = "2889f378-f287-40a5-ac9c-ce77ee963ed7";
        public static final String CIEL = "21ADDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD";
        public static final String PIH = "fb9aaaf1-65e2-4c18-b53c-16b575f2f385";
        public static final String PIH_MALAWI = "12ADDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD";
        public static final String SNOMED_CT = "1ADDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD";
        public static final String SNOMED_NP = "2ADDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD";
        public static final String SNOMED_MVP = "14ADDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD";
        public static final String EMRAPI_MODULE = "edd52713-8887-47b7-ba9e-6e1148824ca4";
        public static final String MDRTB_MODULE = "ddb6b595-0b85-4a80-9243-efe4ba404eef";
        public static final String AMPATH = "13ADDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD";
    }

    // these exist already, we don't create them
    public static final class ConceptDatatypes {
        public static final String N_A = ConceptDatatype.N_A_UUID;
        public static final String CODED = ConceptDatatype.CODED_UUID;
        public static final String TEXT = ConceptDatatype.TEXT_UUID;
        public static final String NUMERIC = ConceptDatatype.NUMERIC_UUID;
        public static final String DATETIME = ConceptDatatype.DATETIME_UUID;
        public static final String DATE = ConceptDatatype.DATE_UUID;
        public static final String TIME = ConceptDatatype.TIME_UUID;
    }

    public static final class ConceptClasses {
        public static final String MISC = "8d492774-c2cc-11de-8d13-0010c6dffd0f";
        public static final String DIAGNOSIS = "8d4918b0-c2cc-11de-8d13-0010c6dffd0f";
        public static final String QUESTION = "8d491e50-c2cc-11de-8d13-0010c6dffd0f";
        public static final String CONV_SET = "8d492594-c2cc-11de-8d13-0010c6dffd0f";
        public static final String FINDING = "8d491a9a-c2cc-11de-8d13-0010c6dffd0f";
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
        install(conceptClass("Question", "Question (eg, patient history, SF36 items)", ConceptClasses.QUESTION));
        install(conceptClass("ConvSet", "Term to describe convenience sets", ConceptClasses.CONV_SET));
        install(conceptClass("Finding", "Practitioner observation/finding", ConceptClasses.FINDING));

        install(conceptSource("PIH", "Partners In Health concept dictionary using concept ids and preferred English names", null, ConceptSources.PIH));
        install(conceptSource("PIH Malawi", "Partners in Health Malawi concept dictionary", null, ConceptSources.PIH_MALAWI)); //
        install(conceptSource("CIEL", "Columbia International eHealth Laboratory concept ID", null, ConceptSources.CIEL));
        install(conceptSource("SNOMED CT", "SNOMED Preferred mapping", null, ConceptSources.SNOMED_CT));
        install(conceptSource("SNOMED MVP", "MVP Namespace Identifier extensions to SNOMED CT", null, ConceptSources.SNOMED_MVP));
        install(conceptSource("SNOMED NP", "Non-preferred SNOMED CT mappings", null, ConceptSources.SNOMED_NP));
        install(conceptSource("AMPATH", "AMPATH concept dictionary", null, ConceptSources.AMPATH));
        install(conceptSource("LOINC", "A universal code system for identifying laboratory and clinical observations.", null, ConceptSources.LOINC));
        install(conceptSource("org.openmrs.module.emrapi", "Source used to tag concepts used in the emr-api module", null, ConceptSources.EMRAPI_MODULE));
        install(conceptSource("org.openmrs.module.mdrtb", "Concepts used with the MDR-TB module", null, ConceptSources.MDRTB_MODULE));
    }

}
