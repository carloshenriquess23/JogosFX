package com.jogosfx.model.user;

public abstract class User {

    private int userId;
    private String email;
    private String password;
    private String name;
    private double balance;

    protected boolean checkPassword(String rawPassword){

        return rawPassword.equals(this.password);
    }

    public boolean requiresExtraAuthentication(){
        return false;
    }
}
