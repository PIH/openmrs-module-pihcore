package org.openmrs.module.pihcore.deploy.bundle.haiti.mirebalais;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.haiti.HaitiMetadataBundle;
import org.springframework.stereotype.Component;

@Component
@Requires({ HaitiMetadataBundle.class, MirebalaisLocationsBundle.class, MirebalaisRadiologyBundle.class })
public class MirebalaisMetadataBundle extends AbstractMetadataBundle {
    @Override
    public void install() throws Exception {

    }
}
