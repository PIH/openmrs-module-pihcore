package org.openmrs.module.pihcore.deploy.bundle;

import org.openmrs.module.metadatadeploy.bundle.CoreConstructors;
import org.openmrs.module.pihcore.descriptor.PatientIdentifierTypeDescriptor;
import org.openmrs.module.pihcore.metadata.PatientIdentifierTypes;
import org.springframework.stereotype.Component;

@Component
public class PatientIdentifierTypeBundle extends PihMetadataBundle {

    @Override
    public void install() throws Exception {

        log.info("Installing PatientIdentifierTypes");

        install(PatientIdentifierTypes.ZL_EMR_ID);
        install(PatientIdentifierTypes.DOSSIER_NUMBER);
        install(PatientIdentifierTypes.EXTERNAL_DOSSIER_NUMBER);
        install(PatientIdentifierTypes.HIVEMR_V1);
    }

    //***** BUNDLE INSTALLATION METHODS FOR DESCRIPTORS

    protected void install(PatientIdentifierTypeDescriptor d) {
        install(CoreConstructors.patientIdentifierType(d.name(), d.description(), d.format(), d.formatDescription(), d.validator(), d.locationBehavior(), d.required(), d.uuid()));
    }
}
