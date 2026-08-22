package com.aoe4.randomizer.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Eagerly loads every civ icon PNG from the classpath at startup and caches it as a
 * base64 data URI string keyed by the icon path used in API responses (e.g.
 * "/images/civs/english.png"). This sidesteps the JavaFX WebView networking bug that
 * causes sequential HTTP image requests to silently fail: by embedding icons as data URIs
 * in the JSON payload the browser makes zero additional HTTP requests for icons.
 *
 * <p>Tradeoff: base64 encoding increases per-icon payload size by ~33%. With ~23 small
 * static PNG files bundled in the jar this is negligible for a local-only loopback app.
 */
@Service
public class CivIconService {

    private static final Logger log = LoggerFactory.getLogger(CivIconService.class);

    /** Maps icon path (e.g. "/images/civs/english.png") → data URI. */
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    @PostConstruct
    void loadIcons() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:static/images/civs/*.png");
            for (Resource res : resources) {
                String filename = res.getFilename();
                if (filename == null) continue;
                String iconPath = "/images/civs/" + filename;
                try (InputStream is = res.getInputStream()) {
                    byte[] bytes = is.readAllBytes();
                    String dataUri = "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
                    cache.put(iconPath, dataUri);
                } catch (IOException e) {
                    log.warn("Could not load civ icon '{}': {}", iconPath, e.getMessage());
                }
            }
            log.info("CivIconService: cached {} civ icon(s) as base64 data URIs", cache.size());
        } catch (IOException e) {
            log.warn("CivIconService: could not scan civ icons directory: {}", e.getMessage());
        }
    }

    /**
     * Returns the base64 data URI for the given icon path, or {@code null} if not found.
     *
     * @param iconPath e.g. "/images/civs/english.png"
     */
    public String getDataUri(String iconPath) {
        if (iconPath == null) return null;
        return cache.get(iconPath);
    }
}
