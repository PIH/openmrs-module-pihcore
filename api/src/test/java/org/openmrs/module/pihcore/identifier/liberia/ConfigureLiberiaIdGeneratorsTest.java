package org.openmrs.module.pihcore.identifier.liberia;

import org.junit.jupiter.api.Test;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.pihcore.LiberiaConfigConstants;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ConfigureLiberiaIdGeneratorsTest extends PihCoreContextSensitiveTest {

    @Override
    public String getPihConfig() {
        return "default";
    }

    /**
     * A generic, country-only Liberia config (no site) should not throw when configuring the
     * generators -- previously this threw a NullPointerException on config.getSite().equalsIgnoreCase(...).
     * A non-null primaryIdentifierPrefix is set so the assertion is isolated to the null-site fix,
     * not the unrelated null-prefix NPE at ConfigureLiberiaIdGenerators.java:29.
     *
     * The standard OpenMRS test dataset does not include the "Liberia EMR ID" patient identifier
     * type that ConfigureLiberiaIdGenerators looks up by UUID (normally loaded via Initializer CSV
     * content in a real Liberia install), so it is seeded here to let saveIdentifierSource succeed
     * end-to-end, the same way it would against a real Liberia instance.
     */
    @Test
    public void configureGeneratorsShouldNotThrowForNullSite() {
        PatientIdentifierType liberiaEmrId = new PatientIdentifierType();
        liberiaEmrId.setUuid(LiberiaConfigConstants.PATIENTIDENTIFIERTYPE_LIBERIAEMRID_UUID);
        liberiaEmrId.setName("Liberia EMR ID");
        liberiaEmrId.setDescription("Test fixture identifier type for Liberia EMR ID");
        liberiaEmrId.setFormat(null);
        liberiaEmrId.setRequired(false);
        Context.getPatientService().savePatientIdentifierType(liberiaEmrId);

        ConfigDescriptor descriptor = new ConfigDescriptor();
        descriptor.setCountry(ConfigDescriptor.Country.LIBERIA);
        descriptor.setPrimaryIdentifierPrefix("L");
        // deliberately not calling descriptor.setSite(...) -- site is null
        Config config = new Config(descriptor);

        IdentifierSourceService identifierSourceService = Context.getService(IdentifierSourceService.class);

        assertDoesNotThrow(() -> ConfigureLiberiaIdGenerators.configureGenerators(identifierSourceService, config));
    }
}
