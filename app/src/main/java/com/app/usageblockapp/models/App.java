package com.app.usageblockapp.models;

public class App {
    private String name;
    private String packageName;
    private boolean activated;

    public App() {
    }

    public App(String name, String packageName, boolean activated) {
        this.name = name;
        this.packageName = packageName;
        this.activated = activated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }
}
