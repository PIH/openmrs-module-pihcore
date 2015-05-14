package org.openmrs.module.pihcore.provider;

import junit.framework.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.account.ProviderIdentifierGenerator;
import org.openmrs.module.providermanagement.Provider;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Properties;

public class PihProviderIdentifierGeneratorComponentTest extends BaseModuleContextSensitiveTest {

    @Override
    public Properties getRuntimeProperties() {
        Properties p = super.getRuntimeProperties();
        p.setProperty("pih.config", "pihcore");
        return p;
    }


    @Test
    public void shouldGenerateAppropriateIdentifierForProvider() {
        Provider provider = new Provider();
        provider.setId(4);
        String identifier = (Context.getRegisteredComponents(ProviderIdentifierGenerator.class).get(0)).generateIdentifier(provider);
        Assert.assertEquals("MAAFD", identifier);
    }

}
