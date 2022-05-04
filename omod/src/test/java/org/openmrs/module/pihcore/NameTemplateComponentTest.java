package org.openmrs.module.pihcore;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openmrs.layout.name.NameSupport;
import org.openmrs.layout.name.NameTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class NameTemplateComponentTest {

    @Autowired
    private NameSupport nameSupport;

    private PihCoreActivator activator;


    @Disabled
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
