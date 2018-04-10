package org.binas.domain;

public class User {

    private String email;
    private boolean hasBina;
    private int credit;

    public User(String email, boolean hasBina, int credit) {
        this.email = email;
        this.hasBina = hasBina;
        this.credit = credit;
    }
    public User(String email,int credit) {
    	this.email = email;
        this.hasBina = false;
        this.credit = credit;
    }

    public String getEmail() {
        return email;
    }

    public boolean hasBina() {
        return hasBina;
    }

    public int getCredit() {
        return credit;
    }
}
