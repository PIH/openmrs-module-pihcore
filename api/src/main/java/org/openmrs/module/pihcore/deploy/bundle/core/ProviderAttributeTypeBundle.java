package org.openmrs.module.pihcore.deploy.bundle.core;


import org.openmrs.ProviderAttributeType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ProviderService;
import org.openmrs.customdatatype.datatype.DateDatatype;
import org.openmrs.customdatatype.datatype.FreeTextDatatype;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.providerAttributeType;

/**
 * Installs VHW provider attributes
 */
@Component
public class ProviderAttributeTypeBundle extends AbstractMetadataBundle {

    @Autowired
    private ConceptService conceptService;

    @Autowired
    private ProviderService providerService;

    public static final class ProviderAttributeTypes {
        public static final String NUMBER_OF_HOUSEHOLDS  = "0c267ae8-f793-4cf8-9b27-93accaa45d86";
        public static final String DATE_HIRED  = "c8ef8a16-a8cd-4748-b0ea-e8a1ec503fbb";

    }
    /**
     * Performs the installation of the metadata items
     *
     * @throws Exception if an error occurs
     */
    @Override
    public void install() throws Exception {
        install(providerAttributeType(
                "Households",
                "Number of households monitored by a VHW",
                FreeTextDatatype.class,
                null,
                0,
                1,
                ProviderAttributeTypes.NUMBER_OF_HOUSEHOLDS));

        ProviderAttributeType dateHired = install(providerAttributeType(
                "Date Hired",
                "The date the provider was hired.",
                DateDatatype.class,
                null,
                0,
                1,
                ProviderAttributeTypes.DATE_HIRED));
        if (dateHired != null) {
            dateHired.setPreferredHandlerClassname("org.openmrs.web.attribute.handler.DateFieldGenDatatypeHandler");
            providerService.saveProviderAttributeType(dateHired);
        }

    }
}
