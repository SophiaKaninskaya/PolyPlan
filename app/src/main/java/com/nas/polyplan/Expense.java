package com.nas.polyplan;

public class Expense {

    private String family;
    private int amount;
    private String category;
    private String date;
    private String type;

    public Expense(String family, int amount, String category, String date, String type) {
        this.family = family;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.type = type;
    }

    public Expense() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
