package org.openmrs.module.pihcore.page.controller.visit;

import org.openmrs.Visit;
import org.openmrs.api.VisitService;
import org.openmrs.module.emrapi.domainwrapper.DomainWrapperFactory;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

public class TodaysVisitsPageController {

    public void get(PageModel model,
                    @SpringBean("visitService") VisitService visitService,
                    @SpringBean("domainWrapperFactory")DomainWrapperFactory domainWrapperFactory) {

        LocalDate todayDate = LocalDate.now();
        Date startOfDayToday = java.sql.Timestamp.valueOf(LocalDateTime.of(todayDate, LocalTime.MIN));
        Date endOfDayToday = java.sql.Timestamp.valueOf(LocalDateTime.of(todayDate, LocalTime.MAX));

        // all non-voided visits that have been started any time today
        List<Visit> visits = visitService.getVisits(null, null, null, null, startOfDayToday, endOfDayToday, null, null, null, true, false);
        model.addAttribute("visits", visits);
    }

}
