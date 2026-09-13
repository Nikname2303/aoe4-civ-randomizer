package com.aoe4.randomizer;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

class StyleCssPerformanceHintsTest {

    @Test
    void styleCssUsesFixedIconSizingAndContainmentHints() throws IOException {
        String styleCss = readResource("/static/style.css");

        assertTrue(styleCss.contains(".civ-inline {") && styleCss.contains("contain: layout paint;"),
                "civ inline rendering should use containment hints");
        assertTrue(styleCss.contains(".civ-icon {") && styleCss.contains("width: 64px;")
                        && styleCss.contains("height: 36px;") && styleCss.contains("object-fit: contain;"),
                "small civ icons should use fixed rendered dimensions");
        assertTrue(styleCss.contains(".civ-icon-large {") && styleCss.contains("width: 160px;")
                        && styleCss.contains("height: 90px;") && styleCss.contains("object-fit: contain;"),
                "large civ icons should use fixed rendered dimensions");
        assertTrue(styleCss.contains(".civ-item {") && styleCss.contains(".civ-group {"),
                "civ groups and items should keep performance-related styling");
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
