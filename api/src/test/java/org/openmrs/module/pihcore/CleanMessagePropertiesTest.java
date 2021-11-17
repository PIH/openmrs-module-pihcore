package org.openmrs.module.pihcore;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.openmrs.util.OpenmrsUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * This is an integration test that connects to the pihemr config project and manipulates the message properties domain
 * To use this, you'll need to configure/pass a system property named "domain_dir_path" when running this test.
 * If you are running this test from the command line, that can be done with a -D argument like this:
 *
 *  mvn test -f api/pom.xml -Dtest=CleanMessagePropertiesTest -Ddomain_dir_path=/code/openmrs-config-pihemr/configuration/messageproperties
 */
public class CleanMessagePropertiesTest {

    public static String[] baseFileNames = {"messages", "messages_encountertypes"};

    public static String primaryLocale = "en";

    public static String[] secondaryLocales = {"es", "fr", "ht"};

    static String domainDirPath = System.getProperty("domain_dir_path");

    @Test
    public void execute() throws Exception {

        // Only run this test if it is being run intentionally with a system property.  Under normal building, this should do nothing
        if (StringUtils.isEmpty(domainDirPath)) {
            return;
        }
        File domainDir = new File(domainDirPath);
        if (!domainDir.exists() || !domainDir.isDirectory()) {
            System.out.println(domainDirPath + " does not point to a valid directory");
            return;
        }

        for (String baseFileName : baseFileNames) {
            File primaryFile = new File(domainDir, baseFileName + "_" + primaryLocale + ".properties");
            if (!primaryFile.exists()) {
                throw new RuntimeException("Unable to find file: " + primaryFile.getAbsolutePath());
            }
            System.out.println("Loading primary properties from: " + primaryFile.getAbsolutePath());
            List<String> primaryLines = FileUtils.readLines(primaryFile, "UTF-8");
            for (String secondaryLocale : secondaryLocales) {
                File secondaryFile = new File(domainDir, baseFileName + "_" + secondaryLocale + ".properties");
                if (secondaryFile.exists()) {
                    Properties existingSecondaryProperties = loadProperties(secondaryFile);
                    List<String> newLines = new ArrayList<>();
                    for (String line : primaryLines) {
                        if (StringUtils.isEmpty(line)) {
                            newLines.add(line);
                        }
                        else {
                            String[] split = line.split("=", 2);
                            String property = split[0].trim();
                            String primaryVal = (split.length == 2 ? split[1] : "").trim();
                            String secondaryVal = existingSecondaryProperties.getProperty(property, "").trim();
                            if (StringUtils.isEmpty(secondaryVal) || secondaryVal.equals(primaryVal)) {
                                if (!property.startsWith("#")) {
                                    property = "# " + property;
                                }
                                secondaryVal = "";
                            }
                            String newLine = property;
                            if (StringUtils.isNotEmpty(primaryVal) || StringUtils.isNotEmpty(secondaryVal)) {
                                newLine += "=" + secondaryVal;
                            }
                            newLines.add(newLine);
                        }
                    }
                    System.out.println("Writing new properties to: " + secondaryFile.getAbsolutePath());
                    FileUtils.writeLines(secondaryFile, StandardCharsets.UTF_8.name(), newLines);
                }
                else {
                    System.out.println("WARNING: Could not find secondary language file: " + secondaryFile.getAbsolutePath());
                }
            }

        }
    }

    public Properties loadProperties(File propertiesFile) {
        Properties p = new Properties();
        OpenmrsUtil.loadProperties(p, propertiesFile);
        return p;
    }
}
