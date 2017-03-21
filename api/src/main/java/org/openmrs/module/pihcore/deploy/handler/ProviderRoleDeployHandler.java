package org.openmrs.module.pihcore.deploy.handler;

import org.openmrs.annotation.Handler;
import org.openmrs.module.metadatadeploy.handler.AbstractObjectDeployHandler;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Deployment handler for provider roles
 */
@Handler(supports = {ProviderRole.class})
public class ProviderRoleDeployHandler extends AbstractObjectDeployHandler<ProviderRole> {

    @Autowired
    @Qualifier("providerManagementService")
    private ProviderManagementService providerManagementService;


    /**
     * Fetches an object by primary identifier
     *
     * @param identifier the identifier
     * @return the object or null
     */
    @Override
    public ProviderRole fetch(String identifier) {
        return providerManagementService.getProviderRoleByUuid(identifier);
    }

    /**
     * Saves the given object to the database
     *
     * @param obj the object to save
     * @return the saved object
     */
    @Override
    public ProviderRole save(ProviderRole obj) {
        return providerManagementService.saveProviderRole(obj);
    }

    /**
     * Removes the given object which may be implemented as a void, retire or purge depending on the object
     *
     * @param obj    the object to uninstall
     * @param reason the reason for removal
     */
    @Override
    public void uninstall(ProviderRole obj, String reason) {
        providerManagementService.retireProviderRole(obj, reason);
    }
}
