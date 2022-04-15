package org.openmrs.module.pihcore.setup;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.dispensing.importer.DrugImporter;
import org.openmrs.module.dispensing.importer.ImportNotes;
import org.openmrs.module.pihcore.PihCoreUtil;

/*
 * Installs drugs from a drug list file in configuration/pih/drugs/
 * 
 * Compares MD5 checksums in configuration_checksums/pih/drugs/ to check if
 * the file is new.
 */
public class DrugListSetup {

    protected static final Log log = LogFactory.getLog(DrugListSetup.class);

    public static void installDrugList() {
        try {
            File drugListFile = loadDrugListFile();
            if (drugListFile != null && isNew(drugListFile)) {
                installDrugListFile(drugListFile);
                Context.flushSession();  // copied from previous implementation in mirebalaismetadata
            }
        } catch (Exception e) {
            log.error("Unable to load drug list", e);
        }
    }

    private static File loadDrugListFile() {
        Collection<File> files = null;
        String path = PihCoreUtil.getDrugListDirectory();
        try {
            File drugsDir = new File(path);
            if (drugsDir.exists()) {
                files = FileUtils.listFiles(drugsDir, null, true);
            }
        } catch (Exception e) {
            log.warn("Unable to open drug list directory " + path, e);
        }
        if (files == null || files.size() == 0) {
            log.warn("No files found in drug list directory " + path);
            return null;
        } else if (files.size() > 1) {
            log.error("More than one drug list found. Only one drug list is supported. "
                    + "Not loading any drug list. See " + path);
            return null;
        } else {
            return files.iterator().next();
        }
    }

    private static boolean isNew(File drugListFile) {
        String checksum = computeChecksum(drugListFile); // can be null
        String oldChecksum = "old checksum not available"; // ensure not null by default
        try {
            String checksumPath = PihCoreUtil.getDrugListChecksumDirectory() + drugListFile.getName();
            oldChecksum = new String(Files.readAllBytes(Paths.get(checksumPath)));
        } catch (IOException e) {
            log.info("Couldn't read old checksum for drug list file. It's probably new. At path "
                    + drugListFile.getAbsolutePath());
        }
        return !oldChecksum.equals(checksum);
    }

    private static void installDrugListFile(File drugListFile) {
        try {
            DrugImporter drugImporter = Context.getRegisteredComponents(DrugImporter.class).get(0);
            FileInputStream inputStream = new FileInputStream(drugListFile);
            InputStreamReader reader = new InputStreamReader(inputStream);
            ImportNotes notes = drugImporter.importSpreadsheet(reader);
            if (notes.hasErrors()) {
                log.error("Failed to install drug list file " + drugListFile.getAbsolutePath()
                        + ". Here's what went wrong:\n" + notes);
            } else {
                saveChecksum(drugListFile);
            }
        } catch (FileNotFoundException e) {
            log.error("Failed to install drug list file " + drugListFile.getAbsolutePath(), e);
        } catch (IOException e) {
            log.error("Failed to install drug list file " + drugListFile.getAbsolutePath(), e);
        }
    }

    /**
     * Compute the checksum of a configuration file.
     * 
     * Code taken from openmrs-module-initializer.
     */
    private static String computeChecksum(File configFile) {
        String checksum = null;
        if (configFile.exists()) {
            try {
                FileInputStream fis = new FileInputStream(configFile);
                checksum = DigestUtils.md5Hex(fis);
                fis.close();
            } catch (Exception e) {
                log.warn("Error computing checksum of drug list file " + configFile.getAbsolutePath(), e);
            }
        }
        return checksum;
    }

    private static void saveChecksum(File drugListFile) throws IOException {
               String checksum = computeChecksum(drugListFile);
               if (checksum == null) {
                   log.error(
                           "Could not compute checksum for drug list file. It will be re-loaded in the future. At path "
                                   + drugListFile.getAbsolutePath());
                   return;
               }
               File checksumDir = new File(PihCoreUtil.getDrugListChecksumDirectory());
               if (!checksumDir.exists()) {
                   boolean created = checksumDir.mkdirs();
                   if (!created) {
                       log.error(
                               "Could not create drug list checksum directory. Drug lists will be re-loaded in the future. At path "
                                       + checksumDir.getAbsolutePath());
                       return;
                   }
               }
               File checksumFile = new File(PihCoreUtil.getDrugListChecksumDirectory() + drugListFile.getName());
               FileWriter fw = new FileWriter(checksumFile.getAbsoluteFile());
               BufferedWriter bw = new BufferedWriter(fw);
               bw.write(checksum);
               bw.close();
    }
}
