package org.openmrs.module.pihcore.order;

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

/**
 * Site-wide order number generator, despite the historical "Radiology" naming of its predecessor.
 * <p>
 * This bean is wired in via the OpenMRS core global property {@code order.orderNumberGeneratorBeanId}
 * (see core's {@code OpenmrsConstants.GP_ORDER_NUMBER_GENERATOR_BEAN_ID}). When that global property is
 * set to this bean's id ({@link PihCoreConstants#PIH_ORDER_NUMBER_GENERATOR_BEAN_ID}), core's
 * {@code OrderServiceImpl} delegates order number generation for *every* order type, not just radiology
 * orders, to this class.
 * <p>
 * Behavior:
 * <ul>
 *     <li>Radiology orders are special-cased: they get a separate numeric sequence (see
 *     {@link PihCoreService#getNextRadiologyOrderNumberSeedSequenceValue()}), padded to 10 digits with a
 *     trailing Luhn mod-10 check digit.</li>
 *     <li>All other order types fall back to reproducing OpenMRS core's own default order number format,
 *     {@code "ORD-" + orderService.getNextOrderNumberSeedSequenceValue()}. This is duplicated here rather
 *     than delegated to core because core exposes no public API for it: {@code OrderService} does not
 *     declare a {@code getNewOrderNumber} method, and the {@code "ORD-"} prefix constant on
 *     {@code OrderServiceImpl} is private. (It's technically possible to get core's default behavior by
 *     casting the {@code orderService} Spring bean to {@code OrderNumberGenerator} - {@code OrderServiceImpl}
 *     implements both {@code OrderService} and {@code OrderNumberGenerator}, and its unconfigured
 *     {@code TransactionProxyFactoryBean} auto-detects every interface it implements - but that depends on
 *     internal Spring proxy wiring rather than a documented contract, so we intentionally don't rely on it.)</li>
 * </ul>
 */
@Component(PihCoreConstants.PIH_ORDER_NUMBER_GENERATOR_BEAN_ID)
public class PihOrderNumberGenerator implements OrderNumberGenerator {

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
