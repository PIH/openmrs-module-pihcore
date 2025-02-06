package org.openmrs.module.pihcore;

import org.hibernate.cfg.Environment;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.web.test.jupiter.BaseModuleWebContextSensitiveTest;

import java.io.File;
import java.util.Properties;

public abstract class PihCoreWebContextSensitiveTest extends BaseModuleWebContextSensitiveTest {

    public PihCoreWebContextSensitiveTest() {
        super();
        {
            Module mod = new Module("");
            mod.setModuleId("metadatamapping");
            mod.setVersion("1.3.4");
            mod.setFile(new File(""));
            ModuleFactory.getStartedModulesMap().put(mod.getModuleId(), mod);
        }
    }

    @Override
    public Properties getRuntimeProperties() {
        Properties p = super.getRuntimeProperties();
        String h2Url = PihCoreContextSensitiveTest.getH2Url();
        p.setProperty(Environment.URL, h2Url);
        p.setProperty("connection.url", h2Url);
        runtimeProperties = p;
        return p;
    }
}
