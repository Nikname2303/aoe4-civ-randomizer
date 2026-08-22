package com.aoe4.randomizer.controller;

import com.aoe4.randomizer.model.Civilization;
import com.aoe4.randomizer.service.RandomizerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/random")
public class RandomizerController {

    private final RandomizerService service;

    public RandomizerController(RandomizerService service) {
        this.service = service;
    }

    /** POST /api/random/single — picks one random civilization from the enabled pool */
    @PostMapping("/single")
    public Civilization randomSingle() {
        return service.randomSingle();
    }

    /** POST /api/random/lobby — assigns civs to a list of players */
    @PostMapping("/lobby")
    public Map<String, String> randomLobby(@RequestBody LobbyRequest request) {
        return service.randomLobby(request.playerNames(), request.allowDuplicates());
    }

    /** Request body for the lobby randomizer */
    public record LobbyRequest(List<String> playerNames, boolean allowDuplicates) {}
}
