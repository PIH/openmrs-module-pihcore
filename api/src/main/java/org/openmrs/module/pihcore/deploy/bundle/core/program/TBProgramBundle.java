package org.openmrs.module.pihcore.deploy.bundle.core.program;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.pihcore.metadata.core.program.TBProgram;
import org.springframework.stereotype.Component;

@Component
public class TBProgramBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
        log.info("Installing TB program");
        install(TBProgram.TB);
    }
}