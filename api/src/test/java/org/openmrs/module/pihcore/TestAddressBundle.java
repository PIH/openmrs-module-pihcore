package org.openmrs.module.pihcore;

import org.openmrs.module.addresshierarchy.AddressField;
import org.openmrs.module.pihcore.deploy.bundle.AddressBundle;
import org.openmrs.module.pihcore.deploy.bundle.AddressComponent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TestAddressBundle extends AddressBundle {

    @Override
    public int getVersion() {
        return 10;
    }

    @Override
    public List<AddressComponent> getAddressComponents() {
        List<AddressComponent> l = new ArrayList<AddressComponent>();
        l.add(new AddressComponent(AddressField.COUNTRY, "haiticore.address.country", 40, "Haiti", true));
        l.add(new AddressComponent(AddressField.STATE_PROVINCE, "haiticore.address.stateProvince", 40, null, true));
        l.add(new AddressComponent(AddressField.CITY_VILLAGE, "haiticore.address.cityVillage", 40, null, true));
        l.add(new AddressComponent(AddressField.ADDRESS_3, "haiticore.address.neighborhoodCell", 60, null, true));
        l.add(new AddressComponent(AddressField.ADDRESS_1, "haiticore.address.address1", 60, null, false));
        l.add(new AddressComponent(AddressField.ADDRESS_2, "haiticore.address.address2", 60, null, false));
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
        return "test_address_hierarchy_entries.csv";
    }
}
