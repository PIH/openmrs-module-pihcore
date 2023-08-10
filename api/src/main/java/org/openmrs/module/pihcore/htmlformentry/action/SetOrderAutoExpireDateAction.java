package org.openmrs.module.pihcore.htmlformentry.action;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.TestOrder;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.ImmutableOrderInterceptor;
import org.openmrs.module.htmlformentry.CustomFormSubmissionAction;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.pihcore.service.PihCoreService;

import java.util.Date;

/**
 * For an encounter that is saved that contains Orders, this will
 * ensure that the autoExpireDate is set to 30 days after the dateActivated
 * if the order is a TestOrder, is not a Discontinue Order, and does not already have an effective stop date
 */
public class SetOrderAutoExpireDateAction implements CustomFormSubmissionAction {

    private static final int AUTO_EXPIRY_DAYS = 30;

    protected final Log log = LogFactory.getLog(getClass());

    @Override
    public void applyAction(FormEntrySession formEntrySession) {
        PihCoreService service = Context.getService(PihCoreService.class);
        Encounter encounter = formEntrySession.getEncounter();
        ImmutableOrderInterceptor ioi = Context.getRegisteredComponents(ImmutableOrderInterceptor.class).get(0);
        try {
            ioi.addMutablePropertiesForThread("autoExpireDate");
            for (Order order : encounter.getOrders()) {
                if (order instanceof TestOrder) {
                    if (!order.getAction().equals(Order.Action.DISCONTINUE)) {
                        if (order.getEffectiveStopDate() == null) {
                            Date autoExpireDate = DateUtils.addDays(order.getEffectiveStartDate(), AUTO_EXPIRY_DAYS);
                            order.setAutoExpireDate(autoExpireDate);
                            log.debug("Saving autoExpireDate of order " + order.getUuid() + " to " + autoExpireDate);
                            service.saveOrder(order);
                        }
                    }
                }
            }
        }
        finally {
            ioi.removeMutablePropertiesForThread();
        }
    }
}
