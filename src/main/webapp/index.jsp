<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="com.github.epochcoder.kalah.game.Kalah"%>
<%@page import="com.github.epochcoder.kalah.servlet.KalahServlet"%>
<%@page import="com.github.epochcoder.kalah.game.KalahConfiguration"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!doctype html>
<html class="no-js" lang="">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <title>Kalah Home</title>
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
    <%
        final Kalah game = (Kalah) session.getAttribute(KalahServlet.GAME_KEY);
        final boolean hasGame = game != null;
    %>
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
                        <li class="active"><a href="index.jsp">Home</a></li>
                        <li><a title="See what happens with direct game output" href="random.jsp">Debug Play</a></li>
                        <li><a href="https://en.wikipedia.org/wiki/Kalah">About</a></li>
                        <li><a href="https://github.com/epochcoder">Contact</a></li>
                    </ul>
                </div>
            </div>
        </nav>
        <div class="container">
            <div class="row">
                <div class="col-md-1"></div>
                <div class="col-md-4">
                    <form id="newGameForm" class="form-group">
                        <div class="form-group">
                            <label for="playerTwo">Player One Name</label>
                            <input type="text" class="form-control" id="playerOne" placeholder="Luke" value="<%= StringEscapeUtils.escapeHtml(hasGame ? game.getPlayerOne().getPlayerName() : "") %>">
                        </div>
                        <div class="form-group">
                            <label for="playerOne">Player Two Name</label>
                            <input type="text" class="form-control" id="playerTwo" placeholder="Darth" value="<%= StringEscapeUtils.escapeHtml(hasGame ? game.getPlayerTwo().getPlayerName() : "") %>">
                        </div>
                        <div class="form-group">
                            <label for="pps">Pits Per Side</label>
                            <select class="form-control" id="pps">
                                <option value="2" <% if (hasGame && 2 == game.getConfiguration().getPits()) {%>selected="selected"<%} %>>2</option>
                                <option value="4" <% if (hasGame && 4 == game.getConfiguration().getPits()) {%>selected="selected"<%} %>>4</option>
                                <option value="6" <% if (hasGame && 6 == game.getConfiguration().getPits()) {%>selected="selected"<%} %>>6</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="spp">Seeds Per Pit</label>
                            <input type="number" class="form-control" id="spp" min="<%= KalahConfiguration.MIN_SEEDS %>" max="<%= KalahConfiguration.MAX_SEEDS %>" value="<%= hasGame ? game.getConfiguration().getSeeds(): KalahConfiguration.MIN_SEEDS %>">
                        </div>
                        <div class="form-group">
                            <button id="startGame" type="button" class="btn btn-primary">Start Game</button>
                        </div>
                    </form>
                </div>
                <div class="col-md-7"></div>
            </div>
            <div class="row">
                <div class="col-md-1"></div>
                <div class="col-md-10">
                    <div id="game" style="display: none;">
                        <div class="row">
                            <div id="kalahp1" class="kalah col-md-2"></div>
                            <div class="col-md-8">
                                <div id="playerOnePits" class="row"></div>
                                <div id="playerTwoPits" class="row"></div>
                            </div>
                            <div id="kalahp2" class="kalah col-md-2"></div>
                        </div>
                    </div>
                </div>
                <div class="col-md-1"></div>
            </div>
            <div class="row">
                <div class="col-md-1"></div>
                <div class="col-md-10">
                    <div id="output">
                        <div class="alert alert-info" role="alert">
                            Please enter your names and start the game
                        </div>
                    </div>
                </div>
                <div class="col-md-1"></div>
            </div>
            <hr>
            <footer>
                <p><a href="https://github.com/epochcoder">&copy; epochcoder</a> 2015</p>
            </footer>
        </div>
        <script>var hasGame = <%= hasGame %>;</script>
        <script src="resources/js/vendor/prototype.js"></script>
        <script src="resources/js/main.js"></script>
    </body>
</html>