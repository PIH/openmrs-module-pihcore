package org.openmrs.module.pihcore.deploy.bundle.core.program;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.pihcore.metadata.core.program.HIVProgram;
import org.springframework.stereotype.Component;

@Component
public class HIVProgramBundle  extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
        install(HIVProgram.HIV);
    }
}
