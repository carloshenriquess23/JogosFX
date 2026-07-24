package com.jogosfx.model.user;

class Adm extends User{

    private int credential;

    public boolean requiresExtraAuthentication(){
        return true;
    }
}
