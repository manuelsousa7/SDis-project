package org.binas.domain;

public class User {

    protected String email;
    protected boolean hasBina;
    protected int credit;

    public User(String email, boolean hasBina, int credit) {
        this.email = email;
        this.hasBina = hasBina;
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
