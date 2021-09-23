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

package org.openmrs.module.pihcore.fragment.controller.field;

import org.openmrs.Location;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.util.ByFormattedObjectComparator;

import java.util.Collections;
import java.util.List;

public class InpatientWardFragmentController {

    public void controller(@SpringBean AdtService adtService,
                           UiUtils ui,
                           FragmentModel model) {

        List<Location> wards = adtService.getInpatientLocations();
        Collections.sort(wards, new ByFormattedObjectComparator(ui));
        model.addAttribute("wards", wards);
    }

}
