package org.openmrs.module.pihcore.htmlformentry;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.htmlformentryui.HtmlFormUtil;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.pihcore.deploy.bundle.OrderEntryConceptsFromMetadataSharing;
import org.openmrs.module.pihcore.deploy.bundle.haiti.mirebalais.MirebalaisBundle;
import org.openmrs.module.pihcore.setup.HtmlFormSetup;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;

import java.io.InputStream;
import java.util.Properties;

@SkipBaseSetup
public class HtmlFormComponentTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private MetadataDeployService metadataDeployService;

    @Override
    public Properties getRuntimeProperties() {
        Properties p = super.getRuntimeProperties();
        p.setProperty("pih.config", "pihcore");
        return p;
    }

    @Before
    public void beforeEachTest() throws Exception {
        initializeInMemoryDatabase();
        executeDataSet("requiredDataTestDataset.xml");
        authenticate();

        // load the API test bundle of MDS concepts
        metadataDeployService.installBundle(Context.getRegisteredComponents(OrderEntryConceptsFromMetadataSharing.class).get(0));

        // set up metadata from pih core first
        metadataDeployService.installBundle(Context.getRegisteredComponents(MirebalaisBundle.class).get(0));

        // load the test bundle of MDS concepts
        metadataDeployService.installBundle(Context.getRegisteredComponents(ConceptsFromMetadataSharing.class).get(0));

        HtmlFormSetup.setupHtmlFormEntryTagHandlers();
    }

    @Test
    public void testHtmlForms() throws Exception {
        String[] formsToTest = {
                "patientRegistration.xml",
                "patientRegistration-rs.xml",
                "patientRegistration-contact.xml",
                "patientRegistration-social.xml",
                //"surgicalPostOpNote.xml",
                //"vitals.xml",
                "transferNote.xml",
                "dischargeNote.xml",
                //"outpatientConsult.xml",
                //"edNote.xml",
                "deathCertificate.xml",
                //"section-history.xml",
                // TODO fix so this test passes
                //"haiti/section-exam.xml",
                "section-dx.xml",
                //"haiti/checkin.xml",
                //"haiti/liveCheckin.xml",
                "haiti/patientRegistration-contact.xml",
                "haiti/patientRegistration-social.xml",
                //"liberia/checkin.xml",
                //"liberia/liveCheckin.xml",

        };

        for (String formName : formsToTest) {
            // loading up all of the UI Framework beans to fetch this resource is problematic, so just get it directly:
            InputStream in = getClass().getClassLoader().getResourceAsStream("web/module/resources/htmlforms/" + formName);
            String xml = IOUtils.toString(in);
            IOUtils.closeQuietly(in);

            try {
                HtmlForm form = HtmlFormUtil.getHtmlFormFromResourceXml(Context.getFormService(), Context.getService(HtmlFormEntryService.class), xml);
                FormEntrySession fes = new FormEntrySession(new Patient(), form, FormEntryContext.Mode.ENTER, new MockHttpSession());
                String html = fes.getHtmlToDisplay();
            }
            catch (Exception ex) {
                ex.printStackTrace();
                Assert.fail("Failed to load " + formName + ": " + ex.getMessage());
            }
        }
    }


}
