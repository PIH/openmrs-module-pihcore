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
package org.openmrs.module.pihcore.descriptor;

import org.openmrs.OrderType;

/**
 * Encapsulates the information needed to create a new OrderType
 */
public abstract class OrderTypeDescriptor extends MetadataDescriptor<OrderType> {

    /**
     * @return the java classname required on an order type
     */
    public abstract String javaClassName();

    /**
     * @return the descriptor representing the parent for this order type
     */
    public abstract OrderTypeDescriptor parent();

    /**
     * @see Descriptor#getDescribedType()
     */
    @Override
    public Class<OrderType> getDescribedType() {
        return OrderType.class;
    }
}
