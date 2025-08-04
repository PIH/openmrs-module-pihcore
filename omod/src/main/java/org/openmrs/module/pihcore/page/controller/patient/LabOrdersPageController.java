package org.openmrs.module.pihcore.page.controller.patient;

import org.apache.commons.lang.BooleanUtils;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.TestOrder;
import org.openmrs.api.OrderService;
import org.openmrs.api.db.hibernate.HibernateUtil;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.pihcore.PihUiUtils;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LabOrdersPageController {

    public void get(PageModel model, UiUtils ui,
                      @InjectBeans PatientDomainWrapper patientDomainWrapper,
                      @RequestParam(value = "patient") Patient patient,
                      @SpringBean("orderService") OrderService orderService) throws IOException {

        List<TestOrder> labOrders = new ArrayList<>();
        for (Order order : orderService.getAllOrdersByPatient(patient)) {
            order = HibernateUtil.getRealObjectFromProxy(order);
            if (order instanceof TestOrder && BooleanUtils.isNotTrue(order.getVoided())) {
                labOrders.add((TestOrder) order);
            }
        }

        // Sort these drug orders in chronological order
        labOrders.sort((d1, d2) -> {
            // Get all previous orders for d1.  If any are d2, then d1 is later
            for (Order d1Prev = d1.getPreviousOrder(); d1Prev != null; d1Prev = d1Prev.getPreviousOrder()) {
                if (d1Prev.equals(d2)) {
                    return 1;
                }
            }
            // Get all previous orders for d2.  If any are d1, then d2 is later
            for (Order d2Prev = d2.getPreviousOrder(); d2Prev != null; d2Prev = d2Prev.getPreviousOrder()) {
                if (d2Prev.equals(d1)) {
                    return -1;
                }
            }
            // If neither is a revision of the other, then compare based on effective start date
            int dateCompare = d1.getEffectiveStartDate().compareTo(d2.getEffectiveStartDate());
            if (dateCompare != 0) {
                return dateCompare;
            }
            // If they are still the same, then order based on end date
            int ret = OpenmrsUtil.compareWithNullAsLatest(d1.getEffectiveStopDate(), d2.getEffectiveStopDate());
            if (ret == 0) {
                // Finally, order based on orderId
                ret = d1.getOrderId().compareTo(d2.getOrderId());
            }
            return ret;
        });

        // Order should be chronologically descending
        Collections.reverse(labOrders);

        List<TestOrder> activeOrders = new ArrayList<>();
        List<TestOrder> inactiveOrders = new ArrayList<>();
        Map<TestOrder, TestOrder> ordersToDiscontinueOrders = new HashMap<>();

        for (TestOrder order : labOrders) {
            if (order.getAction() == Order.Action.DISCONTINUE) {
                ordersToDiscontinueOrders.put((TestOrder)order.getPreviousOrder(), order);
            }
            else {
                if (order.isActive()) {
                    activeOrders.add(order);
                }
                else {
                    inactiveOrders.add(order);
                }
            }
        }

        patientDomainWrapper.setPatient(patient);
        model.addAttribute("patient", patientDomainWrapper);
        model.addAttribute("labOrders", labOrders);
        model.addAttribute("activeOrders", activeOrders);
        model.addAttribute("inactiveOrders", inactiveOrders);
        model.addAttribute("ordersToDiscontinueOrders", ordersToDiscontinueOrders);
        model.addAttribute("pihui", new PihUiUtils());
    }
}
