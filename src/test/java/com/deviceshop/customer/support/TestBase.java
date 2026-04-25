package com.deviceshop.customer.support;

import org.junit.jupiter.api.BeforeAll;
import org.platformlambda.core.system.AutoStart;
import org.platformlambda.core.util.AppConfigReader;

import java.util.concurrent.atomic.AtomicInteger;

public class TestBase {
    private static final AtomicInteger startCounter = new AtomicInteger(0);
    protected static String HOST;

    @BeforeAll
    static void setup() {
        if (startCounter.incrementAndGet() == 1) {
            AppConfigReader config = AppConfigReader.getInstance();
            HOST = "http://127.0.0.1:" + config.getProperty("server.port", "8101");
            AutoStart.main(new String[0]);
        }
    }
}
