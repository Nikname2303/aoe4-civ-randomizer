package com.aoe4.randomizer.support;

import com.aoe4.randomizer.bridge.JavaBridge;
import com.aoe4.randomizer.persistence.DatabaseManager;
import com.aoe4.randomizer.repository.CivilizationRepository;
import com.aoe4.randomizer.service.CivIconService;
import com.aoe4.randomizer.service.RandomizerService;

import java.nio.file.Path;

public record TestAppContext(DatabaseManager databaseManager,
                             CivilizationRepository repository,
                             RandomizerService randomizerService,
                             CivIconService civIconService,
                             JavaBridge javaBridge) {
    public static TestAppContext create(Path dataDirectory) {
        DatabaseManager databaseManager = new DatabaseManager(dataDirectory);
        databaseManager.initialize();
        CivilizationRepository repository = new CivilizationRepository(databaseManager);
        RandomizerService randomizerService = new RandomizerService(repository);
        CivIconService civIconService = new CivIconService();
        JavaBridge javaBridge = new JavaBridge(randomizerService, civIconService);
        return new TestAppContext(databaseManager, repository, randomizerService, civIconService, javaBridge);
    }
}
