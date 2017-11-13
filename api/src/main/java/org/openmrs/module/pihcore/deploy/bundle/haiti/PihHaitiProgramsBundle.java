package org.openmrs.module.pihcore.deploy.bundle.haiti;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.core.program.HIVProgramBundle;
import org.openmrs.module.pihcore.deploy.bundle.core.program.NCDProgramBundle;
import org.openmrs.module.pihcore.deploy.bundle.core.program.ZikaProgramBundle;
import org.springframework.stereotype.Component;

@Component
@Requires({ HIVProgramBundle.class, NCDProgramBundle.class, ZikaProgramBundle.class} )
public class PihHaitiProgramsBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
    }
}
