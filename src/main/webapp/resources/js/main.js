/* global Ajax, $R */

var Game = (function() {
    var game = $('game'),
        p1Pits = $('playerOnePits'),
        p2Pits = $('playerTwoPits'),
        config,
        currentPlayer,
        playerOne,
        playerTwo;

    /**
     * resets the game
     */
    function reset() {
        config = null;
        currentPlayer = null;
        playerOne = null;
        playerTwo = null;

        game.hide();
        p1Pits.update();
        p2Pits.update();
    }

    function createPits(pps) {
        $R(0, pps - 1).each(function(idx) {
            p1Pits.insert({
                bottom: new Element('div', {
                    id: 'p1p' + (pps - 1 - idx),
                    class: 'pit col-md-' + (12 / pps)
                })
            });

            p2Pits.insert({
                bottom: new Element('div', {
                    id: 'p2p' + idx,
                    class: 'pit col-md-' + (12 / pps)
                })
            });
        });
    }

    /**
     * starts a new game
     */
    function startNewGame() {
        reset();

        var pps = $F('pps');
        new Ajax.Request('api', {
            method: 'post',
            parameters: {
                action: 'new',
                playerOne: $F('playerOne'),
                playerTwo: $F('playerTwo'),
                pps: $F('pps'),
                spp: $F('spp')
            },
            onSuccess: function(t) {
                // create the initial pits
                createPits(pps);

                // and update the state
                updateState(t.responseJSON);

                // show the game
                $('game').show();
            }
        });
    }

    /**
     * updates the state without modifying anything
     */
    function loadGameFromSession() {
        var json;
        new Ajax.Request('api', {
            method: 'post',
            onSuccess: function(t) {
                json = t.responseJSON;
                
                // create the loaded pits
                createPits(json.game.configuration.pits);
                
                // update the state
                updateState(json);

                // show the game
                game.show();
            }
        });
    }

    /**
     * plays a move from the selected pit.
     */
    function playMove(pitElement) {
        var parts = /^p(\d{1})p(\d{1})$/.exec(pitElement.id);
        if (!parts || parts[1] !== String(currentPlayer)) {
            updateMessage('warning', 'That is not your pit '
                    + getCurrentPlayer().playerName + ', please try again!');
        } else {
            new Ajax.Request('api', {
                method: 'post',
                parameters: {
                    action: 'play',
                    pitId: parts[2]
                },
                onSuccess: function(t) {
                    updateState(t.responseJSON);
                }
            });
        }
    }

    function updatePlayerStoreAndPits(player) {
        var kalah = $('kalahp' + player.playerId),
            pitEl, isActive;

        // check if this is the active player.
        isActive = player.playerId === currentPlayer;

        kalah.update('<span class="label label-primary">'
                + player.playerName + '</span><h2>'
                + player.store.seeds.length + '</h2>');

        // set active states
        kalah[(isActive ? 'add' : 'remove') + 'ClassName']('active');

        player.pits.each(function(pit) {
            pitEl = $('p' + player.playerId + 'p' + pit.pitId);
            pitEl.update('<span class="badge">' + pit.seeds.length + '</span>');

            // set active states
            pitEl[(isActive ? 'add' : 'remove') + 'ClassName']('active');
        });

        // get opposite player first pit for styling
        var opp = player.playerId === playerOne.playerId
                ? playerTwo.playerId : playerOne.playerId;

        // set opposite player first pit active style
        $('p' + opp + 'p0')[(isActive ? 'add' : 'remove')
                    + 'ClassName']('opp-active');
    }

    function getCurrentPlayer() {
        return playerOne.playerId === currentPlayer
                ? playerOne : playerTwo;
    }

    /**
     * updates the game state after any API call
     */
    function updateState(state) {
        if (state) {
            config = state.game.configuration;
            currentPlayer = state.game.currentPlayer;
            playerOne = state.game.playerOne;
            playerTwo = state.game.playerTwo;

            updatePlayerStoreAndPits(playerOne);
            updatePlayerStoreAndPits(playerTwo);

            if (!state.freeMovePlayer) {
                updateMessage('info', 'Please make a move '
                        + getCurrentPlayer().playerName);
            } else {
                updateMessage('success', 'You have a free move '
                        + getCurrentPlayer().playerName);
            }

            if (state.problem) {
                switch (state.problem) {
                    case 'NO_SEEDS_IN_PIT': {
                        updateMessage('danger', 'You have no seeds left in this pit '
                                + getCurrentPlayer().playerName);
                        break;
                    }
                    default: updateMessage('danger', state.problem);
                }
            } else {
                if (state.tied) {
                    updateMessage('success', 'Game Over, you have a tie!');
                } else if (state.winner && state.loser) {
                    updateMessage('success', 'Game Over, ' + state.winner
                            + ' won, and ' + state.loser + ' lost!');
                }
            }
        }
    }

    /**
     * shows a message in the game's output console
     */
    function updateMessage(type, msg) {
        type = type || 'info'; // warning, success, danger
        $('output').update('<div class="alert alert-' + type
                + '" role="alert">' + (msg || '') + '</div>');
    }

    return {
        loadGameFromSession: loadGameFromSession,
        startNewGame: startNewGame,
        playMove: playMove
    };
})();

document.observe('dom:loaded', function () {
    document.on('click', '#startGame', function() {
        Game.startNewGame();
    });

    document.on('click', 'div.pit[id] > span.badge', function(ev, el) {
        Game.playMove(el.up('div.pit'));
    });

    if (window.hasGame) {
        // reload previous game
        Game.loadGameFromSession();
    }
});