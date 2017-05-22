package org.openmrs.module.pihcore.page.controller.export;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.module.emr.utils.GeneralUtils;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;

import java.util.List;

public class ExportPatientsPageController {

    protected final Log log = LogFactory.getLog(getClass());

    public String get(PageModel model, UiUtils ui,
                      @SpringBean("patientService") PatientService patientService
                      ) {

        List<Patient> allPatients = patientService.getAllPatients();
        model.addAttribute("allPatients", allPatients);
        model.addAttribute("addressHierarchyLevels", GeneralUtils.getAddressHierarchyLevels());
        return null;
    }
}
