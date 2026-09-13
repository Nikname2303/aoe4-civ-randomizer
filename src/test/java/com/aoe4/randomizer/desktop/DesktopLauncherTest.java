package com.aoe4.randomizer.desktop;

import com.aoe4.randomizer.bridge.JavaBridge;
import com.aoe4.randomizer.support.TestAppContext;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DesktopLauncherTest {
    @TempDir
    Path tempDir;

    @Test
    void initializeWindowInjectsBridgeAndTriggersAppInit() throws Exception {
        TestAppContext context = TestAppContext.create(tempDir);
        DesktopLauncher launcher = new DesktopLauncher();
        injectBridge(launcher, context.javaBridge());
        RecordingJsObject window = new RecordingJsObject();
        AtomicReference<String> executedScript = new AtomicReference<>();

        launcher.initializeWindow(window, executedScript::set, "jar:file:/tmp/app!/static/");

        assertSame(context.javaBridge(), window.members.get("javaBridge"));
        assertEquals("jar:file:/tmp/app!/static/", window.members.get("appBaseUrl"));
        assertEquals("window.appInit && window.appInit();", executedScript.get());
    }

    @Test
    void handleLoadFailureReportsMessageAndShutsDown() {
        DesktopLauncher launcher = new DesktopLauncher();
        AtomicReference<String> reportedMessage = new AtomicReference<>();
        AtomicBoolean shutdownTriggered = new AtomicBoolean(false);

        launcher.handleLoadFailure(reportedMessage::set, () -> shutdownTriggered.set(true), new IllegalStateException("boom"));

        assertEquals("boom", reportedMessage.get());
        assertTrue(shutdownTriggered.get());
    }

    @Test
    void loadFailureMessageFallsBackWhenExceptionMissing() {
        DesktopLauncher launcher = new DesktopLauncher();

        assertEquals("Unknown UI load error.", launcher.loadFailureMessage(null));
        assertEquals("boom", launcher.loadFailureMessage(new IllegalStateException("boom")));
    }

    @Test
    void resolveResourceBaseUrlReturnsStaticDirectoryUrl() throws Exception {
        DesktopLauncher launcher = new DesktopLauncher();
        String baseUrl = launcher.resolveResourceBaseUrl(DesktopLauncher.class.getResource("/static/index.html"));

        assertTrue(baseUrl.endsWith("/static/"));
    }

    private void injectBridge(DesktopLauncher launcher, JavaBridge javaBridge) throws Exception {
        var field = DesktopLauncher.class.getDeclaredField("javaBridge");
        field.setAccessible(true);
        field.set(launcher, javaBridge);
    }

    private static final class RecordingJsObject extends JSObject {
        private final Map<String, Object> members = new HashMap<>();

        @Override
        public Object call(String methodName, Object... args) throws JSException {
            return null;
        }

        @Override
        public Object eval(String s) throws JSException {
            return null;
        }

        @Override
        public Object getMember(String name) throws JSException {
            return members.get(name);
        }

        @Override
        public void setMember(String name, Object value) throws JSException {
            members.put(name, value);
        }

        @Override
        public void removeMember(String name) throws JSException {
            members.remove(name);
        }

        @Override
        public Object getSlot(int index) throws JSException {
            return null;
        }

        @Override
        public void setSlot(int index, Object value) throws JSException {
        }
    }
}
