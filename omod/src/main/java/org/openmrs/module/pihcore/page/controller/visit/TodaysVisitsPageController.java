package org.openmrs.module.pihcore.page.controller.visit;

import org.joda.time.DateMidnight;
import org.openmrs.Visit;
import org.openmrs.api.VisitService;
import org.openmrs.module.emrapi.domainwrapper.DomainWrapperFactory;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;

import java.util.List;

public class TodaysVisitsPageController {

    public void get(PageModel model,
                    @SpringBean("visitService") VisitService visitService,
                    @SpringBean("domainWrapperFactory")DomainWrapperFactory domainWrapperFactory) {

        // all non-voided visits that started today
        List<Visit> visits = visitService.getVisits(null, null, null, null, new DateMidnight().toDate(), null, null, null, null, true, false);
        model.addAttribute("visits", visits);
    }

}
