package org.openmrs.module.pihcore.merge;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.disposition.DispositionService;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigLoader;
import org.openmrs.module.radiologyapp.RadiologyProperties;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class PihTestOrdersMergeActionsComponentTest extends BaseModuleContextSensitiveTest {

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
    @Qualifier("pihTestOrdersMergeActions")
    private PihTestOrdersMergeActions pihTestOrdersMergeActions;

    @Autowired
    @Qualifier("adminService")
    private AdministrationService administrationService;

    @Before
    public void beforeAllTests() throws Exception {
        executeDataSet("pihRadiologyOrdersMergeActionsComponentTestDataset.xml");
        adtService.addPatientMergeAction(pihTestOrdersMergeActions);
    }

    @Test
    public void shouldMergePatientsWhenNonPreferredPatientHasNoTestOrders()
            throws Exception {

        Patient preferredPatient = patientService.getPatient(10005);
        List<Encounter> encountersByPatient = encounterService.getEncountersByPatient(preferredPatient);
        // patient has 1 Test
        Assert.assertEquals(1, encountersByPatient.size());
        Assert.assertEquals("TestOrder", encountersByPatient.get(0).getEncounterType().getName());
        Set<Order> orders = encountersByPatient.get(0).getOrders();
        Assert.assertEquals(1, orders.size());
        Order testOrder = orders.iterator().next();

        Assert.assertEquals("Test order", testOrder.getOrderType().getName());

        Patient nonPreferredPatient = patientService.getPatient(10001);
        adtService.mergePatients(preferredPatient, nonPreferredPatient);

        assertTrue("TestOrder is present after merging the patients", orderService.getAllOrdersByPatient(preferredPatient).contains(testOrder));

    }

    @Test
    public void shouldMergePatientsWhenNonPreferredPatientHasOnlyTestOrders()
            throws Exception {

        Patient preferredPatient = patientService.getPatient(6);
        Patient nonPreferredPatient = patientService.getPatient(10005);
        List<Encounter> encountersByPatient = encounterService.getEncountersByPatient(nonPreferredPatient);
        // patient has 1 Test Order Encounter
        Assert.assertEquals(1, encountersByPatient.size());
        Assert.assertEquals("TestOrder", encountersByPatient.get(0).getEncounterType().getName());
        Set<Order> orders = encountersByPatient.get(0).getOrders();
        Assert.assertEquals(1, orders.size());
        Order testOrder = orders.iterator().next();

        Assert.assertEquals("Test order", testOrder.getOrderType().getName());
        adtService.mergePatients(preferredPatient, nonPreferredPatient);

        assertTrue("Test Order was merged", orderService.getAllOrdersByPatient(preferredPatient).contains(testOrder));
    }

    @Test
    public void shouldMergePatientsWhenNonPreferredPatientHasPathologyTestOrders()
            throws Exception {

        Patient preferredPatient = patientService.getPatient(6);
        Patient nonPreferredPatient = patientService.getPatient(10006);
        List<Encounter> encountersByPatient = encounterService.getEncountersByPatient(nonPreferredPatient);
        // patient has 1 Test Order Encounter
        Assert.assertEquals(1, encountersByPatient.size());
        Assert.assertEquals("TestOrder", encountersByPatient.get(0).getEncounterType().getName());
        Set<Order> orders = encountersByPatient.get(0).getOrders();
        Assert.assertEquals(1, orders.size());
        Order testOrder = orders.iterator().next();

        Assert.assertEquals("PathologyOrder", testOrder.getOrderType().getName());
        adtService.mergePatients(preferredPatient, nonPreferredPatient);

        assertTrue("Pathology Test Order was merged", orderService.getAllOrdersByPatient(preferredPatient).contains(testOrder));
    }

    @Test
    public void shouldMergePatientsFailWhenBothPatientHaveOpenPathologyTestOrders()
            throws Exception {

        Patient preferredPatient = patientService.getPatient(10006);
        Patient nonPreferredPatient = patientService.getPatient(10007);
        List<Encounter> encountersByPatient = encounterService.getEncountersByPatient(nonPreferredPatient);
        // patient has 1 Test Order Encounter
        Assert.assertEquals(1, encountersByPatient.size());
        Assert.assertEquals("TestOrder", encountersByPatient.get(0).getEncounterType().getName());
        Set<Order> orders = encountersByPatient.get(0).getOrders();
        Assert.assertEquals(1, orders.size());
        Order testOrder = orders.iterator().next();

        Assert.assertEquals("PathologyOrder", testOrder.getOrderType().getName());
        try {
            adtService.mergePatients(preferredPatient, nonPreferredPatient);
        } catch (Exception e) {
            Assert.assertEquals("Patient.merge.cannotHaveSameTypeActiveOrders", e.getMessage());
        }

        //assertTrue("Pathology Test Order was merged", orderService.getAllOrdersByPatient(preferredPatient).contains(testOrder));
    }
}
