package org.openmrs.module.pihcore.provider;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.module.emrapi.account.ProviderIdentifierGenerator;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.validator.LuhnMod30IdentifierValidator;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.providermanagement.Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component   /// this is autowired in the DomainWrapperFactory (which injects it into Account Domain Wrapper instances)
public class PihProviderIdentifierGenerator implements ProviderIdentifierGenerator {

    private static String BASE_CHARACTER_SET = "ACDEFGHJKLMNPRTUVWXY1234567890";

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

        if (StringUtils.isNotBlank(config.getProviderIdentifierPrefix())) {
            SequentialIdentifierGenerator generator = new SequentialIdentifierGenerator();
            generator.setBaseCharacterSet(BASE_CHARACTER_SET);
            generator.setFirstIdentifierBase("100");
            generator.setPrefix(config.getProviderIdentifierPrefix());
            generator.setMaxLength(4 + config.getProviderIdentifierPrefix().length());
            generator.setMinLength(3 + config.getProviderIdentifierPrefix().length());

            String identifier = generator.getIdentifierForSeed(provider.getId().longValue());

            // just don't generate a check digit if prefix is invalid, see: UHM-6927
            if (isValidPrefix(config.getProviderIdentifierPrefix())) {
                return new LuhnMod30IdentifierValidator().getValidIdentifier(identifier);
            }
            else {
                return identifier;
            }
        }
        else {
            return null;
        }

    }

    private Boolean isValidPrefix(String prefix) {
        for (char c:prefix.toCharArray()) {
            if (new LuhnMod30IdentifierValidator().getBaseCharacters().indexOf(c) == -1) {
                return false;
            }
        }
        return true;
    }


    // for mocking
    public void setConfig(Config config) {
        this.config = config;
    }
}
