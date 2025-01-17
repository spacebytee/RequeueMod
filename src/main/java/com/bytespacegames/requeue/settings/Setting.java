package com.bytespacegames.requeue.settings;

public abstract class Setting {
    private String name;
    private String description;
    public Setting(String name, String description) {
        this.name = name;
        this.description = description;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public abstract void parseValue(String value);
    public abstract String representValue();
    public abstract boolean isEnabled();
}
