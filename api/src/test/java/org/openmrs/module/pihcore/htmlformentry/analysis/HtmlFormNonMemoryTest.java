package org.openmrs.module.pihcore.htmlformentry.analysis;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.htmlformentry.HtmlFormEntryUtil;
import org.openmrs.module.htmlformentry.handler.TagHandler;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

@SkipBaseSetup
public class HtmlFormNonMemoryTest extends BaseModuleContextSensitiveTest {

    Properties p = null;
    private static final Logger log = LoggerFactory.getLogger(HtmlFormNonMemoryTest.class);

    public HtmlFormNonMemoryTest() {
        super();
        p = getRuntimeProperties();
    }

    @Override
    public Properties getRuntimeProperties() {
        Properties props = new Properties();
        String serverId = System.getProperty("serverId");
        if (serverId != null) {
            String path = System.getProperty("user.home") + File.separator + "openmrs" + File.separator + serverId + File.separator + "openmrs-runtime.properties";
            try (FileInputStream fis = new FileInputStream(path)) {
                props.load(fis);
            } catch (Exception e) {
                System.out.println("Error loading properties from " + path + ": " + e.getMessage());
            }
            if (!props.isEmpty()) {
                System.setProperty("databaseUrl", props.getProperty("connection.url"));
                System.setProperty("databaseUsername", props.getProperty("connection.username"));
                System.setProperty("databasePassword", props.getProperty("connection.password"));
                System.setProperty("databaseDriver", props.getProperty("connection.driver_class"));
                System.setProperty("databaseDialect", "org.hibernate.dialect.MySQLDialect");
                System.setProperty("useInMemoryDatabase", "false");
            }
        }
        props = super.getRuntimeProperties();
        props.setProperty("junit.username", "admin");
        props.setProperty("junit.password", "Admin123");
        return props;
    }

    @Test
    public void execute() throws Exception {
        authenticate();

        Map<String, TagHandler> tagHandlers = Context.getService(HtmlFormEntryService.class).getHandlers();
        Set<String> nodeNames = new TreeSet<>(tagHandlers.keySet());
        for (String nodeName : nodeNames) {
            System.out.println(nodeName);
        }
    }

    public static FormEntrySession getFormEntrySession(HtmlForm htmlForm) {
        try {
            Patient patient = HtmlFormEntryUtil.getFakePerson();
            HttpSession session = new MockHttpSession();
            session.setAttribute("emrContext.sessionLocationId", Context.getLocationService().getAllLocations().get(0));
            FormEntrySession s = new FormEntrySession(patient, null, FormEntryContext.Mode.ENTER, htmlForm, session);
            s.getHtmlToDisplay();
            return s;
        }
        catch (Exception e) {
            throw new IllegalStateException("Unable to get htmlform schema: ", e);
        }
    }

    public HtmlForm getHtmlFrom(String path) throws Exception {
        HtmlForm htmlform = new HtmlForm();
        Form form = new Form();
        htmlform.setForm(form);
        form.setEncounterType(new EncounterType());
        htmlform.setDateCreated(new Date());
        htmlform.setXmlData(FileUtils.readFileToString(new File(path), StandardCharsets.UTF_8));
        return htmlform;
    }
}
