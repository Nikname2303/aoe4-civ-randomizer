package com.aoe4.randomizer.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CivIconService {
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public String getDataUri(String iconPath) {
        if (iconPath == null || iconPath.isBlank()) {
            return null;
        }
        return cache.computeIfAbsent(iconPath, this::loadDataUri);
    }

    public String getGenericIconDataUri() {
        String dataUri = getDataUri("/images/civs/generic.png");
        return dataUri == null ? "" : dataUri;
    }

    private String loadDataUri(String iconPath) {
        String normalizedPath = iconPath.startsWith("/") ? iconPath : "/" + iconPath;
        String resourcePath = "/static" + normalizedPath;
        try (InputStream stream = CivIconService.class.getResourceAsStream(resourcePath)) {
            if (stream == null) {
                return null;
            }
            byte[] bytes = stream.readAllBytes();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not load civ icon: " + iconPath, ex);
        }
    }
}
