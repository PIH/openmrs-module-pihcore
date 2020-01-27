package org.openmrs.module.pihcore.provider;

import org.openmrs.module.emrapi.account.ProviderIdentifierGenerator;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.validator.LuhnMod30IdentifierValidator;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.providermanagement.Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component   /// this is autowired in the DomainWrapperFactory (which injects it into Account Domain Wrapper instances)
public class PihProviderIdentifierGenerator implements ProviderIdentifierGenerator {

    @Autowired
    private Config config;

    // this generates a 3 or 4 digit base 30 identifier with an "M" prefix and a check-digit suffix
    // the identifier is seeded on the primary key of the associated provider
    // since 30^4 = 27000, this will start to fail if the primary key every goes above 810000

    @Override
    public String generateIdentifier(Provider provider) {

        if (provider.getId() == null) {
            throw new IllegalStateException("Cannot generate identifier for provider without primary key");
        }

        SequentialIdentifierGenerator generator = new SequentialIdentifierGenerator();
        generator.setBaseCharacterSet("ACDEFGHJKLMNPRTUVWXY1234567890");
        generator.setFirstIdentifierBase("100");
        generator.setPrefix(config.getProviderIdentifierPrefix());
        generator.setMaxLength(4 + config.getProviderIdentifierPrefix().length());
        generator.setMinLength(3 + config.getProviderIdentifierPrefix().length());

        String identifier = generator.getIdentifierForSeed(provider.getId().longValue());

        return new LuhnMod30IdentifierValidator().getValidIdentifier(identifier);
    }

    // for mocking
    public void setConfig(Config config) {
        this.config = config;
    }
}
