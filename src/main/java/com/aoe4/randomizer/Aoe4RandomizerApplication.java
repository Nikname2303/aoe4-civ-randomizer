package com.aoe4.randomizer;

import com.aoe4.randomizer.desktop.DesktopLauncher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Aoe4RandomizerApplication {
    public static void main(String[] args) {
        if (isDesktopLaunch(args)) {
            DesktopLauncher.main(args);
            return;
        }
        SpringApplication.run(Aoe4RandomizerApplication.class, args);
    }

    static boolean isDesktopLaunch(String[] args) {
        if (args == null) {
            return false;
        }
        for (String arg : args) {
            if ("--desktop".equals(arg)) {
                return true;
            }
        }
        return false;
    }
}
