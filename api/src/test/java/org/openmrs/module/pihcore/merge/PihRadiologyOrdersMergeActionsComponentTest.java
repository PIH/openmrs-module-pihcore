package org.openmrs.module.pihcore.merge;


import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.disposition.DispositionService;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;
import org.openmrs.module.pihcore.config.Components;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.radiologyapp.RadiologyProperties;
import org.openmrs.module.radiologyapp.RadiologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertTrue;

;

public class PihRadiologyOrdersMergeActionsComponentTest extends PihCoreContextSensitiveTest {

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

    private Config config;

    @BeforeEach
    public void beforeAllTests() throws Exception {
        executeDataSet("pihRadiologyOrdersMergeActionsComponentTestDataset.xml");
        config = mock(Config.class);
        when(config.isComponentEnabled(Components.RADIOLOGY)).thenReturn(true);
        pihRadiologyOrdersMergeActions.setConfig(config);
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

        assertTrue("RadiologyOrder is present after merging the patients", orderService.getAllOrdersByPatient(preferredPatient).contains(radiologyOrder));

    }

    @Test
    public void shouldMergePatientsWhenNonPreferredPatientHasOnlyRadiologyOrders()
            throws Exception {

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

    @Test
    public void shouldMergePatientsWhenNonPreferredPatientHasRadiologyOrdersInOverlappingVisit()
            throws Exception {

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
    public void shouldMergePatientsWhenNonPreferredPatientHasDrugOrders()
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

        assertTrue("DrugOrder was merged", (orderService.getAllOrdersByPatient(preferredPatient).contains(nonPreferredPatientOrder) == true));
    }
}
