package com.aoe4.randomizer.dto;

import com.aoe4.randomizer.model.Civilization;

/**
 * API response DTO for a civilization. Extends the persisted data with an
 * {@code iconDataUri} field: a base64-encoded data URI of the civ's icon PNG,
 * so clients (especially JavaFX WebView) can render icons without additional
 * HTTP requests.  The original {@code iconPath} field is preserved for
 * backward compatibility and browser fallback.
 */
public class CivResponse {

    private Long id;
    private String name;
    private String dlc;
    private String iconPath;
    private boolean enabled;
    private String iconDataUri;

    public CivResponse() {}

    public static CivResponse from(Civilization civ, String iconDataUri) {
        CivResponse r = new CivResponse();
        r.id = civ.getId();
        r.name = civ.getName();
        r.dlc = civ.getDlc();
        r.iconPath = civ.getIconPath();
        r.enabled = civ.isEnabled();
        r.iconDataUri = iconDataUri;
        return r;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDlc() { return dlc; }
    public String getIconPath() { return iconPath; }
    public boolean isEnabled() { return enabled; }
    public String getIconDataUri() { return iconDataUri; }
}
