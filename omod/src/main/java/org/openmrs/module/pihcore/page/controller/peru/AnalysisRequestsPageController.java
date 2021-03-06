package org.openmrs.module.pihcore.page.controller.peru;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.api.EncounterService;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.parameter.EncounterSearchCriteria;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Date;
import java.util.Calendar;

// by uiframework convention, this gets automatically wired in as the controller for the url "pihcore/peru/analysisRequests.page"
// (because it's provided by the "pihcore" module and it's in the "peru" subpackage of package "org.openmrs.module.pihcore.page.controller")
// TODO we could also rename this depending on how it ends up fitting into the whole workflow
public class AnalysisRequestsPageController {

    public void get(PageModel model,   //  a model we can add data to, that will then passed to the view
                    @RequestParam(value="startDate", required=false) Date startDate,//DateFormat is YYYY-MM-DD
                    @RequestParam(value="endDate", required=false) Date endDate,//DateFormat is YYYY-MM-DD
                    @SpringBean("encounterService") EncounterService encounterService // wires in the OpenMRS Core Encounter Service API
    )  throws IOException {


        // NOTE: if you are using the OpenMRS SDK and "watching" this module, you should be able to "hot" reload this page
        // and it should automatically pick up changes you've made... you will need to have your IDE build (in IntelliJ I do: Build->Build Project)
        // you also should be able to attach the debugger to this page

        // first we fetch the encounter type by uuid
        EncounterType testOrderEncounterType = Metadata.lookupEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_TEST_ORDER_UUID);

        //if(startDate == null)
          //  startDate=defaultStartDate();
        // then we create a search criteria for encounters to limit to that encounter type
        // TODO: will likely want to filter by date, sort in reverse order...

        EncounterSearchCriteria criteria = new EncounterSearchCriteria(null, null, startDate, endDate,
                null, null, Collections.singleton(testOrderEncounterType), null,
                null, null, false);

        // calls the API to match all the encounters that match the critera
        List<Encounter> encounters = encounterService.getEncounters(criteria);

        // see EncounterService class in OpenMRS Core for full API:
        //   https://github.com/openmrs/openmrs-core/blob/2.3.x/api/src/main/java/org/openmrs/api/EncounterService.java

        // add this list to the model under "encounters"
        model.put("encounters", encounters);

        // we don't need to specify the view here... since the controller is named "AnalysisRequestsPageController" and it's in the peru package.
        // the view should exist in "webapp/pages/peru/analysisRequests.gsp"
    }

}
