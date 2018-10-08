package org.openmrs.module.pihcore.config.registration;

import org.codehaus.jackson.annotate.JsonProperty;

public class ProgramsDescriptor {

    @JsonProperty
    private ProgramsFieldDescriptor activeCasefinding;

    public ProgramsFieldDescriptor getActiveCasefinding() {
        return activeCasefinding;
    }

    public void setActiveCasefinding(ProgramsFieldDescriptor activeCasefinding) {
        this.activeCasefinding = activeCasefinding;
    }

}
