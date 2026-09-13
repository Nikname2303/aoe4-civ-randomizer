package com.aoe4.randomizer.dto;

import java.util.List;

public record LobbyRequest(List<String> playerNames, boolean allowDuplicates) {
}
