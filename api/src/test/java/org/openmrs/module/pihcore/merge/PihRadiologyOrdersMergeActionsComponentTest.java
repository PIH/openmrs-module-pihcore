package org.openmrs.module.pihcore.merge;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.disposition.DispositionService;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigLoader;
import org.openmrs.module.radiologyapp.RadiologyProperties;
import org.openmrs.module.radiologyapp.RadiologyService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class PihRadiologyOrdersMergeActionsComponentTest extends BaseModuleContextSensitiveTest {

    @Autowired
    @Qualifier("radiologyService")
    RadiologyService radiologyService;

    @Autowired
    @Qualifier("adtService")
    private AdtService adtService;

    @Autowired
    @Qualifier("patientService")
    private PatientService patientService;

    @Autowired
    @Qualifier("providerService")
    private ProviderService providerService;

    @Autowired
    @Qualifier("orderService")
    private OrderService orderService;

    @Autowired
    @Qualifier("conceptService")
    private ConceptService conceptService;

    @Autowired
    @Qualifier("locationService")
    private LocationService locationService;

    @Autowired
    @Qualifier("visitService")
    private VisitService visitService;

    @Autowired
    @Qualifier("encounterService")
    private EncounterService encounterService;


    @Autowired
    @Qualifier("emrApiProperties")
    private EmrApiProperties emrApiProperties;

    @Autowired
    @Qualifier("radiologyProperties")
    private RadiologyProperties radiologyProperties;

    @Autowired
    DispositionService dispositionService;

    @Autowired
    @Qualifier("pihRadiologyOrdersMergeActions")
    private PihRadiologyOrdersMergeActions pihRadiologyOrdersMergeActions;

    @Autowired
    @Qualifier("adminService")
    private AdministrationService administrationService;

    @Before
    public void beforeAllTests() throws Exception {
        executeDataSet("pihRadiologyOrdersMergeActionsComponentTestDataset.xml");
        adtService.addPatientMergeAction(pihRadiologyOrdersMergeActions);
    }

    @Test
    public void shouldMergePatientsWhenNonPreferredPatientHasNoOrders()
            throws Exception {

        Patient preferredPatient = patientService.getPatient(6);
        List<Encounter> encountersByPatient = encounterService.getEncountersByPatient(preferredPatient);
        // patient has 1 Radiology Encounter
        Assert.assertEquals(1, encountersByPatient.size());
        Assert.assertEquals("RadiologyOrder", encountersByPatient.get(0).getEncounterType().getName());
        Set<Order> orders = encountersByPatient.get(0).getOrders();
        Assert.assertEquals(1, orders.size());
        Order radiologyOrder = null;
        for (Order order : orders) {
            radiologyOrder = order;
        }
        Assert.assertEquals("RadiologyOrder", radiologyOrder.getOrderType().getName());

        Patient nonPreferredPatient = patientService.getPatient(10001);
        adtService.mergePatients(preferredPatient, nonPreferredPatient);

        assertTrue("RadiologyOrder is not present after merging the patients", orderService.getAllOrdersByPatient(preferredPatient).contains(radiologyOrder));

    }

    @Test
    public void shouldMergePatientsWhenNonPreferredPatientHasOnlyRadiologyOrders()
            throws Exception {


        PihRadiologyOrdersMergeActions pihRadiologyOrdersMergeActions = Context.getRegisteredComponent("pihRadiologyOrdersMergeActions", PihRadiologyOrdersMergeActions.class);
        Config config = mock(Config.class);
        config = new Config(ConfigLoader.load("mirebalais"));
        pihRadiologyOrdersMergeActions.setConfig(config);

        Patient preferredPatient = patientService.getPatient(6);

        Patient nonPreferredPatient = patientService.getPatient(10003);
        List<Encounter> encountersByPatient = encounterService.getEncountersByPatient(nonPreferredPatient);
        // patient has 1 Radiology Encounter
        Assert.assertEquals(1, encountersByPatient.size());
        Assert.assertEquals("RadiologyOrder", encountersByPatient.get(0).getEncounterType().getName());
        Set<Order> orders = encountersByPatient.get(0).getOrders();
        Assert.assertEquals(1, orders.size());
        Order radiologyOrder = null;
        for (Order order : orders) {
            radiologyOrder = order;
        }
        Assert.assertEquals("RadiologyOrder", radiologyOrder.getOrderType().getName());
        adtService.mergePatients(preferredPatient, nonPreferredPatient);

        assertTrue("RadiologyOrder was not merged", orderService.getAllOrdersByPatient(preferredPatient).contains(radiologyOrder));
    }

    // TODO un-ignore once we have the EMR API module updated
    @Test
    @Ignore
    public void shouldMergePatientsWhenNonPreferredPatientHasRadiologyOrdersInOverlappingVisit()
            throws Exception {


        PihRadiologyOrdersMergeActions pihRadiologyOrdersMergeActions = Context.getRegisteredComponent("pihRadiologyOrdersMergeActions", PihRadiologyOrdersMergeActions.class);
        Config config = mock(Config.class);
        config = new Config(ConfigLoader.load("mirebalais"));
        pihRadiologyOrdersMergeActions.setConfig(config);

        Patient preferredPatient = patientService.getPatient(6);

        Patient nonPreferredPatient = patientService.getPatient(10004);
        List<Encounter> encountersByPatient = encounterService.getEncountersByPatient(nonPreferredPatient);
        // patient has 1 Radiology Encounter
        Assert.assertEquals(1, encountersByPatient.size());
        Assert.assertEquals("RadiologyOrder", encountersByPatient.get(0).getEncounterType().getName());
        Set<Order> orders = encountersByPatient.get(0).getOrders();
        Assert.assertEquals(1, orders.size());
        Order radiologyOrder = null;
        for (Order order : orders) {
            radiologyOrder = order;
        }
        Assert.assertEquals("RadiologyOrder", radiologyOrder.getOrderType().getName());
        adtService.mergePatients(preferredPatient, nonPreferredPatient);

        assertTrue("RadiologyOrder was not merged", orderService.getAllOrdersByPatient(preferredPatient).contains(radiologyOrder));
    }


    // this restriction has been relaxed in Core 2.x  A "non-preferred" patient can now have orders as long as they aren't of the same type as those of the preferred patient
    // ignoring this test, should eventually remove but keeping it around for now
    @Test
    @Ignore
    public void shouldFailToMergePatientsWhenNonPreferredPatientHasDrugOrders()
            throws Exception {

        Patient preferredPatient = patientService.getPatient(6);

        // patient with unvoided DrugOrder
        Patient nonPreferredPatient = patientService.getPatient(10002);
        List<Encounter> encountersByPatient = encounterService.getEncountersByPatient(nonPreferredPatient);
        // nonPreferred patient has no RadiologyOrders
        Assert.assertEquals(1, encountersByPatient.size());
        Assert.assertNotEquals("RadiologyOrder", encountersByPatient.get(0).getEncounterType().getName());
        List<Order> allNonPreferredPatientOrders = orderService.getAllOrdersByPatient(nonPreferredPatient);
        Assert.assertEquals(1, allNonPreferredPatientOrders.size());
        Order nonPreferredPatientOrder = null;
        for (Order order : allNonPreferredPatientOrders) {
            nonPreferredPatientOrder = order;
        }
        Assert.assertNotEquals("RadiologyOrder", nonPreferredPatientOrder.getOrderType().getName());

        try {
            adtService.mergePatients(preferredPatient, nonPreferredPatient);
        } catch (Exception e) {
            Assert.assertEquals("Cannot merge patients where the not preferred patient has unvoided orders", e.getMessage());
        }

        assertTrue("DrugOrder was merged", (orderService.getAllOrdersByPatient(preferredPatient).contains(nonPreferredPatientOrder) == false));
    }
}
