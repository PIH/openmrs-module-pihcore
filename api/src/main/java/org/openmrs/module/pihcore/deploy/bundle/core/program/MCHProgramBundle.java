package org.openmrs.module.pihcore.deploy.bundle.core.program;

import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowDescriptor;
import org.openmrs.module.pihcore.metadata.core.program.MCHProgram;
import org.springframework.stereotype.Component;

@Component
public class MCHProgramBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
        log.info("Installing MCH program");
        install(MCHProgram.MCH);
    }
}
