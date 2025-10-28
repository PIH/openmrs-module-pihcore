/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.pihcore.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.ProviderRole;
import org.openmrs.api.ProviderService;
import org.openmrs.ui.framework.converter.util.ConversionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToProviderRoleConverter implements Converter<String, ProviderRole> {
	
	@Autowired
	@Qualifier("providerService")
	public ProviderService service;
	
	/**
	 * Retrieves a given provider role from a string representing either the id or uuid of the ProviderRole
	 */
	@Override
	public ProviderRole convert(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		} else if (ConversionUtil.onlyDigits(id)) {
			return service.getProviderRole(Integer.valueOf(id));
		} else {
			return service.getProviderRoleByUuid(id);
		}
	}
}
