package org.openmrs.module.pihcore.deploy.bundle.core.program;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.pihcore.metadata.core.program.ANCProgram;
import org.springframework.stereotype.Component;

@Component
public class ANCProgramBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
        log.info("Installing ANC program");
        install(ANCProgram.ANC);
    }
}
