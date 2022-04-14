package org.openmrs.module.pihcore.radiology;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.OrderType;
import org.openmrs.api.OrderContext;
import org.openmrs.api.OrderService;
import org.openmrs.module.pihcore.service.PihCoreService;
import org.openmrs.module.radiologyapp.RadiologyProperties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RadiologyOrderNumberGeneratorTest {

    private RadiologyOrderNumberGenerator generator;

    private PihCoreService pihCoreService;

    private OrderService orderService;

    private RadiologyProperties radiologyProperties;

    private OrderType radiologyOrderType = new OrderType();

    private OrderType otherOrderType = new OrderType();

    @BeforeEach
    public void setup() {
        pihCoreService = mock(PihCoreService.class);
        orderService = mock(OrderService.class);
        radiologyProperties = mock(RadiologyProperties.class);

        when(orderService.getNextOrderNumberSeedSequenceValue()).thenReturn(new Long(15));
        when(pihCoreService.getNextRadiologyOrderNumberSeedSequenceValue()).thenReturn(new Long(10));
        when(radiologyProperties.getRadiologyTestOrderType()).thenReturn(radiologyOrderType);

        generator = new RadiologyOrderNumberGenerator();
        generator.setRadiologyProperties(radiologyProperties);
        generator.setPihCoreService(pihCoreService);
        generator.setOrderService(orderService);
    }

    @Test
    public void shouldReturnStandardOrderNumberForNonRadiologyOrder() {
        OrderContext context = new OrderContext();
        context.setOrderType(otherOrderType);
        assertThat(generator.getNewOrderNumber(context), is("ORD-15"));
    }

    @Test
    public void shouldReturnRadiologyOrderNumberForRadiologyOrder() {
        OrderContext context = new OrderContext();
        context.setOrderType(radiologyOrderType);
        assertThat(generator.getNewOrderNumber(context), is("0000000109"));
    }

}
