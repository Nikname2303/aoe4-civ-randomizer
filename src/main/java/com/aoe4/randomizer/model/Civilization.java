package com.aoe4.randomizer.model;

import jakarta.persistence.*;

@Entity
public class Civilization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String dlc;

    @Column(name = "icon_path")
    private String iconPath;

    @Column(nullable = false)
    private boolean enabled = true;

    public Civilization() {}

    public Civilization(String name, String dlc, String iconPath) {
        this.name = name;
        this.dlc = dlc;
        this.iconPath = iconPath;
        this.enabled = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDlc() { return dlc; }
    public void setDlc(String dlc) { this.dlc = dlc; }

    public String getIconPath() { return iconPath; }
    public void setIconPath(String iconPath) { this.iconPath = iconPath; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
