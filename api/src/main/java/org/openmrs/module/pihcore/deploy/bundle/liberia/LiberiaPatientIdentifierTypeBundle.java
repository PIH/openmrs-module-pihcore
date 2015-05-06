package org.openmrs.module.pihcore.deploy.bundle.liberia;

import org.openmrs.module.metadatadeploy.bundle.CoreConstructors;
import org.openmrs.module.pihcore.deploy.bundle.PihMetadataBundle;
import org.openmrs.module.pihcore.descriptor.PatientIdentifierTypeDescriptor;
import org.openmrs.module.pihcore.metadata.liberia.LiberiaPatientIdentifierTypes;
import org.springframework.stereotype.Component;

@Component
public class LiberiaPatientIdentifierTypeBundle extends PihMetadataBundle {

    @Override
    public void install() throws Exception {

        log.info("Installing PatientIdentifierTypes");

        install(LiberiaPatientIdentifierTypes.PLEEBO_EMR_ID);

    }
    //***** BUNDLE INSTALLATION METHODS FOR DESCRIPTORS

    protected void install(PatientIdentifierTypeDescriptor d) {
        install(CoreConstructors.patientIdentifierType(d.name(), d.description(), d.format(), d.formatDescription(), d.validator(), d.locationBehavior(), d.required(), d.uuid()));
    }
}
