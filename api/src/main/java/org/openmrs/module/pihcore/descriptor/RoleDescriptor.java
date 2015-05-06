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

import org.openmrs.Role;

import java.util.List;
import java.util.Set;

/**
 * Encapsulates the information needed to create a new Role
 */
public abstract class RoleDescriptor implements Descriptor<Role> {

    /**
     * @return the name of the role
     */
    public abstract String role();

    /**
     * @return the description of the role
     */
    public abstract String description();

    /**
     * @return the role descriptors that this role should inherit from. By default, this is null
     */
    public List<RoleDescriptor> inherited() {
        return null;
    }

    /**
     * @return the privilege descriptors that this role should contain
     */
    public abstract List<PrivilegeDescriptor> privileges();

    /**
     * @see Descriptor#getDescribedType()
     */
    @Override
    public Class<Role> getDescribedType() {
        return Role.class;
    }
}
