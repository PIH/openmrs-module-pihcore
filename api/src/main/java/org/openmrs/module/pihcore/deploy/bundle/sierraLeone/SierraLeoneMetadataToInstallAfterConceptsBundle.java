package org.openmrs.module.pihcore.deploy.bundle.sierraLeone;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.core.program.MCHProgramBundle;
import org.openmrs.module.pihcore.deploy.bundle.core.program.MentalHealthProgramBundle;
import org.springframework.stereotype.Component;

// this is a separate bundle that needs to be installed via the Mirebalais Metadata module *after*
// concepts have been installed via metadata sharing (since programs reference concepts)
// TODO: incorporate all PIH metadata installation into this module, remove Mirebalais Metadata module?
@Component
@Requires( {
        MCHProgramBundle.class,
        MentalHealthProgramBundle.class
} )
public class SierraLeoneMetadataToInstallAfterConceptsBundle extends AbstractMetadataBundle {
    @Override
    public void install() throws Exception {

    }
}
