package org.openmrs.module.pihcore.deploy.bundle.haiti;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.pihcore.metadata.haiti.mirebalais.PihHaitiPrograms;
import org.springframework.stereotype.Component;

@Component
public class PihHaitiProgramsBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
        install(PihHaitiPrograms.ZIKA);
        install(PihHaitiPrograms.HIV);
    }
}
