package org.pih.oclutil;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OclConceptName {

    @JsonProperty
    private String uuid;

    @JsonProperty
    private String name;

    @JsonProperty
    private String locale;

    @JsonProperty("locale_preferred")
    private Boolean localePreferred;

    @JsonProperty("name_type")
    private String nameType;

    public OclConceptName() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Boolean getLocalePreferred() {
        return localePreferred == null ? false : localePreferred;
    }

    public void setLocalePreferred(Boolean localePreferred) {
        this.localePreferred = localePreferred;
    }

    public String getNameType() {
        return nameType;
    }

    public void setNameType(String nameType) {
        this.nameType = nameType;
    }
}
