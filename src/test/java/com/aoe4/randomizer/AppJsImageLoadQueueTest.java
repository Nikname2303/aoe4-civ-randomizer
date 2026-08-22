package com.aoe4.randomizer;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AppJsImageLoadQueueTest {

    @Test
    void appJsContainsConcurrencyLimitedImageLoadingQueue() throws IOException {
        String appJs = new ClassPathResource("static/app.js")
                .getContentAsString(StandardCharsets.UTF_8);

        assertTrue(appJs.contains("MAX_CONCURRENT_IMAGE_LOADS"),
                "app.js should define MAX_CONCURRENT_IMAGE_LOADS");
        assertTrue(appJs.contains("function queueImageLoad("),
                "app.js should define queueImageLoad function");
        assertTrue(appJs.contains("function processQueue("),
                "app.js should define processQueue function");
        assertTrue(appJs.contains("queueImageLoad(img, withIconCacheToken(iconPath || GENERIC_CIV_ICON_PATH));"),
                "createCivInline should use queueImageLoad instead of direct img.src assignment");
    }
}
