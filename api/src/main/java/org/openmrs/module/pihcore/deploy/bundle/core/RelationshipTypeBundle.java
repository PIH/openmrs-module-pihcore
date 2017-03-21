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
    }
    /**
     * Performs the installation of the metadata items
     *
     * @throws Exception if an error occurs
     */
    @Override
    public void install() throws Exception {
        install(relationshipType("Community Health Worker", "Patient", "CHW to Patient relationship", RelationshipTypes.CHW_TO_PATIENT));
    }
}
