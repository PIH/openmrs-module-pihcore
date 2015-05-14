package org.openmrs.module.pihcore.provider;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.providermanagement.Provider;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PihProviderIdentifierGeneratorTest {

    private PihProviderIdentifierGenerator generator;

    private Config config;

    @Before
    public void setup() {
        generator = new PihProviderIdentifierGenerator();

        config = mock(Config.class);
        when(config.getProviderIdentifierPrefix()).thenReturn("M");

        generator.setConfig(config);
    }

    @Test
    public void shouldGenerateAppropriateIdentifierForProvider() {

        when(config.getProviderIdentifierPrefix()).thenReturn("M");

        Provider provider = new Provider();
        provider.setId(4);
        String identifier = (generator).generateIdentifier(provider);
        Assert.assertEquals("MAAFD", identifier);
    }

    @Test
    public void shouldNotFailForHighId() {

        when(config.getProviderIdentifierPrefix()).thenReturn("M");

        Provider provider = new Provider();
        provider.setId(26000);
        String identifier = (generator).generateIdentifier(provider);
        Assert.assertEquals("M971E", identifier);
    }

    @Test
    public void shouldGenerateIdentifierWithTwoDigitPrefix() {

        when(config.getProviderIdentifierPrefix()).thenReturn("PL");

        Provider provider = new Provider();
        provider.setId(26000);
        String identifier = (generator).generateIdentifier(provider);
        Assert.assertEquals("PL971Y", identifier);
    }


    // we are generating a 3-digit base 30 number, which gives us 27000 possible combinations
    // after that, things will start to fail

    @Test(expected = RuntimeException.class)
    public void shouldFailForVeryHighId() {

        when(config.getProviderIdentifierPrefix()).thenReturn("M");

        Provider provider = new Provider();
        provider.setId(27000);
        String identifier = (generator).generateIdentifier(provider);
    }
}
