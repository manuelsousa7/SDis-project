package org.binas.domain;

import java.util.concurrent.atomic.AtomicInteger;

public class User {

    private String email;
    private boolean hasBina;
    private AtomicInteger credit;

    public User(String email, boolean hasBina, int credit) {
        this.email = email;
        this.hasBina = hasBina;
        this.credit = new AtomicInteger(credit);
    }

    public User(String email,int credit){
        this.email = email;
        this.hasBina = false;
        this.credit = new AtomicInteger(credit);
    }

    public String getEmail() {
        return email;
    }
    public synchronized void setHasBina(boolean hasBina) {
    	this.hasBina = hasBina;
    }
    public synchronized void addBonus(int bonus) {
    	this.credit.addAndGet(bonus);
    }
    public boolean hasBina() {
        return hasBina;
    }

    public int getCredit() {
        return credit.get();
    }
}
