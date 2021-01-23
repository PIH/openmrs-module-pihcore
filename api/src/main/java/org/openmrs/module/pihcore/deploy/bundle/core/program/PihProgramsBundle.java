package org.openmrs.module.pihcore.deploy.bundle.core.program;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.springframework.stereotype.Component;

@Component
@Requires({
        ANCProgramBundle.class,
        AsthmaProgramBundle.class,
        Covid19ProgramBundle.class,
        DiabetesProgramBundle.class,
        EpilepsyProgramBundle.class,
        HypertensionProgramBundle.class,
        HIVProgramBundle.class,  // requires Haiti HIV MDS package
        MalnutritionProgramBundle.class,
        MentalHealthProgramBundle.class,
        MCHProgramBundle.class,  // requires PIH Maternal Child Health MDS package
        NCDProgramBundle.class,
        OVCProgramBundle.class,
        OncologyProgramBundle.class,
        TBProgramBundle.class,
        ZikaProgramBundle.class
})
public class PihProgramsBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
    }
}
