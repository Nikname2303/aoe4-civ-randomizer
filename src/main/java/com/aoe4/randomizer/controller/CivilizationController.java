package com.aoe4.randomizer.controller;

import com.aoe4.randomizer.dto.CivResponse;
import com.aoe4.randomizer.service.CivIconService;
import com.aoe4.randomizer.service.RandomizerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/civs")
public class CivilizationController {

    private final RandomizerService service;
    private final CivIconService iconService;

    public CivilizationController(RandomizerService service, CivIconService iconService) {
        this.service = service;
        this.iconService = iconService;
    }

    /** GET /api/civs — returns all civilizations sorted by DLC then name */
    @GetMapping
    public List<CivResponse> getAllCivs() {
        return service.getAllCivs().stream()
                .map(c -> CivResponse.from(c, iconService.getDataUri(c.getIconPath())))
                .toList();
    }

    /** POST /api/civs/{id}/toggle — flips the enabled state of one civilization */
    @PostMapping("/{id}/toggle")
    public CivResponse toggle(@PathVariable Long id) {
        var civ = service.toggle(id);
        return CivResponse.from(civ, iconService.getDataUri(civ.getIconPath()));
    }

    /**
     * POST /api/civs/dlc/set?dlcName=...&enabled=true|false
     * Sets every civilization in the named DLC group to the given enabled state.
     * Returns the updated list of civs in that group.
     */
    @PostMapping("/dlc/set")
    public List<CivResponse> setDlcEnabled(
            @RequestParam String dlcName,
            @RequestParam boolean enabled) {
        return service.setDlcEnabled(dlcName, enabled).stream()
                .map(c -> CivResponse.from(c, iconService.getDataUri(c.getIconPath())))
                .toList();
    }

    /**
     * GET /api/civs/generic-icon — returns the base64 data URI for the generic fallback icon.
     * Useful for pre-loading the fallback icon in the frontend without an extra HTTP request.
     */
    @GetMapping("/generic-icon")
    public Map<String, String> genericIcon() {
        String dataUri = iconService.getDataUri("/images/civs/generic.png");
        return Map.of("iconDataUri", dataUri != null ? dataUri : "");
    }
}
