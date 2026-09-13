package com.aoe4.randomizer.model;

public class Civilization {

    private Long id;
    private String name;
    private String dlc;
    private String iconPath;
    private boolean enabled = true;

    public Civilization() {
    }

    public Civilization(String name, String dlc, String iconPath) {
        this.name = name;
        this.dlc = dlc;
        this.iconPath = iconPath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDlc() {
        return dlc;
    }

    public void setDlc(String dlc) {
        this.dlc = dlc;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
