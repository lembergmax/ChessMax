/*
 * Copyright (c) 2025 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.tests.pieces;

import com.mlprograms.chess.game.engine.Move;
import com.mlprograms.chess.game.pieces.*;
import com.mlprograms.chess.game.ui.Board;
import com.mlprograms.chess.human.Human;
import com.mlprograms.chess.utils.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KingTest {

	private Board board;
	private King whiteKing;
	private King blackKing;

	@BeforeEach
	void setUp() {
		board = new Board(new Human(), new Human(), true);
		// Bei grundlegenden Bewegungs-Tests kann man auch Figuren im Brett belassen.
		board.getPieceList().clear();
		whiteKing = new King(board, 4, 4, true);
		blackKing = new King(board, 4, 0, false);
		board.getPieceList().add(whiteKing);
		board.getPieceList().add(blackKing);
	}

	// ===================== Grundlegende Bewegungs-Tests für den weißen König =====================

	@Test
	@DisplayName("White King valid move: one square up")
	void testWhiteKingMoveUp() {
		whiteKing.setPosition(4, 4);
		assertTrue(whiteKing.isValidMovement(4, 3), "King should be able to move one square up.");
	}

	@Test
	@DisplayName("White King valid move: one square down")
	void testWhiteKingMoveDown() {
		whiteKing.setPosition(4, 4);
		assertTrue(whiteKing.isValidMovement(4, 5), "King should be able to move one square down.");
	}

	@Test
	@DisplayName("White King valid move: one square left")
	void testWhiteKingMoveLeft() {
		whiteKing.setPosition(4, 4);
		assertTrue(whiteKing.isValidMovement(3, 4), "King should be able to move one square left.");
	}

	@Test
	@DisplayName("White King valid move: one square right")
	void testWhiteKingMoveRight() {
		whiteKing.setPosition(4, 4);
		assertTrue(whiteKing.isValidMovement(5, 4), "King should be able to move one square right.");
	}

	@Test
	@DisplayName("White King valid move: diagonal up-left")
	void testWhiteKingMoveDiagonalUpLeft() {
		whiteKing.setPosition(4, 4);
		assertTrue(whiteKing.isValidMovement(3, 3), "King should be able to move diagonally up-left.");
	}

	@Test
	@DisplayName("White King valid move: diagonal up-right")
	void testWhiteKingMoveDiagonalUpRight() {
		whiteKing.setPosition(4, 4);
		assertTrue(whiteKing.isValidMovement(5, 3), "King should be able to move diagonally up-right.");
	}

	@Test
	@DisplayName("White King valid move: diagonal down-left")
	void testWhiteKingMoveDiagonalDownLeft() {
		whiteKing.setPosition(4, 4);
		assertTrue(whiteKing.isValidMovement(3, 5), "King should be able to move diagonally down-left.");
	}

	@Test
	@DisplayName("White King valid move: diagonal down-right")
	void testWhiteKingMoveDiagonalDownRight() {
		whiteKing.setPosition(4, 4);
		assertTrue(whiteKing.isValidMovement(5, 5), "King should be able to move diagonally down-right.");
	}

	@Test
	@DisplayName("White King invalid move: more than one square away")
	void testWhiteKingInvalidMoveTwoSquares() {
		whiteKing.setPosition(4, 4);
		assertFalse(whiteKing.isValidMovement(4, 6), "King should not move two squares vertically.");
		assertFalse(whiteKing.isValidMovement(6, 4), "King should not move two squares horizontally.");
		assertFalse(whiteKing.isValidMovement(6, 6), "King should not move two squares diagonally.");
	}

	@Test
	@DisplayName("White King move to same position is invalid")
	void testWhiteKingMoveToSamePosition() {
		whiteKing.setPosition(4, 4);
		assertFalse(whiteKing.isValidMovement(4, 4), "King cannot move to the same square.");
	}

	@Test
	@DisplayName("White King move out of board is invalid")
	void testWhiteKingMoveOutOfBoard() {
		whiteKing.setPosition(4, 4);
		assertFalse(whiteKing.isValidMovement(4, -1), "Moving outside the board (negative row) should be invalid.");
		assertFalse(whiteKing.isValidMovement(-1, 4), "Moving outside the board (negative column) should be invalid.");
		assertFalse(whiteKing.isValidMovement(8, 4), "Moving outside the board (column too high) should be invalid.");
		assertFalse(whiteKing.isValidMovement(4, 8), "Moving outside the board (row too high) should be invalid.");
	}

	// ===================== Check-Tests für den weißen König =====================

	@Test
	@DisplayName("White King cannot move into check (attacked by enemy Rook)")
	void testWhiteKingCannotMoveIntoCheck_Rook() {
		board.getPieceList().clear();
		whiteKing.setPosition(4, 4);
		board.getPieceList().add(whiteKing);
		// Place an enemy rook so that it controls the 5th column.
		// Rook at (7,4) attacks horizontally, so square (5,4) is in its line.
		Rook enemyRook = new Rook(board, 7, 4, false);
		board.getPieceList().add(enemyRook);
		assertFalse(whiteKing.isValidMovement(5, 4),
			"King should not move into a square attacked by an enemy rook.");
	}

	@Test
	@DisplayName("White King cannot move into check (attacked by enemy Bishop)")
	void testWhiteKingCannotMoveIntoCheck_Bishop() {
		board.getPieceList().clear();
		whiteKing.setPosition(4, 4);
		board.getPieceList().add(whiteKing);
		// Place an enemy bishop such that square (3,3) is attacked.
		Bishop enemyBishop = new Bishop(board, 2, 2, false);
		board.getPieceList().add(enemyBishop);

		assertFalse(whiteKing.getLegalMoves(board).stream().anyMatch(move -> move.getNewColumn() == 3 && move.getNewRow() == 3),
			"King should not move into a square attacked by an enemy bishop.");
	}

	@Test
	@DisplayName("White King cannot move into check (attacked by enemy Knight)")
	void testWhiteKingCannotMoveIntoCheck_Knight() {
		board.getPieceList().clear();
		whiteKing.setPosition(4, 4);
		board.getPieceList().add(whiteKing);
		// Platzierung eines feindlichen Springers so, dass er (4,5) angreift.
		// Ein Springer an (6,4) greift u.a. (4,5) an.
		Knight enemyKnight = new Knight(board, 6, 4, false);
		board.getPieceList().add(enemyKnight);
		assertFalse(whiteKing.isValidMovement(4, 5),
			"King should not move into a square attacked by an enemy knight.");
	}

	@Test
	@DisplayName("White King in check by enemy Queen (diagonal)")
	void testWhiteKingCheckByQueenDiagonal() {
		board.getPieceList().clear();
		whiteKing.setPosition(4, 4);
		board.getPieceList().add(whiteKing);
		// Setze eine feindliche Dame diagonal ein, sodass (5,5) angegriffen wird.
		Queen enemyQueen = new Queen(board, 7, 7, false);
		board.getPieceList().add(enemyQueen);
		assertFalse(whiteKing.isValidMovement(5, 5),
			"King should not move into a square attacked by an enemy queen.");
	}

	@Test
	@DisplayName("White King escapes check only by capturing the checking piece")
	void testWhiteKingEscapeByCapturingCheckingPiece() {
		board.getPieceList().clear();
		board.loadPositionFromFen("6k1/8/8/3q1q2/3rKr2/8/8/8 w - - 0 1");

		assertEquals(1,  board.getPieceAt(4, 4).getLegalMoves(board).size(), "King should have exactly one legal move when in check.");
	}

	@Test
	@DisplayName("White King is checkmated (no legal moves)")
	void testWhiteKingCheckmate() {
		board.getPieceList().clear();
		// Platziere den weißen König in der Ecke.
		whiteKing.setPosition(0, 0);
		board.getPieceList().add(whiteKing);
		// Setze feindliche Figuren so, dass alle angrenzenden Felder angegriffen bzw. besetzt sind.
		Queen enemyQueen = new Queen(board, 1, 1, false);
		Rook enemyRook = new Rook(board, 0, 1, false);
		Bishop enemyBishop = new Bishop(board, 1, 0, false);
		// Optional: Ein Springer, der zusätzliche Felder abdeckt.
		Knight enemyKnight = new Knight(board, 2, 1, false);
		board.getPieceList().add(enemyQueen);
		board.getPieceList().add(enemyRook);
		board.getPieceList().add(enemyBishop);
		board.getPieceList().add(enemyKnight);
		// Der König sollte nun keine legalen Züge mehr haben.
		assertTrue(whiteKing.getLegalMoves(board).isEmpty(),
			"White king should have no legal moves when checkmated.");
	}

	// ===================== Grundlegende Bewegungs-Tests für den schwarzen König =====================

	@Test
	@DisplayName("Black King valid move: one square down")
	void testBlackKingMoveDown() {
		blackKing.setPosition(4, 0);
		// Bei schwarz ist angenommen, dass die Zeile 0 oben ist.
		assertTrue(blackKing.isValidMovement(4, 1), "Black king should be able to move one square down.");
	}

	@Test
	@DisplayName("Black King move out of board is invalid")
	void testBlackKingMoveOutOfBoard_Black() {
		blackKing.setPosition(4, 0);
		assertFalse(blackKing.isValidMovement(4, -1), "Moving outside the board should be invalid.");
		assertFalse(blackKing.isValidMovement(-1, 0), "Moving outside the board should be invalid.");
		assertFalse(blackKing.isValidMovement(8, 0), "Moving outside the board should be invalid.");
		assertFalse(blackKing.isValidMovement(4, 8), "Moving outside the board should be invalid.");
	}

	// ===================== Checkmate-Test für den schwarzen König =====================

	@Test
	@DisplayName("Black King is checkmated")
	void testBlackKingCheckmate() {
		board.getPieceList().clear();

		board.loadPositionFromFen("7k/8/8/3q4/4K3/5q2/8/8 w - - 0 1");

		// Der schwarze König sollte nun schachmatt sein.
		assertTrue(board.getPieceAt(4, 4).getLegalMoves(board).isEmpty(),
			"Black king should be checkmated with no legal moves.");
	}
}
