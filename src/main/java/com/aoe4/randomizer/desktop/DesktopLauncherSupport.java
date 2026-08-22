package com.aoe4.randomizer.desktop;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URL;

final class DesktopLauncherSupport {
    static final int DEFAULT_DESKTOP_PORT = 18080;
    private static final String DESKTOP_PORT_PROPERTY = "desktop.port";
    private static final String DESKTOP_PORT_ENV = "DESKTOP_PORT";

    private static final String WEBKIT_HTTP2_LOADER_PROPERTY = "com.sun.webkit.useHTTP2Loader";

    private DesktopLauncherSupport() {}

    static void disableWebViewHttp2Loader() {
        System.setProperty(WEBKIT_HTTP2_LOADER_PROPERTY, "false");
    }

    static int resolveDesktopPort() {
        String configuredPort = System.getProperty(DESKTOP_PORT_PROPERTY);
        if (configuredPort == null || configuredPort.isBlank()) {
            configuredPort = System.getenv(DESKTOP_PORT_ENV);
        }
        if (configuredPort == null || configuredPort.isBlank()) {
            return DEFAULT_DESKTOP_PORT;
        }
        try {
            int parsed = Integer.parseInt(configuredPort.trim());
            if (parsed < 1 || parsed > 65535) {
                throw new IllegalArgumentException("desktop port must be between 1 and 65535");
            }
            return parsed;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("desktop port must be a number", ex);
        }
    }

    static boolean isPortAvailable(int port) {
        try (ServerSocket socket = new ServerSocket()) {
            socket.setReuseAddress(false);
            socket.bind(new InetSocketAddress("127.0.0.1", port));
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    static String localAppUrl(int port) {
        return "http://127.0.0.1:" + port;
    }

    static boolean waitForServerReady(String appUrl, int maxAttempts, long waitMillis) {
        String readinessUrl = appUrl + "/";
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(readinessUrl).openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(1000);
                connection.setReadTimeout(1000);
                int responseCode = connection.getResponseCode();
                if (responseCode >= 200 && responseCode < 400) {
                    return true;
                }
            } catch (IOException ignored) {
            }

            try {
                Thread.sleep(waitMillis);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }
}
