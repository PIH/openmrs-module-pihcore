package org.pih.oclutil;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OclMapping {

    @JsonProperty("external_id")
    private String externalId;

    @JsonProperty("map_type")
    private String mapType;

    @JsonProperty("to_concept_code")
    private String toConceptCode;

    @JsonProperty("to_source_url")
    private String toSourceUrl;

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getMapType() {
        return mapType;
    }

    public void setMapType(String mapType) {
        this.mapType = mapType;
    }

    public String getToConceptCode() {
        return toConceptCode;
    }

    public void setToConceptCode(String toConceptCode) {
        this.toConceptCode = toConceptCode;
    }

    public String getToSourceUrl() {
        return toSourceUrl;
    }

    public void setToSourceUrl(String toSourceUrl) {
        this.toSourceUrl = toSourceUrl;
    }

    static Pattern sourceRegex = Pattern.compile(".+/sources/(.+)/");

    public String getToConceptSource() {
        if (toSourceUrl == null) {
            return null;
        }
        Matcher matcher = sourceRegex.matcher(toSourceUrl);
        if (!matcher.matches()) {
            return null;
        }
        return matcher.group(1);
    }
}
