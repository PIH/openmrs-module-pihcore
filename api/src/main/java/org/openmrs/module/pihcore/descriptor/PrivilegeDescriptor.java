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

import org.openmrs.Privilege;

/**
 * Encapsulates the information needed to create a new Privilege
 */
public abstract class PrivilegeDescriptor implements Descriptor<Privilege> {

    /**
     * @return the name of the privilege
     */
    public abstract String privilege();

    /**
     * @return the description of the privilege
     */
    public abstract String description();

    /**
     * @see Descriptor#getDescribedType()
     */
    @Override
    public Class<Privilege> getDescribedType() {
        return Privilege.class;
    }
}
