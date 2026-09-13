package com.aoe4.randomizer;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AppJsImageLoadQueueTest {

    @Test
    void appJsUsesDesktopBridgeAndResourceFallbacks() throws IOException {
        String appJs = readResource("/static/app.js");

        assertTrue(appJs.contains("window.appInit = function appInit()"),
                "app.js should expose an explicit init function for JavaFX bridge startup");
        assertTrue(appJs.contains("bridgeCallJson('getCivs')"),
                "app.js should load civilizations through the Java bridge");
        assertTrue(appJs.contains("bridgeCallJson('randomSingle')"),
                "solo roll should use the Java bridge");
        assertTrue(appJs.contains("bridgeCallJson('randomLobby'"),
                "lobby randomization should use the Java bridge");
        assertTrue(appJs.contains("bridgeCallJson('setDlcEnabled'"),
                "DLC toggles should use the Java bridge");
        assertTrue(appJs.contains("img.src = normalizeIconPath(civ.iconPath || GENERIC_CIV_ICON_PATH);"),
                "icon fallback should resolve bundled resource paths without HTTP");
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
