package org.openmrs.module.pihcore.deploy.bundle.core;

import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.OrderEntryConcepts;
import org.openmrs.module.pihcore.deploy.bundle.PihMetadataBundle;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.AdministrativeConcepts;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.ClinicalConsultationConcepts;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.DeathConcepts;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.DiagnosisConcepts;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.SocioEconomicConcepts;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.VaccinationConcepts;
import org.springframework.stereotype.Component;

/**
 * Consolidates all core non-concept metadata into a single required set
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
        OrderEntryConcepts.class,
        AdministrativeConcepts.class,
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
