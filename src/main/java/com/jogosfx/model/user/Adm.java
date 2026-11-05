package com.jogosfx.model.user;

public class Adm extends User{

    private String credential;

    public Adm(int userId, String email,String name, String credential){
        super(userId, email, name);
        this.credential = credential;
    }

    public String getCredential() {
        return this.credential;
    }

    public boolean requiresExtraAuthentication(){
        return true;
    }

    private boolean credentialAuthentication(String credential){

        return credential.equals(this.credential);
    }
}
