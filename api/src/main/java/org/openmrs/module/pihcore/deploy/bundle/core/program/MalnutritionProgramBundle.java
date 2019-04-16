package org.openmrs.module.pihcore.deploy.bundle.core.program;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.pihcore.metadata.core.program.DiabetesProgram;
import org.openmrs.module.pihcore.metadata.core.program.MalnutritionProgram;
import org.springframework.stereotype.Component;

@Component
public class MalnutritionProgramBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
        install(MalnutritionProgram.MALNUTRITION);
    }
}
