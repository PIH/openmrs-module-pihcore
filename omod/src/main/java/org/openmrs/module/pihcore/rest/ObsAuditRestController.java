package org.openmrs.module.pihcore.rest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.User;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.service.PihCoreService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.representation.CustomRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * Searches observations by the user who created them or the user who voided them, which the core
 * REST API cannot do: neither the obs resource, the observation search handler nor core's own
 * {@link org.openmrs.parameter.ObsSearchCriteria} has a creator or voidedBy field, so those columns
 * can be read off an observation but not searched on.
 *
 * <p>Results are paged and ordered most recent action first, and each observation is rendered in the
 * standard obs representation, so a client can ask for whatever it needs with `v`:
 *
 * <pre>
 * GET /openmrs/ws/rest/v1/pihcore/obsaudit?createdBy=butch&amp;limit=20&amp;totalCount=true
 * GET /openmrs/ws/rest/v1/pihcore/obsaudit?voidedBy=cd8a4b8e-...&amp;v=custom:(uuid,concept:(display),auditInfo)
 * GET /openmrs/ws/rest/v1/pihcore/obsaudit?createdBy=butch&amp;startDate=2026-09-01&amp;endDate=2026-09-30
 * </pre>
 *
 * <p>`startDate` and `endDate` bound when the audit action happened rather than the observation's
 * own datetime, and run inclusively.
 */
@Controller
public class ObsAuditRestController {

    protected Log log = LogFactory.getLog(getClass());

    /**
     * The same gate as this package's other administrative endpoints. An observation audit reaches
     * across every patient's record, so it is not something a clinical role should be able to run.
     */
    private static final String REQUIRED_PRIVILEGE = "App: coreapps.systemAdministration";

    /**
     * Enough to say what was recorded and who touched it. `auditInfo` is what makes this an audit
     * result: it carries the creator and the voiding user with their timestamps.
     */
    private static final String DEFAULT_REPRESENTATION =
            "(uuid,display,obsDatetime,voided,concept:(uuid,display),person:(uuid,display)," +
                    "encounter:(uuid),value:ref,comment,auditInfo)";

    @Autowired
    private UserService userService;

    @Autowired
    private PihCoreService pihCoreService;

    @RequestMapping(value = "/rest/v1/pihcore/obsaudit", method = RequestMethod.GET)
    @ResponseBody
    public Object searchObs(HttpServletRequest request, HttpServletResponse response,
                            @RequestParam(value = "createdBy", required = false) String createdBy,
                            @RequestParam(value = "voidedBy", required = false) String voidedBy,
                            @RequestParam(value = "startDate", required = false) String startDate,
                            @RequestParam(value = "endDate", required = false) String endDate) {

        if (!Context.hasPrivilege(REQUIRED_PRIVILEGE)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (StringUtils.isBlank(createdBy) && StringUtils.isBlank(voidedBy)) {
            return AuditRestSupport.errorResponse("Please specify createdBy, voidedBy, or both.");
        }

        User creator = null;
        if (StringUtils.isNotBlank(createdBy)) {
            creator = lookupUser(createdBy);
            if (creator == null) {
                return AuditRestSupport.errorResponse("No user found with id: " + createdBy);
            }
        }

        User voider = null;
        if (StringUtils.isNotBlank(voidedBy)) {
            voider = lookupUser(voidedBy);
            if (voider == null) {
                return AuditRestSupport.errorResponse("No user found with id: " + voidedBy);
            }
        }

        Date fromDate;
        Date toDate;
        try {
            fromDate = AuditRestSupport.parseBound(startDate, false);
            toDate = AuditRestSupport.parseBound(endDate, true);
        }
        catch (Exception e) {
            return AuditRestSupport.errorResponse(AuditRestSupport.dateFormatMessage());
        }

        if (fromDate != null && toDate != null && fromDate.after(toDate)) {
            return AuditRestSupport.errorResponse("startDate must not be after endDate.");
        }

        try {
            RequestContext context = RestUtil.getRequestContext(request, response,
                    new CustomRepresentation(DEFAULT_REPRESENTATION));

            Long totalCount = pihCoreService.getCountOfObsByAuditUser(creator, voider, fromDate, toDate);
            List<Obs> results = pihCoreService.getObsByAuditUser(creator, voider, fromDate, toDate,
                    context.getStartIndex(), context.getLimit());
            boolean hasMore = totalCount > context.getStartIndex() + context.getLimit();

            // AlreadyPaged renders the envelope every other OpenMRS list endpoint returns: results in
            // the requested representation, next and previous links, and totalCount when asked for.
            return new AlreadyPaged<>(context, results, hasMore, totalCount).toSimpleObject(null);
        }
        catch (Exception e) {
            log.error("Failed to search observations by audit user", e);
            return AuditRestSupport.errorResponse(e);
        }
    }

    /**
     * Accepts whichever handle the caller has: a user uuid, a username, or a system id.
     *
     * <p>Note that core's username lookup skips retired users, so an audit of a deactivated account
     * has to name it by uuid.
     */
    private User lookupUser(String identifier) {
        User user = userService.getUserByUuid(identifier);
        if (user == null) {
            user = userService.getUserByUsername(identifier);
        }
        return user;
    }

}
