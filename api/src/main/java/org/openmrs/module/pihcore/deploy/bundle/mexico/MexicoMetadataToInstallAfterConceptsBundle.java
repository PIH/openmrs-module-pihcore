package org.openmrs.module.pihcore.deploy.bundle.mexico;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.core.program.PihProgramsBundle;
import org.springframework.stereotype.Component;


// this is a separate bundle that needs to be installed via the Mirebalais Metadata module *after*
// concepts have been installed via metadata sharing (since programs reference concepts)
@Component
@Requires( { PihProgramsBundle.class } )
public class MexicoMetadataToInstallAfterConceptsBundle extends AbstractMetadataBundle {
    @Override
    public void install() throws Exception {

    }
}
