package org.openmrs.module.pihcore.fragment.controller.dashboardwidgets;

import org.openmrs.Concept;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.parameter.OrderSearchCriteria;
import org.openmrs.parameter.OrderSearchCriteriaBuilder;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PendingLabOrdersFragmentController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public void controller(@SpringBean("patientService") PatientService patientService,
                           @SpringBean("orderService")OrderService orderService,
                           @FragmentParam("app") AppDescriptor app,
                           FragmentConfiguration config,
                           FragmentModel model) {

        Object patientConfig = config.get("patient");
        Patient patient = null;
        if (patientConfig != null && (patientConfig instanceof PatientDomainWrapper)) {
            patient = ((PatientDomainWrapper) patientConfig).getPatient();
        }
        if (patient == null) {
            throw new IllegalArgumentException("No patient found. Please pass patient into the configuration");
        }

        List<Concept> orderables = new ArrayList<>();

        if (app.getConfig().get("orderables") != null) {
            for (String str : app.getConfig().get("orderables").getTextValue().split(",")) {
                Concept concept = Context.getConceptService().getConceptByUuid(str);
                if (concept == null) {
                    throw new IllegalArgumentException("No lab orderable found with uuid " + str);
                }
                orderables.add(concept);
            }
        }

        OrderType testOrderType = orderService.getOrderTypeByUuid(OrderType.TEST_ORDER_TYPE_UUID);

        OrderSearchCriteria criteria = new OrderSearchCriteriaBuilder()
                .setPatient(patient)
                .setOrderTypes(Collections.singletonList(testOrderType))
                .setConcepts(orderables.isEmpty() ? null : orderables)
                .setExcludeCanceledAndExpired(true).setExcludeDiscontinueOrders(true).build();

        List<Order> orderList = orderService.getOrders(criteria);
        List<Order> filteredOrderList = orderList.stream().filter(order -> { return order.getFulfillerStatus() == null
                || order.getFulfillerStatus().equals(Order.FulfillerStatus.RECEIVED)
                || order.getFulfillerStatus().equals(Order.FulfillerStatus.IN_PROGRESS); }).collect(Collectors.toList());

        filteredOrderList.sort(new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                return -o1.getDateActivated().compareTo(o2.getDateActivated());
            }
        });

        model.put("app", app);
        model.put("orders", filteredOrderList);

    }

}
