package org.openmrs.module.pihcore.deploy.bundle.haiti;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.core.program.Covid19ProgramBundle;
import org.openmrs.module.pihcore.deploy.bundle.core.program.MCHProgramBundleZL;
import org.openmrs.module.pihcore.deploy.bundle.core.program.PihProgramsBundle;
import org.springframework.stereotype.Component;


// this is a separate bundle that needs to be installed via the Mirebalais Metadata module *after*
// concepts have been installed via metadata sharing (since programs reference concepts)
// TODO: incorporate all PIH metadata installation into this module, remove Mirebalais Metadata module?
@Component
@Requires( {
    OrderEntryConcepts.class,
    PihProgramsBundle.class,
    MCHProgramBundleZL.class,
    Covid19ProgramBundle.class
} )
public class HaitiMetadataToInstallAfterConceptsBundle extends AbstractMetadataBundle {
    @Override
    public void install() throws Exception {

    }
}
