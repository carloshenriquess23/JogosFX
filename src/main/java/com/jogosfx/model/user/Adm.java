package com.jogosfx.model.user;

class Adm extends User{

    private String credential;

    public boolean requiresExtraAuthentication(){
        return true;
    }

    private boolean credentialAuthentication(String credential){

        return credential.equals(this.credential);
    }
}
