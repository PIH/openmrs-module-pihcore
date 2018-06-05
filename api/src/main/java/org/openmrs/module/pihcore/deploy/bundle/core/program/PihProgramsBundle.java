package org.openmrs.module.pihcore.deploy.bundle.core.program;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.springframework.stereotype.Component;

@Component
@Requires({ HIVProgramBundle.class, NCDProgramBundle.class, OncologyProgramBundle.class, ZikaProgramBundle.class, MentalHealthProgramBundle.class} )
public class PihProgramsBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
    }
}
