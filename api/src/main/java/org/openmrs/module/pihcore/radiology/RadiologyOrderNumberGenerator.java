package org.openmrs.module.pihcore.radiology;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.OrderContext;
import org.openmrs.api.OrderNumberGenerator;
import org.openmrs.api.OrderService;
import org.openmrs.module.idgen.validator.LuhnMod10IdentifierValidator;
import org.openmrs.module.pihcore.PihCoreConstants;
import org.openmrs.module.pihcore.service.PihCoreService;
import org.openmrs.module.radiologyapp.RadiologyProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(PihCoreConstants.RADIOLOGY_ORDER_NUMBER_GENERATOR_BEAN_ID)
public class RadiologyOrderNumberGenerator implements OrderNumberGenerator {

    @Autowired
    private RadiologyProperties radiologyProperties;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PihCoreService pihCoreService;

    private static final String ORDER_NUMBER_PREFIX = "ORD-";

    @Override
    public String getNewOrderNumber(OrderContext orderContext) {
        if (orderContext!=null && orderContext.getOrderType() != null && orderContext.getOrderType().equals(radiologyProperties.getRadiologyTestOrderType())) {
            String orderNumber = pihCoreService.getNextRadiologyOrderNumberSeedSequenceValue().toString();
            orderNumber =  new LuhnMod10IdentifierValidator().getValidIdentifier(orderNumber);  // add check digit
            return StringUtils.leftPad(orderNumber, 10, "0"); // pad to ten digits
        }
        else {
            // use standard order format
            return ORDER_NUMBER_PREFIX + orderService.getNextOrderNumberSeedSequenceValue();
        }
    }

    // setters to allow injection of mocks
    public void setRadiologyProperties(RadiologyProperties radiologyProperties) {
        this.radiologyProperties = radiologyProperties;
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    public void setPihCoreService(PihCoreService pihCoreService) {
        this.pihCoreService = pihCoreService;
    }
}
