package org.openmrs.module.pihcore.search;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Knows which given names are commonly used interchangeably (ex: David/Dave, William/Bill, Jean/John), based on the
 * groups of names in givenNameEquivalents.txt. Names are compared ignoring case and accent marks.
 * The lookup is not transitive across groups: the variants of a name are the union of the groups it appears in.
 */
public class GivenNameEquivalents {

    private static final Log log = LogFactory.getLog(GivenNameEquivalents.class);

    private static final String RESOURCE = "/givenNameEquivalents.txt";

    private static volatile Map<String, Set<String>> variantsByName;

    /**
     * @return the given name (normalized) plus all of its known equivalents, never empty unless the name is blank
     */
    public static Set<String> getVariants(String name) {
        Set<String> variants = new LinkedHashSet<String>();
        String normalized = normalize(name);
        if (StringUtils.isNotBlank(normalized)) {
            variants.add(normalized);
            Set<String> known = getVariantsByName().get(normalized);
            if (known != null) {
                variants.addAll(known);
            }
        }
        return variants;
    }

    public static boolean areEquivalent(String name1, String name2) {
        String normalized1 = normalize(name1);
        String normalized2 = normalize(name2);
        if (StringUtils.isBlank(normalized1) || StringUtils.isBlank(normalized2)) {
            return false;
        }
        if (normalized1.equals(normalized2)) {
            return true;
        }
        Set<String> known = getVariantsByName().get(normalized1);
        return known != null && known.contains(normalized2);
    }

    public static String normalize(String name) {
        if (name == null) {
            return null;
        }
        String s = Normalizer.normalize(name.trim(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return s.toLowerCase();
    }

    private static Map<String, Set<String>> getVariantsByName() {
        if (variantsByName == null) {
            synchronized (GivenNameEquivalents.class) {
                if (variantsByName == null) {
                    variantsByName = load();
                }
            }
        }
        return variantsByName;
    }

    private static Map<String, Set<String>> load() {
        Map<String, Set<String>> map = new HashMap<String, Set<String>>();
        InputStream in = GivenNameEquivalents.class.getResourceAsStream(RESOURCE);
        if (in == null) {
            log.warn("Unable to find " + RESOURCE + ", given name equivalents will not be used");
            return Collections.emptyMap();
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.length() == 0 || line.startsWith("#")) {
                    continue;
                }
                Set<String> group = new HashSet<String>();
                for (String name : line.split(",")) {
                    String normalized = normalize(name);
                    if (StringUtils.isNotBlank(normalized)) {
                        group.add(normalized);
                    }
                }
                for (String name : group) {
                    Set<String> variants = map.get(name);
                    if (variants == null) {
                        variants = new HashSet<String>();
                        map.put(name, variants);
                    }
                    variants.addAll(group);
                }
            }
        }
        catch (IOException e) {
            log.error("Unable to read " + RESOURCE, e);
        }
        finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            }
            catch (IOException e) {
                // ignore
            }
        }
        return map;
    }
}
