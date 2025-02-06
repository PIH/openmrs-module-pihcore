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
        String url = p.getProperty(Environment.URL);
        url = url.replace("DB_CLOSE_DELAY=30", "DB_CLOSE_DELAY=-1");
        p.setProperty(Environment.URL, url);
        p.setProperty("connection.url", url);
        runtimeProperties = p;
        return p;
    }
}
