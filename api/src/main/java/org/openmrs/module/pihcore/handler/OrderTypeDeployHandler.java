/**
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

package org.openmrs.module.pihcore.handler;

import org.openmrs.OrderType;
import org.openmrs.annotation.Handler;
import org.openmrs.api.OrderService;
import org.openmrs.module.metadatadeploy.handler.AbstractObjectDeployHandler;
import org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Deployment handler for order types
 */
@SuppressWarnings("deprecation")
@Handler(supports = { OrderType.class })
public class OrderTypeDeployHandler extends AbstractObjectDeployHandler<OrderType> {

	@Autowired
	@Qualifier("orderService")
	private OrderService orderService;

	/**
	 * @see ObjectDeployHandler#fetch(String)
	 */
	@Override
	public OrderType fetch(String uuid) {
		return orderService.getOrderTypeByUuid(uuid);
	}

	/**
	 * @see ObjectDeployHandler#save(org.openmrs.OpenmrsObject)
	 */
	@Override
	public OrderType save(OrderType obj) {
		return orderService.saveOrderType(obj);
	}

	/**
	 * @see .ObjectDeployHandler#findAlternateMatch(org.openmrs.OpenmrsObject)
	 */
	@Override
	public OrderType findAlternateMatch(OrderType incoming) {
		return null;
	}

	/**
	 * @see ObjectDeployHandler#uninstall(org.openmrs.OpenmrsObject, String)
	 * @param obj the object to uninstall
	 */
	@Override
	public void uninstall(OrderType obj, String reason) {
		orderService.retireOrderType(obj, reason);
	}
}