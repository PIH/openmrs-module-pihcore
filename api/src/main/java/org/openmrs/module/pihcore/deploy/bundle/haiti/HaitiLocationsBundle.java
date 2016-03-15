package org.openmrs.module.pihcore.deploy.bundle.haiti;

import org.openmrs.Location;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.core.LocationAttributeTypeBundle;
import org.openmrs.module.pihcore.deploy.bundle.core.LocationTagBundle;
import org.openmrs.module.pihcore.metadata.haiti.HaitiLocations;
import org.springframework.stereotype.Component;

@Component
@Requires({ LocationTagBundle.class, LocationAttributeTypeBundle.class} )
public class HaitiLocationsBundle extends AbstractMetadataBundle {

    // TODO figure out how to install Mirebalais locations in a non-Mirebalais context; one issue is that we have a parent
    // TODO location "Mirebalais" that encompasses both CDI and Mirebalais, and CDI and Mirebalais aren't visit locations in the Mirebalais syste,

    @Override
    public void install() throws Exception {
        install(HaitiLocations.BELLADERE);
        install(HaitiLocations.BOUCAN_CARRE);
        install(HaitiLocations.CANGE);
        install(HaitiLocations.CERCA_LA_SOURCE);
        install(HaitiLocations.HINCHE);
        install(HaitiLocations.HSN_SAINT_MARC);
        install(HaitiLocations.LACOLLINE);
        install(HaitiLocations.PETITE_RIVIERE);
        install(HaitiLocations.SSPE_SAINT_MARC);
        install(HaitiLocations.THOMONDE);
        install(HaitiLocations.VERRETTES);

        // we are uninstalling this until we are actually using
        uninstall(possible(Location.class, HaitiLocations.POZ.uuid()), "uninstall until we actually start using this");
    }
}
