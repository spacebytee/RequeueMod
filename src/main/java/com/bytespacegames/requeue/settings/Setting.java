package com.bytespacegames.requeue.settings;

public abstract class Setting {
    private String name;
    public Setting(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public abstract void parseValue(String value);
    public abstract String representValue();
    public abstract boolean isEnabled();
}
