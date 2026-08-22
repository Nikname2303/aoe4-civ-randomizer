package com.aoe4.randomizer.controller;

import com.aoe4.randomizer.dto.CivResponse;
import com.aoe4.randomizer.service.CivIconService;
import com.aoe4.randomizer.service.RandomizerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/random")
public class RandomizerController {

    private final RandomizerService service;
    private final CivIconService iconService;

    public RandomizerController(RandomizerService service, CivIconService iconService) {
        this.service = service;
        this.iconService = iconService;
    }

    /** POST /api/random/single — picks one random civilization from the enabled pool */
    @PostMapping("/single")
    public CivResponse randomSingle() {
        var civ = service.randomSingle();
        return CivResponse.from(civ, iconService.getDataUri(civ.getIconPath()));
    }

    /** POST /api/random/lobby — assigns civs to a list of players */
    @PostMapping("/lobby")
    public Map<String, String> randomLobby(@RequestBody LobbyRequest request) {
        return service.randomLobby(request.playerNames(), request.allowDuplicates());
    }

    /** Request body for the lobby randomizer */
    public record LobbyRequest(List<String> playerNames, boolean allowDuplicates) {}
}
