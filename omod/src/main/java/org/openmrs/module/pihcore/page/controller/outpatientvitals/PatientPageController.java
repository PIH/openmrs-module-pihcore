/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.pihcore.page.controller.outpatientvitals;

import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.api.FormService;
import org.openmrs.module.emr.EmrContext;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.pihcore.apploader.CustomAppLoaderConstants;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class PatientPageController {

    public void controller(@RequestParam("patientId") Patient patient,
                           UiUtils ui,
                           EmrContext emrContext,
                           PageModel model,
                           @SpringBean("formService") FormService formService,
                           @InjectBeans PatientDomainWrapper patientDomainWrapper) {

        patientDomainWrapper.setPatient(patient);

        SimpleObject appHomepageBreadcrumb = SimpleObject.create("label", ui.message("mirebalais.outpatientVitals.title"), "link", ui.pageLink("coreapps", "findpatient/findPatient", Collections.singletonMap("app", (Object) "mirebalais.outpatientVitals")));
        SimpleObject patientPageBreadcrumb = SimpleObject.create("label", patient.getFamilyName() + ", " + patient.getGivenName(), "link", ui.thisUrlWithContextPath());

        Form outpatientVitalsForm = formService.getFormByUuid("68728aa6-4985-11e2-8815-657001b58a90");

        List<Encounter> existingEncounters = new ArrayList<Encounter>();
        if (emrContext.getActiveVisit() != null) {
            for (Encounter encounter : emrContext.getActiveVisit().getVisit().getEncounters()) {
                if (!encounter.isVoided()
                        && outpatientVitalsForm.equals(encounter.getForm())) {
                    existingEncounters.add(encounter);
                }
            }
        }

        model.addAttribute("visit", emrContext.getActiveVisit() != null ? emrContext.getActiveVisit().getVisit() : null);
        model.addAttribute("existingEncounters", existingEncounters);
        model.addAttribute("patient", patientDomainWrapper);
        model.addAttribute("breadcrumbOverride", ui.toJson(Arrays.asList(appHomepageBreadcrumb, patientPageBreadcrumb)));
        model.addAttribute("appName", CustomAppLoaderConstants.Apps.UHM_VITALS);
    }

}
