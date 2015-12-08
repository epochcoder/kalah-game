<%-- 
    Document   : index
    Created on : Dec 8, 2015, 7:02:26 PM
    Author     : Willie Scholtz
--%>

<%@page import="com.github.epochcoder.kalah.game.entity.Seed"%>
<%@page import="com.github.epochcoder.kalah.game.entity.SeedAcceptor"%>
<%@page import="com.github.epochcoder.kalah.game.KalahException"%>
<%@page import="com.github.epochcoder.kalah.game.entity.Pit"%>
<%@page import="com.github.epochcoder.kalah.game.entity.Player"%>
<%@page import="com.github.epochcoder.kalah.game.events.KalahListener"%>
<%@page import="com.github.epochcoder.kalah.game.Kalah"%>
<%@page import="com.github.epochcoder.kalah.game.KalahConfiguration"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>
        
        
        <%
        
            KalahConfiguration conofig = new KalahConfiguration(3, 3);
            Kalah game = new Kalah(conofig, new KalahListener() {
                @Override
                public void gameStart() {
                    System.out.println("starting....");
                }

                @Override
                public void gameEnd(Player whoWon) {
                    System.out.println("ending.... player[" + whoWon + "] won!");
                }

                @Override
                public void sowStart(Player player, Pit fromPit) {
                    System.out.println("player[" + player + "] started sowing from pit[" + fromPit + "]");
                }

                @Override
                public void sowEnd(Player player, Pit fromPit) {
                    System.out.println("player[" + player + "] ended sowing from pit[" + fromPit + "]");
                }

                @Override
                public void playerSwitch(Player newPlayer) {
                    System.out.println("game switched players to [" + newPlayer + "]");
                }

                @Override
                public void freeMove(Player forPlayer) {
                     System.out.println("player[" + forPlayer + "] has a free move!");
                }

                @Override
                public void pitEmpty(Pit pit) {
                    System.out.println("pit[" + pit + "] is now empty!");
                }

                @Override
                public void seedAdded(Player player, SeedAcceptor acceptor, Seed seed) {
                    System.out.println("player[" + player + "] received seed[" 
                            + seed + "] in acceptor[" + acceptor + "]");
                }
            });
        
            game.setPlayerOne(new Player(game, 1, "Willie") {
                @Override
                public void play() throws KalahException { 
                    this.sowFromPit((int) Math.round(Math.random() * 2d));
                }
            });

            game.setPlayerTwo(new Player(game, 2, "Rudi") {
                @Override
                public void play() throws KalahException {
                    this.sowFromPit((int) Math.round(Math.random() * 2d));
                }
            });
            
            game.startGame();

            while (game.isEndOfGame() == null) {
                game.getCurrentPlayer().play();    
            }
        %>
    </body>
</html>
