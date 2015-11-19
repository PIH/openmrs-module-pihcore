package org.openmrs.module.pihcore.deploy.bundle.core;

import org.openmrs.customdatatype.datatype.FreeTextDatatype;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.springframework.stereotype.Component;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.visitAttributeType;

@Component
public class VisitAttributeTypeBundle extends AbstractMetadataBundle {

    public static final class VisitAttributeTypes {
        public static final String VISIT_TEMPLATE = "f7b07c80-27c3-49de-8830-cb9e3e805eeb";
    }

    @Override
    public void install() throws Exception {
        install(visitAttributeType("Visit Template", "Describes how the visit dashboard should be laid out",
                FreeTextDatatype.class, null, 0, 1, VisitAttributeTypes.VISIT_TEMPLATE));
    }
}
