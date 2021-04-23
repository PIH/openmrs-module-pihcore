package org.openmrs.module.pihcore.deploy.bundle.haiti.hsn;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.haiti.HaitiMetadataBundle;
import org.springframework.stereotype.Component;

@Component
@Requires({ HaitiMetadataBundle.class })
public class HSNMetadataBundle extends AbstractMetadataBundle {
    @Override
    public void install() throws Exception {

    }
}
