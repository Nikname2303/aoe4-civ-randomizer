package com.aoe4.randomizer.service;

import com.aoe4.randomizer.model.Civilization;
import com.aoe4.randomizer.repository.CivilizationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class RandomizerService {

    private final CivilizationRepository civRepo;

    public RandomizerService(CivilizationRepository civRepo) {
        this.civRepo = civRepo;
    }

    /** Returns all civs sorted by DLC then name. */
    public List<Civilization> getAllCivs() {
        return civRepo.findAllByOrderByDlcAscNameAsc();
    }

    /** Flips the enabled state of a civilization and saves it. */
    public Civilization toggle(Long id) {
        Civilization civ = civRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Civilization not found: " + id));
        civ.setEnabled(!civ.isEnabled());
        return civRepo.save(civ);
    }

    /** Picks one random civ from the enabled pool. */
    public Civilization randomSingle() {
        List<Civilization> enabled = civRepo.findByEnabledTrue();
        if (enabled.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No civilizations are enabled. Please enable at least one civilization first.");
        }
        return enabled.get(new Random().nextInt(enabled.size()));
    }

    /**
     * Assigns a civilization to each player.
     * If allowDuplicates=true: each player gets an independent random pick (with replacement).
     * If allowDuplicates=false: players are assigned without replacement (shuffle).
     */
    public Map<String, String> randomLobby(List<String> playerNames, boolean allowDuplicates) {
        List<Civilization> enabled = civRepo.findByEnabledTrue();
        if (enabled.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No civilizations are enabled. Please enable at least one civilization first.");
        }

        if (!allowDuplicates && playerNames.size() > enabled.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Not enough enabled civilizations for unique assignment. " +
                    "You have " + playerNames.size() + " players but only " + enabled.size() +
                    " enabled civilizations. Please enable more civilizations or allow duplicate civilizations.");
        }

        Map<String, String> result = new LinkedHashMap<>();
        Random random = new Random();

        if (allowDuplicates) {
            for (String player : playerNames) {
                Civilization picked = enabled.get(random.nextInt(enabled.size()));
                result.put(player, picked.getName());
            }
        } else {
            // Shuffle a copy of the enabled list and assign in order
            List<Civilization> pool = new ArrayList<>(enabled);
            Collections.shuffle(pool, random);
            for (int i = 0; i < playerNames.size(); i++) {
                result.put(playerNames.get(i), pool.get(i).getName());
            }
        }

        return result;
    }
}
