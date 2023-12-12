package org.openmrs.module.pihcore.fragment.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.pihcore.printer.template.SLWristbandTemplate;
import org.openmrs.module.pihcore.printer.template.WristbandTemplate;
import org.openmrs.module.paperrecord.PaperRecordService;
import org.openmrs.module.paperrecord.UnableToPrintLabelException;
import org.openmrs.module.printer.Printer;
import org.openmrs.module.printer.PrinterService;
import org.openmrs.module.printer.PrinterType;
import org.openmrs.module.printer.UnableToPrintViaSocketException;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor.Country;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Fragment that prints a wristband. Current ways to trigger:
 *  1) Called from printWristband.js, which is configured as a overall action on the patient dashboard (see wristband_extension.json)
 *  2) Called from within the real-time check-in form (liveCheckin.xml)
 *  3) Called from within the admission note (admissionNote.xml)
 */
public class WristbandFragmentController {

    private final Log log = LogFactory.getLog(getClass());

    public SimpleObject printWristband(UiUtils ui,
                                      @RequestParam("patientId") Patient patient,
                                      @RequestParam(value = "locationId", required = false) Location location,
                                      @SpringBean("wristbandTemplate") WristbandTemplate wristbandTemplate,
                                      @SpringBean("printerService") PrinterService printerService,
                                      @SpringBean("paperRecordService") PaperRecordService paperRecordService,
                                       @SpringBean Config config,
                                       UiSessionContext uiSessionContext) throws UnableToPrintLabelException {
        try {

            if (location == null) {
                location = uiSessionContext.getSessionLocation();
            }

            String data = "";
            if (config.getCountry().equals(Country.SIERRA_LEONE)) {
                SLWristbandTemplate slWristbandTemplate = Context.getRegisteredComponent("slWristbandTemplate", SLWristbandTemplate.class);
                data = slWristbandTemplate.generateWristband(patient, location, uiSessionContext.getLocale());
            } else {
                // make sure a paper record has been created
                if (!paperRecordService.paperRecordExistsForPatient(patient, location)) {
                    paperRecordService.createPaperRecord(patient, location);
                }
                data = wristbandTemplate.generateWristband(patient, location);
            }

            Printer printer = printerService.getDefaultPrinter(location, PrinterType.WRISTBAND);

            if (printer == null) {
                // TODO: better warning if no default printer
                return SimpleObject.create("success", false, "message", ui.message("mirebalais.error.noWristbandPrinterConfiguredForLocation") + " " + ui.format(location));
            }

            printerService.printViaSocket(data, printer, "UTF-8");

            return SimpleObject.create("success", true, "message", ui.message("mirebalais.printWristband.patientDashboard.successMessage") + " " + printer.getPhysicalLocation().getName());

        } catch (UnableToPrintViaSocketException e) {
            log.warn("User " + uiSessionContext.getCurrentUser() + " unable to print wristband at location "
                    + location, e);
            return SimpleObject.create("success", false, "message", ui.message("mirebalais.error.unableToPrintWristband"));
        }
    }

}
