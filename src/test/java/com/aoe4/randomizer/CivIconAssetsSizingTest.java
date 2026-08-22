package com.aoe4.randomizer;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CivIconAssetsSizingTest {

    @Test
    void civIconsAreSizedForRenderedDimensions() throws Exception {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:static/images/civs/*.png");

        for (Resource resource : resources) {
            BufferedImage image = ImageIO.read(resource.getInputStream());
            assertNotNull(image, "Expected to read civ icon image: " + resource.getFilename());

            String filename = resource.getFilename();
            if ("generic.png".equals(filename)) {
                assertTrue(image.getWidth() <= 64, "generic fallback should stay small");
                assertTrue(image.getHeight() <= 64, "generic fallback should stay small");
            } else {
                assertTrue(image.getWidth() <= 160, () -> filename + " should not exceed large display width");
                assertTrue(image.getHeight() <= 90, () -> filename + " should not exceed large display height");
            }
        }
    }
}
