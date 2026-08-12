package org.openmrs.module.pihcore;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;

public class PihCoreActivatorTest {

    @Test
    public void buildStartupFailureBannerShouldContainRootCauseMessage() {
        Exception cause = new IllegalStateException("PIH CONFIGURATION ERROR: something specific went wrong");
        String banner = PihCoreActivator.buildStartupFailureBanner(cause);

        assertThat(banner, containsString("PIH CORE MODULE FAILED TO START"));
        assertThat(banner, containsString("PIH CONFIGURATION ERROR: something specific went wrong"));
    }
}
