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
                    System.out.println("starting new game of Kalah");
                }

                @Override
                public void gameEnd(Player whoWon, Player whoLost) {
                    if (whoWon == null || whoLost == null) {
                        System.out.println("game ended, it was a tie!");
                    } else {
                        System.out.println("game ended:");
                        System.out.println("\tplayer[" + whoWon 
                                + "] won :) - score=" + whoWon.getScore(false, true));
                        System.out.println("\tplayer[" + whoLost 
                                + "] lost :(- score=" + whoLost.getScore(false, true));
                    }
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
                public void seedAdded(SeedAcceptor acceptor, Seed seed) {
                    System.out.println("acceptor[" + acceptor 
                            + "] received seed["  + seed + "]");
                }
            });
        
            game.setPlayerOne(new Player(game, 1, "Willie") {
                @Override
                public void play() throws KalahException { 
                    Pit p = this.getPitById((int) Math.round(Math.random() * 2d));
                    while (p.amountOfSeeds() == 0) {
                        p = this.getPitById((int) Math.round(Math.random() * 2d));
                    }

                    this.sowFromPit(p.getPitId());
                }
            });

            game.setPlayerTwo(new Player(game, 2, "Rudi") {
                @Override
                public void play() throws KalahException {
                    Pit p = this.getPitById((int) Math.round(Math.random() * 2d));
                    while (p.amountOfSeeds() == 0) {
                        p = this.getPitById((int) Math.round(Math.random() * 2d));
                    }

                    this.sowFromPit(p.getPitId());
                }
            });
            
            game.startGame();

            while (game.isEndOfGame() == null) {
                game.getCurrentPlayer().play();    
            }
        %>
    </body>
</html>
