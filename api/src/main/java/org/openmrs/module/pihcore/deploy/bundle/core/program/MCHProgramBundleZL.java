package org.openmrs.module.pihcore.deploy.bundle.core.program;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.metadata.core.program.MCHProgramZL;
import org.springframework.stereotype.Component;

@Component
@Requires( { MCHProgramBundle.class } )
public class MCHProgramBundleZL extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
        log.info("Installing ZL MCH program");
        install(MCHProgramZL.MCH);
    }
}
