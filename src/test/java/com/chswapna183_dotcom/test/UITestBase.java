package com.chswapna183_dotcom.test;

import com.chswapna183_dotcom.base.TestBase;
import com.chswapna183_dotcom.support.MockServer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public abstract class UITestBase extends TestBase {

    @BeforeClass(alwaysRun = true)
    public void startMockServerForUi() {
        MockServer.start();
        System.setProperty("baseUrl", MockServer.getBaseUrl());
    }

    @AfterClass(alwaysRun = true)
    public void stopMockServerForUi() {
        System.clearProperty("baseUrl");
        MockServer.stop();
    }
}

