package org.openmrs.module.pihcore.deploy.bundle.core.program;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.pihcore.metadata.core.program.EpilepsyProgram;
import org.springframework.stereotype.Component;

@Component
public class EpilepsyProgramBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
        install(EpilepsyProgram.EPILEPSY);
    }
}
