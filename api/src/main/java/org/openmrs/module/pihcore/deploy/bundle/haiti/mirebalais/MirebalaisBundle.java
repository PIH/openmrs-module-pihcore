package org.openmrs.module.pihcore.deploy.bundle.haiti.mirebalais;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.haiti.HaitiBundle;
import org.springframework.stereotype.Component;

@Component
@Requires({ HaitiBundle.class, MirebalaisLocationsBundle.class, MirebalaisRadiologyBundle.class })
public class MirebalaisBundle extends AbstractMetadataBundle {
    @Override
    public void install() throws Exception {

    }
}
