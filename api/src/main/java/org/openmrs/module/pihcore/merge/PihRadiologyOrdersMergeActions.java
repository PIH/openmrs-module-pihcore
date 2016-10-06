package org.openmrs.module.pihcore.merge;

import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.api.OrderService;
import org.openmrs.module.emrapi.merge.PatientMergeAction;
import org.openmrs.module.pihcore.config.Components;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.radiologyapp.RadiologyProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom actions to perform when merging two patients with radiology orders.
 * Since upgrading openmrs-core to 1.10 we are no longer able to merge patients with
 * radiology orders.
 *
 * This pre-merge action checks if the non-preferred patient has any Radiology orders and voids them
 * before the merging and then un-voids them after the merging.
 */
@Component("pihRadiologyOrdersMergeActions")
public class PihRadiologyOrdersMergeActions implements PatientMergeAction {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RadiologyProperties radiologyProperties;

    @Autowired
    private Config config;

    private List<Integer> unvoidOrders;

    public void setConfig(Config config) {
        this.config = config;
    }

    /**
     * This method will be called before calling the underlying OpenMRS
     * {@link org.openmrs.api.PatientService#mergePatients(org.openmrs.Patient, org.openmrs.Patient)} method, but in the
     * same transaction. Any thrown exception will cancel the merge
     *
     * @param preferred
     * @param notPreferred
     */
    @Override
    public void beforeMergingPatients(Patient preferred, Patient notPreferred) {
        if (config.isComponentEnabled(Components.RADIOLOGY)) {
            OrderType radiologyTestOrderType = radiologyProperties.getRadiologyTestOrderType();
            List<Order> orders = orderService.getAllOrdersByPatient(notPreferred);
            if (orders != null && orders.size() > 0) {
                this.unvoidOrders = new ArrayList<Integer>();
                for (Order order : orders) {
                    if (order.getOrderType().equals(radiologyTestOrderType) && !order.isVoided()) {
                        // void Radiology orders so openmrs-core will allowed them to be merged
                        orderService.voidOrder(order, "pre-merge radiology orders");
                        this.unvoidOrders.add(order.getId());
                    }
                }
            }
        }
    }

    /**
     * This method will be called after calling the underlying OpenMRS
     * {@link org.openmrs.api.PatientService#mergePatients(org.openmrs.Patient, org.openmrs.Patient)} method, but in the
     * same transaction.
     *
     * @param preferred
     * @param notPreferred
     */
    @Override
    public void afterMergingPatients(Patient preferred, Patient notPreferred) {
        if (unvoidOrders !=null && unvoidOrders.size() > 0) {
            for (Integer unvoidOrder : unvoidOrders) {
                // un-void the radiology orders that were voided during the pre-merge step
                orderService.unvoidOrder(orderService.getOrder(unvoidOrder));
            }
        }
    }

    // for injecting mocks during tests
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    public void setRadiologyProperties(RadiologyProperties radiologyProperties) {
        this.radiologyProperties = radiologyProperties;
    }
}
