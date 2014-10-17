package com.example.bivy.sunshine;

import android.test.suitebuilder.TestSuiteBuilder;
import junit.framework.Test;
/**
 * Created by bivy on 14/10/14.
 */
public class fullTestSuite {

    public static Test suite() {

        return new TestSuiteBuilder(fullTestSuite.class).
            includeAllPackagesUnderHere().build();
    }

    public fullTestSuite() {
        super();
    }
}
