/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.pihcore.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.GlobalProperty;
import org.openmrs.Obs;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.db.OrderDAO;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.pihcore.PihCoreConstants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Data Access Layer for PihCore
 */
public class PihCoreDAO {

    protected final Log log = LogFactory.getLog(this.getClass());

    private DbSessionFactory sessionFactory;

    /**
     * @see OrderDAO#getNextOrderNumberSeedSequenceValue() ()
     */
    public Long getNextRadiologyOrderNumberSeedSequenceValue() {
        String gp = PihCoreConstants.GP_NEXT_RADIOLOGY_ORDER_NUMBER_SEED;
        Criteria searchCriteria = sessionFactory.getCurrentSession().createCriteria(GlobalProperty.class);
        searchCriteria.add(Restrictions.eq("property", gp));
        GlobalProperty globalProperty = (GlobalProperty) sessionFactory.getCurrentSession().get(GlobalProperty.class, gp, LockOptions.UPGRADE);

        if (globalProperty == null) {
            throw new APIException("Missing global property named: " + gp);
        }

        String gpTextValue = globalProperty.getPropertyValue();
        if (StringUtils.isBlank(gpTextValue)) {
            throw new APIException("Invalid value for global property named: " + gp);
        }

        Long gpNumericValue;
        try {
            gpNumericValue = Long.parseLong(gpTextValue);
        } catch (NumberFormatException ex) {
            throw new APIException("Invalid value for global property named: " + gp);
        }

        globalProperty.setPropertyValue(String.valueOf(gpNumericValue + 1));

        sessionFactory.getCurrentSession().save(globalProperty);

        return gpNumericValue;
    }

    /**
     * Observations whose audit trail names the given user, most recent action first.
     *
     * @param createdBy restrict to observations this user created, or null not to
     * @param voidedBy restrict to observations this user voided, or null not to
     * @param fromDate restrict to actions at or after this moment, or null not to
     * @param toDate restrict to actions at or before this moment, or null not to
     * @param startIndex the row to start at, or null to start at the first
     * @param limit how many rows to return, or null for all of them
     * @return the matching observations
     */
    @SuppressWarnings("unchecked")
    public List<Obs> getObsByAuditUser(User createdBy, User voidedBy, Date fromDate, Date toDate, Integer startIndex,
            Integer limit) {
        Query query = sessionFactory.getCurrentSession().createQuery("select o from Obs o "
                + auditWhereClause(createdBy, voidedBy, fromDate, toDate) + auditOrderByClause(voidedBy));
        setAuditParameters(query, createdBy, voidedBy, fromDate, toDate);
        if (startIndex != null && startIndex > 0) {
            query.setFirstResult(startIndex);
        }
        if (limit != null && limit > 0) {
            query.setMaxResults(limit);
        }
        return query.list();
    }

    /**
     * How many observations {@link #getObsByAuditUser(User, User, Date, Date, Integer, Integer)}
     * would return were it not paged.
     */
    public Long getCountOfObsByAuditUser(User createdBy, User voidedBy, Date fromDate, Date toDate) {
        Query query = sessionFactory.getCurrentSession()
                .createQuery("select count(o) from Obs o " + auditWhereClause(createdBy, voidedBy, fromDate, toDate));
        setAuditParameters(query, createdBy, voidedBy, fromDate, toDate);
        return (Long) query.uniqueResult();
    }

    /**
     * Voided observations are deliberately left in: a search for who voided something would return
     * nothing otherwise, and an auditor looking at what a user created wants to see the rows that
     * have since been deleted just as much as the surviving ones. Callers can tell them apart by
     * each observation's voided flag.
     *
     * <p>Both filters narrow, so supplying each names observations that the one user created and
     * the other voided. At least one user is required, since an unfiltered query would walk the
     * whole obs table; a date range alone would not narrow it to anything an audit is about.
     *
     * <p>A date range bounds the audit action itself, not the observation's own datetime: an
     * observation backdated to last year but entered this morning was modified this morning, which
     * is what a search over a timeframe is asking about. Each user filter is bounded by the column
     * belonging to its action, so naming both users and a range asks for observations that one user
     * created and the other voided, each within the window.
     */
    private String auditWhereClause(User createdBy, User voidedBy, Date fromDate, Date toDate) {
        List<String> conditions = new ArrayList<>();
        if (createdBy != null) {
            conditions.add("o.creator = :createdBy");
            addDateConditions(conditions, "o.dateCreated", fromDate, toDate);
        }
        if (voidedBy != null) {
            conditions.add("o.voidedBy = :voidedBy");
            addDateConditions(conditions, "o.dateVoided", fromDate, toDate);
        }
        if (conditions.isEmpty()) {
            throw new APIException("An observation audit search needs at least one of createdBy or voidedBy");
        }
        return "where " + StringUtils.join(conditions, " and ");
    }

    /** Bounds run inclusively, so a range names the whole of the days at either end of it. */
    private void addDateConditions(List<String> conditions, String property, Date fromDate, Date toDate) {
        if (fromDate != null) {
            conditions.add(property + " >= :fromDate");
        }
        if (toDate != null) {
            conditions.add(property + " <= :toDate");
        }
    }

    /**
     * "Most recent first" means the most recent audit action: when the row was created for a
     * createdBy search, when it was voided for a voidedBy search. Ordering by the observation's own
     * datetime would bury an obs backdated to last year but entered this morning, which is the
     * opposite of what an audit needs. The obs id breaks ties so that paging cannot repeat or skip
     * a row when several share a timestamp.
     */
    private String auditOrderByClause(User voidedBy) {
        return voidedBy != null ? " order by o.dateVoided desc, o.obsId desc" : " order by o.dateCreated desc, o.obsId desc";
    }

    private void setAuditParameters(Query query, User createdBy, User voidedBy, Date fromDate, Date toDate) {
        if (createdBy != null) {
            query.setParameter("createdBy", createdBy);
        }
        if (voidedBy != null) {
            query.setParameter("voidedBy", voidedBy);
        }
        // The same bounds apply to whichever audit columns the user filters brought in, so the
        // parameters are set once however many conditions referred to them.
        if (fromDate != null) {
            query.setParameter("fromDate", fromDate);
        }
        if (toDate != null) {
            query.setParameter("toDate", toDate);
        }
    }

    /**
     * Encounters whose audit trail names the given user, or that name the given provider, most
     * recent action first.
     *
     * @param createdBy restrict to encounters this user created, or null not to
     * @param changedBy restrict to encounters this user changed, or null not to
     * @param voidedBy restrict to encounters this user voided, or null not to
     * @param provider restrict to encounters this provider is recorded on, or null not to
     * @param encounterType restrict to encounters of this type, or null not to
     * @param fromDate restrict to actions at or after this moment, or null not to
     * @param toDate restrict to actions at or before this moment, or null not to
     * @param startIndex the row to start at, or null to start at the first
     * @param limit how many rows to return, or null for all of them
     * @return the matching encounters
     */
    @SuppressWarnings("unchecked")
    public List<Encounter> getEncountersByAuditUser(User createdBy, User changedBy, User voidedBy, Provider provider,
            EncounterType encounterType, Date fromDate, Date toDate, Integer startIndex, Integer limit) {
        Query query = sessionFactory.getCurrentSession()
                .createQuery("select e from Encounter e "
                        + encounterAuditWhereClause(createdBy, changedBy, voidedBy, provider, encounterType, fromDate,
                            toDate)
                        + encounterAuditOrderByClause(createdBy, changedBy, voidedBy));
        setEncounterAuditParameters(query, createdBy, changedBy, voidedBy, provider, encounterType, fromDate, toDate);
        if (startIndex != null && startIndex > 0) {
            query.setFirstResult(startIndex);
        }
        if (limit != null && limit > 0) {
            query.setMaxResults(limit);
        }
        return query.list();
    }

    /**
     * How many encounters
     * {@link #getEncountersByAuditUser(User, User, User, Provider, EncounterType, Date, Date, Integer, Integer)}
     * would return were it not paged.
     */
    public Long getCountOfEncountersByAuditUser(User createdBy, User changedBy, User voidedBy, Provider provider,
            EncounterType encounterType, Date fromDate, Date toDate) {
        Query query = sessionFactory.getCurrentSession()
                .createQuery("select count(e) from Encounter e "
                        + encounterAuditWhereClause(createdBy, changedBy, voidedBy, provider, encounterType, fromDate,
                            toDate));
        setEncounterAuditParameters(query, createdBy, changedBy, voidedBy, provider, encounterType, fromDate, toDate);
        return (Long) query.uniqueResult();
    }

    /**
     * Voided encounters are deliberately left in, for the same reason voided observations are: a
     * search for who voided something would otherwise return nothing, and an auditor looking at
     * what a user entered wants to see what has since been deleted. Callers can tell them apart by
     * each encounter's voided flag.
     *
     * <p>Every filter narrows, so naming several asks for the encounters that satisfy all of them.
     * At least one user or provider is required, since neither a date range nor nothing at all
     * narrows the encounter table to something an audit is about.
     *
     * <p>A date range bounds whatever the search is about. Where an audit action is named it bounds
     * that action's column — an encounter backdated to last year but entered this morning was
     * entered this morning, and core's own encounter search already covers encounterDatetime for
     * the cases it can reach. A provider search names no action, so there it bounds the encounter's
     * own datetime, which is both what a provider's caseload is asked about and something core
     * cannot filter by provider.
     */
    private String encounterAuditWhereClause(User createdBy, User changedBy, User voidedBy, Provider provider,
            EncounterType encounterType, Date fromDate, Date toDate) {
        List<String> conditions = new ArrayList<>();
        if (encounterType != null) {
            // narrows an audit, but is not an audit of anything on its own, so it does not satisfy
            // the requirement below
            conditions.add("e.encounterType = :encounterType");
        }
        if (createdBy != null) {
            conditions.add("e.creator = :createdBy");
            addDateConditions(conditions, "e.dateCreated", fromDate, toDate);
        }
        if (changedBy != null) {
            conditions.add("e.changedBy = :changedBy");
            addDateConditions(conditions, "e.dateChanged", fromDate, toDate);
        }
        if (voidedBy != null) {
            conditions.add("e.voidedBy = :voidedBy");
            addDateConditions(conditions, "e.dateVoided", fromDate, toDate);
        }
        if (provider != null) {
            // an exists clause rather than a join, so that an encounter naming the provider more
            // than once is still returned once, without a distinct that would constrain the sort
            conditions.add("exists (select 1 from EncounterProvider ep where ep.encounter = e "
                    + "and ep.provider = :provider and ep.voided = false)");
            if (createdBy == null && changedBy == null && voidedBy == null) {
                addDateConditions(conditions, "e.encounterDatetime", fromDate, toDate);
            }
        }
        if (createdBy == null && changedBy == null && voidedBy == null && provider == null) {
            throw new APIException(
                    "An encounter audit search needs at least one of createdBy, changedBy, voidedBy or provider");
        }
        return "where " + StringUtils.join(conditions, " and ");
    }

    /**
     * Orders by the most recent of the audit actions the search named, so that "most recent first"
     * means the most recent thing the search is about, and matches whichever column a date range
     * bounded. A provider search names no action, so it orders by the encounter's own datetime.
     */
    private String encounterAuditOrderByClause(User createdBy, User changedBy, User voidedBy) {
        if (voidedBy != null) {
            return " order by e.dateVoided desc, e.encounterId desc";
        }
        if (changedBy != null) {
            return " order by e.dateChanged desc, e.encounterId desc";
        }
        if (createdBy != null) {
            return " order by e.dateCreated desc, e.encounterId desc";
        }
        return " order by e.encounterDatetime desc, e.encounterId desc";
    }

    private void setEncounterAuditParameters(Query query, User createdBy, User changedBy, User voidedBy,
            Provider provider, EncounterType encounterType, Date fromDate, Date toDate) {
        if (encounterType != null) {
            query.setParameter("encounterType", encounterType);
        }
        if (createdBy != null) {
            query.setParameter("createdBy", createdBy);
        }
        if (changedBy != null) {
            query.setParameter("changedBy", changedBy);
        }
        if (voidedBy != null) {
            query.setParameter("voidedBy", voidedBy);
        }
        if (provider != null) {
            query.setParameter("provider", provider);
        }
        if (fromDate != null) {
            query.setParameter("fromDate", fromDate);
        }
        if (toDate != null) {
            query.setParameter("toDate", toDate);
        }
    }

    public void setSessionFactory(DbSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public DbSessionFactory getSessionFactory() {
        return sessionFactory;
    }
}