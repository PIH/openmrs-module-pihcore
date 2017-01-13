package org.openmrs.module.pihcore.deploy.bundle.core;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.metadata.core.MetadataSources;
import org.openmrs.module.pihcore.metadata.core.MetadataTermMappings;
import org.springframework.stereotype.Component;

@Component
@Requires({ LocationBundle.class, VisitTypeBundle.class, EncounterRoleBundle.class, EncounterTypeBundle.class } )
public class MetadataMappingsBundle extends AbstractMetadataBundle {


    @Override
    public void install() throws Exception {

        install(MetadataSources.emrApiSource);

        install(MetadataTermMappings.unknownLocation);
        install(MetadataTermMappings.atFacilityVisitType);
        install(MetadataTermMappings.clinicianEncounterRole);
        install(MetadataTermMappings.orderingProviderEncounterRole);
        install(MetadataTermMappings.checkInClerkEncounterRole);
        install(MetadataTermMappings.admissionEncounterType);
        install(MetadataTermMappings.checkInEncounterType);
        install(MetadataTermMappings.exitFromInpatientEncounterType);
        install(MetadataTermMappings.transferWithinHospitalEncounterType);
        install(MetadataTermMappings.visitNoteEncounterType);
        install(MetadataTermMappings.admissionForm);
        install(MetadataTermMappings.transferWithinHospitalForm);
        install(MetadataTermMappings.exitFromInpatientForm);

    }
}