package org.openmrs.module.pihcore.deploy.bundle.liberia;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.core.program.PihProgramsBundle;
import org.openmrs.module.pihcore.deploy.bundle.haiti.OrderEntryConcepts;
import org.springframework.stereotype.Component;

// this is a separate bundle that needs to be installed via the Mirebalais Metadata module *after*
// concepts have been installed via metadata sharing (since programs reference concepts)
// TODO: incorporate all PIH metadata installation into this module, remove Mirebalais Metadata module?
@Component
@Requires( { PihProgramsBundle.class, OrderEntryConcepts.class  } )
public class LiberiaMetadataToInstallAfterConceptsBundle extends AbstractMetadataBundle {
    @Override
    public void install() throws Exception {

    }
}
