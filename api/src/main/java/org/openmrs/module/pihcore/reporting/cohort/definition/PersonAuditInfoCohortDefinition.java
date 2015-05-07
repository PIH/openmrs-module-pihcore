/*
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

package org.openmrs.module.pihcore.reporting.cohort.definition;

import org.openmrs.User;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

import java.util.Date;
import java.util.List;

/**
 * For searching for patients based on being created, modified, or voided at a time or a by a user
 */
public class PersonAuditInfoCohortDefinition extends BaseCohortDefinition {

    @ConfigurationProperty(group="created")
    private Date createdOnOrAfter;

    @ConfigurationProperty(group="created")
    private Date createdOnOrBefore;

    @ConfigurationProperty(group="created")
    private List<User> createdByUsers;

    @ConfigurationProperty(group="changed")
    private Date changedOnOrAfter;

    @ConfigurationProperty(group="changed")
    private Date changedOnOrBefore;

    @ConfigurationProperty(group="changed")
    private List<User> changedByUsers;

    @ConfigurationProperty(group="voided")
    private boolean includeVoided = false;

    @ConfigurationProperty(group="voided")
    private Date voidedOnOrAfter;

    @ConfigurationProperty(group="voided")
    private Date voidedOnOrBefore;

    @ConfigurationProperty(group="voided")
    private List<User> voidedByUsers;


    public Date getCreatedOnOrBefore() {
        return createdOnOrBefore;
    }

    public void setCreatedOnOrBefore(Date createdOnOrBefore) {
        this.createdOnOrBefore = createdOnOrBefore;
    }

    public Date getCreatedOnOrAfter() {
        return createdOnOrAfter;
    }

    public void setCreatedOnOrAfter(Date createdOnOrAfter) {
        this.createdOnOrAfter = createdOnOrAfter;
    }

    public List<User> getCreatedByUsers() {
        return createdByUsers;
    }

    public void setCreatedByUsers(List<User> createdByUsers) {
        this.createdByUsers = createdByUsers;
    }

    public Date getChangedOnOrBefore() {
        return changedOnOrBefore;
    }

    public void setChangedOnOrBefore(Date changedOnOrBefore) {
        this.changedOnOrBefore = changedOnOrBefore;
    }

    public Date getChangedOnOrAfter() {
        return changedOnOrAfter;
    }

    public void setChangedOnOrAfter(Date changedOnOrAfter) {
        this.changedOnOrAfter = changedOnOrAfter;
    }

    public List<User> getChangedByUsers() {
        return changedByUsers;
    }

    public void setChangedByUsers(List<User> changedByUsers) {
        this.changedByUsers = changedByUsers;
    }

    public boolean isIncludeVoided() {
        return includeVoided;
    }

    public void setIncludeVoided(boolean includeVoided) {
        this.includeVoided = includeVoided;
    }

    public Date getVoidedOnOrBefore() {
        return voidedOnOrBefore;
    }

    public void setVoidedOnOrBefore(Date voidedOnOrBefore) {
        this.voidedOnOrBefore = voidedOnOrBefore;
    }

    public Date getVoidedOnOrAfter() {
        return voidedOnOrAfter;
    }

    public void setVoidedOnOrAfter(Date voidedOnOrAfter) {
        this.voidedOnOrAfter = voidedOnOrAfter;
    }

    public List<User> getVoidedByUsers() {
        return voidedByUsers;
    }

    public void setVoidedByUsers(List<User> voidedByUsers) {
        this.voidedByUsers = voidedByUsers;
    }

}
