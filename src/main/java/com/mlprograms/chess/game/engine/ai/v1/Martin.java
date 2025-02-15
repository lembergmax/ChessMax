/*
 * Copyright (c) 2025 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game.engine.ai.v1;

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

		// Alle legalen Züge der weißen Figuren sammeln
		for (Piece piece : getBoard().getPieceList().stream().filter(piece -> piece.isWhite() == getBoard().isWhiteTurn()).toList()) {
			possibleMoves.addAll(piece.getLegalMoves(getBoard()));
		}

		// Für jeden möglichen Zug simulieren wir die Stellung
		for (Move move : possibleMoves) {
			// Simuliere den Zug anhand einer tiefen Kopie der PieceList
			List<Piece> simulatedPieceList = simulateMove(getBoard().getPieceList(), move);

			// Bewertet wird die simulierte Stellung
			double evaluation = new PositionEvaluation(getBoard())
				                    .evaluatePosition(simulatedPieceList, getBoard().isWhiteTurn());

			// Speichern des besten Zugs
			if (evaluation > highestValue) {
				highestValue = evaluation;
				bestMove = move;
			}
		}

		return bestMove;
	}

	/**
	 * Erzeugt eine tiefe Kopie der übergebenen Figurenliste, simuliert den Zug
	 * und gibt die modifizierte Liste zurück.
	 * <p>
	 * Zusätzlich wird überprüft, ob am Ziel des Zugs ein gegnerisches Piece steht.
	 * Ist dies der Fall, wird das Piece als geschlagen markiert (mittels move.setCapturedPiece)
	 * und aus der Liste entfernt – dies ermöglicht auch Schlagzüge, bei denen kein materieller Verlust entsteht.
	 *
	 * @param originalPieceList
	 * 	die aktuelle Figurenliste
	 * @param move
	 * 	der zu simulierende Zug
	 *
	 * @return eine neue Figurenliste, in der der Zug simuliert wurde
	 */
	private List<Piece> simulateMove(List<Piece> originalPieceList, Move move) {
		// Erstelle eine tiefe Kopie der Figurenliste (vorausgesetzt, jede Figur implementiert copy())
		List<Piece> newPieceList = originalPieceList.stream()
			                           .map(Piece::clone)
			                           .collect(Collectors.toList());

		// Finde die bewegte Figur anhand ihrer Startposition
		Piece movingPiece = newPieceList.stream()
			                    .filter(piece -> piece.getRow() == move.getOldRow() && piece.getColumn() == move.getOldColumn())
			                    .findFirst()
			                    .orElse(null);

		if (movingPiece == null) {
			return newPieceList;
		}

		// Prüfe, ob am Zielfeld ein gegnerisches Piece steht
		Piece capturedPiece = newPieceList.stream()
			                      .filter(piece -> piece.getRow() == move.getNewRow() &&
				                                       piece.getColumn() == move.getNewColumn() &&
				                                       piece.isWhite() != movingPiece.isWhite())
			                      .findFirst()
			                      .orElse(null);

		if (capturedPiece != null) {
			// Das geschlagene Piece wird dem Move zugeordnet
			move.setCapturedPiece(capturedPiece);
			// Entferne das geschlagene Piece aus der Liste
			newPieceList.remove(capturedPiece);
		}

		// Aktualisiere die Position der bewegten Figur
		movingPiece.setRow(move.getNewRow());
		movingPiece.setColumn(move.getNewColumn());

		return newPieceList;
	}

}
