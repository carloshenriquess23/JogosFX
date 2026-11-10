package com.jogosfx.Test;

import com.jogosfx.dao.AdmDAO;
import com.jogosfx.dao.GameDAO;
import com.jogosfx.dao.GameRatingDAO;
import com.jogosfx.dao.UserDAO;
import com.jogosfx.model.game.*;
import com.jogosfx.model.user.Adm;
import com.jogosfx.model.user.Client;
import com.jogosfx.model.user.User;

import java.sql.SQLException;
import java.util.ArrayList;

public class TestDAO {

    final static int userAtual=16;

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
        Adm adm1=new Adm(usersFoundId.get(0).getUserId(),usersFoundId.get(0).getEmail(),usersFoundId.get(0).getName(),"6565656566"); //10 digitos na credential
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

    final static int gameAtual=5;
    final static int developerTeste = 1;
    final static int genderTeste = 1;
    final static int platformTeste = 1;

    private static void imprimirGames(ArrayList<Game> games) {
        for (Game g : games) {
            System.out.println("ID Game: " + g.getGameId());
            System.out.println("Name Game: " + g.getName());
            System.out.println("Price Game: " + g.getPrice());
            System.out.println("Description Game: " + g.getDescription());
            System.out.println("Rating Game: " + g.getRating());
            System.out.println("Active Game: " + g.isActive());
            System.out.println("Poster Game: " + g.getPoster());
            System.out.println("Developer Game: " + g.getDeveloper().getName());
            System.out.println("Gender Game: " + g.getGender().getName());
            System.out.println("Platform Game: " + g.getPlatform().getName());
            System.out.println("---");
        }
    }

    public static void gameDAOTest() throws ClassNotFoundException, SQLException {

        Developer developer = new Developer(developerTeste,"Steam");
        Gender gender = new Gender(genderTeste,"Ação");
        Platform platform = new Platform(platformTeste,"PC");

        System.out.println("-----------Testando Insert GAME DAO-----------");
        Game g1 = new Game("Jogo Teste "+(gameAtual+1), 59.90, "Descrição do jogo de teste", developer,true,
                "poster_teste.jpg", gender, platform);

        System.out.println("Inserindo jogo:\n" +
                "Nome: " + g1.getName() + "\n" +
                "Preço: " + g1.getPrice());
        int testInsert = GameDAO.insertGame(g1);
        System.out.println("Insert Result: " + testInsert);

        System.out.println("-----------Testando Query Name GAME DAO (ativos)-----------");
        ArrayList<Game> gamesFoundName = GameDAO.findGameByName(g1.getName(), true);
        System.out.println("Jogos encontrados pelo Nome: " + gamesFoundName.size());
        imprimirGames(gamesFoundName);

        System.out.println("-----------Testando Query Name GAME DAO (inativos)-----------");
        ArrayList<Game> gamesFoundInactive = GameDAO.findGameByName("Jogo Teste "+(gameAtual+1), false);
        System.out.println("Jogos inativos encontrados pelo Nome (esperado 0): " + gamesFoundInactive.size());

        System.out.println("-----------Testando Query ID GAME DAO-----------");
        ArrayList<Game> gamesFoundId = GameDAO.FindGameById(gameAtual+1);
        System.out.println("Jogos encontrados pelo ID : " + gamesFoundId.size());
        imprimirGames(gamesFoundId);

        System.out.println("-----------Testando Update GAME DAO-----------");

        Game gameAtualizado = new Game(gameAtual+1, "Jogo Teste "+(gameAtual+1) + " (Atualizado)", 79.90,
                "Descrição atualizada", 4.5, true, "poster_atualizado.jpg",
                developer, gender, platform);
        int updateResult = GameDAO.updateGame(gameAtualizado);
        System.out.println("Update Result: " + updateResult);

       System.out.println("-----------Testando Delete GAME DAO-----------");
       int deleteResult = GameDAO.deleteGame(gameAtual);
       System.out.println("Delete Result: " + deleteResult);
    }

    public static void gameRatingDAOTest() throws ClassNotFoundException, SQLException {

        int gameId = gameAtual;
        int userId = userAtual;
        int ratingAtual=1;

        System.out.println("-----------Testando Insert GAME RATING DAO-----------");
        GameRating r1 = new GameRating(gameId, userId, "Avaliação de teste: ótimo jogo!",5);

        System.out.println("Inserindo avaliação:\n" +
                "Game ID: " + r1.getGameID() + "\n" +
                "User ID: " + r1.getUserID() + "\n" +
                "Nota: " + r1.getRating());
        int testInsert = GameRatingDAO.insertGameRating(r1);
        System.out.println("Insert Result (esperado 1): " + testInsert);

        System.out.println("-----------Testando Query Game ID GAME RATING DAO-----------");
        ArrayList<GameRating> ratingsByGame = GameRatingDAO.findByGameId(gameId);
        System.out.println("Avaliações encontradas pelo Game ID (esperado >= 1): " + ratingsByGame.size());
        imprimirGameRatings(ratingsByGame);

        System.out.println("-----------Testando Query User ID GAME RATING DAO-----------");
        ArrayList<GameRating> ratingsByUser = GameRatingDAO.findByUserId(userId);
        System.out.println("Avaliações encontradas pelo User ID (esperado >= 1): " + ratingsByUser.size());
        imprimirGameRatings(ratingsByUser);

        System.out.println("-----------Testando Update GAME RATING DAO-----------");
        GameRating ratingAtualizado = new GameRating(ratingAtual, gameId, userId, 3,
                "Avaliação de teste atualizada");
        int updateResult = GameRatingDAO.updateGameRating(ratingAtualizado);
        System.out.println("Update Result (esperado 1): " + updateResult);

        System.out.println("-----------Testando Delete GAME RATING DAO-----------");
        int deleteResult = GameRatingDAO.deleteGameRating(ratingAtual);
        System.out.println("Delete Result (esperado 1): " + deleteResult);
    }

    private static void imprimirGameRatings(ArrayList<GameRating> ratings) {
        for (GameRating r : ratings) {
            System.out.println("Game ID: " + r.getGameID() +
                    " | User ID: " + r.getUserID() +
                    " | Nota: " + r.getRating() +
                    " | Descrição: " + r.getDescription());
        }
    }
}
