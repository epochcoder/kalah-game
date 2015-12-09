<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
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
<!doctype html>
<html class="no-js" lang="">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <title>Random CPU vs Random CPU</title>
        <meta name="description" content="">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <link rel="stylesheet" href="resources/css/vendor/bootstrap.min.css">
        <style>
            body {
                padding-top: 50px;
                padding-bottom: 20px;
            }
        </style>
        <link rel="stylesheet" href="resources/css/vendor/bootstrap-theme.min.css">
        <link rel="stylesheet" href="resources/css/main.css">

        <!--[if lt IE 9]>
            <script src="//html5shiv.googlecode.com/svn/trunk/html5.js"></script>
            <script>window.html5 || document.write('<script src="resources/js/vendor/html5shiv.js"><\/script>')</script>
        <![endif]-->
    </head>
    <body>
        <!--[if lt IE 8]>
            <p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade your browser</a> to improve your experience.</p>
        <![endif]-->
        <nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
            <div class="container">
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <a class="navbar-brand" href="#">Kalah</a>
                </div>
                <div id="navbar" class="navbar-collapse collapse">
                    <ul class="nav navbar-nav">
                        <li><a href="index.jsp">Home</a></li>
                        <li><a href="https://en.wikipedia.org/wiki/Kalah">About</a></li>
                        <li><a href="https://github.com/epochcoder">Contact</a></li>
                    </ul>
                </div>
            </div>
        </nav>
        <div class="jumbotron">
            <div class="container">
                <h1>CPU Player vs CPU Player</h1>
                <p>
                    This example illustrates how the game works internally via it's events, 
                    the game output is written directly to the screen. nothing special was done 
                    as this page was originally just used for quickly running a random game
                </p>
                <p>
                    These random players just try to play the game without making invalid moves,
                    it does not have any AI beyond that
                </p>
            </div>
        </div>
        <div class="container">
            <div class="row">
                <div class="col-md-12">
                    <h2>Game Output</h2>
                    <ol>
                    <%
                        final JspWriter out2 = out;
                        final KalahConfiguration conofig = new KalahConfiguration(3, 3);
                        
                        Kalah game = new Kalah(conofig, new KalahListener() {
                            private void println(String msg) {
                                try {
                                    out2.println("<li><code>" + StringEscapeUtils
                                            .escapeHtml(msg) + "</code></li>");
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
                                    println("player[" + whoWon  + "] won :) - score="
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
                                println("acceptor[" + acceptor + "] received seed["  + seed + "]");
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
                </div>
            </div>
            <hr>
            <footer>
                <p><a href="https://github.com/epochcoder">&copy; epochcoder</a> 2015</p>
            </footer>
        </div>
        <script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
        <script>window.jQuery || document.write('<script src="resources/js/vendor/jquery-1.11.2.min.js"><\/script>')</script>
        <script src="resources/js/vendor/bootstrap.min.js"></script>
        <script src="resources/js/main.js"></script>
    </body>
</html>