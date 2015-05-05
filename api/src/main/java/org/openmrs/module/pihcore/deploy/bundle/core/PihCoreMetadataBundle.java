package org.openmrs.module.pihcore.deploy.bundle.core;

import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.PihMetadataBundle;
import org.springframework.stereotype.Component;

/**
 * Consolidates all core non-concept metadata into a single required set
 */

@Component
@Requires(
        { EncounterRoleBundle.class,
        EncounterTypeBundle.class,
        GlobalPropertiesBundle.class,
        LocationAttributeTypeBundle.class,
        LocationBundle.class,
        LocationTagBundle.class,
        OrderTypeBundle.class,
        PersonAttributeTypeBundle.class,
        RolesAndPrivilegesBundle.class,
        VisitTypeBundle.class } )
public class PihCoreMetadataBundle extends PihMetadataBundle {
    @Override
    public void install() throws Exception {

    }
}
