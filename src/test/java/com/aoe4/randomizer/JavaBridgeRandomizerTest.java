package com.aoe4.randomizer;

import com.aoe4.randomizer.support.TestAppContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JavaBridgeRandomizerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @TempDir
    Path tempDir;

    @Test
    void randomSingleReturnsEnabledCivilizationWithIconData() throws Exception {
        TestAppContext context = TestAppContext.create(tempDir);

        JsonNode civ = objectMapper.readTree(context.javaBridge().randomSingle());

        assertTrue(civ.get("enabled").asBoolean());
        assertTrue(civ.get("iconDataUri").asText().startsWith("data:image/png;base64,"));
    }

    @Test
    void randomLobbyWithoutDuplicatesReturnsUniqueAssignments() throws Exception {
        TestAppContext context = TestAppContext.create(tempDir);

        JsonNode assignments = objectMapper.readTree(
                context.javaBridge().randomLobby("""
                        {"playerNames":["Alice","Bob","Charlie"],"allowDuplicates":false}
                        """));

        Set<String> assignedCivs = new HashSet<>();
        assignments.fields().forEachRemaining(entry -> assignedCivs.add(entry.getValue().asText()));
        assertEquals(3, assignedCivs.size());
    }

    @Test
    void randomLobbyReturnsBridgeErrorPayloadWhenUniqueAssignmentsImpossible() throws Exception {
        TestAppContext context = TestAppContext.create(tempDir);
        JsonNode civs = objectMapper.readTree(context.javaBridge().getCivs());
        for (JsonNode civ : civs) {
            if (!"English".equals(civ.get("name").asText())) {
                context.javaBridge().toggleCiv(civ.get("id").asLong());
            }
        }

        JsonNode response = objectMapper.readTree(
                context.javaBridge().randomLobby("""
                        {"playerNames":["Alice","Bob"],"allowDuplicates":false}
                        """));

        assertTrue(response.get("error").asBoolean());
        assertTrue(response.get("message").asText().contains("Not enough enabled civilizations"));
    }

    @Test
    void setDlcEnabledDisablesWholeGroup() throws Exception {
        TestAppContext context = TestAppContext.create(tempDir);

        JsonNode updated = objectMapper.readTree(context.javaBridge().setDlcEnabled("Base Game", false));

        assertFalse(updated.isEmpty());
        for (JsonNode civ : updated) {
            assertEquals("Base Game", civ.get("dlc").asText());
            assertFalse(civ.get("enabled").asBoolean());
        }
    }
}
