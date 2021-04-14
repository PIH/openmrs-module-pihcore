package org.openmrs.module.pihcore.deploy.bundle.mexico;


import org.openmrs.Location;
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
        install(MexicoLocations.CHIAPAS);
        install(MexicoLocations.CAPITAN);
        install(MexicoLocations.HONDURAS);
        install(MexicoLocations.LAGUNA_DEL_COFRE);
        install(MexicoLocations.LETRERO);
        install(MexicoLocations.MATAZANO);
        install(MexicoLocations.MONTERREY);
        install(MexicoLocations.PLAN_ALTA);
        install(MexicoLocations.PLAN_BAJA);
        install(MexicoLocations.REFORMA);
        install(MexicoLocations.SALVADOR);
        install(MexicoLocations.SOLEDAD);
        install(MexicoLocations.HOSPITAL);
        install(MexicoLocations.OFFICE);
        install(MexicoLocations.CASA_MATERNA);
        install(MexicoLocations.CER);

        uninstall(possible(Location.class, MexicoLocations.JALTENANGO.uuid()), "not used");
        uninstall(possible(Location.class, MexicoLocations.SURGERY.uuid()), "not used");
        uninstall(possible(Location.class, MexicoLocations.PLAN_DE_LA_LIBERTAD.uuid()), "was used; split into Alta and Baja");
    }
}
