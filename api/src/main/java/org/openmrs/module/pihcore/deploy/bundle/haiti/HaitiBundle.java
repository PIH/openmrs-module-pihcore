package org.openmrs.module.pihcore.deploy.bundle.haiti;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.core.PihCoreMetadataBundle;
import org.springframework.stereotype.Component;

/**
 * Parent Bundle that @Requires all bundles requires for Haiti implementations
 */
@Component
@Requires( { PihCoreMetadataBundle.class,
        OrderEntryConcepts.class,
        // TestOrderConcepts.class,  I believe we are now installing these via MDS
        HaitiLocationsBundle.class,
        HaitiPropertiesAndMappingsBundle.class,
        HaitiPatientIdentifierTypeBundle.class,
        HaitiAddressBundle.class
} )
public class HaitiBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {

    }
}
