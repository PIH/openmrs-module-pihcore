package org.openmrs.module.pihcore.setup;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.ImportConfig;
import org.openmrs.module.metadatasharing.ImportMode;
import org.openmrs.module.metadatasharing.ImportedPackage;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.api.MetadataSharingService;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;
import org.openmrs.module.pihcore.PihCoreUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class MetadataSharingSetup {

    protected static final Log log = LogFactory.getLog(MetadataSharingSetup.class);

    public static void installMetadataSharingPackages() {

        try {
            Collection<File> mdsFiles = loadMdsFiles();

            if (mdsFiles == null) {
                log.error("Unable to find any metadata sharing files");
                return;
            }

            List<PackageImporter> packageImporters = loadPackageImporters(mdsFiles);
            removeAlreadyLoadedPackageImporters(packageImporters);
            sortPackageImporters(packageImporters);
            loadPackages(packageImporters);

            Context.flushSession();
        }
        catch(Exception e) {
            log.error("Unable to load metadata sharing packages", e);
        }

    }

    protected static Collection<File> loadMdsFiles() {

        Collection<File> files = null;

        try {
            File conceptsDir = new File(PihCoreUtil.getConceptsMetadataSharingDirectory());
            files = FileUtils.listFiles(conceptsDir, null, true);
        }
        catch (Exception e) {
            log.error("Unable to open MDS packages directory", e);
        }

        if (files == null || files.size() == 0) {
            log.error("No files found in MDS packages directory");
        }

        return files;
    }

    protected static List<PackageImporter> loadPackageImporters(Collection<File> mdsFiles ) throws IOException{
        List<PackageImporter> packageImporters = new ArrayList<>();

        for (File file : mdsFiles) {

            try {
                PackageImporter metadataImporter = MetadataSharing.getInstance().newPackageImporter();
                metadataImporter.setImportConfig(ImportConfig.valueOf(ImportMode.MIRROR));  // currently we always do mirror mode
                log.info("...loading package: " + file.getName());
                metadataImporter.loadSerializedPackageStream(new BufferedInputStream(new FileInputStream(file)));
                packageImporters.add(metadataImporter);
            }
            catch (Exception ex) {
                log.error("Failed to load metadata sharing package " + file.getName(), ex);
            }
        }

        return packageImporters;
    }

    protected static List<PackageImporter> removeAlreadyLoadedPackageImporters(List<PackageImporter> packageImporters) throws IOException {

        Iterator<PackageImporter> i = packageImporters.iterator();

        while(i.hasNext()) {
            PackageImporter packageImporter = i.next();
            ImportedPackage installed = Context.getService(MetadataSharingService.class).getImportedPackageByGroup(packageImporter.getPackage().getGroupUuid());
            if (installed != null && installed.getVersion() >= packageImporter.getPackage().getVersion()) {
                log.info("Metadata package " + packageImporter.getPackage().getName() + " is already installed with version "
                        + packageImporter.getPackage().getVersion());
                i.remove();
            }
        }

        return packageImporters;
    }


    protected synchronized static List<PackageImporter> sortPackageImporters(List<PackageImporter> packageImporters) {

        // do the actual imports
        if (packageImporters.size() > 0) {

            // sort ConceptSources package first, and then in order of date created, with most recently created coming last: this is so in the case of inconsistent metadata between packages, the most recent one wins
            Collections.sort(packageImporters, new Comparator<PackageImporter>() {
                @Override
                public int compare(PackageImporter i1, PackageImporter i2) {
                    if(i1.getPackage().getName().equals("PIH Concept Sources")) {
                        return -1;
                    }
                    else if (i2.getPackage().getName().equals("PIH Concept Sources")) {
                        return 1;
                    }
                    else {
                        return i1.getImportedPackage().getDateCreated().compareTo(i2.getImportedPackage().getDateCreated());
                    }
                }
            });
       }

        return packageImporters;
    }

    protected synchronized static void loadPackages(List<PackageImporter> packageImporters) {

        // do the actual imports
        if (packageImporters.size() > 0) {

            for (PackageImporter packageImporter : packageImporters) {
                long timer = System.currentTimeMillis();
                log.info("Importing package: " + packageImporter.getImportedPackage().getName());
                packageImporter .importPackage();
                log.info("Imported " + packageImporter.getImportedPackage().getName() + " in " + (System.currentTimeMillis() - timer) + "ms");
            }
        }

    }

}
