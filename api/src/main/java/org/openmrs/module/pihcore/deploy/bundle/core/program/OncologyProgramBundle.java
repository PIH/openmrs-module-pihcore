package org.openmrs.module.pihcore.deploy.bundle.core.program;

import org.openmrs.ProgramWorkflow;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.pihcore.metadata.core.program.OncologyProgram;
import org.springframework.stereotype.Component;

@Component
public class OncologyProgramBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
        // ToDo: Clear out the old unused states
        // uninstall(possible(ProgramWorkflow.class, OncologyProgram.ONCOLOGY_PROGRESS_STATUS.uuid()), "refresh");
        install(OncologyProgram.ONCOLOGY);
    }
}
