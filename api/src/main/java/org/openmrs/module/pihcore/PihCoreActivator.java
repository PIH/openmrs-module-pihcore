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
package org.openmrs.module.pihcore;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.Daemon;
import org.openmrs.layout.name.NameSupport;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.DaemonTokenAware;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.emrapi.disposition.DispositionService;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.pihcore.config.registration.BiometricsConfigDescriptor;
import org.openmrs.module.pihcore.deploy.bundle.haiti.HaitiMetadataBundle;
import org.openmrs.module.pihcore.deploy.bundle.haiti.hsn.HSNMetadataBundle;
import org.openmrs.module.pihcore.deploy.bundle.haiti.mirebalais.MirebalaisMetadataBundle;
import org.openmrs.module.pihcore.deploy.bundle.liberia.LiberiaMetadataBundle;
import org.openmrs.module.pihcore.deploy.bundle.mexico.MexicoMetadataBundle;
import org.openmrs.module.pihcore.deploy.bundle.peru.PeruMetadataBundle;
import org.openmrs.module.pihcore.deploy.bundle.sierraLeone.SierraLeoneMetadataBundle;
import org.openmrs.module.pihcore.setup.AttachmentsSetup;
import org.openmrs.module.pihcore.setup.CloseStaleVisitsSetup;
import org.openmrs.module.pihcore.setup.GlobalResourceSetup;
import org.openmrs.module.pihcore.setup.HtmlFormSetup;
import org.openmrs.module.pihcore.setup.LocationTagSetup;
import org.openmrs.module.pihcore.setup.MergeActionsSetup;
import org.openmrs.module.pihcore.setup.MetadataMappingsSetup;
import org.openmrs.module.pihcore.setup.MetadataSetupTask;
import org.openmrs.module.pihcore.setup.MetadataSharingSetup;
import org.openmrs.module.pihcore.setup.NameTemplateSetup;
import org.openmrs.module.pihcore.setup.PacIntegrationSetup;
import org.openmrs.module.pihcore.setup.PatientIdentifierSetup;
import org.openmrs.module.pihcore.setup.LiquibaseSetup;
import org.openmrs.module.registrationcore.RegistrationCoreConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class PihCoreActivator extends BaseModuleActivator implements DaemonTokenAware {

	protected Log log = LogFactory.getLog(getClass());

    private Config config;

    private DaemonToken daemonToken;

    // hack that allows us to ignore some errors and installation during testing
    private Boolean testingContext = false;

    @Override
    public void setDaemonToken(DaemonToken daemonToken) {
        this.daemonToken = daemonToken;
    }

    @Override
	public void started() {

        try {
            MetadataMappingService metadataMappingService = Context.getService(MetadataMappingService.class);
            PatientService patientService = Context.getPatientService();
            FormService formService = Context.getFormService();
            LocationService locationService = Context.getLocationService();
            EncounterService encounterService = Context.getEncounterService();
            VisitService visitService = Context.getVisitService();
            AdministrationService administrationService = Context.getAdministrationService();
            IdentifierSourceService identifierSourceService = Context.getService(IdentifierSourceService.class);
            ConceptService conceptService = Context.getService(ConceptService.class);

            if (config == null) {  // hack to allow injecting a mock config for testing
                config = Context.getRegisteredComponents(Config.class).get(0); // currently only one of these
            }

            setDispositionConfig(config);
            installMetadataBundles(config);
            setGlobalProperties(config);
            setExtraIdentifierTypes(metadataMappingService, patientService, config);
            MergeActionsSetup.registerMergeActions();
            LocationTagSetup.setupLocationTags(locationService, config);
            HtmlFormSetup.setupHtmlFormEntryTagHandlers();
            MetadataMappingsSetup.setupGlobalMetadataMappings(metadataMappingService,locationService, encounterService, visitService);
            MetadataMappingsSetup.setupPrimaryIdentifierTypeBasedOnCountry(metadataMappingService, patientService, config);
            MetadataMappingsSetup.setupFormMetadataMappings(metadataMappingService);
            PatientIdentifierSetup.setupIdentifierGeneratorsIfNecessary(identifierSourceService, locationService, config);
            PacIntegrationSetup.setup(config);
            LiquibaseSetup.setup();
            AttachmentsSetup.migrateAttachmentsConceptsIfNecessary(conceptService);
           // RetireProvidersSetup.setupRetireProvidersTask();
            GlobalResourceSetup.includeGlobalResources();
            setupCommCareUser();

            if (config.shouldRebuildSearchIndex()) {
                Context.updateSearchIndex();
            }

            // see https://pihemr.atlassian.net/browse/UHM-4459 for details of why we do this this way
            Runnable metadataSetupTask = new MetadataSetupTask(config, testingContext);
            String runInSeparateThread = Context.getAdministrationService().getGlobalProperty(PihCoreConstants.GP_RUN_CONCEPT_SETUP_TASK_IN_SEPARATE_THREAD);
            if ("true".equalsIgnoreCase(runInSeparateThread)) {
                Daemon.runInDaemonThread(metadataSetupTask, daemonToken);
            }
            else {
                metadataSetupTask.run();
            }

        }
        catch (Exception e) {
            Module mod = ModuleFactory.getModuleById("pihcore");
            ModuleFactory.stopModule(mod, true, true);
            throw new RuntimeException("failed to setup the required modules", e);
        }

    }

    @Override
    public void stopped() {
        MergeActionsSetup.deregisterMergeActions();
    }

    @Override
    public void contextRefreshed() {

        setDispositionConfig(config);
        MetadataSharingSetup.setMetadataSharingResolvers();

        CloseStaleVisitsSetup.setupCloseStaleVisitsTask();

        NameSupport nameSupport = Context.getRegisteredComponent("nameSupport", NameSupport.class);
        // hack: configure both name support beans, since two actually exist (?)
        NameTemplateSetup.configureNameTemplate(nameSupport, config);
        NameTemplateSetup.configureNameTemplate(NameSupport.getInstance(), config);

    }

    private void installMetadataBundles(Config config) {

        MetadataDeployService deployService = Context.getService(MetadataDeployService.class);

        // make this more dynamic, less dependent on if-thens

        if (config.getSite().equalsIgnoreCase("MIREBALAIS")) {
            deployService.installBundle(Context.getRegisteredComponents(MirebalaisMetadataBundle.class).get(0));
        }
        else if (config.getSite().equalsIgnoreCase("HSN_SAINT_MARC")) {
            deployService.installBundle(Context.getRegisteredComponents(HSNMetadataBundle.class).get(0));
        }
        else if (config.getCountry().equals(ConfigDescriptor.Country.HAITI) && !config.getSite().equals("MIREBALAIS")) {
            deployService.installBundle(Context.getRegisteredComponents(HaitiMetadataBundle.class).get(0));
        }
        else if (config.getCountry().equals(ConfigDescriptor.Country.LIBERIA)) {
            deployService.installBundle(Context.getRegisteredComponents(LiberiaMetadataBundle.class).get(0));
        }
        else if (config.getCountry().equals(ConfigDescriptor.Country.SIERRA_LEONE)) {
            deployService.installBundle(Context.getRegisteredComponents(SierraLeoneMetadataBundle.class).get(0));
        }
        else if (config.getCountry().equals(ConfigDescriptor.Country.MEXICO)) {
            deployService.installBundle(Context.getRegisteredComponents(MexicoMetadataBundle.class).get(0));
        }
        else if (config.getCountry().equals(ConfigDescriptor.Country.PERU)) {
            deployService.installBundle(Context.getRegisteredComponents(PeruMetadataBundle.class).get(0));
        }

    }

    // configure which disposition config to use
    public void setDispositionConfig(Config config) {
        if (config != null && config.getDispositionConfig() != null) {
            Context.getService(DispositionService.class).setDispositionConfig(config.getDispositionConfig());
        }
    }

    public void setExtraIdentifierTypes(MetadataMappingService metadataMappingService, PatientService patientService, Config config) {
        if (config != null && config.getExtraIdentifierTypes() != null && config.getExtraIdentifierTypes().size() > 0) {

            List<PatientIdentifierType> extraIdentifierTypes = new ArrayList<PatientIdentifierType>();
            for (String uuid : config.getExtraIdentifierTypes()) {
                extraIdentifierTypes.add(patientService.getPatientIdentifierTypeByUuid(uuid));
            }

            metadataMappingService.mapMetadataItems(extraIdentifierTypes, EmrApiConstants.EMR_METADATA_SOURCE_NAME, EmrApiConstants.GP_EXTRA_PATIENT_IDENTIFIER_TYPES);
        }
    }

    public void setGlobalProperties(Config config) {
        if (config != null && config.getGlobalProperties() != null) {
            Map<String, String> globalProperties = config.getGlobalProperties();
            for (String name : globalProperties.keySet()) {
                setGlobalProperty(name, globalProperties.get(name));
            }
        }
        if (config != null) {
            BiometricsConfigDescriptor biometricsDescriptor = config.getBiometricsConfig();
            setGlobalProperty(RegistrationCoreConstants.GP_BIOMETRICS_IMPLEMENTATION, biometricsDescriptor.getBiometricEngine());
        }
    }

    public User setupCommCareUser(){

	    User user = null;
        String commCareUserName = Context.getRuntimeProperties().getProperty("commcare.username", null);
        String commCareUserPassword = Context.getRuntimeProperties().getProperty("commcare.password", null);
        if (StringUtils.isNotBlank(commCareUserName) && StringUtils.isNotBlank(commCareUserPassword)) {
            user = Context.getUserService().getUserByUsername(commCareUserName);
            if (user == null) {
                // create a new user if one does not already exist
                user = new User();
            }
            user.setUsername(commCareUserName);
            Person person = user.getPerson();
            if (person == null) {
                person = new Person();
                person.addName(new PersonName("CommCare", "", "Integration"));
                person.setGender("M");
                user.setPerson(person);
            }

            user.addRole(Context.getUserService().getRole("Authenticated"));
            user.addRole(Context.getUserService().getRole("Privilege Level: Full"));
            if (user.getId() == null ) {
                // create a new record
                user = Context.getUserService().createUser(user, commCareUserPassword);
            } else {
                // update an existing record
                user = Context.getUserService().saveUser(user);
            }
            person = user.getPerson();
            Collection<Provider> providers = Context.getProviderService().getProvidersByPerson(person);
            if (providers == null || providers.size() < 1 ){
                // if the user does not have a Provider yet then create one
                Provider provider = new Provider();
                provider.setUuid(PihCoreConstants.COMMCARE_PROVIDER_UUID);
                provider.setPerson(person);
                Context.getProviderService().saveProvider(provider);
            }
        }

        return user;
    }

    // hack to allow injecting a mock config for testing
    public void setConfig(Config config) {
        this.config = config;
    }

    //  hack that allows us to ignore some errors and installation during testing
    public void setTestingContext(Boolean testingContext) {
        this.testingContext = testingContext;
    }

    protected void setGlobalProperty(String propertyName, String propertyValue) {
        AdministrationService administrationService = Context.getAdministrationService();
        GlobalProperty gp = administrationService.getGlobalPropertyObject(propertyName);
        if (gp == null) {
            gp = new GlobalProperty(propertyName);
        }
        gp.setPropertyValue(propertyValue);
        administrationService.saveGlobalProperty(gp);
    }
}
