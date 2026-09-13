package com.aoe4.randomizer;

import com.aoe4.randomizer.support.TestAppContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CivilizationApiTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @TempDir
    Path tempDir;

    @Test
    void getCivsIncludesIconPath() throws Exception {
        TestAppContext context = TestAppContext.create(tempDir);

        JsonNode civs = objectMapper.readTree(context.javaBridge().getCivs());

        assertTrue(civs.isArray());
        assertTrue(civs.get(0).hasNonNull("name"));
        assertTrue(civs.get(0).hasNonNull("iconPath"));
    }

    @Test
    void getCivsIncludesIconDataUri() throws Exception {
        TestAppContext context = TestAppContext.create(tempDir);

        JsonNode civs = objectMapper.readTree(context.javaBridge().getCivs());

        assertTrue(civs.get(0).get("iconDataUri").asText().startsWith("data:image/png;base64,"));
    }

    @Test
    void togglePersistsAcrossRepositoryReinitialization() throws Exception {
        TestAppContext firstContext = TestAppContext.create(tempDir);
        JsonNode civs = objectMapper.readTree(firstContext.javaBridge().getCivs());
        JsonNode firstCiv = civs.get(0);
        long civId = firstCiv.get("id").asLong();
        boolean originalEnabled = firstCiv.get("enabled").asBoolean();

        JsonNode toggled = objectMapper.readTree(firstContext.javaBridge().toggleCiv(civId));
        assertEquals(!originalEnabled, toggled.get("enabled").asBoolean());

        TestAppContext secondContext = TestAppContext.create(tempDir);
        JsonNode reloaded = objectMapper.readTree(secondContext.javaBridge().getCivs());
        JsonNode sameCiv = findById(reloaded, civId);
        assertEquals(toggled.get("enabled").asBoolean(), sameCiv.get("enabled").asBoolean());
    }

    private JsonNode findById(JsonNode civs, long id) {
        for (JsonNode civ : civs) {
            if (civ.get("id").asLong() == id) {
                return civ;
            }
        }
        throw new AssertionError("Could not find civ id " + id);
    }
}
