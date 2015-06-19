package org.openmrs.module.pihcore.deploy.bundle.haiti;

import org.openmrs.module.addresshierarchy.AddressField;
import org.openmrs.module.pihcore.deploy.bundle.AddressBundle;
import org.openmrs.module.pihcore.deploy.bundle.AddressComponent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HaitiAddressBundle extends AddressBundle {

    @Override
    public int getVersion() {
        return 8;
    }

    @Override
    public List<AddressComponent> getAddressComponents() {
        List<AddressComponent> l = new ArrayList<AddressComponent>();
        l.add(new AddressComponent(AddressField.COUNTRY, "mirebalais.address.country", 40, "Haiti", true));
        l.add(new AddressComponent(AddressField.STATE_PROVINCE, "mirebalais.address.stateProvince", 40, null, true));
        l.add(new AddressComponent(AddressField.CITY_VILLAGE, "mirebalais.address.cityVillage", 40, null, true));
        l.add(new AddressComponent(AddressField.ADDRESS_3, "mirebalais.address.neighborhoodCell", 60, null, true));
        l.add(new AddressComponent(AddressField.ADDRESS_1, "mirebalais.address.address1", 60, null, false));
        l.add(new AddressComponent(AddressField.ADDRESS_2, "mirebalais.address.address2", 60, null, false));
        return l;
    }

    @Override
    public List<String> getLineByLineFormat() {
        List<String> l = new ArrayList<String>();
        l.add("address2");
        l.add("address1");
        l.add("address3, cityVillage");
        l.add("stateProvince, country");
        return l;
    }

    @Override
    public String getAddressHierarchyEntryPath() {
        return "addresshierarchy/haiti_address_hierarchy_entries_6.csv";
    }
}
