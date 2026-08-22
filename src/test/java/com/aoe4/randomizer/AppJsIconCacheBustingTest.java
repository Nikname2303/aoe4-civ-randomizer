package com.aoe4.randomizer;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AppJsIconCacheBustingTest {

    @Test
    void appJsAddsStableCacheTokenToPrimaryAndFallbackIconUrls() throws IOException {
        String appJs = new ClassPathResource("static/app.js")
                .getContentAsString(StandardCharsets.UTF_8);

        assertTrue(appJs.contains("const CIV_ICON_CACHE_TOKEN = 'desktop-icon-cache-1';"));
        assertTrue(appJs.contains("img.src = withIconCacheToken(iconPath || GENERIC_CIV_ICON_PATH);"));
        assertTrue(appJs.contains("img.src = withIconCacheToken(GENERIC_CIV_ICON_PATH);"));
    }
}
