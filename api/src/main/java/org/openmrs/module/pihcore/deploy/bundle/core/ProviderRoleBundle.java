package org.openmrs.module.pihcore.deploy.bundle.core;

import org.openmrs.ProviderAttributeType;
import org.openmrs.RelationshipType;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProviderService;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.providermanagement.ProviderRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.openmrs.module.pihcore.deploy.PihConstructors.providerRole;

/**
 * Install CHW Provider Roles
 */
@Component
public class ProviderRoleBundle extends AbstractMetadataBundle {

    @Autowired
    private PersonService personService;

    @Autowired
    private ProviderService providerService;

    public static final class ProviderRoles {
        public static final String CHW = "68624C4C-9E10-473B-A849-204820D16C45";
        public static final String CHW_SUPERVISOR = "11C1A56D-82F7-4269-95E8-2B67B9A3D837";
        public static final String NURSE_ACCOMPAGNATEUR = "9a4b44b2-8a9f-11e8-9a94-a6cf71072f73";
        public static final String CLINICIAN = "2fa6f8da-aa58-11e8-98d0-529269fb1459";
        public static final String MH_CHW_SUPERVISOR = "4f88dd49-0c64-4ecd-af0f-4dff272a4971";
    }

    /**
     * Performs the installation of the metadata items
     *
     * @throws Exception if an error occurs
     */
    @Override
    public void install() throws Exception {

        RelationshipType chwToPatient =
                personService.getRelationshipTypeByUuid(PihEmrConfigConstants.RELATIONSHIPTYPE_CHWTOPATIENT_UUID);
        RelationshipType nurseChwToPatient =
                personService.getRelationshipTypeByUuid(PihEmrConfigConstants.RELATIONSHIPTYPE_NURSECHWTOPATIENT_UUID);
        RelationshipType clinicianToPatient =
                personService.getRelationshipTypeByUuid(PihEmrConfigConstants.RELATIONSHIPTYPE_CLINICIANTOPATIENT_UUID);

        Set<ProviderAttributeType> providerAttributes = new HashSet<ProviderAttributeType>();
        ProviderAttributeType providerAttributeType =
                providerService.getProviderAttributeTypeByUuid(PihEmrConfigConstants.PROVIDERATTRIBUTETYPE_DATEHIRED_UUID);
        if (providerAttributeType != null) {
            providerAttributes.add(providerAttributeType);
        }
        providerAttributeType =
                providerService.getProviderAttributeTypeByUuid(PihEmrConfigConstants.PROVIDERATTRIBUTETYPE_HOUSEHOLDS_UUID);
        if (providerAttributeType != null) {
            providerAttributes.add(providerAttributeType);
        }

        ProviderRole vhwSupervisee = install(providerRole("CHW", null, Collections.singleton(chwToPatient), providerAttributes, ProviderRoles.CHW));

        install(providerRole("CHW Supervisor", Collections.singleton(vhwSupervisee), Collections.singleton(chwToPatient), providerAttributes, ProviderRoles.CHW_SUPERVISOR));

        install(providerRole("Nurse Accompagnateur", null, Collections.singleton(nurseChwToPatient), providerAttributes, ProviderRoles.NURSE_ACCOMPAGNATEUR));

        install(providerRole("Clinician", null, Collections.singleton(clinicianToPatient), providerAttributes, ProviderRoles.CLINICIAN));

        install(providerRole("MH CHW Supervisor", Collections.singleton(vhwSupervisee), Collections.singleton(chwToPatient), providerAttributes, ProviderRoles.MH_CHW_SUPERVISOR));
    }

}
