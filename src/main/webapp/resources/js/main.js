/* global Ajax */

var Game = (function() {
    var config,
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
    }

    /**
     * starts a new game
     */
    function startNewGame() {
        reset();

        new Ajax.Request('api', {
            method: 'post',
            parameters: {
                action: 'new',
                playerOne: $F('playerOne'),
                playerTwo: $F('playerTwo')
            },
            onSuccess: function(t) {
                updateState(t.responseJSON);
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
        $('kalahp' + player.playerId).update('<span class="label label-primary">'
                + player.playerName + '</span><h2>'
                + player.store.seeds.length + '</h2>');
        player.pits.each(function(pit) {
            $('p' + player.playerId + 'p' + pit.pitId)
                    .update('<span class="badge">' + pit.seeds.length + '</span>');
        });
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
            console.log(state);
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
});