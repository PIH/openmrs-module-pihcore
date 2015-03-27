package org.openmrs.module.pihcore.deploy.bundle;

/**
 * Implements install() such that it calls one method on every install (e.g. module startup) but only calls another method
 * if a we're installing a new version (by checking a global property for the current installed version)
 */
public abstract class VersionedPihMetadataBundle extends PihMetadataBundle {

    /**
     * You need to increment this every time you commit a new change to the bundle, so that the infrastructure knows
     * whether or not to call #installNewVersion() (based on checking a global property)
     * @return
     */
    public abstract int getVersion();

    @Override
    public void install() throws Exception {
        String gp = "metadatadeploy.bundle.version." + getClass().getName();
        String val = administrationService.getGlobalProperty(gp);
        boolean installNewVersion = true;
        try {
            if (getVersion() <= Integer.valueOf(val)) {
                installNewVersion = false;
            }
        } catch (Exception ex) {
            // pass
        }
        installEveryTime();
        if (installNewVersion) {
            installNewVersion();
            setGlobalProperty(gp, "" + getVersion());
        }
    }

    protected abstract void installEveryTime() throws Exception;

    protected abstract void installNewVersion() throws Exception;

}
