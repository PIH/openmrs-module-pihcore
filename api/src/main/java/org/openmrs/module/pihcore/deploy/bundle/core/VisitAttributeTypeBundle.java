package org.openmrs.module.pihcore.deploy.bundle.core;

import org.openmrs.VisitAttributeType;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.springframework.stereotype.Component;

@Component
public class VisitAttributeTypeBundle extends AbstractMetadataBundle {

    public static final class VisitAttributeTypes {
        public static final String VISIT_TEMPLATE = "f7b07c80-27c3-49de-8830-cb9e3e805eeb";
    }

    @Override
    public void install() throws Exception {
        uninstall(possible(VisitAttributeType.class, VisitAttributeTypes.VISIT_TEMPLATE), "no longer using this");
    }
}
