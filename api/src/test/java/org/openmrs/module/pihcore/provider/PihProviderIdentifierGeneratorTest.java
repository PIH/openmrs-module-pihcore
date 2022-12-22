package org.openmrs.module.pihcore.provider;

;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.providermanagement.Provider;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PihProviderIdentifierGeneratorTest {

    private PihProviderIdentifierGenerator generator;

    private Config config;

    @BeforeEach
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
        provider.setId(30000);
        String identifier = (generator).generateIdentifier(provider);
        Assert.assertEquals("MCEMA2", identifier);
    }

    @Test
    public void shouldGenerateIdentifierWithTwoDigitPrefix() {

        when(config.getProviderIdentifierPrefix()).thenReturn("PL");

        Provider provider = new Provider();
        provider.setId(26000);
        String identifier = (generator).generateIdentifier(provider);
        Assert.assertEquals("PL971Y", identifier);
    }

    @Test
    public void shouldReturnWithoutCheckDigitIfPrefixHasNonLuhmCharacter() {

        when(config.getProviderIdentifierPrefix()).thenReturn("SOL");

        Provider provider = new Provider();
        provider.setId(26000);
        String identifier = (generator).generateIdentifier(provider);
        Assert.assertEquals("SOL971", identifier);
    }


    // we are generating a 3-digit base 30 number, which gives us 27000 possible combinations
    // after that, things will start to fail

    public void shouldFailForVeryHighId() {

        when(config.getProviderIdentifierPrefix()).thenReturn("M");

        Provider provider = new Provider();
        provider.setId(1000000);
        assertThrows(RuntimeException.class, () -> generator.generateIdentifier(provider));
    }
}
