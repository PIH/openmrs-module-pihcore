package org.openmrs.module.pihcore.page.controller.meds;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.db.hibernate.HibernateUtil;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.pihcore.metadata.haiti.PihHaitiPatientIdentifierTypes;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.web.bind.annotation.RequestParam;

public class DrugOrdersPageController {

    public void get(PageModel model, UiUtils ui,
                      @InjectBeans PatientDomainWrapper patientDomainWrapper,
                      @RequestParam(required = false, value = "patient") Patient patient,
                      @RequestParam(required = false, value = "hivemrId") String hivemrId,
                      @SpringBean("conceptService") ConceptService conceptService,
                      @SpringBean("patientService") PatientService patientService,
                      @SpringBean("orderService") OrderService orderService) throws IOException {

        if (patient == null) {
            PatientIdentifierType hivemrV1 = patientService.getPatientIdentifierTypeByUuid(PihHaitiPatientIdentifierTypes.HIVEMR_V1.uuid());
            List<PatientIdentifier> idList = patientService.getPatientIdentifiers(hivemrId, Arrays.asList(hivemrV1), null, null, null);
            if (idList.isEmpty()) {
                throw new IllegalArgumentException("No patients found with identifier: " + hivemrId);
            }
            if (idList.size() > 1) {
                throw new IllegalArgumentException(idList.size() + " patients found with identifier: " + hivemrId);
            }
            patient = idList.get(0).getPatient();
        }

        // Get all of the drug orders for the patient
        List<DrugOrder> drugOrders = new ArrayList<>();
        for (Order order : orderService.getAllOrdersByPatient(patient)) {
            order = HibernateUtil.getRealObjectFromProxy(order);
            if (order instanceof DrugOrder && BooleanUtils.isNotTrue(order.getVoided())) {
                DrugOrder drugOrder = (DrugOrder) order;
                drugOrders.add(drugOrder);
            }
        }

        // Sort these drug orders in chronological order
        Collections.sort(drugOrders, (d1, d2) -> {
            // Get all of the previous orders for d1.  If any are d2, then d1 is later
            for (Order d1Prev = d1.getPreviousOrder(); d1Prev != null; d1Prev = d1Prev.getPreviousOrder()) {
                if (d1Prev.equals(d2)) {
                    return 1;
                }
            }
            // Get all of the previous orders for d2.  If any are d1, then d2 is later
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

        /*
            TODO: For now we will organize these by order reason, as this is the way they are being migrated in
            from the HIV EMR.  Once we have established concept sets for organizing orderables by areas we can adjust
            to organize in that way.  We'll keep our model generic (based on string category names) to allow for this
            We also remove the discontinue orders and associate them back with the orders they discontinue in a map so that
            we can organize them together in a single row.
         */

        List<Concept> categories = new ArrayList<>();
        categories.add(conceptService.getConceptByUuid("138405AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")); // HIV
        categories.add(conceptService.getConceptByUuid("3ccca7cc-26fe-102b-80cb-0017a47871b2")); // TB
        categories.add(conceptService.getConceptByUuid("160538AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")); // PTME
        categories.add(conceptService.getConceptByUuid("1691AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")); // Prophylaxis

        List<DrugOrder> activeOrders = new ArrayList<>();
        List<DrugOrder> completedOrders = new ArrayList<>();
        Map<String, List<DrugOrder>> activeOrdersByCategory = new LinkedHashMap<>();
        Map<String, List<DrugOrder>> completedOrdersByCategory = new LinkedHashMap<>();
        Map<DrugOrder, DrugOrder> ordersToDiscontinueOrders = new HashMap<>();

        for (Concept category : categories) {
            activeOrdersByCategory.put(category.getDisplayString(), new ArrayList<>());
            completedOrdersByCategory.put(category.getDisplayString(), new ArrayList<>());
        }
        activeOrdersByCategory.put("", new ArrayList<>());
        completedOrdersByCategory.put("", new ArrayList<>());

        for (DrugOrder d : drugOrders) {
            if (d.getAction() == Order.Action.DISCONTINUE) {
                ordersToDiscontinueOrders.put((DrugOrder)d.getPreviousOrder(), d);
            }
            else {
                String category = "";
                if (d.getOrderReason() != null && categories.contains(d.getOrderReason())) {
                    category = d.getOrderReason().getDisplayString();
                }
                if (d.isActive()) {
                    activeOrders.add(d);
                    activeOrdersByCategory.get(category).add(d);
                }
                else {
                    completedOrders.add(d);
                    completedOrdersByCategory.get(category).add(d);
                }
            }
        }

        patientDomainWrapper.setPatient(patient);
        model.addAttribute("patient", patientDomainWrapper);
        model.addAttribute("drugOrders", drugOrders);
        model.addAttribute("categories", categories);
        model.addAttribute("activeOrders", activeOrders);
        model.addAttribute("completedOrders", completedOrders);
        model.addAttribute("activeOrdersByCategory", activeOrdersByCategory);
        model.addAttribute("completedOrdersByCategory", completedOrdersByCategory);
        model.addAttribute("ordersToDiscontinueOrders", ordersToDiscontinueOrders);
    }
}
