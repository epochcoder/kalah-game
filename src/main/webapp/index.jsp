<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!doctype html>
<html class="no-js" lang="">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <title></title>
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
                        <li class="active"><a href="#">Home</a></li>
                        <li><a href="https://en.wikipedia.org/wiki/Kalah">About</a></li>
                        <li><a href="https://github.com/epochcoder">Contact</a></li>
                        <li class="dropdown">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
                                Extras 
                                <span class="caret"></span>
                            </a>
                            <ul class="dropdown-menu">
                                <li title="See what happens with direct game output">
                                    <a href="random.jsp">Random CPU vs Random CPU</a>
                                </li>
                            </ul>
                        </li>
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
                            <input type="text" class="form-control" id="playerOne" placeholder="Luke">
                        </div>
                        <div class="form-group">
                            <label for="playerOne">Player Two Name</label>
                            <input type="text" class="form-control" id="playerTwo" placeholder="Darth">
                        </div>
                        <div class="form-group">
                            <button type="button" class="btn btn-primary">Start Game</button>
                        </div>
                    </form>
                </div>
                <div class="col-md-7"></div>
            </div>
            <div class="row">
                <div class="col-md-1"></div>
                <div class="col-md-10">
                    <canvas id="game"></canvas>
                </div>
                <div class="col-md-1"></div>
            </div>
            <div class="row">
                <div class="col-md-1"></div>
                <div class="col-md-10">
                    <div id="output">
                        <p>
                            <div class="alert alert-success" role="alert">...</div>
                            <div class="alert alert-info" role="alert">...</div>
                            <div class="alert alert-warning" role="alert">...</div>
                            <div class="alert alert-danger" role="alert">...</div>
                        </p>
                    </div>
                </div>
                <div class="col-md-1"></div>
            </div>
            <hr>
            <footer>
                <p><a href="https://github.com/epochcoder">&copy; epochcoder</a> 2015</p>
            </footer>
        </div>
        <script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
        <script>window.jQuery || document.write('<script src="resources/js/vendor/jquery-1.11.2.min.js"><\/script>')</script>
        <script src="resources/js/vendor/bootstrap.min.js"></script>
        <script src="resources/js/vendor/raf.js"></script>
        <script src="resources/js/main.js"></script>
    </body>
</html>