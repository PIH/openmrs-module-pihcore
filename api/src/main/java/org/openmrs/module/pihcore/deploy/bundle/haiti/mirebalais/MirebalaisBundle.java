package org.openmrs.module.pihcore.deploy.bundle.haiti.mirebalais;

import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.PihMetadataBundle;
import org.springframework.stereotype.Component;

@Component
@Requires({ MirebalaisLocationsBundle.class, MirebalaisRadiologyBundle.class })
public class MirebalaisBundle extends PihMetadataBundle{
    @Override
    public void install() throws Exception {

    }
}
