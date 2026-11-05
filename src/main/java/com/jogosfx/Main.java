package com.jogosfx;

import java.sql.Connection;
import java.sql.SQLException;

import static com.jogosfx.dao.ConnectionDB.generateConnection;
import com.jogosfx.dao.UserDAO;
import com.jogosfx.model.user.Client;

public class Main {

     static void main(String[] args) throws SQLException, ClassNotFoundException {

       Client cl2=new Client(4,"pessoa4@gmail.com","Pessoa4",0);

       System.out.println("Testando Update USER DAO:");
       System.out.println(UserDAO.updateUser(cl2,"12345"));
    }


}
