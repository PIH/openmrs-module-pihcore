package org.openmrs.module.pihcore.config;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.InputStream;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ConfigDescriptorTest {

    @Test
    public void testParsing() throws Exception {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config/testConfig.json");
        ConfigDescriptor configDescriptor = new ObjectMapper().readValue(inputStream, ConfigDescriptor.class);
        assertThat(configDescriptor.getComponents().size(), is(2));
        assertThat(configDescriptor.getComponents().contains("someComponent"), is(true));
        assertThat(configDescriptor.getComponents().contains("anotherComponent"), is(true));
        assertThat(configDescriptor.getComponents().contains("missingComponent"), is(false));
        assertThat(configDescriptor.getWelcomeMessage(), is("Hello World!"));
        assertThat(configDescriptor.getSite(), is(ConfigDescriptor.Site.MIREBALAIS));
        assertThat(configDescriptor.getVisitTemplates().contains("inpatientTemplate"), is(true));
        assertThat(configDescriptor.getVisitTemplates().contains("outpatientTemplate"), is(true));
    }

}
