/*
 * Copyright (c) 2025 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game.engine;

public enum GameEnding {
	CHECKMATE,             // The king is in check and cannot escape; opponent wins.
	STALEMATE,             // The player has no legal moves, but the king is not in check; results in a draw.
	AGREED_DRAW,           // Both players agree to end the game as a draw.
	INSUFFICIENT_MATERIAL, // Neither player has enough material to deliver a checkmate; game ends in a draw.
	FIFTY_MOVE_RULE,       // No pawn moves or captures in the last 50 moves; can result in a draw.
	THREEFOLD_REPETITION,  // Synonym for POSITION_REPETITION; refers to three identical board states.
	RESIGNATION,           // A player concedes the game, leading to an opponent's victory.
	TIME_FORFEIT,          // A player runs out of time on the clock, resulting in a loss.
	IN_PROGRESS				  // The game is still ongoing.
}