package org.openmrs.module.pihcore.deploy.bundle.core.program;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.pihcore.metadata.core.program.MCHProgram;
import org.springframework.stereotype.Component;

@Component
public class MCHProgramBundleZL extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
        log.info("Installing ZL MCH program");
        install(MCHProgram.MCH);
    }
}
