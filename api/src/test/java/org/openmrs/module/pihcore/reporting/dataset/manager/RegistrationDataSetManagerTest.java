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

package org.openmrs.module.pihcore.reporting.dataset.manager;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.contrib.testdata.builder.EncounterBuilder;
import org.openmrs.contrib.testdata.builder.PatientBuilder;
import org.openmrs.layout.web.address.AddressSupport;
import org.openmrs.layout.web.address.AddressTemplate;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.SocioEconomicConcepts;
import org.openmrs.module.pihcore.deploy.bundle.haiti.HaitiAddressBundle;
import org.openmrs.module.pihcore.deploy.bundle.haiti.HaitiPatientIdentifierTypeBundle;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.pihcore.metadata.core.EncounterTypes;
import org.openmrs.module.pihcore.metadata.core.PersonAttributeTypes;
import org.openmrs.module.pihcore.metadata.haiti.HaitiPatientIdentifierTypes;
import org.openmrs.module.pihcore.metadata.haiti.mirebalais.MirebalaisLocations;
import org.openmrs.module.pihcore.reporting.BaseReportTest;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ReflectionUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetUtil;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class RegistrationDataSetManagerTest extends BaseReportTest {;

    @Autowired
    RegistrationDataSetManager registrationDataSetManager;

    @Autowired
    DataSetDefinitionService dataSetDefinitionService;

    @Autowired
    HaitiAddressBundle haitiAddressBundle;

    @Autowired
    SocioEconomicConcepts socioEconomicConcepts;

    @Autowired
    HaitiPatientIdentifierTypeBundle haitiPatientIdentifierTypeBundle;

    @Autowired
    EmrApiProperties emrApiProperties;

    @Autowired
    TestDataManager data;

    @Before
    public void setUp() throws Exception {

        deployService.installBundle(haitiPatientIdentifierTypeBundle);;
        deployService.installBundle(haitiAddressBundle);
        deployService.installBundle(socioEconomicConcepts);

        PatientBuilder pb = data.patient();
        pb.name(new PersonName("John", "Smitty", "Smith"));
        pb.birthdate("1977-11-23").birthdateEstimated(false);
        pb.male();
        pb.personAttribute(Metadata.lookup(PersonAttributeTypes.TELEPHONE_NUMBER), "555-1234");
        pb.personAttribute(Metadata.lookup(PersonAttributeTypes.UNKNOWN_PATIENT), "false");
        pb.personAttribute(Metadata.lookup(PersonAttributeTypes.BIRTHPLACE), "Wichita");
        pb.personAttribute(Metadata.lookup(PersonAttributeTypes.MOTHERS_FIRST_NAME), "Isabel");
        address(pb, AddressSupport.getInstance().getDefaultLayoutTemplate(), "USA", "MA", "Boston", "JP", "Pondside", "");
        pb.identifier(Metadata.lookup(HaitiPatientIdentifierTypes.ZL_EMR_ID), "X3XK71");
        Patient p = pb.save();

        Date registrationDate = DateUtil.getDateTime(2015, 4, 15);
        EncounterBuilder eb = data.encounter();
        eb.patient(p);
        eb.encounterDatetime(registrationDate);
        eb.location(Metadata.lookup(MirebalaisLocations.CLINIC_REGISTRATION));
        eb.encounterType(Metadata.lookup(EncounterTypes.PATIENT_REGISTRATION));

        // TODO: Add More Obs to test
        eb.obs(concept(SocioEconomicConcepts.Concepts.CIVIL_STATUS), concept(SocioEconomicConcepts.Concepts.MARRIED));
        eb.save();
    }

    @Test
    public void testDataSet() throws Exception {
        DataSetDefinition dsd = registrationDataSetManager.constructDataSet();
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("startDate", DateUtil.getDateTime(2015, 1, 1));
        context.addParameterValue("endDate", DateUtil.getDateTime(2015, 12, 31));
        DataSet dataSet = dataSetDefinitionService.evaluate(dsd, context);
        DataSetUtil.printDataSet(dataSet, System.out);
    }

    protected PatientBuilder address(PatientBuilder pb, AddressTemplate template, String... values) {
        PersonAddress a = new PersonAddress();
        int index = 0;
        for (String property : template.getNameMappings().keySet()) {
            ReflectionUtil.setPropertyValue(a, property, values[index]);
            index++;
        }
        return pb.address(a);
    }

    protected Concept concept(String uuid) {
        return MetadataUtils.existing(Concept.class, uuid);
    }

}
