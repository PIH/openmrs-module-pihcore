package org.openmrs.module.pihcore.htmlformentry;

import org.apache.velocity.VelocityContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.velocity.VelocityContextContentProvider;
import org.openmrs.module.pihcore.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PihCoreVelocityContextProvider implements VelocityContextContentProvider {

    @Autowired
    private Config config;

    @Override
    public void populateContext(FormEntrySession formEntrySession, VelocityContext velocityContext) {
        formEntrySession.addToVelocityContext("config", config);
    }
}
