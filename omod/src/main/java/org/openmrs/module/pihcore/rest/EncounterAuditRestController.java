package org.openmrs.module.pihcore.rest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.service.PihCoreService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.representation.CustomRepresentation;
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
 * Searches encounters by the user who created, changed or voided them, and by the provider recorded
 * on them — none of which the core REST API can do. Core's `EncounterSearchCriteria` carries a
 * providers field but no search handler exposes it, and it has no creator, changedBy or voidedBy
 * field at all, so those columns can be read off an encounter but not searched on.
 *
 * <p>Results are paged and ordered by the most recent of the audit actions the search named, and
 * each encounter is rendered in the standard encounter representation, so a client can ask for
 * whatever it needs with `v`:
 *
 * <pre>
 * GET /openmrs/ws/rest/v1/pihcore/encounteraudit?createdBy=butch&amp;limit=20&amp;totalCount=true
 * GET /openmrs/ws/rest/v1/pihcore/encounteraudit?changedBy=butch&amp;startDate=2026-09-01&amp;endDate=2026-09-30
 * GET /openmrs/ws/rest/v1/pihcore/encounteraudit?provider=MCPRPG&amp;v=custom:(uuid,encounterDatetime,auditInfo)
 * GET /openmrs/ws/rest/v1/pihcore/encounteraudit?provider=MCPRPG&amp;encounterType=&lt;uuid&gt;
 * </pre>
 *
 * <p>Every filter narrows, so naming several asks for the encounters satisfying all of them. At
 * least one user or provider is required; `encounterType` narrows an audit but is not an audit of
 * anything on its own. `startDate` and `endDate` bound when the audit action
 * happened rather than the encounter's own datetime, which is what core's encounter search already
 * covers; they run inclusively, and a bare date names the whole of that day.
 */
@Controller
public class EncounterAuditRestController {

    protected Log log = LogFactory.getLog(getClass());

    /**
     * The same gate as this package's other administrative endpoints. An encounter audit reaches
     * across every patient's record, so it is not something a clinical role should be able to run.
     */
    private static final String REQUIRED_PRIVILEGE = "App: coreapps.systemAdministration";

    /**
     * Enough to say what the encounter was and who touched it. `auditInfo` is what makes this an
     * audit result: it carries the creating, changing and voiding users with their timestamps.
     */
    private static final String DEFAULT_REPRESENTATION =
            "(uuid,display,encounterDatetime,voided,encounterType:(uuid,display),form:(uuid,display)," +
                    "location:(uuid,display),patient:(uuid,display)," +
                    "encounterProviders:(uuid,voided,provider:(uuid,display),encounterRole:(uuid,display)),auditInfo)";

    @Autowired
    private UserService userService;

    @Autowired
    private ProviderService providerService;

    @Autowired
    private EncounterService encounterService;

    @Autowired
    private PihCoreService pihCoreService;

    @RequestMapping(value = "/rest/v1/pihcore/encounteraudit", method = RequestMethod.GET)
    @ResponseBody
    public Object searchEncounters(HttpServletRequest request, HttpServletResponse response,
                                   @RequestParam(value = "createdBy", required = false) String createdBy,
                                   @RequestParam(value = "changedBy", required = false) String changedBy,
                                   @RequestParam(value = "voidedBy", required = false) String voidedBy,
                                   @RequestParam(value = "provider", required = false) String provider,
                                   @RequestParam(value = "encounterType", required = false) String encounterType,
                                   @RequestParam(value = "startDate", required = false) String startDate,
                                   @RequestParam(value = "endDate", required = false) String endDate) {

        if (!Context.hasPrivilege(REQUIRED_PRIVILEGE)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (StringUtils.isBlank(createdBy) && StringUtils.isBlank(changedBy) && StringUtils.isBlank(voidedBy)
                && StringUtils.isBlank(provider)) {
            return AuditRestSupport.errorResponse(
                    "Please specify at least one of createdBy, changedBy, voidedBy or provider.");
        }

        User creator = null;
        if (StringUtils.isNotBlank(createdBy)) {
            creator = lookupUser(createdBy);
            if (creator == null) {
                return AuditRestSupport.errorResponse("No user found with id: " + createdBy);
            }
        }

        User changer = null;
        if (StringUtils.isNotBlank(changedBy)) {
            changer = lookupUser(changedBy);
            if (changer == null) {
                return AuditRestSupport.errorResponse("No user found with id: " + changedBy);
            }
        }

        User voider = null;
        if (StringUtils.isNotBlank(voidedBy)) {
            voider = lookupUser(voidedBy);
            if (voider == null) {
                return AuditRestSupport.errorResponse("No user found with id: " + voidedBy);
            }
        }

        Provider encounterProvider = null;
        if (StringUtils.isNotBlank(provider)) {
            encounterProvider = lookupProvider(provider);
            if (encounterProvider == null) {
                return AuditRestSupport.errorResponse("No provider found with id: " + provider);
            }
        }

        EncounterType type = null;
        if (StringUtils.isNotBlank(encounterType)) {
            type = lookupEncounterType(encounterType);
            if (type == null) {
                return AuditRestSupport.errorResponse("No encounter type found with id: " + encounterType);
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

            Long totalCount = pihCoreService.getCountOfEncountersByAuditUser(creator, changer, voider,
                    encounterProvider, type, fromDate, toDate);
            List<Encounter> results = pihCoreService.getEncountersByAuditUser(creator, changer, voider,
                    encounterProvider, type, fromDate, toDate, context.getStartIndex(), context.getLimit());
            boolean hasMore = totalCount > context.getStartIndex() + context.getLimit();

            return new AlreadyPaged<>(context, results, hasMore, totalCount).toSimpleObject(null);
        }
        catch (Exception e) {
            log.error("Failed to search encounters by audit user", e);
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

    /** Accepts an encounter type uuid or its name. */
    private EncounterType lookupEncounterType(String identifier) {
        EncounterType encounterType = encounterService.getEncounterTypeByUuid(identifier);
        if (encounterType == null) {
            encounterType = encounterService.getEncounterType(identifier);
        }
        return encounterType;
    }

    /** Accepts a provider uuid or a provider identifier. */
    private Provider lookupProvider(String identifier) {
        Provider provider = providerService.getProviderByUuid(identifier);
        if (provider == null) {
            provider = providerService.getProviderByIdentifier(identifier);
        }
        return provider;
    }
}
