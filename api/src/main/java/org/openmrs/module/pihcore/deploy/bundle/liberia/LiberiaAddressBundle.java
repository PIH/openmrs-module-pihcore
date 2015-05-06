package org.openmrs.module.pihcore.deploy.bundle.liberia;

import org.openmrs.api.SerializationService;
import org.openmrs.layout.web.address.AddressTemplate;
import org.openmrs.module.pihcore.deploy.bundle.PihMetadataBundle;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LiberiaAddressBundle extends PihMetadataBundle {

    @Autowired
    SerializationService serializationService;

    @Override
    public void install() throws Exception {
        installAddressTemplate();
        // TODO: Move address hierarchy setup into this bundle
    }

    /**
     * Install the appropriate address template
     */
    public void installAddressTemplate() throws Exception {
        log.info("Installing Address Template");
        String template = serializationService.getDefaultSerializer().serialize(getAddressTemplate());
        setGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_ADDRESS_TEMPLATE, template);
    }

    /**
     * @return a new AddressTemplate instance for Mirebalais
     */
    public AddressTemplate getAddressTemplate() {
        AddressTemplate addressTemplate = new AddressTemplate("");

        Map<String, String> nameMappings = new HashMap<String, String>();
        nameMappings.put("country", "Country");
        nameMappings.put("stateProvince", "District");
        nameMappings.put("cityVillage", "Settlement");
        addressTemplate.setNameMappings(nameMappings);

        Map<String, String> sizeMappings = new HashMap<String, String>();
        sizeMappings.put("country", "40");
        sizeMappings.put("stateProvince", "40");
        sizeMappings.put("cityVillage", "40");

        addressTemplate.setSizeMappings(sizeMappings);

        Map<String, String> elementDefaults = new HashMap<String, String>();
        elementDefaults.put("country", "Liberia");
        addressTemplate.setElementDefaults(elementDefaults);

        List<String> lineByLineFormat = new ArrayList<String>();
        lineByLineFormat.add("cityVillage");
        lineByLineFormat.add("stateProvince, country");
        addressTemplate.setLineByLineFormat(lineByLineFormat);

        return addressTemplate;
    }
}
