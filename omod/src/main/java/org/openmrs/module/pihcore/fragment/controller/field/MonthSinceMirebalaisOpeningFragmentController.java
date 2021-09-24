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

import org.joda.time.YearMonth;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.ArrayList;
import java.util.List;

public class MonthSinceMirebalaisOpeningFragmentController {

    public void controller(FragmentModel model) {
        List<SimpleObject> months = new ArrayList<SimpleObject>();

        YearMonth earliest = new YearMonth(2013, 2);
        YearMonth month = new YearMonth().minusMonths(1);
        while (month.isAfter(earliest)) {
            months.add(SimpleObject.create("label", month.toString("MMM yyyy", Context.getLocale()), "value", month.toString("yyyy-MM-01")));
            month = month.minusMonths(1);
        }
        model.addAttribute("months", months);
    }

}
