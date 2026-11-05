package com.jogosfx.model.user;

public abstract class User {

    private int userId;
    private String email;
    private String name;
    private double balance;

    protected User(int userId, String email, String name, double balance) {

        this.userId = userId;
        this.email = email;
        this.name = name;
        this.balance = balance;
    }

    protected User(int userId,String email, String name) {

        this.userId = userId;
        this.email = email;
        this.name = name;
    }

    public User(String email, String name) {
    }

    public boolean requiresExtraAuthentication(){
        return false;
    }

    public int getUserId() {

        return this.userId;
    }

    public String getEmail(){
        return this.email;
    }

    public String getName(){
        return this.name;
    }

    public double getBalance(){ return this.balance;}
}
