package org.openmrs.module.pihcore.deploy.bundle.haiti.hsn;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.haiti.HaitiMetadataBundle;
import org.openmrs.module.pihcore.metadata.haiti.hsn.HSNLocations;
import org.springframework.stereotype.Component;

@Component
@Requires({ HaitiMetadataBundle.class })
public class HSNLocationsBundle extends AbstractMetadataBundle {
    @Override
    public void install() throws Exception {
        install(HSNLocations.COVID19_ISOLATION_HSN);
        install(HSNLocations.COVID19_UMI_HSN);
        install(HSNLocations.SANTE_MANTAL_HSN);
    }
}
