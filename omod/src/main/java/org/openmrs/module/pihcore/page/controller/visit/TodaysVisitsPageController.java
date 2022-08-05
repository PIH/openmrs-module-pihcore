package org.openmrs.module.pihcore.page.controller.visit;

import org.openmrs.Visit;
import org.openmrs.api.VisitService;
import org.openmrs.module.emrapi.domainwrapper.DomainWrapperFactory;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Collections;



public class TodaysVisitsPageController {

    public void get(PageModel model,
                    @SpringBean("visitService") VisitService visitService,
                    @SpringBean("domainWrapperFactory")DomainWrapperFactory domainWrapperFactory,
                    UiSessionContext uiSessionContext) {

        LocalDate todayDate = LocalDate.now();
        Date startOfDayToday = java.sql.Timestamp.valueOf(LocalDateTime.of(todayDate, LocalTime.MIN));
        Date endOfDayToday = java.sql.Timestamp.valueOf(LocalDateTime.of(todayDate, LocalTime.MAX));
       
        LocationService locationService = Context.getLocationService();
        LocationTag visitLocationTag =  locationService.getLocationTagByName(EmrApiConstants.LOCATION_TAG_SUPPORTS_VISITS);

        Location location = uiSessionContext.getSessionLocation();

        while (location != null && !location.hasTag(visitLocationTag.toString())) {
            location = location.getParentLocation();
        }

        //all non-voided visits that have been started any time today at the current session location.
        List<Visit> visits = visitService.getVisits(null, null, location != null ? Collections.singletonList(location) : null, null, startOfDayToday, endOfDayToday, null, null, null, true, false);
        model.addAttribute("visits", visits);
    }

}
