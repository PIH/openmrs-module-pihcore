package org.openmrs.module.pihcore;

import org.junit.Ignore;
import org.junit.Test;

import java.util.UUID;

/**
 * This is not a test case.
 * It is a convenience way to generate a bunch of UUID which you can then copy-paste elsewhere.
 */
@Ignore
public class GenerateUuids {

    final int N = 100;

    @Test
    public void generateSomeUuids() throws Exception {
        for (int i = 0; i < N; i++) {
            System.out.println(UUID.randomUUID().toString());
        }
    }

}
