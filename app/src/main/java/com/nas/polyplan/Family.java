package com.nas.polyplan;

import java.util.HashMap;
import java.util.Map;

public class Family {

    private String name;
    private Map<String, String> members = new HashMap<>();
    private Map<String, String> documents = new HashMap<>();
    private int balance;

    public Family(String name, Map<String, String> members, int balance) {
        this.name = name;
        this.members = members;
        this.balance = balance;
    }

    public Family() {
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public Map<String, String> getDocuments() {
        return documents;
    }

    public void setDocuments(Map<String, String> documents) {
        this.documents = documents;
    }

    public Map<String, String> getMembers() {
        return members;
    }

    public void setMembers(Map<String, String> members) {
        this.members = members;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
