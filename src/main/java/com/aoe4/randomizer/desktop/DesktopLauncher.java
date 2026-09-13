package com.aoe4.randomizer.desktop;

import com.aoe4.randomizer.bridge.JavaBridge;
import com.aoe4.randomizer.persistence.DatabaseManager;
import com.aoe4.randomizer.repository.CivilizationRepository;
import com.aoe4.randomizer.service.CivIconService;
import com.aoe4.randomizer.service.RandomizerService;
import com.sun.javafx.webkit.WebConsoleListener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.net.URL;

public class DesktopLauncher extends Application {
    private JavaBridge javaBridge;

    @Override
    public void init() {
        DatabaseManager databaseManager = DatabaseManager.createDefault();
        databaseManager.initialize();
        CivilizationRepository repository = new CivilizationRepository(databaseManager);
        RandomizerService randomizerService = new RandomizerService(repository);
        CivIconService civIconService = new CivIconService();
        javaBridge = new JavaBridge(randomizerService, civIconService);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("AoE4 Civ Randomizer");
        primaryStage.setWidth(1280);
        primaryStage.setHeight(820);
        primaryStage.setOnCloseRequest(event -> Platform.exit());

        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();
        configureDebugLogging(engine);

        URL indexUrl = DesktopLauncher.class.getResource("/static/index.html");
        if (indexUrl == null) {
            showStartupError("Missing UI resources", "Could not find /static/index.html in the application resources.");
            Platform.exit();
            return;
        }

        engine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) engine.executeScript("window");
                window.setMember("javaBridge", javaBridge);
                engine.executeScript("window.appInit && window.appInit();");
            } else if (newState == Worker.State.FAILED) {
                Throwable exception = engine.getLoadWorker().getException();
                showStartupError("Failed to load UI", exception == null ? "Unknown UI load error." : exception.getMessage());
                Platform.exit();
            }
        });

        engine.load(indexUrl.toExternalForm());
        primaryStage.setScene(new Scene(webView));
        primaryStage.show();
    }

    private void configureDebugLogging(WebEngine engine) {
        if (!Boolean.getBoolean("desktop.debug")) {
            return;
        }
        WebConsoleListener.setDefaultListener((view, message, lineNumber, sourceId) ->
                System.out.println("[webview-console] " + sourceId + ":" + lineNumber + " " + message));
        engine.setOnError(event -> System.out.println("[webview-error] " + event.getMessage()));
        engine.getLoadWorker().exceptionProperty().addListener((obs, oldEx, newEx) -> {
            if (newEx != null) {
                System.out.println("[webview-load-exception] " + newEx);
                newEx.printStackTrace();
            }
        });
        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) ->
                System.out.println("[webview-load-state] " + oldState + " -> " + newState));
    }

    private void showStartupError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message == null ? "Unknown startup error." : message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
