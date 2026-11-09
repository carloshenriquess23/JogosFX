package com.jogosfx.Test;

import com.jogosfx.dao.AdmDAO;
import com.jogosfx.dao.UserDAO;
import com.jogosfx.model.user.Adm;
import com.jogosfx.model.user.Client;
import com.jogosfx.model.user.User;

import java.sql.SQLException;
import java.util.ArrayList;

public class TestDAO {

    final static int userAtual=10;

    public static void userDAOTest() throws ClassNotFoundException, SQLException {

        System.out.println("-----------Testando Insert USER DAO-----------");

        Client cl1=new Client("pessoa"+(userAtual+1)+"@gmail.com","Pessoa"+(userAtual+1));
        System.out.println("Inserindo usuário:\n" +
                "Email: "+cl1.getEmail()+"\n"+
                "Nome: "+cl1.getName());
        int testInsert= UserDAO.insertUser(cl1,"12345");
        System.out.println("Insert Result:"+testInsert);

        System.out.println("-----------Testando Query ID USER DAO-----------");
        ArrayList<User> usersFoundId= UserDAO.findById(userAtual);

        System.out.println("Usuários encontrados pelo ID:"+ usersFoundId.size());
        for(User u:usersFoundId){

            System.out.println("ID User:"+u.getUserId());
            System.out.println("Email User:"+u.getEmail());
            System.out.println("Name User:"+u.getName());
            System.out.println("Balance User:"+u.getBalance());
        }

        System.out.println("-----------Testando Query Email USER DAO-----------");
        ArrayList<User> usersFoundEmail= UserDAO.findByEmail(cl1.getEmail());

        System.out.println("Usuários encontrados pelo Email:"+ usersFoundEmail.size());
        for(User u:usersFoundId){

            System.out.println("ID User:"+u.getUserId());
            System.out.println("Email User:"+u.getEmail());
            System.out.println("Name User:"+u.getName());
            System.out.println("Balance User:"+u.getBalance());
        }

        System.out.println("-----------Testando Query Name USER DAO-----------");
        ArrayList<User> usersFoundName= UserDAO.findByName(cl1.getName());

        System.out.println("Usuários encontrados pelo Nome:"+ usersFoundName.size());
        for(User u:usersFoundId){

            System.out.println("ID User:"+u.getUserId());
            System.out.println("Email User:"+u.getEmail());
            System.out.println("Name User:"+u.getName());
            System.out.println("Balance User:"+u.getBalance());
        }

        System.out.println("-----------Testando Update USER DAO-----------");

        User u1=usersFoundId.get(0);
        int updateResult = UserDAO.updateUser(u1,"1234");
        System.out.println("Update Result:"+updateResult);

        System.out.println("-----------Testando Delete USER DAO-----------");

        int deleteResult=UserDAO.deleteUser(userAtual);
        System.out.println("Delete Result:"+deleteResult);
    }

    public static void admDAOTest() throws ClassNotFoundException, SQLException {

        System.out.println("-----------Testando Insert ADM DAO-----------");

        ArrayList<User> usersFoundId= UserDAO.findById((userAtual));
        Adm adm1=new Adm(usersFoundId.get(0).getUserId(),usersFoundId.get(0).getEmail(),usersFoundId.get(0).getName(),"6565656565"); //10 digitos na credential
        int insertResult= AdmDAO.insertAdmin(adm1);
        System.out.println("Admin Insert Result:"+insertResult);

        System.out.println("-----------Testando Query Credential ADM DAO-----------");
        ArrayList<Adm> admFoundCredential= AdmDAO.findByCredential(adm1.getCredential());

        System.out.println("Quantidade de Admins encontrados: "+admFoundCredential.size());
        System.out.println("Adm Credential: "+admFoundCredential.get(0).getCredential());
        System.out.println("Adm User ID: "+admFoundCredential.get(0).getUserId());
        System.out.println("Adm Email: "+admFoundCredential.get(0).getEmail());
        System.out.println("Adm Nome: "+admFoundCredential.get(0).getName());
    }
}
