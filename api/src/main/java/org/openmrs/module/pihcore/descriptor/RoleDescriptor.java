package org.openmrs.module.pihcore.descriptor;

import java.util.Set;

public abstract class RoleDescriptor implements Descriptor {

    public abstract String role();

    public abstract String description();

    public abstract Set<String> inherited();

    public abstract Set<String> privileges();

}
