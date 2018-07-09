package org.openmrs.module.pihcore.deploy.bundle.mexico;


import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.core.PihCoreMetadataBundle;
import org.openmrs.module.pihcore.metadata.mexico.MexicoLocations;
import org.springframework.stereotype.Component;

@Component
@Requires({ PihCoreMetadataBundle.class } )
public class MexicoLocationsBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
        install(MexicoLocations.JALTENANGO);
        install(MexicoLocations.CAPITAN);
        install(MexicoLocations.HONDURAS);
        install(MexicoLocations.LAGUNA_DEL_COFRE);
        install(MexicoLocations.LETRERO);
        install(MexicoLocations.MATAZANO);
        install(MexicoLocations.MONTERREY);
        install(MexicoLocations.PLAN_DE_LA_LIBERTAD);
        install(MexicoLocations.REFORMA);
        install(MexicoLocations.SALVADOR);
        install(MexicoLocations.SOLEDAD);
    }
}
