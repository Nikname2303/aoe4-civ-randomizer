package com.aoe4.randomizer.desktop;

import com.aoe4.randomizer.Aoe4RandomizerApplication;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DesktopLauncher extends Application {
    private final ExecutorService bootstrapExecutor = Executors.newSingleThreadExecutor();
    private ConfigurableApplicationContext springContext;
    private volatile boolean shuttingDown;

    @Override
    public void start(Stage primaryStage) {
        final int desktopPort;
        try {
            desktopPort = DesktopLauncherSupport.resolveDesktopPort();
        } catch (IllegalArgumentException ex) {
            showStartupError("Invalid desktop port configuration", ex.getMessage());
            shutdownAndExit();
            return;
        }

        if (!DesktopLauncherSupport.isPortAvailable(desktopPort)) {
            showStartupError(
                    "Desktop port is already in use",
                    "Port " + desktopPort + " is busy. Close the conflicting app or run with -Ddesktop.port=<free-port>."
            );
            shutdownAndExit();
            return;
        }

        primaryStage.setTitle("AoE4 Civ Randomizer");
        primaryStage.setWidth(1280);
        primaryStage.setHeight(820);
        primaryStage.setOnCloseRequest(event -> shutdownAndExit());

        CompletableFuture.supplyAsync(() -> startServerAndWait(desktopPort), bootstrapExecutor)
                .thenAccept(appUrl -> Platform.runLater(() -> showMainWindow(primaryStage, appUrl)))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        Throwable rootCause = rootCause(ex);
                        showStartupError("Failed to start AoE4 Civ Randomizer", rootCause.getMessage());
                        shutdownAndExit();
                    });
                    return null;
                });
    }

    private String startServerAndWait(int desktopPort) {
        springContext = SpringApplication.run(
                Aoe4RandomizerApplication.class,
                "--server.port=" + desktopPort
        );
        if (shuttingDown) {
            springContext.close();
            springContext = null;
            throw new IllegalStateException("Desktop launcher was closed during startup.");
        }
        String appUrl = DesktopLauncherSupport.localAppUrl(desktopPort);
        boolean ready = DesktopLauncherSupport.waitForServerReady(appUrl, 60, 300);
        if (!ready) {
            springContext.close();
            springContext = null;
            throw new IllegalStateException("Local server did not become ready in time.");
        }
        return appUrl;
    }

    private void showMainWindow(Stage stage, String appUrl) {
        WebView webView = new WebView();
        webView.getEngine().load(appUrl);
        stage.setScene(new Scene(webView));
        stage.show();
    }

    private void showStartupError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message == null ? "Unknown startup error." : message);
        alert.showAndWait();
    }

    private Throwable rootCause(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current;
    }

    private void shutdownAndExit() {
        shuttingDown = true;
        if (springContext != null) {
            springContext.close();
            springContext = null;
        }
        bootstrapExecutor.shutdownNow();
        Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
