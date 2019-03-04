package org.openmrs.module.pihcore.deploy.bundle.sierraLeone;

import org.openmrs.module.addresshierarchy.AddressField;
import org.openmrs.module.pihcore.deploy.bundle.AddressComponent;
import org.openmrs.module.pihcore.deploy.bundle.AddressBundle;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SierraLeoneAddressBundle extends AddressBundle {

    @Override
    public int getVersion() {
        return 4;
    }

    @Override
    public List<AddressComponent> getAddressComponents() {
        List<AddressComponent> l = new ArrayList<AddressComponent>();
        l.add(new AddressComponent(AddressField.COUNTRY, "Country", 40, "Sierra Leone", true));
        l.add(new AddressComponent(AddressField.STATE_PROVINCE, "District", 40, null, true));
        l.add(new AddressComponent(AddressField.COUNTY_DISTRICT, "Chiefdom", 40, null, true));
        l.add(new AddressComponent(AddressField.CITY_VILLAGE, "Section", 40, null, false));
        l.add(new AddressComponent(AddressField.ADDRESS_1, "Village", 40, null, false));
        l.add(new AddressComponent(AddressField.ADDRESS_2, "Address", 80, null, false));
        return l;
    }

    @Override
    public List<String> getLineByLineFormat() {
        List<String> l = new ArrayList<String>();
        l.add("address2");
        l.add("address1");
        l.add("cityVillage");
        l.add("countyDistrict");
        l.add("stateProvince");
        l.add("country");
        return l;
    }

    @Override
    public String getAddressHierarchyEntryPath() {
        return "addresshierarchy/sierra_leone_address_hierarchy_entries_2.csv";
    }
}
