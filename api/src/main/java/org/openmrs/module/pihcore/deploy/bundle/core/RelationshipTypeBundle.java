package org.openmrs.module.pihcore.deploy.bundle.core;


import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.springframework.stereotype.Component;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.relationshipType;

/**
 * Installs the relationship types required by the VHW module
 */
@Component
public class RelationshipTypeBundle extends AbstractMetadataBundle {

    public static final class RelationshipTypes {
        public static final String CHW_TO_PATIENT = "eb567be2-fda1-4746-9d51-833de8a7e81f";
        public static final String SPOUSE_PARTNER = "C8CDE783-00EB-4FEA-9E4B-A2BDCAD8D33E";
        public static final String NURSE_CHW_PATIENT = "9a4b3eea-8a9f-11e8-9a94-a6cf71072f73";
//      public static final String MOTHER_CHILD = "9a4b3b84-8a9f-11e8-9a94-a6cf71072f73";
    }

    /**
     * Performs the installation of the metadata items
     *
     * @throws Exception if an error occurs
     */
    @Override
    public void install() throws Exception {
        install(relationshipType("Community Health Worker", "Patient", "CHW to Patient relationship", RelationshipTypes.CHW_TO_PATIENT));
        install(relationshipType("Spouse/Partner", "Spouse/Partner", "Spouse/Partner relationship", RelationshipTypes.SPOUSE_PARTNER));
        install(relationshipType("Nurse accompagnateur", "Patient", "Nurse accompagnateur to Patient relationship", RelationshipTypes.NURSE_CHW_PATIENT));
//      install(relationshipType("Mother", "Child", "Mother/Child relationship", RelationshipTypes.MOTHER_CHILD));

    }
}
