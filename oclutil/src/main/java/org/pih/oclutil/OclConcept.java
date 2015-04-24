package org.pih.oclutil;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OclConcept {

    @JsonProperty
    private String uuid;

    @JsonProperty("concept_class")
    private String conceptClass;

    @JsonProperty
    private String datatype;

    @JsonProperty
    private List<OclConceptName> names;

    @JsonProperty
    private List<OclConceptDescription> descriptions;

    @JsonProperty
    private List<OclMapping> mappings;

    @JsonProperty
    private Map<String, String> extras;

    @Override
    public String toString() {
        if (names != null && names.size() > 0) {
            return names.get(0).getName();
        }
        return super.toString();
    }

    static Map<String, String> replacements = new HashMap<String, String>();
    static {
        replacements.put("Boolean", "coded");
        replacements.put("Coded", "coded");
        replacements.put("N/A", "notApplicable");

        replacements.put("Drug", "drug");

        replacements.put("NARROWER-THAN", "narrowerThan");
        replacements.put("SAME-AS", "sameAs");

        replacements.put("FULLY_SPECIFIED", "ConceptNameType.FULLY_SPECIFIED");
        replacements.put("SHORT", "ConceptNameType.SHORT");

        replacements.put("en", "Locale.ENGLISH");
        replacements.put("fr", "Locale.FRENCH");
        replacements.put("ht", "locale_HAITI");
    }

    private String replace(String in) {
        return replacements.containsKey(in) ? replacements.get(in) : in;
    }

    public String codeForBuilder(String id) {
        StringWriter sw = new StringWriter();
        PrintWriter w = new PrintWriter(sw);

        w.println("// TODO handle if it was numeric");

        w.println("install(new ConceptBuilder(\"" + extras.get("external_id") + "\")");
        w.println(".datatype(" + replace(datatype) + ")");
        w.println(".conceptClass(" + replace(conceptClass) + ")");

        Collections.sort(names, new Comparator<OclConceptName>() {
            public int compare(OclConceptName left, OclConceptName right) {
                int temp = left.getLocale().compareTo(right.getLocale());
                if (temp == 0) {
                    temp = right.getLocalePreferred().compareTo(left.getLocalePreferred());
                }
                return temp;
            }
        });

        for (OclConceptName name : names) {
            String comment = name.getLocalePreferred() ? " // locale-preferred" : "";
            w.println(".name(\"" + name.getUuid() + "\", \"" + name.getName().replaceAll("\"", "\\\"") + "\", " + replace(name.getLocale()) + ", " + replace(name.getNameType()) + ")" + comment);
        }

        // manually add a CIEL mapping
        w.println(".mapping(new ConceptMapBuilder(\"" + uuid + "\")");
        w.println(".type(sameAs).ensureTerm(ciel, \"" + id +"\").build())");

        for (OclMapping mapping : mappings) {
            w.println(".mapping(new ConceptMapBuilder(\"" + mapping.getExternalId() + "\")");
            w.println(".type(" + replace(mapping.getMapType()) + ").ensureTerm(" + replace(mapping.getToConceptSource()) + ", \"" + mapping.getToConceptCode() +"\").build())");
        }

        w.println("// TODO descriptions");
        w.println("// TODO answers");
        w.println("// TODO setMembers");

        // TODO descriptions

        w.println(".build());");
        return sw.toString();
    }
}
