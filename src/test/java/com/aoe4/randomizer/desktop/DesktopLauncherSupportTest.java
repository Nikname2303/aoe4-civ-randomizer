package com.aoe4.randomizer.desktop;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DesktopLauncherSupportTest {
    private static final String PORT_PROPERTY = "desktop.port";

    @AfterEach
    void clearDesktopPortProperty() {
        System.clearProperty(PORT_PROPERTY);
    }

    @Test
    void disableWebViewHttp2LoaderSetsFalseSystemProperty() {
        String previous = System.getProperty("com.sun.webkit.useHTTP2Loader");
        try {
            System.clearProperty("com.sun.webkit.useHTTP2Loader");
            DesktopLauncherSupport.disableWebViewHttp2Loader();
            assertEquals("false", System.getProperty("com.sun.webkit.useHTTP2Loader"));
        } finally {
            if (previous == null) {
                System.clearProperty("com.sun.webkit.useHTTP2Loader");
            } else {
                System.setProperty("com.sun.webkit.useHTTP2Loader", previous);
            }
        }
    }

    @Test
    void usesDefaultPortWhenNoConfigurationProvided() {
        assertEquals(DesktopLauncherSupport.DEFAULT_DESKTOP_PORT, DesktopLauncherSupport.resolveDesktopPort());
    }

    @Test
    void usesSystemPropertyWhenConfigured() {
        System.setProperty(PORT_PROPERTY, "19090");
        assertEquals(19090, DesktopLauncherSupport.resolveDesktopPort());
    }

    @Test
    void rejectsInvalidPortValues() {
        System.setProperty(PORT_PROPERTY, "abc");
        assertThrows(IllegalArgumentException.class, DesktopLauncherSupport::resolveDesktopPort);

        System.setProperty(PORT_PROPERTY, "70000");
        assertThrows(IllegalArgumentException.class, DesktopLauncherSupport::resolveDesktopPort);
    }

    @Test
    void detectsServerReadinessWhenEndpointIsAvailable() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            byte[] response = "ok".getBytes();
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream body = exchange.getResponseBody()) {
                body.write(response);
            }
        });
        server.start();
        try {
            String url = "http://127.0.0.1:" + server.getAddress().getPort();
            assertTrue(DesktopLauncherSupport.waitForServerReady(url, 5, 20));
        } finally {
            server.stop(0);
        }
    }

    @Test
    void returnsFalseWhenEndpointNeverBecomesAvailable() {
        int freePort = findFreePort();
        String url = "http://127.0.0.1:" + freePort;
        assertFalse(DesktopLauncherSupport.waitForServerReady(url, 2, 20));
    }

    private int findFreePort() {
        try (java.net.ServerSocket socket = new java.net.ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException ex) {
            throw new IllegalStateException("Could not allocate test port", ex);
        }
    }
}
