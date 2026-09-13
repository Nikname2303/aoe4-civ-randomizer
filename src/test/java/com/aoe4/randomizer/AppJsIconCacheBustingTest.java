package com.aoe4.randomizer;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppJsIconCacheBustingTest {

    @Test
    void appJsPrefersDataUrisAndGenericIconBridgeFallback() throws IOException {
        String appJs = readResource("/static/app.js");

        assertTrue(appJs.contains("bridgeCallJson('getGenericIcon')"));
        assertTrue(appJs.contains("if (civ.iconDataUri) {"));
        assertTrue(appJs.contains("img.src = genericIconDataUri;"));
        assertFalse(appJs.contains("fetch('/api/"),
                "desktop-only app.js should not call HTTP API endpoints anymore");
    }

    private String readResource(String path) throws IOException {
        try (InputStream stream = getClass().getResourceAsStream(path)) {
            if (stream == null) {
                throw new AssertionError("Missing resource: " + path);
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
