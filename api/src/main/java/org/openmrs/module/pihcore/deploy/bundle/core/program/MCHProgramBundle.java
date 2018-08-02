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

        // This is the important part
        log.info("Installing MCH program");
        install(MCHProgram.MCH);

        // ToDo:  Remove these after it cleans up on my local and all the deployments
/*
        log.info("Retiring unused MCH states and workflows");
        uninstall(possible(ProgramWorkflowState.class, MCHProgram.GROUP1.uuid()), "no longer used");
        uninstall(possible(ProgramWorkflowState.class, MCHProgram.GROUP2.uuid()), "no longer used");
        uninstall(possible(ProgramWorkflowState.class, MCHProgram.GROUP3.uuid()), "no longer used");
        uninstall(possible(ProgramWorkflowState.class, MCHProgram.GROUP4.uuid()), "no longer used");
        uninstall(possible(ProgramWorkflowState.class, MCHProgram.GROUP5.uuid()), "no longer used");
        uninstall(possible(ProgramWorkflowState.class, MCHProgram.GROUP6.uuid()), "no longer used");
        uninstall(possible(ProgramWorkflowState.class, MCHProgram.GROUP7.uuid()), "no longer used");
        uninstall(possible(ProgramWorkflowState.class, MCHProgram.GROUP8.uuid()), "no longer used");
        uninstall(possible(ProgramWorkflowState.class, MCHProgram.GROUP9.uuid()), "no longer used");
        uninstall(possible(ProgramWorkflowState.class, MCHProgram.GROUP10.uuid()), "no longer used");
        uninstall(possible(ProgramWorkflowState.class, MCHProgram.GROUP11.uuid()), "no longer used");
        uninstall(possible(ProgramWorkflowState.class, MCHProgram.GROUP12.uuid()), "no longer used");
        uninstall(possible(ProgramWorkflow.class, MCHProgram.MOTHER_GROUP.uuid()), "no longer used");

        uninstall(possible(ProgramWorkflowState.class, MCHProgram.GROUPC1.uuid()), "no longer used");
        uninstall(possible(ProgramWorkflowState.class, MCHProgram.GROUPC2.uuid()), "no longer used");
        uninstall(possible(ProgramWorkflowState.class, MCHProgram.GROUPC3.uuid()), "no longer used");
        uninstall(possible(ProgramWorkflowState.class, MCHProgram.GROUPC4.uuid()), "no longer used");
        uninstall(possible(ProgramWorkflowState.class, MCHProgram.GROUPC5.uuid()), "no longer used");
        uninstall(possible(ProgramWorkflowState.class, MCHProgram.GROUPC6.uuid()), "no longer used");
        uninstall(possible(ProgramWorkflowState.class, MCHProgram.GROUPC7.uuid()), "no longer used");
        uninstall(possible(ProgramWorkflowState.class, MCHProgram.GROUPC8.uuid()), "no longer used");
        uninstall(possible(ProgramWorkflowState.class, MCHProgram.GROUPC9.uuid()), "no longer used");
        uninstall(possible(ProgramWorkflowState.class, MCHProgram.GROUPC10.uuid()), "no longer used");
        uninstall(possible(ProgramWorkflowState.class, MCHProgram.GROUPC11.uuid()), "no longer used");
        uninstall(possible(ProgramWorkflowState.class, MCHProgram.GROUPC12.uuid()), "no longer used");
        uninstall(possible(ProgramWorkflow.class, MCHProgram.CHILD_GROUP.uuid()), "no longer used");

        // uninstall(possible(Program.class,MCHProgram.MCH.uuid()), "need to reset");
        */

    }
}
