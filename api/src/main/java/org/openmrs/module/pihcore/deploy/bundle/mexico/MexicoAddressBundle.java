package org.openmrs.module.pihcore.deploy.bundle.mexico;

import org.openmrs.module.addresshierarchy.AddressField;
import org.openmrs.module.pihcore.deploy.bundle.AddressBundle;
import org.openmrs.module.pihcore.deploy.bundle.AddressComponent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MexicoAddressBundle extends AddressBundle {

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public List<AddressComponent> getAddressComponents() {
        List<AddressComponent> l = new ArrayList<AddressComponent>();
        l.add(new AddressComponent(AddressField.CITY_VILLAGE, "Comunidad", 40, null, false));
        l.add(new AddressComponent(AddressField.ADDRESS_1, "Dirección", 80, null, false));
        return l;
    }

    @Override
    public List<String> getLineByLineFormat() {
        List<String> l = new ArrayList<String>();
        l.add("address1");
        l.add("cityVillage");
        return l;
    }

    @Override
    public String getAddressHierarchyEntryPath() {
        return "addresshierarchy/mexico_address_hierarchy_entries_1.csv";
    }
}
