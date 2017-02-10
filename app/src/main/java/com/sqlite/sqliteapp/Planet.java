package com.sqlite.sqliteapp;

public class Planet {
    private String name = "";
    private boolean checked = false;

    public Planet() {
    }

    public Planet(String name) {
        this.name = name;
    }

    public Planet(String name, boolean checked) {
        this.name = name;
        this.checked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String toString() {
        return name;
    }

    public void toggleChecked() {
        checked = !checked;
    }
}
