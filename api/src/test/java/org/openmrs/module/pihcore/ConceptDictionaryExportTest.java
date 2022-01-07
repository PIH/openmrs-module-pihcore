package org.openmrs.module.pihcore;

import org.apache.commons.lang.BooleanUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptComplex;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNumeric;
import org.openmrs.ConceptSet;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * This is an integration test that connects to an existing database and exports all concepts
 *
 * To execute this against an appropriate database, ensure you have a properties file with the following settings:
 * -  connection.url, connection.username, connection.password properties that point to a valid DB connection
 * -  junit.username and junit.password properties that point to a valid OpenMRS account
 *
 * Configure a system property named "concept_export_properties_file" with the location of this properties file.
 * If you are running this test from the command line, that can be done with a -D argument like this:
 *
 * mvn test -Dtest=ConceptDictionaryExportTest -Dconcept_export_properties_file=/tmp/concept_export.properties
 */
@SkipBaseSetup
//@Ignore
public class ConceptDictionaryExportTest extends BaseModuleContextSensitiveTest {

    static Properties props = null;
    static String EXPORT_DIR = System.getProperty("java.io.tmpdir");
    static String EXPORT_FILENAME = "concepts.json";

    static {
        String propFile = System.getProperty("concept_export_properties_file");
        if (propFile != null) {
            props = new Properties();
            try {
                props.load(new FileInputStream(propFile));
            } catch (Exception e) {
                System.out.println("Error loading properties from " + propFile + ": " + e.getMessage());
            }
        }

        if (props != null && !props.isEmpty()) {
            System.setProperty("databaseUrl", props.getProperty("connection.url"));
            System.setProperty("databaseUsername", props.getProperty("connection.username"));
            System.setProperty("databasePassword", props.getProperty("connection.password"));
            System.setProperty("databaseDriver", props.getProperty("connection.driver_class"));
            System.setProperty("databaseDialect", "org.hibernate.dialect.MySQLDialect");
            System.setProperty("useInMemoryDatabase", "false");
            EXPORT_DIR = props.getProperty("exportDir", System.getProperty("java.io.tmpdir"));
            EXPORT_FILENAME = props.getProperty("exportFilename", "concepts.json");
        }
    }

    @Autowired
    DbSessionFactory sessionFactory;

    @Override
    public Properties getRuntimeProperties() {
        Properties p = new Properties(props);
        p.putAll(super.getRuntimeProperties());
        return p;
    }

    /**
     * Intentional issues currently with this, in order to isolate away known issues.  These should be removed as appropriate:
     * - Normalizes concept source name, due to known issue needed in OCL to facilitate source matching
     * - Excludes voided concept names (which are really retired names that may have references), as these are not provided by ocl
     * - Converts answer sort weight from null -> 1.0 if only one answer exists for the concept
     */
    @Test
    public void exportData() throws Exception {

        // Only run this test if it is being run alone.  This ensures this test will not run in normal build.
        if (props == null) {
            return;
        }

        if (!Context.isSessionOpen()) {
            Context.openSession();
        }
        authenticate();

        List<Map<String, Object>> concepts = new ArrayList<>();
        System.out.println("Pulling concepts");
        int i = 0;

        List<Concept> conceptList = Context.getConceptService().getAllConcepts();
        conceptList.sort(Comparator.comparing(t -> t.getUuid().toUpperCase()));

        List<Concept> conceptsToIgnore = getConceptsToIgnore();
        conceptList.removeAll(conceptsToIgnore);

        for (Concept concept : conceptList) {
            System.out.println(i++ + ": Pulling concept: " + concept.getUuid());
            Map<String, Object> c = new LinkedHashMap<>();
            concepts.add(c);
            c.put("uuid", concept.getUuid());
            c.put("concept_class", concept.getConceptClass().getName());
            c.put("concept_datatype", concept.getDatatype().getName());
            c.put("is_set", BooleanUtils.isTrue(concept.getSet()) ? "true" : "false");
            c.put("version", concept.getVersion() == null ? "" : concept.getVersion());
            c.put("retired", BooleanUtils.isTrue(concept.getRetired()) ? "true" : "false");
            c.put("retireReason", concept.getRetireReason() == null ? "" : concept.getRetireReason());
            if (concept instanceof ConceptNumeric) {
                ConceptNumeric cn = (ConceptNumeric) concept;
                c.put("hi_absolute", cn.getHiAbsolute() == null ? "" : cn.getHiAbsolute().toString());
                c.put("hi_critical", cn.getLowAbsolute() == null ? "" : cn.getLowAbsolute().toString());
                c.put("hi_normal", cn.getHiNormal() == null ? "" : cn.getHiNormal().toString());
                c.put("low_absolute", cn.getLowAbsolute() == null ? "" : cn.getLowAbsolute().toString());
                c.put("low_critical", cn.getLowCritical() == null ? "" : cn.getLowCritical().toString());
                c.put("low_normal", cn.getLowNormal() == null ? "" : cn.getLowNormal().toString());
                c.put("units", cn.getUnits() == null ? "" : cn.getUnits());
                c.put("allow_decimal", BooleanUtils.isTrue(cn.getAllowDecimal()) ? "true" : "false");
                c.put("display_precision", cn.getHiAbsolute() == null ? "" : cn.getHiAbsolute().toString());
            }
            if (concept instanceof ConceptComplex) {
                ConceptComplex cc = (ConceptComplex) concept;
                c.put("handler", cc.getHandler());
            }

            List<ConceptName> names = new ArrayList<>(concept.getNames(true));
            names.sort(Comparator
                    .comparing(ConceptName::getName)
                    .thenComparing(cn -> cn.getConceptNameType() == null ? "" : cn.getConceptNameType().name())
                    .thenComparing(cn -> cn.getLocale().toString())
                    .thenComparing(ConceptName::getLocalePreferred));

            List<Map<String, Object>> nameList = new ArrayList<>();
            for (ConceptName name : names) {
                if (BooleanUtils.isNotTrue(name.getVoided())) {
                    Map<String, Object> n = new LinkedHashMap<>();
                    n.put("name", name.getName());
                    n.put("locale", name.getLocale().toString());
                    n.put("locale_preferred", BooleanUtils.isTrue(name.getLocalePreferred()) ? "true" : "false");
                    n.put("type", name.getConceptNameType() == null ? "" : name.getConceptNameType().name());
                    n.put("voided", BooleanUtils.isTrue(name.getVoided()) ? "true" : "false");
                    nameList.add(n);
                }
            }
            c.put("names", nameList);

            List<ConceptDescription> descriptions = new ArrayList<>(concept.getDescriptions());
            descriptions.sort(Comparator.comparing(ConceptDescription::getDescription));
            List<Map<String, Object>> descriptionList = new ArrayList<>();
            for (ConceptDescription description : descriptions) {
                Map<String, Object> n = new LinkedHashMap<>();
                n.put("description", description.getDescription());
                n.put("locale", description.getLocale().toString());
                descriptionList.add(n);
            }
            c.put("descriptions", descriptionList);

            List<ConceptAnswer> answers = new ArrayList<>(concept.getAnswers(true));
            answers.sort(Comparator.comparingDouble(ConceptAnswer::getSortWeight).thenComparing(ca -> ca.getAnswerConcept().getUuid()));
            List<Map<String, Object>> answerList = new ArrayList<>();
            for (ConceptAnswer ca : answers) {
                if (!conceptsToIgnore.contains(ca.getAnswerConcept())) {
                    Map<String, Object> n = new LinkedHashMap<>();
                    n.put("answerConcept", ca.getAnswerConcept().getUuid());
                    n.put("sortWeight", ca.getSortWeight() == null ? (answers.size() == 1 ? "1.0" : "") : ca.getSortWeight().toString());
                    answerList.add(n);
                }
            }
            c.put("answers", answerList);

            List<ConceptSet> setMembers = new ArrayList<>(concept.getConceptSets());
            setMembers.sort(Comparator.comparingDouble(ConceptSet::getSortWeight).thenComparing(cs -> cs.getConcept().getUuid()));
            List<Map<String, Object>> setMemberList = new ArrayList<>();
            for (ConceptSet cs : setMembers) {
                if (!conceptsToIgnore.contains(cs.getConcept())) {
                    Map<String, Object> n = new LinkedHashMap<>();
                    n.put("concept", cs.getConcept().getUuid());
                    n.put("sortWeight", cs.getSortWeight() == null ? "" : cs.getSortWeight().toString());
                    setMemberList.add(n);
                }
            }
            c.put("setMembers", setMemberList);

            List<ConceptMap> mappings = new ArrayList<>(concept.getConceptMappings());
            mappings.sort(Comparator
                    .comparing((ConceptMap m) -> m.getConceptReferenceTerm().getConceptSource().getName())
                    .thenComparing(m -> m.getConceptMapType().getName())
                    .thenComparing(m -> m.getConceptReferenceTerm().getCode())
            );
            List<Map<String, Object>> mappingList = new ArrayList<>();
            for (ConceptMap cm : mappings) {
                Map<String, Object> n = new LinkedHashMap<>();
                n.put("source", normalizeSourceName(cm.getConceptReferenceTerm().getConceptSource().getName()));
                n.put("type", cm.getConceptMapType().getName());
                n.put("code", cm.getConceptReferenceTerm().getCode());
                mappingList.add(n);
            }
            c.put("mappings", mappingList);
        }

        File exportFile = new File(EXPORT_DIR, EXPORT_FILENAME);
        System.out.println("Writing concepts to: " + exportFile);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(exportFile, concepts);
        System.out.println("Concepts exported successfully");
    }

    public String normalizeSourceName(String name) {
        return name.toUpperCase()
                .replace(" ", "")
                .replace("-", "")
                .replace("_", "");
    }

    public List<Concept> getConceptsToIgnore() {
        List<Concept> l = new ArrayList<>();
        return l;
    }

    public Concept getConcept(Integer pihMapping) {
        Concept c = Context.getConceptService().getConceptByMapping(pihMapping.toString(), "PIH");
        if (c == null) {
            throw new IllegalArgumentException("Unable to find concept PIH:" + pihMapping);
        }
        return c;
    }
}
