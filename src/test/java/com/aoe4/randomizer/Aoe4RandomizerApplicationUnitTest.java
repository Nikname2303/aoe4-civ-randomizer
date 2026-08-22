package com.aoe4.randomizer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Aoe4RandomizerApplicationUnitTest {

    @Test
    void detectsDesktopFlag() {
        assertTrue(Aoe4RandomizerApplication.isDesktopLaunch(new String[]{"--desktop"}));
        assertTrue(Aoe4RandomizerApplication.isDesktopLaunch(new String[]{"--server.port=8080", "--desktop"}));
    }

    @Test
    void ignoresDesktopModeWhenFlagMissing() {
        assertFalse(Aoe4RandomizerApplication.isDesktopLaunch(null));
        assertFalse(Aoe4RandomizerApplication.isDesktopLaunch(new String[0]));
        assertFalse(Aoe4RandomizerApplication.isDesktopLaunch(new String[]{"--server.port=8080"}));
    }
}
