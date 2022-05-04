package org.openmrs.module.pihcore.radiology;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Order;
import org.openmrs.TestOrder;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderContext;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProviderService;
import org.openmrs.module.radiologyapp.RadiologyOrder;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RadiologyOrderNumberGeneratorComponentTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private EncounterService encounterService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private ProviderService providerService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ConceptService conceptService;

    @Autowired
    @Qualifier("adminService")
    private AdministrationService adminService;

    @BeforeEach
    public void setup() throws Exception {
        executeDataSet("radiologyOrderNumberGeneratorTestDataset.xml");
        // it appears we need to fully commit this in order for it to be properly fetched when called with LockOptions.UPGRADE
        // (see PihCoreDAO.getNextRadiologyOrderNumberSeedSequenceValue()
        getConnection().commit();
    }

    @AfterEach
    public void tearDown() throws Exception {
        deleteAllData();
    }

    @Test
    public void shouldGenerateStandardOrderNumber() {

        // new test order
        Order order = new TestOrder();
        order.setEncounter(encounterService.getEncounter(6));
        order.setPatient(patientService.getPatient(7));
        order.setConcept(conceptService.getConcept(5497));
        order.setOrderer(providerService.getProvider(1));
        order.setCareSetting(orderService.getCareSetting(1));
        order.setOrderType(orderService.getOrderType(2));
        order.setEncounter(encounterService.getEncounter(3));
        order.setDateActivated(new Date());
        OrderContext orderContext = new OrderContext();
        order = orderService.saveOrder(order, orderContext);

        // the GP "order.nextOrderNumberSeed" is set to 1 in the standard test dataset
        assertThat(order.getOrderNumber(), is("ORD-1"));
    }

    @Test
    public void shouldGenerateRadiologyOrderNumber() {

        // new test order
        Order order = new RadiologyOrder();
        order.setEncounter(encounterService.getEncounter(6));
        order.setPatient(patientService.getPatient(7));
        order.setConcept(conceptService.getConcept(5497));
        order.setOrderer(providerService.getProvider(1));
        order.setCareSetting(orderService.getCareSetting(1));
        order.setOrderType(orderService.getOrderType(1001));
        order.setEncounter(encounterService.getEncounter(3));
        order.setDateActivated(new Date());
        OrderContext orderContext = new OrderContext();
        orderContext.setOrderType(orderService.getOrderType(1001));
        order = orderService.saveOrder(order, orderContext);

        assertThat(order.getOrderNumber(), is("0000000125"));
    }
}
