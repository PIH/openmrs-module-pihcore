package org.openmrs.module.pihcore.deploy.bundle.haiti;

import org.openmrs.Location;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.core.LocationAttributeTypeBundle;
import org.openmrs.module.pihcore.metadata.core.Locations;
import org.openmrs.module.pihcore.metadata.haiti.PihHaitiLocations;
import org.openmrs.module.pihcore.metadata.haiti.mirebalais.MirebalaisLocations;
import org.springframework.stereotype.Component;

@Component
@Requires({ LocationAttributeTypeBundle.class} )
public class PihHaitiLocationsBundle extends AbstractMetadataBundle {

    // TODO figure out how to install Mirebalais locations in a non-Mirebalais context; one issue is that we have a parent
    // TODO location "Mirebalais" that encompasses both CDI and Mirebalais, and CDI and Mirebalais aren't visit locations in the Mirebalais syste,

    @Override
    public void install() throws Exception {
        // Login Locations
        install(PihHaitiLocations.BELLADERE);
        install(PihHaitiLocations.BOUCAN_CARRE);
        install(PihHaitiLocations.CANGE);
        install(PihHaitiLocations.CERCA_LA_SOURCE);
        install(PihHaitiLocations.HINCHE);
        install(PihHaitiLocations.HSN_SAINT_MARC);
        install(PihHaitiLocations.LACOLLINE);
        install(PihHaitiLocations.PETITE_RIVIERE);
        install(PihHaitiLocations.SSPE_SAINT_MARC);
        install(PihHaitiLocations.THOMONDE);
        install(PihHaitiLocations.VERRETTES);
        install(PihHaitiLocations.POZ);
        install(MirebalaisLocations.MIREBALAIS_CDI_PARENT);
        install(Locations.HOME_VISIT);

        // Other Locations migrated from HIV EMR
        install(PihHaitiLocations.CERCA_CAVAJAL);
        install(PihHaitiLocations.THOMASSIQUE);
        install(PihHaitiLocations.SAVANETTE);
        install(PihHaitiLocations.BAPTISTE);
        install(PihHaitiLocations.MAISSADE);
        install(PihHaitiLocations.TILORY);
        install(PihHaitiLocations.DUFAILLY);
        install(PihHaitiLocations.JEAN_DENIS);

        uninstall(possible(Location.class, PihHaitiLocations.LASCAHOBAS.uuid()), "not used");

    }
}
