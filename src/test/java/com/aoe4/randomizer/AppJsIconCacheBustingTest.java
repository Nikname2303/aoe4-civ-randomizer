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
        // Fallback path still uses the cache-busting token
        assertTrue(appJs.contains("img.src = withIconCacheToken(GENERIC_CIV_ICON_PATH);"));
        // Data URI is preferred when available; HTTP fallback uses the queue with cache token
        assertTrue(appJs.contains("queueImageLoad(img, withIconCacheToken(civ.iconPath || GENERIC_CIV_ICON_PATH));"));
    }
}
