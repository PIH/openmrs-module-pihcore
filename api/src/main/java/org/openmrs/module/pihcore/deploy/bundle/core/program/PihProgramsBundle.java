package org.openmrs.module.pihcore.deploy.bundle.core.program;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.springframework.stereotype.Component;

@Component
@Requires({ HypertensionProgramBundle.class, HIVProgramBundle.class, MentalHealthProgramBundle.class,
        MCHProgramBundle.class, NCDProgramBundle.class, OncologyProgramBundle.class, ZikaProgramBundle.class })
public class PihProgramsBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
    }
}
