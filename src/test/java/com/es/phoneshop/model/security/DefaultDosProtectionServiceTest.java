package com.es.phoneshop.model.security;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class DefaultDosProtectionServiceTest {
    private DefaultDosProtectionService dosProtectionService = new DefaultDosProtectionService();

    @Test
    public void testIsAllowedFirstRequest() {
        String ip = "192.168.1.1";
        boolean result = dosProtectionService.isAllowed(ip);
        assertTrue("The first request should be allowed.", result);
    }

    @Test
    public void testIsAllowedUnderThreshold() {
        String ip = "192.168.1.1";
        for (int i = 0; i < 19; i++) {
            dosProtectionService.isAllowed(ip);
        }
        boolean result = dosProtectionService.isAllowed(ip);
        assertTrue("The 20th request should be allowed.", result);
    }

    @Test
    public void testIsNotAllowedOverThreshold() {
        String ip = "192.168.1.1";
        for (int i = 0; i < 20; i++) {
            dosProtectionService.isAllowed(ip);
        }
        boolean result = dosProtectionService.isAllowed(ip);
        assertFalse("The request should be blocked after reaching the threshold.", result);
    }

    @Test
    public void testIsAllowedAfterTimeFrame() throws InterruptedException {
        String ip = "192.168.1.1";
        for (int i = 0; i < 20; i++) {
            dosProtectionService.isAllowed(ip);
        }
        Thread.sleep(61 * 1000);
        boolean result = dosProtectionService.isAllowed(ip);
        assertTrue("A request should be allowed after the time frame has passed.", result);
    }
}
