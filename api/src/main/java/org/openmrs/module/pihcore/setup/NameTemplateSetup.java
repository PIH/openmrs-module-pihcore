package org.openmrs.module.pihcore.setup;

import org.openmrs.layout.name.NameSupport;
import org.openmrs.layout.name.NameTemplate;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NameTemplateSetup {

    public static void configureNameTemplate(NameSupport nameSupport, Config config){
        if(config !=null){
            if (config.getCountry() == ConfigDescriptor.Country.PERU) {
                NameTemplate nameTemplate = new NameTemplate();
                nameTemplate.setCodeName("short");  // we are redefining the short name template for use in our context

                Map<String, String> nameMappings = new HashMap<String, String>();
                nameMappings.put("givenName", "zl.givenName");
                nameMappings.put("familyName", "zl.familyName");
                nameMappings.put("familyName2", "zl.familyName2");
                nameTemplate.setNameMappings(nameMappings);

                Map<String, String> sizeMappings = new HashMap<String, String>();
                sizeMappings.put("givenName", "50");
                sizeMappings.put("familyName", "50");
                sizeMappings.put("familyName2", "50");
                nameTemplate.setSizeMappings(sizeMappings);

                List<String> lineByLineFormat = new ArrayList<String>();
                lineByLineFormat.add("familyName");
                lineByLineFormat.add("familyName2,");
                lineByLineFormat.add("givenName");

                nameTemplate.setLineByLineFormat(lineByLineFormat);

                List<NameTemplate> templates = new ArrayList<NameTemplate>();
                templates.add(nameTemplate);

                // we blow away the other templates here, is that a bad thing?
                nameSupport.setLayoutTemplates(templates);
                nameSupport.setDefaultLayoutFormat("short");
            } else {

                NameTemplate nameTemplate = new NameTemplate();
                nameTemplate.setCodeName("short");  // we are redefining the short name template for use in our context

                Map<String, String> nameMappings = new HashMap<String, String>();
                nameMappings.put("givenName", "zl.givenName");
                nameMappings.put("familyName", "zl.familyName");
                nameMappings.put("middleName", "zl.nickname");
                nameTemplate.setNameMappings(nameMappings);

                Map<String, String> sizeMappings = new HashMap<String, String>();
                sizeMappings.put("givenName", "50");
                sizeMappings.put("familyName", "50");
                sizeMappings.put("middleName", "50");
                nameTemplate.setSizeMappings(sizeMappings);

                List<String> lineByLineFormat = new ArrayList<String>();
                lineByLineFormat.add("familyName,");
                lineByLineFormat.add("givenName");
                lineByLineFormat.add("'middleName'");

                nameTemplate.setLineByLineFormat(lineByLineFormat);

                List<NameTemplate> templates = new ArrayList<NameTemplate>();
                templates.add(nameTemplate);

                // we blow away the other templates here, is that a bad thing?
                nameSupport.setLayoutTemplates(templates);
                nameSupport.setDefaultLayoutFormat("short");
            }
        }
    }

}
