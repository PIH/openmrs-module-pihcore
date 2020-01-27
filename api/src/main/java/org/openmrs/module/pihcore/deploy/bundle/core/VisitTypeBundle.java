package org.openmrs.module.pihcore.deploy.bundle.core;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.springframework.stereotype.Component;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.visitType;

@Component
public class VisitTypeBundle extends AbstractMetadataBundle {

    public static final class VisitTypes {
        public static final String CLINIC_OR_HOSPITAL_VISIT = "f01c54cb-2225-471a-9cd5-d348552c337c";
        public static final String HOME_VISIT = "90973824-1AE9-4E22-B2BB-9CBD56FB3238";
    }

    @Override
    public void install() throws Exception {
        install(visitType("Clinic or Hospital Visit", "Patient visits the clinic/hospital (as opposed to a home visit, or telephone contact)", VisitTypes.CLINIC_OR_HOSPITAL_VISIT));

        install(visitType("Home Visit", "Health worker visits the patient at home", VisitTypes.HOME_VISIT));
    }
}
