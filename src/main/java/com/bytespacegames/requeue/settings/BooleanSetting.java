package com.bytespacegames.requeue.settings;

public class BooleanSetting extends Setting {
    private boolean value;
    public BooleanSetting(String name, boolean defaultValue) {
        super(name);
        value = defaultValue;
    }

    @Override
    public void parseValue(String value) {
        this.value = Boolean.parseBoolean(value);
    }

    @Override
    public String representValue() {
        return value + "";
    }
    public boolean isEnabled() {
        return value;
    }
    public void setEnabled(boolean value) {
        this.value = value;
    }
    public boolean toggle() {
        value = !value;
        return value;
    }
}
