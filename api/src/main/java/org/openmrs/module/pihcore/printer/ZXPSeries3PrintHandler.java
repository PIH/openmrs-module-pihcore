package org.openmrs.module.pihcore.printer;

import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.comm.TcpConnection;
import com.zebra.sdk.common.card.containers.GraphicsInfo;
import com.zebra.sdk.common.card.containers.JobStatusInfo;
import com.zebra.sdk.common.card.enumerations.CardSide;
import com.zebra.sdk.common.card.enumerations.GraphicType;
import com.zebra.sdk.common.card.enumerations.OrientationType;
import com.zebra.sdk.common.card.enumerations.PrintType;
import com.zebra.sdk.common.card.exceptions.ZebraCardException;
import com.zebra.sdk.common.card.graphics.ZebraCardGraphics;
import com.zebra.sdk.common.card.graphics.ZebraGraphics;
import com.zebra.sdk.common.card.graphics.barcode.Code39Util;
import com.zebra.sdk.common.card.graphics.barcode.ZebraBarcodeFactory;
import com.zebra.sdk.common.card.graphics.barcode.enumerations.Rotation;
import com.zebra.sdk.common.card.graphics.enumerations.PrinterModel;
import com.zebra.sdk.common.card.printer.ZebraCardPrinter;
import com.zebra.sdk.common.card.printer.ZebraCardPrinterFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.printer.Printer;
import org.openmrs.module.printer.UnableToPrintException;
import org.openmrs.module.printer.handler.PrintHandler;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ZXPSeries3PrintHandler implements PrintHandler {

    private static final int MAX_RETRY = 1; // **no retries**

    private static final int TIME_BETWEEN_RETRIES_IN_MS = 5000;

    private final Log log = LogFactory.getLog(getClass());

    @Override
    public String getDisplayName() {
        return "ZXP Series 3 Print Handler";
    }

    @Override
    public String getBeanName() {
        return "zxpSeries3PrintHandler";
    }

    @Override
    public void print(Printer printer, Map<String, Object> paramMap) throws UnableToPrintException {

        String name = (String) paramMap.get("name");
        String gender = (String) paramMap.get("gender");
        String birthdate = (String) paramMap.get("birthdate");
        Boolean birthdateEstimated = (Boolean) paramMap.get("birthdateEstimated");
        String patientIdentifier = (String) paramMap.get("patientIdentifier");
        List<String> addressLines = (paramMap.containsKey("addressLines") ? (List<String>) paramMap.get("addressLines") : null);
        String telephoneNumber =  (paramMap.containsKey("telephoneNumber") ? (String) paramMap.get("telephoneNumber") : null);
        String issuingLocation = (paramMap.containsKey("issuingLocation") ? (String) paramMap.get("issuingLocation") : null);
        String issuedDate = (String) paramMap.get("issuedDate");
        String customCardLabel = (paramMap.containsKey("customCardLabel") ? (String) paramMap.get("customCardLabel") : null);

        ZebraCardPrinter zebraCardPrinter = null;
        ZebraGraphics graphics = null;
        Connection connection = null;
        List<GraphicsInfo> graphicsData;

        int nameFontSize = 16;

        // less-than-perfect attempt to lower font size for patients with large names
        if (name.length() > 28) {
            nameFontSize = 10;
        }
        else if (name.length() > 22) {
            nameFontSize = 12;
        }

        int retryCount = 0;
        boolean success = false;

        Integer jobId = null;

        while (!success && retryCount < MAX_RETRY) {

            log.info("Starting ID card print job for patient " + patientIdentifier + " on printer " + printer.getName());

            try {
                connection = new TcpConnection(printer.getIpAddress(), 9100);
                connection.open();

                zebraCardPrinter = ZebraCardPrinterFactory.getInstance(connection);
                graphicsData = new ArrayList<GraphicsInfo>();

                log.info("Connection opened for ID card printing for patient " + patientIdentifier  + " on printer " + printer.getName());

                GraphicsInfo grInfo = new GraphicsInfo();
                grInfo.side = CardSide.Front;
                grInfo.printType = PrintType.MonoK;
                grInfo.graphicType = GraphicType.BMP;

                graphics = new ZebraCardGraphics(zebraCardPrinter);
                graphics.setPrinterModel(PrinterModel.ZXPSeries3);
                graphics.initialize(0, 0, OrientationType.Landscape, PrintType.MonoK, Color.WHITE);

                // name
                graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, nameFontSize));
                graphics.drawText(name, 60, 20, Color.BLACK);

                // divider line
                graphics.drawLine(25, 100, 1000, 100, 3, Color.BLACK);

                // gender & birthdate
                graphics.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 6));
                graphics.drawText("Sèks", 60, 320, Color.BLACK);
                graphics.drawText("Dat ou fèt " + (birthdateEstimated != null && birthdateEstimated ? " (Estime)" : " "), 250, 320, Color.BLACK);

                graphics.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 9));
                graphics.drawText((gender.equals("F") ? "Fi" : "Gason"), 60, 350, Color.BLACK);
                graphics.drawText(birthdate, 250, 350, Color.BLACK);

                // address
                graphics.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 9));
                int verticalPosition = 110;
                if (addressLines != null && addressLines.size() > 0) {
                    for (String addressLine : addressLines) {
                        graphics.drawText(addressLine, 60, verticalPosition, Color.BLACK);
                        verticalPosition = verticalPosition + 50;
                    }
                }

                // telephone number
                if (StringUtils.isNotBlank(telephoneNumber)) {
                    graphics.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 6));
                    graphics.drawText("Nimewo Telefòn", 600, 320, Color.BLACK);
                    graphics.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 9));
                    graphics.drawText(telephoneNumber, 600, 350, Color.BLACK);
                }

                // divider line
                graphics.drawLine(25, 420, 1000, 420, 3, Color.BLACK);

                // bar code and identifier
                Code39Util barcode = ZebraBarcodeFactory.getCode39(graphics);
                barcode.setBarHeight(100);
                barcode.drawBarcode(patientIdentifier, 60, 450, Rotation.ROTATE_0);

                // custom card label, if specified
                if (StringUtils.isNotBlank(customCardLabel)) {
                    graphics.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 9));
                    graphics.drawText(customCardLabel, 420, 450, Color.BLACK);
                }

                // date and location issued
                graphics.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 6));
                graphics.drawText("Dat kat la fet la", 420, 520, Color.BLACK);
                graphics.drawText("Kote kat la fet la", 720, 520, Color.BLACK);

                graphics.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 9));
                graphics.drawText(issuedDate, 420, 560, Color.BLACK);
                graphics.drawText((StringUtils.isNotBlank(issuingLocation) ? issuingLocation : ""), 720, 560, Color.BLACK);

                // do the actual printing
                grInfo.graphicData = graphics.createImage(null);
                graphics.clear();
                graphicsData.add(grInfo);

                log.info("Starting ID card printing for patient " + patientIdentifier  + " on printer " + printer.getName());
                jobId = zebraCardPrinter.print(1, graphicsData);
                log.info("Started ID card printing job " +jobId + " for patient " + patientIdentifier  + " on printer " + printer.getName());
                success = pollJobStatus(zebraCardPrinter, jobId, printer);

            } catch (Exception e) {
                log.warn("Unable to print to printer " + printer.getName(), e);
            } finally {
                retryCount++;
                cleanUp(connection, zebraCardPrinter, jobId, graphics, printer, success);
            }

            // TODO remove
            if (success) {
                log.info("Success printing ID card for patient " + patientIdentifier  + " on printer " + printer.getName());
            }
            else {
                log.info("Failed printing ID card for patient " + patientIdentifier  + " on printer " + printer.getName());
            }


            if (!success && retryCount < MAX_RETRY) {
                try {
                    Thread.sleep(TIME_BETWEEN_RETRIES_IN_MS);
                }
                catch (InterruptedException e) {
                    // do nothing
                }
            }

        }
    }


    private boolean pollJobStatus(ZebraCardPrinter device, Integer actionID, Printer printer) throws ConnectionException, ZebraCardException {
        boolean success = false;
        long dropDeadTime = System.currentTimeMillis() + 40000;
        long pollInterval = 500;

        // Poll job status
        JobStatusInfo jStatus = null;

        if (actionID != null) {
            do {
                jStatus = device.getJobStatus(actionID.intValue());

                // TODO get rid of this eventually?
                log.info(String.format("Job %d, Status:%s, Card Position:%s, "
                                + "ReadyForNextJob:%s, Mag Status:%s, Contact Status:%s, Contactless Status:%s, " + "Error Code:%d, Alarm Code:%d", actionID,
                        jStatus.printStatus, jStatus.cardPosition, jStatus.readyForNextJob, jStatus.magneticEncoding, jStatus.contactSmartCard,
                        jStatus.contactlessSmartCard, jStatus.errorInfo.value, jStatus.alarmInfo.value));

                if (jStatus.printStatus.contains("done_ok")) {
                    success = true;
                    break;
                } else if (jStatus.printStatus.contains("error") || jStatus.printStatus.contains("alarm_handling")) {
                    String errorMessage = jStatus.errorInfo != null ? "Err Code: " + jStatus.errorInfo.value + ", " + jStatus.errorInfo.description : "";
                    String alarmMessage = jStatus.alarmInfo != null ? "Alarm Message: " + jStatus.alarmInfo.description : "";
                    log.error("Printer error detected with printer " + printer.getName() + ".  " + errorMessage + ", " + alarmMessage);
                    success = false;
                    break;
                } else if (jStatus.printStatus.contains("cancelled")) {
                    success = false;
                    break;
                }

                if (System.currentTimeMillis() > dropDeadTime) {
                    success = false;
                    break;
                }

                try {
                    Thread.sleep(pollInterval);
                } catch (InterruptedException e) {
                    log.error("Error while printing to printer " + printer.getName(), e);
                }

            } while (true);
        }

        return success;
    }

    private void cleanUp(Connection connection, ZebraCardPrinter device, Integer jobId, ZebraGraphics graphics, Printer printer, boolean success)  {

        // need to cancel any jobs before it will allow you to destroy the printer
        if (!success && device != null && jobId != null) {

            JobStatusInfo jStatus = null;

            try {

                long waitToCancelInterval = 500;
                long dropDeadTime = System.currentTimeMillis() + 5000;

                jStatus = device.getJobStatus(jobId.intValue());

                // TODO figure out the right way to determine if a job needs to be cancelled or not
                while (jStatus != null && !jStatus.printStatus.contains("cancelled") && System.currentTimeMillis() < dropDeadTime) {
                    device.cancel(jobId.intValue());
                    Thread.sleep(waitToCancelInterval);
                    log.info("Cancelled job " + jobId);
                }

            }
            catch (ConnectionException e) {
                log.error("Error while attempting to cancel job on printer " + printer.getName() + ", Status: " + (jStatus != null ? jStatus.printStatus : ""), e);
            }
            catch (ZebraCardException e) {
                log.error("Error while attempting to cancel job on printer " + printer.getName() + ", Status: " + (jStatus != null ? jStatus.printStatus : ""), e);
            }
            catch (InterruptedException e) {
                log.error("Error while attempting to cancel job on printer " + printer.getName() + ", Status: " + (jStatus != null ? jStatus.printStatus : ""), e);
            }
        }

        try {
            if (device != null) {
                device.destroy();
                log.info("Destroyed printer handle for job " + jobId + " " + device.toString());
            }
        } catch (ZebraCardException e) {
            log.error("Unable to destroy printer object for printer " + printer.getName() , e);
        }

        if (graphics != null) {
            graphics.close();
        }

        if (connection != null) {
            try {
                connection.close();
                log.info("Closed connection for job " + jobId + ", Connected:  " + connection.isConnected());
            } catch (ConnectionException e) {
                log.error("Unable to close connection with printer " + printer.getName(), e);
            }
        }
    }
}


