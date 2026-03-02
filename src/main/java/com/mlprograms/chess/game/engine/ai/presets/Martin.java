/*
 * Copyright (c) 2025 Max Lemberg. This file is part of ChessMax.
 * Licensed under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game.engine.ai.presets;

import com.mlprograms.chess.game.engine.Move;
import com.mlprograms.chess.game.engine.ai.Ai;
import com.mlprograms.chess.utils.ConfigFetcher;

import java.util.List;

public class Martin extends Ai {

    private static final int BOT_NUMBER = 1;

    public Martin() {
        super(
                ConfigFetcher.fetchStringConfig("Bots", "BOT_" + BOT_NUMBER + "_NAME"),
                ConfigFetcher.fetchIntegerConfig("Bots", "BOT_" + BOT_NUMBER + "_SPRITE"),
                ConfigFetcher.fetchIntegerConfig("Bots", "BOT_" + BOT_NUMBER + "_ELO"),
                ConfigFetcher.fetchIntegerConfig("Bots", "BOT_" + BOT_NUMBER + "_DEPTH")
        );
    }

    @Override
    public void makeMove() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        List<Move> moves = List.copyOf(getBoard().getPieceList()).stream()
                        .filter(piece -> piece.isWhite() == getBoard().isWhiteTurn())
                        .flatMap(piece -> piece.getLegalMoves(getBoard()).stream())
                        .toList();
        Move randomMove = moves.get((int) (Math.random() * moves.size()));
        getBoard().makeMove(randomMove);
    }

}
