package org.openmrs.module.pihcore.metadata.core;

import org.openmrs.module.pihcore.descriptor.OrderTypeDescriptor;import java.lang.String;

public class OrderTypes {

    public static OrderTypeDescriptor DRUG_ORDER = new OrderTypeDescriptor() {

        public String uuid() { return "131168f4-15f5-102d-96e4-000c29c2a5d7"; }
        public String name() { return "Drug Order"; }
        public String description() { return "An order for a medication to be given to the patient"; }
        public String javaClassName() { return "org.openmrs.DrugOrder"; }
        public OrderTypeDescriptor parent() { return null; }

    };

    public static OrderTypeDescriptor TEST_ORDER = new OrderTypeDescriptor() {

        public String uuid() { return "52a447d3-a64a-11e3-9aeb-50e549534c5e"; }
        public String name() { return "Test Order"; }
        public String description() { return "Order type for test orders"; }
        public String javaClassName() { return "org.openmrs.TestOrder"; }
        public OrderTypeDescriptor parent() { return null; }

    };

    public static OrderTypeDescriptor RADIOLOGY_TEST_ORDER = new OrderTypeDescriptor() {

        public String uuid() { return "5a3a8d2e-97c3-4797-a6a8-5417e6e699ec"; }
        public String name() { return "Radiology Test Order"; }
        public String description() { return "A radiology test order"; }
        public String javaClassName() { return "org.openmrs.module.radiologyapp.RadiologyOrder"; }
        public OrderTypeDescriptor parent() { return TEST_ORDER; }

    };

}
