package org.openmrs.module.pihcore.deploy.bundle.core;

import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.PihMetadataBundle;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.AdministrativeConcepts;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.ClinicalConsultationConcepts;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.DeathConcepts;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.DiagnosisConcepts;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.SocioEconomicConcepts;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.VaccinationConcepts;
import org.springframework.stereotype.Component;

/**
 * Consolidates all core metadata into a single required set
 */

@Component
@Requires(
        { EncounterRoleBundle.class,
        EncounterTypeBundle.class,
        GlobalPropertiesBundle.class,
        LocationAttributeTypeBundle.class,
        LocationBundle.class,
        LocationTagBundle.class,
        OrderTypeBundle.class,
        PersonAttributeTypeBundle.class,
        RolesAndPrivilegesBundle.class,
        VisitTypeBundle.class,
        VisitAttributeTypeBundle.class,
        AdministrativeConcepts.class,
        //AllergyConcepts.class,  // we are now installing all allergy concepts via MDS package
        ClinicalConsultationConcepts.class,
        DeathConcepts.class,
        DiagnosisConcepts.class,
        SocioEconomicConcepts.class,
        VaccinationConcepts.class
        } )
public class PihCoreMetadataBundle extends PihMetadataBundle {
    @Override
    public void install() throws Exception {

    }
}
