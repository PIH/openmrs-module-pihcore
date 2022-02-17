package org.openmrs.module.pihcore.page.controller.admin;

import org.openmrs.api.context.Context;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.notification.Message;
import org.openmrs.notification.MessageService;
import org.openmrs.notification.mail.MailMessageSender;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.page.PageRequest;
import org.openmrs.util.ConfigUtil;
import org.openmrs.util.OpenmrsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Administrative tool to test email configuration
 */
public class EmailTestPageController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static String REQUIRED_PRIVILEGE = "App: coreapps.systemAdministration";

    public void get(PageModel model) throws IOException {

        Context.requirePrivilege(REQUIRED_PRIVILEGE);

        Properties properties = new Properties();
        File propertiesFile = new File(OpenmrsUtil.getApplicationDataDirectory(), "mail.properties");
        if (propertiesFile.exists()) {
            OpenmrsUtil.loadProperties(properties, propertiesFile);
        }
        else {
            properties = Context.getMailProperties();
        }
        model.addAttribute("mailConfig", new TreeMap<>(properties));

        Date date = new Date();
        model.addAttribute("sender", properties.getProperty("mail.from", ConfigUtil.getProperty("mail.from")));
        model.addAttribute("recipients", Context.getAuthenticatedUser().getEmail());
        model.addAttribute("subject", "Test Email: " + date);
        model.addAttribute("message", "This is a test email");
    }

    public String post(@RequestParam(value = "recipients", required = true) String recipients,
                       @RequestParam(value = "sender", required = true) String sender,
                       @RequestParam(value = "subject", required = true) String subject,
                       @RequestParam(value = "message", required = true) String message,
                       @SpringBean("messageService") MessageService messageService,
                       UiUtils ui, UiSessionContext context, PageRequest request) {

        if (Context.hasPrivilege(REQUIRED_PRIVILEGE)) {
            try {

                Message msg = messageService.createMessage(recipients, sender, subject, message);

                // If a custom mail.properties file is found, then create a custom message sender for testing this
                File propertiesFile = new File(OpenmrsUtil.getApplicationDataDirectory(), "mail.properties");
                if (propertiesFile.exists()) {
                    Properties p = new Properties();
                    OpenmrsUtil.loadProperties(p, propertiesFile);
                    Session mailSession = Session.getInstance(p, new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(p.getProperty("mail.user"), p.getProperty("mail.password"));
                        }
                    });
                    MailMessageSender messageSender = new MailMessageSender(mailSession);
                    messageSender.send(msg);
                }
                // Otherwise, use the core message sender and configuration from the runtime properties
                else {
                    messageService.sendMessage(msg);
                }
                request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_INFO_MESSAGE, "Email Sent Successfully");
            }
            catch (Exception e) {
                log.error("Unable to send email", e);
                request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE, e.getMessage());
            }
        }
        else {
            request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE, "You are not authorized to send test emails");
        }
        return "redirect:" + ui.pageLink("pihcore", "admin/emailTest");
    }
}