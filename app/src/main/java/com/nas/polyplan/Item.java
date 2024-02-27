package com.nas.polyplan;

public class Item {

    private String title;
    private String family;

    public Item(String title, String family) {
        this.title = title;
        this.family = family;
    }

    public Item() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }
}
