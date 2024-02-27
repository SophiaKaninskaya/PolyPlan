package com.nas.polyplan;

public class Task {

    private String family;
    private String status;
    private String responsible;
    private String text;

    public Task(String family, String status, String responsible, String text) {
        this.family = family;
        this.status = status;
        this.responsible = responsible;
        this.text = text;
    }

    public Task() {
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
