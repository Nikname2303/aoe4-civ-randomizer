package com.aoe4.randomizer.bridge;

import com.aoe4.randomizer.dto.CivResponse;
import com.aoe4.randomizer.dto.LobbyRequest;
import com.aoe4.randomizer.model.Civilization;
import com.aoe4.randomizer.service.CivIconService;
import com.aoe4.randomizer.service.RandomizerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class JavaBridge {
    private final RandomizerService randomizerService;
    private final CivIconService civIconService;
    private final ObjectMapper objectMapper;

    public JavaBridge(RandomizerService randomizerService, CivIconService civIconService) {
        this(randomizerService, civIconService, new ObjectMapper());
    }

    JavaBridge(RandomizerService randomizerService, CivIconService civIconService, ObjectMapper objectMapper) {
        this.randomizerService = randomizerService;
        this.civIconService = civIconService;
        this.objectMapper = objectMapper;
    }

    public String getCivs() {
        return execute(() -> writeJson(randomizerService.getAllCivs().stream().map(this::toResponse).toList()));
    }

    public String randomSingle() {
        return execute(() -> writeJson(toResponse(randomizerService.randomSingle())));
    }

    public String randomLobby(String optionsJson) {
        return execute(() -> {
            LobbyRequest request = objectMapper.readValue(optionsJson, LobbyRequest.class);
            return writeJson(randomizerService.randomLobby(request.playerNames(), request.allowDuplicates()));
        });
    }

    public String toggleCiv(long id) {
        return execute(() -> writeJson(toResponse(randomizerService.toggle(id))));
    }

    public String setDlcEnabled(String dlcName, boolean enabled) {
        return execute(() -> writeJson(randomizerService.setDlcEnabled(dlcName, enabled).stream().map(this::toResponse).toList()));
    }

    public String getGenericIcon() {
        return execute(() -> writeJson(Map.of("iconDataUri", civIconService.getGenericIconDataUri())));
    }

    private CivResponse toResponse(Civilization civilization) {
        return CivResponse.from(civilization, civIconService.getDataUri(civilization.getIconPath()));
    }

    private String execute(ThrowingSupplier<String> supplier) {
        try {
            return supplier.get();
        } catch (Exception ex) {
            return writeJson(Map.of("error", true, "message", messageFor(ex)));
        }
    }

    private String writeJson(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Could not serialize bridge response", ex);
        }
    }

    private String messageFor(Exception ex) {
        String message = ex.getMessage();
        return message == null || message.isBlank() ? ex.getClass().getSimpleName() : message;
    }

    @FunctionalInterface
    private interface ThrowingSupplier<T> {
        T get() throws Exception;
    }
}
