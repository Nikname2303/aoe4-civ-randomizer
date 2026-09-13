package com.aoe4.randomizer;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CivIconAssetsSizingTest {

    @Test
    void civIconsAreSizedForRenderedDimensions() throws Exception {
        try (Stream<Path> resources = Files.list(resolveIconsDirectory())) {
            resources.filter(path -> path.getFileName().toString().endsWith(".png"))
                    .forEach(this::assertImageDimensions);
        }
    }

    private Path resolveIconsDirectory() throws IOException, URISyntaxException {
        URL resource = getClass().getResource("/static/images/civs");
        if (resource == null) {
            throw new AssertionError("Missing civ icons directory");
        }

        URI uri = resource.toURI();
        if ("jar".equals(uri.getScheme())) {
            FileSystem fileSystem = FileSystems.newFileSystem(uri, Map.of());
            return fileSystem.getPath("/static/images/civs");
        }
        return Path.of(uri);
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
