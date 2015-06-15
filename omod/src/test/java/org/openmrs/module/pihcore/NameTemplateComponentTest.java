package org.openmrs.module.pihcore;

import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.layout.web.name.NameSupport;
import org.openmrs.layout.web.name.NameTemplate;
import org.openmrs.module.mirebalais.MirebalaisHospitalActivator;import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class NameTemplateComponentTest {

    @Autowired
    private NameSupport nameSupport;

    private PihCoreActivator activator;


    @Ignore
    @Test
    public void verifyNameTemplateConfigured() {

        activator = new PihCoreActivator();
        activator.contextRefreshed();

        assertThat(nameSupport.getDefaultLayoutFormat(), is("short"));

        NameTemplate nameTemplate = nameSupport.getLayoutTemplateByCodeName("short");

        assertThat(nameTemplate.getLineByLineFormat().get(0), is("givenName"));
        assertThat(nameTemplate.getLineByLineFormat().get(1), is("familyName"));
        assertThat(nameTemplate.getLineByLineFormat().get(2), is("middleName"));

        assertThat(nameTemplate.getNameMappings().get("givenName"), is("zl.givenName"));
        assertThat(nameTemplate.getNameMappings().get("familyName"), is("zl.familyName"));
        assertThat(nameTemplate.getNameMappings().get("middleName"), is("zl.nickname"));
    }

}
