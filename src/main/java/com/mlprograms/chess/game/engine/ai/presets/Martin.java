/*
 * Copyright (c) 2025 Max Lemberg. This file is part of ChessMax.
 * Licensed under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game.engine.ai.presets;

import com.mlprograms.chess.game.engine.Move;
import com.mlprograms.chess.game.engine.ai.Ai;
import com.mlprograms.chess.game.engine.state.PositionEvaluation;
import com.mlprograms.chess.game.pieces.Piece;
import com.mlprograms.chess.utils.ConfigFetcher;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
	protected Move findStrategicMove() {
		double highestValue = Double.NEGATIVE_INFINITY;
		Move bestMove = null;
		List<Move> possibleMoves = new ArrayList<>();

		// Gather all legal moves for the pieces of the player whose turn it is
		for (Piece piece : getBoard().getPieceList().stream()
			                   .filter(piece -> piece.isWhite() == getBoard().isWhiteTurn())
			                   .toList()) {
			possibleMoves.addAll(piece.getLegalMoves(getBoard()));
		}

		// For each possible move, simulate the resulting position
		for (Move move : possibleMoves) {
			// Simulate the move using a deep copy of the current piece list
			List<Piece> simulatedPieceList = simulateMove(getBoard().getPieceList(), move);

			// Evaluate the simulated position
			double evaluation = new PositionEvaluation(getBoard())
				                    .evaluatePosition(simulatedPieceList, getBoard().isWhiteTurn());

			// Store the best move if the evaluation is higher than the current best
			if (evaluation > highestValue) {
				highestValue = evaluation;
				bestMove = move;
			}
		}

		return bestMove;
	}

	/**
	 * Creates a deep copy of the provided piece list, simulates the move,
	 * and returns the modified list.
	 * <p>
	 * Additionally, this method checks if an opponent's piece is located at the target square.
	 * If so, the piece is marked as captured (via move.setCapturedPiece) and removed from the list—
	 * enabling capture moves that do not result in material loss.
	 *
	 * @param originalPieceList
	 * 	the current list of pieces on the board
	 * @param move
	 * 	the move to simulate
	 *
	 * @return a new piece list reflecting the board state after the move is executed
	 */
	private List<Piece> simulateMove(List<Piece> originalPieceList, Move move) {
		// Create a deep copy of the piece list (assuming each piece implements clone())
		List<Piece> newPieceList = originalPieceList.stream()
			                           .map(Piece::clone)
			                           .collect(Collectors.toList());

		// Find the piece that is moving based on its starting position
		Piece movingPiece = newPieceList.stream()
			                    .filter(piece -> piece.getRow() == move.getOldRow() && piece.getColumn() == move.getOldColumn())
			                    .findFirst()
			                    .orElse(null);

		if (movingPiece == null) {
			return newPieceList;
		}

		// Check if an opponent's piece is at the target position
		Piece capturedPiece = newPieceList.stream()
			                      .filter(piece -> piece.getRow() == move.getNewRow() &&
				                                       piece.getColumn() == move.getNewColumn() &&
				                                       piece.isWhite() != movingPiece.isWhite())
			                      .findFirst()
			                      .orElse(null);

		if (capturedPiece != null) {
			// Assign the captured piece to the move
			move.setCapturedPiece(capturedPiece);
			// Remove the captured piece from the list
			newPieceList.remove(capturedPiece);
		}

		// Update the position of the moving piece
		movingPiece.setRow(move.getNewRow());
		movingPiece.setColumn(move.getNewColumn());

		return newPieceList;
	}

}
