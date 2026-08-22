package com.aoe4.randomizer.desktop;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DesktopLauncherTest {

    private static final String SOURCE_PATH =
            "src/main/java/com/aoe4/randomizer/desktop/DesktopLauncher.java";

    @Test
    void mainMethodCallsDisableWebViewHttp2LoaderBeforeLaunch() throws IOException {
        Path source = Paths.get(SOURCE_PATH);
        String content = Files.readString(source);

        int disableIndex = content.indexOf(
                "DesktopLauncherSupport.disableWebViewHttp2Loader()");
        int launchIndex = content.indexOf("launch(args)");

        assertTrue(disableIndex >= 0,
                "DesktopLauncherSupport.disableWebViewHttp2Loader() must be present in DesktopLauncher.java");
        assertTrue(launchIndex >= 0,
                "launch(args) must be present in DesktopLauncher.java");
        assertTrue(disableIndex < launchIndex,
                "disableWebViewHttp2Loader() must appear before launch(args) in the source file");
    }

    @Test
    void startMethodDoesNotCallDisableWebViewHttp2Loader() throws IOException {
        Path source = Paths.get(SOURCE_PATH);
        String content = Files.readString(source);

        // Find the start() method body and ensure disableWebViewHttp2Loader is not called there
        int startMethodIndex = content.indexOf("public void start(Stage primaryStage)");
        assertTrue(startMethodIndex >= 0, "start(Stage) method must exist");

        // Find the main() method to use as boundary
        int mainMethodIndex = content.indexOf("public static void main(String[] args)");
        assertTrue(mainMethodIndex >= 0, "main(String[]) method must exist");

        // The start() method body is between startMethodIndex and mainMethodIndex
        String startMethodBody = content.substring(startMethodIndex, mainMethodIndex);
        assertTrue(!startMethodBody.contains("disableWebViewHttp2Loader"),
                "start(Stage) should no longer call disableWebViewHttp2Loader(); " +
                "that call must be in main() before launch(args)");
    }
}
