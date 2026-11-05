package com.jogosfx.model.user;

public class Client extends User{


    public Client(int userId, String email, String name, double balance){

        super(userId, email, name, balance);
    }

    public Client(String email, String name) {
        super(email,name);
    }
}
