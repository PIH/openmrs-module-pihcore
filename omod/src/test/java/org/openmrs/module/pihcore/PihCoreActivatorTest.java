package org.openmrs.module.pihcore;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.core.IsNot.not;

public class PihCoreActivatorTest {

    @Test
    public void buildStartupFailureBannerShouldContainRootCauseMessage() {
        Exception cause = new IllegalStateException("PIH CONFIGURATION ERROR: something specific went wrong");
        String banner = PihCoreUtil.buildStartupFailureBanner(cause);

        assertThat(banner, containsString("PIH CORE MODULE FAILED TO START"));
        assertThat(banner, containsString("PIH CONFIGURATION ERROR: something specific went wrong"));
    }

    @Test
    public void buildStartupFailureBannerShouldNotPrintLiteralNullForNullMessage() {
        Exception cause = new NullPointerException();

        String banner = PihCoreUtil.buildStartupFailureBanner(cause);

        assertThat(banner, containsString("PIH CORE MODULE FAILED TO START"));
        assertThat(banner, not(containsString("null")));
        assertThat(banner, containsString("(no message)"));
    }

    @Test
    public void buildStartupFailureBannerShouldContainMessagesFromFullCauseChain() {
        Exception rootCause = new IllegalArgumentException("root cause message");
        Exception wrapper = new RuntimeException("wrapper message", rootCause);

        String banner = PihCoreUtil.buildStartupFailureBanner(wrapper);

        assertThat(banner, containsString("PIH CORE MODULE FAILED TO START"));
        assertThat(banner, containsString("wrapper message"));
        assertThat(banner, containsString("root cause message"));
    }
}
