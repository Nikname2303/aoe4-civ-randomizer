package com.aoe4.randomizer.service;

import com.aoe4.randomizer.model.Civilization;
import com.aoe4.randomizer.repository.CivilizationRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;

public class RandomizerService {

    private final CivilizationRepository civRepo;

    public RandomizerService(CivilizationRepository civRepo) {
        this.civRepo = civRepo;
    }

    public List<Civilization> getAllCivs() {
        return civRepo.findAllByOrderByDlcAscNameAsc();
    }

    public List<Civilization> setDlcEnabled(String dlcName, boolean enabled) {
        List<Civilization> civs = civRepo.findByDlc(dlcName);
        if (civs.isEmpty()) {
            throw new NoSuchElementException("No civilizations found for DLC: " + dlcName);
        }
        civs.forEach(civ -> civ.setEnabled(enabled));
        return civRepo.saveAll(civs);
    }

    public Civilization toggle(long id) {
        Civilization civ = civRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Civilization not found: " + id));
        civ.setEnabled(!civ.isEnabled());
        return civRepo.save(civ);
    }

    public Civilization randomSingle() {
        List<Civilization> enabled = civRepo.findByEnabledTrue();
        if (enabled.isEmpty()) {
            throw new IllegalStateException("No civilizations are enabled. Please enable at least one civilization first.");
        }
        return enabled.get(ThreadLocalRandom.current().nextInt(enabled.size()));
    }

    public Map<String, String> randomLobby(List<String> playerNames, boolean allowDuplicates) {
        List<Civilization> enabled = civRepo.findByEnabledTrue();
        if (enabled.isEmpty()) {
            throw new IllegalStateException("No civilizations are enabled. Please enable at least one civilization first.");
        }

        if (!allowDuplicates && playerNames.size() > enabled.size()) {
            throw new IllegalStateException(
                    "Not enough enabled civilizations for unique assignment. You have "
                            + playerNames.size() + " players but only " + enabled.size()
                            + " enabled civilizations. Please enable more civilizations or allow duplicate civilizations.");
        }

        Map<String, String> result = new LinkedHashMap<>();

        if (allowDuplicates) {
            for (String player : playerNames) {
                Civilization picked = enabled.get(ThreadLocalRandom.current().nextInt(enabled.size()));
                result.put(player, picked.getName());
            }
            return result;
        }

        List<Civilization> pool = new ArrayList<>(enabled);
        Collections.shuffle(pool);
        for (int index = 0; index < playerNames.size(); index++) {
            result.put(playerNames.get(index), pool.get(index).getName());
        }
        return result;
    }
}
