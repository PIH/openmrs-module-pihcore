package org.openmrs.module.pihcore.deploy.bundle.liberia;

import org.openmrs.module.addresshierarchy.AddressField;
import org.openmrs.module.pihcore.deploy.bundle.AddressComponent;
import org.openmrs.module.pihcore.deploy.bundle.AddressBundle;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LiberiaAddressBundle extends AddressBundle {

    @Override
    public int getVersion() {
        return 7;
    }

    @Override
    public List<AddressComponent> getAddressComponents() {
        List<AddressComponent> l = new ArrayList<AddressComponent>();
        l.add(new AddressComponent(AddressField.COUNTRY, "Country", 40, "Liberia", true));
        l.add(new AddressComponent(AddressField.STATE_PROVINCE, "County", 40, null, false));
        l.add(new AddressComponent(AddressField.COUNTY_DISTRICT, "District", 40, null, false));
        l.add(new AddressComponent(AddressField.CITY_VILLAGE, "Settlement", 40, null, false));
        l.add(new AddressComponent(AddressField.ADDRESS_1, "Address", 80, null, false));
        return l;
    }

    @Override
    public List<String> getLineByLineFormat() {
        List<String> l = new ArrayList<String>();
        l.add("address1");
        l.add("cityVillage");
        l.add("countyDistrict, stateProvince");
        return l;
    }

    @Override
    public String getAddressHierarchyEntryPath() {
        return "addresshierarchy/liberia_address_hierarchy_entries_7.csv";
    }
}
