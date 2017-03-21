package org.openmrs.module.pihcore.deploy;


import org.openmrs.ProviderAttributeType;
import org.openmrs.RelationshipType;
import org.openmrs.module.providermanagement.ProviderRole;

import java.util.Set;

public class PihConstructors {

    public static ProviderRole providerRole(
            String name,
            Set<ProviderRole> superviseeProviderRoles,
            Set<RelationshipType> relationshipTypes,
            Set<ProviderAttributeType> providerAttributeTypes,
            String uuid) {

        ProviderRole obj = new ProviderRole();
        obj.setName(name);
        obj.setSuperviseeProviderRoles(superviseeProviderRoles);
        obj.setRelationshipTypes(relationshipTypes);
        obj.setProviderAttributeTypes(providerAttributeTypes);
        obj.setUuid(uuid);
        return obj;
    }
}
