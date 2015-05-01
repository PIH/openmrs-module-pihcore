package org.openmrs.module.pihcore.descriptor;

public abstract class OrderTypeDescriptor extends MetadataDescriptor {

    public abstract String javaClassName();

    public abstract OrderTypeDescriptor parent();

}
