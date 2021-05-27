package org.openmrs.module.pihcore.htmlformentry;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.ConceptSource;
import org.openmrs.Encounter;
import org.openmrs.GlobalProperty;
import org.openmrs.Obs;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.module.htmlformentry.HtmlFormEntryConstants;
import org.openmrs.module.htmlformentry.RegressionTestHelper;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.CoreConceptMetadataBundle;
import org.openmrs.module.pihcore.setup.HtmlFormSetup;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.conceptSource;

public class CauseOfDeathListTagHandlerComponentTest extends PihCoreContextSensitiveTest {

    @Autowired
    private TestDataManager testData;

    @Autowired
    private ConceptService conceptService;

    @Autowired @Qualifier("adminService")
    private AdministrationService administrationService;

    private ObjectMapper jackson = new ObjectMapper();

    private Concept codedCauseConcept;
    private Concept nonCodedCauseConcept;
    private Concept sequenceConcept;
    private Concept groupingConcept;
    private Concept unknownConcept;

    @Before
    public void setUp() throws Exception {

        ConceptSource ciel = conceptSource("CIEL", "Columbia International eHealth Laboratory concept ID", null, CoreConceptMetadataBundle.ConceptSources.CIEL);
        conceptService.saveConceptSource(ciel);

        Context.setLocale(new Locale("en"));  // for some reason, one of the other tests leaves the context dirty, and the locale set to "fr", which causes the tests to fail when matching obs
        unknownConcept = testData.concept().datatype("N/A").conceptClass("Misc")
                .name("Unknown Concept").save();
        administrationService.saveGlobalProperty(
                new GlobalProperty(HtmlFormEntryConstants.GP_UNKNOWN_CONCEPT, unknownConcept.getUuid()));

        codedCauseConcept = testData.concept().datatype("Coded").conceptClass("Misc")
                .name("CAUSE OF DEATH FROM DEATH CERTIFICATE").mapping("SAME-AS", "CIEL:1814").save();

        nonCodedCauseConcept = testData.concept().datatype("Text").conceptClass("Misc")
                .name("Probable cause of death (text)").mapping("SAME-AS", "CIEL:160218").save();

        sequenceConcept = testData.conceptNumeric().datatype("Numeric").conceptClass("Misc")
                .name("DIAGNOSIS SEQUENCE NUMBER").mapping("SAME-AS", "CIEL:1815").save();

        groupingConcept = testData.concept().datatype("N/A").conceptClass("Misc")
                .name("CAUSES OF DEATH FROM DEATH CERTIFICATE").mapping("SAME-AS", "CIEL:1816")
                .setMembers(codedCauseConcept, nonCodedCauseConcept, sequenceConcept).save();

        HtmlFormSetup.setupHtmlFormEntryTagHandlers();
    }

    @Test
    public void testCreateAndEdit() throws Exception {
        final ConceptName causeOne = conceptService.getConceptNameByUuid("2f7263e4-2a57-49b5-aef0-af9a259aac65");
        final Concept causeTwo = conceptService.getConcept(13);
        final Concept editedCauseTwo = conceptService.getConcept(11);
        final String causeThree = "Cause Three";
        final String editedCauseThree = "Edited Cause Three";
        final Concept causeFour = conceptService.getConcept(16);

        ArrayNode submission = jackson.createArrayNode();
        ObjectNode one = submission.addObject();
        one.put("ConceptName", causeOne.getUuid());
        ObjectNode two = submission.addObject();
        two.put("Concept", causeTwo.getUuid());
        ObjectNode three = submission.addObject();
        three.put("NonCodedValue", causeThree);
        ObjectNode four = submission.addObject();
        four.put("Concept", causeFour.getUuid());
        final String createJson = jackson.writeValueAsString(submission);

        two.put("Concept", editedCauseTwo.getUuid());
        three.put("NonCodedValue", editedCauseThree);
        submission.remove(3); // this removes 'four'
        final String editJson = jackson.writeValueAsString(submission);

        final Date date = new Date();

        new RegressionTestHelper() {
            @Override
            public String getXmlDatasetPath() {
                return "";
            }

            @Override
            public String getFormName() {
                return "causesOfDeathForm";
            }

            @Override
            public String[] widgetLabels() {
                return new String[] { "Date:", "Location:", "Provider:", "Causes:" };
            }

            @Override
            public void setupRequest(MockHttpServletRequest request, Map<String, String> widgets) {
                request.setParameter(widgets.get("Date:"), dateAsString(date));
                request.setParameter(widgets.get("Location:"), "2");
                request.setParameter(widgets.get("Provider:"), "1");
                request.setParameter(widgets.get("Causes:"), createJson);
            }

            @Override
            public void testResults(SubmissionResults results) {
                results.assertNoErrors();
                results.assertEncounterCreated();
                results.assertProvider(1);
                results.assertLocation(2);
                results.assertObsGroupCreatedCount(4);
                results.assertObsGroupCreated(groupingConcept.getConceptId(),
                        codedCauseConcept.getId(), causeOne,
                        sequenceConcept.getId(), 1);
                results.assertObsGroupCreated(groupingConcept.getConceptId(),
                        codedCauseConcept.getId(), causeTwo,
                        sequenceConcept.getId(), 2);
                results.assertObsGroupCreated(groupingConcept.getConceptId(),
                        nonCodedCauseConcept.getId(), causeThree,
                        sequenceConcept.getId(), 3);
                results.assertObsGroupCreated(groupingConcept.getConceptId(),
                        codedCauseConcept.getId(), causeFour,
                        sequenceConcept.getId(), 4);
                assertThat(results.getPatient().getCauseOfDeath(), is(causeFour));
            }

            @Override
            public boolean doEditEncounter() {
                return true;
            }

            @Override
            public String[] widgetLabelsForEdit() {
                return widgetLabels();
            }

            @Override
            public void setupEditRequest(MockHttpServletRequest request, Map<String, String> widgets) {
                request.setParameter(widgets.get("Date:"), dateAsString(date));
                request.setParameter(widgets.get("Location:"), "2");
                request.setParameter(widgets.get("Provider:"), "1");
                request.setParameter(widgets.get("Causes:"), editJson);
            }

            @Override
            public void testEditedResults(SubmissionResults results) {
                results.assertNoErrors();
                results.assertEncounterCreated();
                results.assertProvider(1);
                results.assertLocation(2);
                // there should be one voided member obs (causeTwo->causeTwoEdited), and one voided obs group (causeThree)
                results.assertObsVoided(groupingConcept.getConceptId(), null); // was causeThree
                assertVoidedObsInObsGroup(results.getEncounterCreated(), groupingConcept, codedCauseConcept, causeTwo);
                results.assertObsGroupCreatedCount(3);
                results.assertObsGroupCreated(groupingConcept.getConceptId(),
                        codedCauseConcept.getId(), causeOne,
                        sequenceConcept.getId(),1);
                results.assertObsGroupCreated(groupingConcept.getConceptId(),
                        codedCauseConcept.getId(), editedCauseTwo,
                        sequenceConcept.getId(), 2);
                results.assertObsGroupCreated(groupingConcept.getConceptId(),
                        nonCodedCauseConcept.getId(), editedCauseThree,
                        sequenceConcept.getId(), 3);
                assertThat(results.getPatient().getCauseOfDeath(), is(unknownConcept));
            }

            private void assertVoidedObsInObsGroup(Encounter encounter, Concept groupingConcept, Concept concept, Concept value) {
                for (Obs candidate : encounter.getObsAtTopLevel(true)) {
                    if (groupingConcept.equals(candidate.getConcept())) {
                        for (Obs member : candidate.getGroupMembers(true)) {
                            if (member.getVoided() && member.getConcept().equals(concept) && member.getValueCoded().equals(value)) {
                                return;
                            }
                        }
                    }
                }
                Assert.fail("Did not find a voided obs of " + concept + "=" + value + " inside a group of " + groupingConcept);
            }
        }.run();
    }

}