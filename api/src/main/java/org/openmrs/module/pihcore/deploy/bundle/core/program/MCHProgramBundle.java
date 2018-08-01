package org.openmrs.module.pihcore.deploy.bundle.core.program;

import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowDescriptor;
import org.openmrs.module.pihcore.metadata.core.program.MCHProgram;
import org.springframework.stereotype.Component;

@Component
public class MCHProgramBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
        // uninstall(possible(ProgramWorkflow.class, MCHProgram.CHILD_GROUP.uuid()), "no longer used");
        // uninstall(possible(ProgramWorkflow.class, MCHProgram.MOTHER_GROUP.uuid()), "no longer used");
        install(MCHProgram.MCH);
    }
}
