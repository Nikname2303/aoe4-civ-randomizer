package com.aoe4.randomizer;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CivIconAssetsSizingTest {

    @Test
    void civIconsAreSizedForRenderedDimensions() throws Exception {
        try (Stream<Path> resources = Files.list(Path.of("src/main/resources/static/images/civs"))) {
            resources.filter(path -> path.getFileName().toString().endsWith(".png"))
                    .forEach(this::assertImageDimensions);
        }
    }

    private void assertImageDimensions(Path path) {
        try {
            BufferedImage image = ImageIO.read(path.toFile());
            assertNotNull(image, "Expected to read civ icon image: " + path.getFileName());

            String filename = path.getFileName().toString();
            if ("generic.png".equals(filename)) {
                assertTrue(image.getWidth() <= 64, "generic fallback should stay small");
                assertTrue(image.getHeight() <= 64, "generic fallback should stay small");
            } else {
                assertTrue(image.getWidth() <= 160, () -> filename + " should not exceed large display width");
                assertTrue(image.getHeight() <= 90, () -> filename + " should not exceed large display height");
            }
        } catch (IOException ex) {
            throw new AssertionError("Could not read civ icon image: " + path, ex);
        }
    }
}
