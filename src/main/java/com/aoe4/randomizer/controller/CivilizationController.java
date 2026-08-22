package com.aoe4.randomizer.controller;

import com.aoe4.randomizer.model.Civilization;
import com.aoe4.randomizer.service.RandomizerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/civs")
public class CivilizationController {

    private final RandomizerService service;

    public CivilizationController(RandomizerService service) {
        this.service = service;
    }

    /** GET /api/civs — returns all civilizations sorted by DLC then name */
    @GetMapping
    public List<Civilization> getAllCivs() {
        return service.getAllCivs();
    }

    /** POST /api/civs/{id}/toggle — flips the enabled state of one civilization */
    @PostMapping("/{id}/toggle")
    public Civilization toggle(@PathVariable Long id) {
        return service.toggle(id);
    }
}
