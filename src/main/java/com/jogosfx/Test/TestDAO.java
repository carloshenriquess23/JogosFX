package com.jogosfx.Test;

import com.jogosfx.dao.UserDAO;
import com.jogosfx.model.user.Client;

import java.sql.SQLException;

public class TestDAO {

    static void userDAOTest() throws ClassNotFoundException, SQLException {

        int userAtual=5;
        System.out.println("Testando Insert USER DAO:");

        Client cl1=new Client("pessoa"+(userAtual+1)+"@gmail.com","Pessoa"+(userAtual+1));
        System.out.println("Inserindo usuário:" +
                "Email:pessoa"+(userAtual+1)+"@gmail.com" +
                "Nome:Pessoa"+(userAtual+1));
        int testInsert= UserDAO.insertUser(cl1,"12345");
    }
}
