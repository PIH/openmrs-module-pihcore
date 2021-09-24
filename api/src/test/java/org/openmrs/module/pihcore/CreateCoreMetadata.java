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

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;

/**
 * Produces the coreMetadata.xml test dataset.  This should be run as follows:
 * 1. Install a new, empty mirebalais implementation following the steps on the wiki
 *      (it's probably okay to use an existing database, as long as it's nearly-empty of things like users and providers)
 * 2. Point your .OpenMRS/openmrs-runtime.properties file at this database
 * 3. Specify a location where you want the dataset to be written (leaving this blank will skip the execution of this)
 * 4. Run this as a unit test
 * This should produce a new version of coreMetadata.xml at the location you have specified,
 * and you can copy it into the resources folder if and as appropriate
 * 5. If any of your <provider .../> rows have a provider_role_id attribute, remove that
 */
@Ignore
public class CreateCoreMetadata extends BaseModuleContextSensitiveTest {

	public String getOutputDirectory() {
		//return "/home/mseaton/code/github/pih/openmrs-module-pihcore/api/src/test/resources/org/openmrs/module/pihcore";
		return "";
	}

    @Test
	public void run() throws Exception {

		// only run this test if it is being run alone and if an output directory has been specified
		if (getLoadCount() != 1 || ObjectUtil.isNull(getOutputDirectory()))
			return;
		
		// database connection for dbunit
		IDatabaseConnection connection = new DatabaseConnection(getConnection());
		
		// partial database export
		QueryDataSet initialDataSet = new QueryDataSet(connection);

		initialDataSet.addTable("users", "SELECT * FROM users");
		initialDataSet.addTable("person", "SELECT * FROM person p WHERE (select count(*) from users where person_id = p.person_id) > 0 OR (select count(*) from provider where person_id = p.person_id) > 0");

		initialDataSet.addTable("concept", "SELECT * FROM concept");
		initialDataSet.addTable("concept_answer", "SELECT * FROM concept_answer");
		initialDataSet.addTable("concept_class", "SELECT * FROM concept_class");
		initialDataSet.addTable("concept_datatype", "SELECT * FROM concept_datatype");
		initialDataSet.addTable("concept_map_type", "SELECT * FROM concept_map_type");
		initialDataSet.addTable("concept_name", "SELECT * FROM concept_name");
		initialDataSet.addTable("concept_numeric", "SELECT * FROM concept_numeric");
		initialDataSet.addTable("concept_reference_map", "SELECT * FROM concept_reference_map");
		initialDataSet.addTable("concept_reference_source", "SELECT * FROM concept_reference_source");
		initialDataSet.addTable("concept_reference_term", "SELECT * FROM concept_reference_term");
		initialDataSet.addTable("concept_reference_term_map", "SELECT * FROM concept_reference_term_map");
		initialDataSet.addTable("concept_set", "SELECT * FROM concept_set");
		initialDataSet.addTable("encounter_role", "SELECT * FROM encounter_role");
		initialDataSet.addTable("encounter_type", "SELECT * FROM encounter_type");
		initialDataSet.addTable("location", "SELECT * FROM location");
		initialDataSet.addTable("location_attribute", "SELECT * FROM location_attribute");
		initialDataSet.addTable("location_attribute_type", "SELECT * FROM location_attribute_type");
		initialDataSet.addTable("location_tag", "SELECT * FROM location_tag");
		initialDataSet.addTable("location_tag_map", "SELECT * FROM location_tag_map");
		initialDataSet.addTable("order_type", "SELECT * FROM order_type");
		initialDataSet.addTable("patient_identifier_type", "SELECT * FROM patient_identifier_type");
		initialDataSet.addTable("person_attribute_type", "SELECT * FROM person_attribute_type");
		initialDataSet.addTable("privilege", "SELECT * FROM privilege");
		initialDataSet.addTable("provider", "SELECT * FROM provider");
		initialDataSet.addTable("relationship_type", "SELECT * FROM relationship_type");
		initialDataSet.addTable("role", "SELECT * FROM role");
		initialDataSet.addTable("role_privilege", "SELECT * FROM role_privilege");
		initialDataSet.addTable("role_role", "SELECT * FROM role_role");
		initialDataSet.addTable("user_role", "SELECT * FROM user_role");
		initialDataSet.addTable("visit_type", "SELECT * FROM visit_type");
        initialDataSet.addTable("global_property", "SELECT * FROM global_property WHERE property like 'emr.%' or property like 'emrapi.%'");

		File outputFile = new File(getOutputDirectory(), "coreMetadata.xml");

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		FlatXmlDataSet.write(initialDataSet, baos);
		String contents = baos.toString("UTF-8");
		FileWriter writer = new FileWriter(outputFile);

		for (String line : contents.split(System.getProperty("line.separator"))) {
			if (line.contains("<concept ")) {
				line = line.replace("short_name=\"\" ", "");
				line = line.replace("description=\"\" ", "");
			}
			if (line.contains("system_id=\"admin\"")) {
				line = "  <users user_id=\"1\" person_id=\"1\" system_id=\"admin\" username=\"\" password=\"4a1750c8607d0fa237de36c6305715c223415189\" salt=\"c788c6ad82a157b712392ca695dfcf2eed193d7f\" secret_question=\"\" creator=\"1\" date_created=\"2005-01-01 00:00:00.0\" changed_by=\"1\" date_changed=\"2007-09-20 21:54:12.0\" retired=\"false\" retire_reason=\"\" uuid=\"1010d442-e134-11de-babe-001e378eb67e\"/>";
			}
			writer.write(line + System.getProperty("line.separator"));
		}

		writer.flush();
		writer.close();
	}
	
	/**
	 * Make sure we use the database defined by the runtime properties and not the hsql in-memory
	 * database
	 * 
	 * @see org.openmrs.test.BaseContextSensitiveTest#useInMemoryDatabase()
	 */
	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}
}
