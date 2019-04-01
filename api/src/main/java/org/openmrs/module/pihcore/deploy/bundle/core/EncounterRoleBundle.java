package org.openmrs.module.pihcore.deploy.bundle.core;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.springframework.stereotype.Component;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.encounterRole;

@Component
public class EncounterRoleBundle extends AbstractMetadataBundle {


    public static final class EncounterRoles {
        // note that there is an UNKNOWN role defined in code
        public static final String DISPENSER = "bad21515-fd04-4ff6-bfcd-78456d12f168";
        public static final String NURSE = "98bf2792-3f0a-4388-81bb-c78b29c0df92";
        public static final String NURSE_MIDWIFE = "1949e6f8-fd80-11e8-8eb2-f2801f1b9fd1";
        public static final String CONSULTING_CLINICIAN = "4f10ad1a-ec49-48df-98c7-1391c6ac7f05";
        public static final String ADMINISTRATIVE_CLERK = "cbfe0b9d-9923-404c-941b-f048adc8cdc0";
        public static final String ORDERING_PROVIDER = "c458d78e-8374-4767-ad58-9f8fe276e01c";
        public static final String ATTENDING_SURGEON = "9b135b19-7ebe-4a51-aea2-69a53f9383af";
        public static final String ANESTHESIOLOGIST = "de11b25c-a641-4630-9524-5b85ece9a4f8";
        public static final String ASSISTING_SURGEON = "6e630e03-5182-4cb3-9a82-a5b1a85c09a7";

        // TODO are we actually going to use this role?
        public static final String LAB_TECHNICIAN = "781bee46-89c5-4230-a490-68fe21d61383";
    }

    @Override
    public void install() throws Exception {

        install(encounterRole("Dispenser",
                "Provider that dispenses medications or other products",
                EncounterRoles.DISPENSER));

        install(encounterRole("Nurse",
                "A person educated and trained to care for the sick or disabled.",
                EncounterRoles.NURSE));


        install(encounterRole("Nurse Midwife",
                "A person educated and trained in midwifery.",
                EncounterRoles.NURSE_MIDWIFE));

        install(encounterRole("Consulting Clinician",
                "Clinician who is primarily responsible for examining and diagnosing a patient",
                EncounterRoles.CONSULTING_CLINICIAN));

        install(encounterRole("Administrative Clerk",
                "This role is used for creating a Check-in encounter",
                EncounterRoles.ADMINISTRATIVE_CLERK));

        install(encounterRole("Ordering Provider",
                "For encounters associated with orders, used to store the provider responsible for placing the order",
                EncounterRoles.ORDERING_PROVIDER));

        install(encounterRole("Attending Surgeon",
                "The surgeon with primary responsibility for the care of a patient in a particular surgery.",
                EncounterRoles.ATTENDING_SURGEON));

        install(encounterRole("Anesthesiologist",
                "Anesthesiologists are physicians who delivering anesthesia during surgical procedures, care for critically ill patients in an ICU, manage medical emergencies, and pain management.",
                EncounterRoles.ANESTHESIOLOGIST));

        install(encounterRole("Assisting Surgeon",
                "Additional surgeons (beyond the Attending Surgeon) who assist in a surgery.",
                EncounterRoles.ASSISTING_SURGEON));

        install(encounterRole("Lab Technician",
                "Staff member performing lab tests.",
                EncounterRoles.LAB_TECHNICIAN));
    }

}
