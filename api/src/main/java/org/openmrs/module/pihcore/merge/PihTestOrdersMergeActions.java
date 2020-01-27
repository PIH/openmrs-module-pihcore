package org.openmrs.module.pihcore.merge;

import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.OrderService;
import org.openmrs.module.emrapi.merge.PatientMergeAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom actions to perform when merging two patients with Test Order. e.g. Pathology Lab Tracking orders
 */
@Component("pihTestOrdersMergeActions")
public class PihTestOrdersMergeActions implements PatientMergeAction{

    public static final String LAB_TRACKING_TESTORDER_TYPE_UUID = "52a447d3-a64a-11e3-9aeb-50e549534c5e";
    public static final String PATHOLOGY_TESTORDER_TYPE_UUID = "65c912c2-88cf-46c2-83ae-2b03b1f97d3a";

    @Autowired
    private OrderService orderService;

    private List<Integer> unvoidOrders;

    @Override
    public void beforeMergingPatients(Patient preferred, Patient notPreferred) {

        List<Order> orders = orderService.getAllOrdersByPatient(notPreferred);
        if (orders != null && orders.size() > 0) {
            this.unvoidOrders = new ArrayList<Integer>();
            for (Order order : orders) {
                String orderTypeUuid = order.getOrderType().getUuid();
                if ( (orderTypeUuid.equals(LAB_TRACKING_TESTORDER_TYPE_UUID) || orderTypeUuid.equals(PATHOLOGY_TESTORDER_TYPE_UUID) ) && !order.isVoided()) {
                    // void Lab Tracking orders so openmrs-core will allowed them to be merged
                    orderService.voidOrder(order, "pre-merge test orders");
                    this.unvoidOrders.add(order.getId());
                }
            }
        }
    }

    @Override
    public void afterMergingPatients(Patient preferred, Patient notPreferred) {

        if (unvoidOrders !=null && unvoidOrders.size() > 0) {
            for (Integer unvoidOrder : unvoidOrders) {
                // un-void the labtracking orders that were voided during the pre-merge step
                orderService.unvoidOrder(orderService.getOrder(unvoidOrder));
            }
        }
    }
}
