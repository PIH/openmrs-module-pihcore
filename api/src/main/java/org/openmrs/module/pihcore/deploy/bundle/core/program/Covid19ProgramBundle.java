package org.openmrs.module.pihcore.deploy.bundle.core.program;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.pihcore.metadata.core.program.Covid19Program;
import org.springframework.stereotype.Component;

@Component
public class Covid19ProgramBundle  extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
        install(Covid19Program.COVID19);
    }
}
