package org.openmrs.module.pihcore.initializer.providerroles;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.ProviderRole;
import org.openmrs.api.ProviderService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProviderRolesCsvParser extends CsvParser<ProviderRole, BaseLineProcessor<ProviderRole>> {
	
	private final ProviderService providerService;
	
	@Autowired
	public ProviderRolesCsvParser(ProviderService providerService, ProviderRolesLineProcessor processor) {
		super(processor);
		this.providerService = providerService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.PROVIDER_ROLES;
	}
	
	@Override
	public ProviderRole bootstrap(CsvLine line) throws IllegalArgumentException {
		String uuid = line.getUuid();
		if (StringUtils.isEmpty(uuid)) {
			throw new IllegalArgumentException("uuid is required for provider roles");
		}
		ProviderRole role = providerService.getProviderRoleByUuid(uuid);
		if (role == null) {
			role = new ProviderRole();
			role.setUuid(uuid);
		}
		return role;
	}
	
	@Override
	public ProviderRole save(ProviderRole instance) {
		log.warn("Saving provider role: " + instance.getName());
		return providerService.saveProviderRole(instance);
	}
}
