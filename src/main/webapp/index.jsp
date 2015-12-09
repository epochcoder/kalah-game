<%-- 
    Document   : index
    Created on : Dec 8, 2015, 7:02:26 PM
    Author     : Willie Scholtz
--%>
<%@page import="java.io.IOException"%>
<%@page import="com.github.epochcoder.kalah.game.entity.impl.RandomPlayer"%>
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
        <title>Random Play - [CPU1 vs CPU 2]</title>
    </head>
    <body>
        <h1>Random Play - [CPU1 vs CPU 2]</h1>
        <ol>
        <%
            final JspWriter out2 = out;
            
            KalahConfiguration conofig = new KalahConfiguration(3, 3);
            Kalah game = new Kalah(conofig, new KalahListener() {
                private void println(String msg) {
                    try {
                        out2.println("<li>" + msg + "</li>");
                    } catch (IOException ioe) {};
                }
                
                @Override
                public void gameStart() {
                    println("starting new game of Kalah");
                }

                @Override
                public void gameEnd(Player whoWon, Player whoLost) {
                    if (whoWon == null || whoLost == null) {
                        println("game ended, it was a tie!");
                    } else {
                        println("game ended:");
                        println("\tplayer[" + whoWon  + "] won :) - score="
                                + whoWon.getScore(false, true));
                        println("\tplayer[" + whoLost + "] lost :( - score=" 
                                + whoLost.getScore(false, true));
                    }
                }

                @Override
                public void sowStart(Player player, Pit fromPit) {
                    println("player[" + player + "] started sowing from pit[" + fromPit + "]");
                }

                @Override
                public void sowEnd(Player player, Pit fromPit) {
                    println("player[" + player + "] ended sowing from pit[" + fromPit + "]");
                }

                @Override
                public void playerSwitch(Player newPlayer) {
                    println("game switched players to [" + newPlayer + "]");
                }

                @Override
                public void freeMove(Player forPlayer) {
                     println("player[" + forPlayer + "] has a free move!");
                }

                @Override
                public void pitEmpty(Pit pit) {
                    println("pit[" + pit + "] is now empty!");
                }

                @Override
                public void seedAdded(SeedAcceptor acceptor, Seed seed) {
                    println("acceptor[" + acceptor 
                            + "] received seed["  + seed + "]");
                }
            });
        
            game.setPlayerOne(new RandomPlayer(game, 1, "CPU 1"));
            game.setPlayerTwo(new RandomPlayer(game, 2, "CPU 2"));
            game.startGame();

            while (game.isEndOfGame() == null) {
                // play the game until it completes randomly
                ((RandomPlayer) game.getCurrentPlayer()).play();    
            }
        %>
        </ol>
    </body>
</html>