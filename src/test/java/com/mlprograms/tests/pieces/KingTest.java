/*
 * Copyright (c) 2025 Max Lemberg. This file is part of ChessMax.
 * Licensed under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.tests.pieces;

import com.mlprograms.chess.game.engine.Move;
import com.mlprograms.chess.game.pieces.*;
import com.mlprograms.chess.game.ui.Board;
import com.mlprograms.chess.human.Human;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KingTest {

	private Board board;

	// ===================== Basic Movement Tests for White King =====================
	// We load positions using FEN strings rather than manually placing pieces.

	@Test
	@DisplayName("White King valid move: one square up")
	void testWhiteKingMoveUp() {
		// FEN: White King on e4; all other squares empty.
		boardLoad("8/8/8/8/4K3/8/8/8 w - - 0 1");
		// The white king is at e4 (column 4, row 4). Moving one square up means moving to e5,
		// which corresponds to (4,3) in our 0-indexed board with row 0 at the top.
		King whiteKing = (King) board.getPieceAt(4, 4);
		assertTrue(whiteKing.isValidMovement(4, 3), "King should be able to move one square up.");
	}

	@Test
	@DisplayName("White King valid move: one square down")
	void testWhiteKingMoveDown() {
		boardLoad("8/8/8/8/4K3/8/8/8 w - - 0 1");
		King whiteKing = (King) board.getPieceAt(4, 4);
		// Moving one square down from e4 to e3 corresponds to (4,5).
		assertTrue(whiteKing.isValidMovement(4, 5), "King should be able to move one square down.");
	}

	@Test
	@DisplayName("White King valid move: one square left")
	void testWhiteKingMoveLeft() {
		boardLoad("8/8/8/8/4K3/8/8/8 w - - 0 1");
		King whiteKing = (King) board.getPieceAt(4, 4);
		// Moving left from e4 to d4 corresponds to (3,4).
		assertTrue(whiteKing.isValidMovement(3, 4), "King should be able to move one square left.");
	}

	@Test
	@DisplayName("White King valid move: one square right")
	void testWhiteKingMoveRight() {
		boardLoad("8/8/8/8/4K3/8/8/8 w - - 0 1");
		King whiteKing = (King) board.getPieceAt(4, 4);
		// Moving right from e4 to f4 corresponds to (5,4).
		assertTrue(whiteKing.isValidMovement(5, 4), "King should be able to move one square right.");
	}

	@Test
	@DisplayName("White King valid move: diagonal up-left")
	void testWhiteKingMoveDiagonalUpLeft() {
		boardLoad("8/8/8/8/4K3/8/8/8 w - - 0 1");
		King whiteKing = (King) board.getPieceAt(4, 4);
		// Diagonal up-left from e4 to d5 corresponds to (3,3).
		assertTrue(whiteKing.isValidMovement(3, 3), "King should be able to move diagonally up-left.");
	}

	@Test
	@DisplayName("White King valid move: diagonal up-right")
	void testWhiteKingMoveDiagonalUpRight() {
		boardLoad("8/8/8/8/4K3/8/8/8 w - - 0 1");
		King whiteKing = (King) board.getPieceAt(4, 4);
		// Diagonal up-right from e4 to f5 corresponds to (5,3).
		assertTrue(whiteKing.isValidMovement(5, 3), "King should be able to move diagonally up-right.");
	}

	@Test
	@DisplayName("White King valid move: diagonal down-left")
	void testWhiteKingMoveDiagonalDownLeft() {
		boardLoad("8/8/8/8/4K3/8/8/8 w - - 0 1");
		King whiteKing = (King) board.getPieceAt(4, 4);
		// Diagonal down-left from e4 to d3 corresponds to (3,5).
		assertTrue(whiteKing.isValidMovement(3, 5), "King should be able to move diagonally down-left.");
	}

	@Test
	@DisplayName("White King valid move: diagonal down-right")
	void testWhiteKingMoveDiagonalDownRight() {
		boardLoad("8/8/8/8/4K3/8/8/8 w - - 0 1");
		King whiteKing = (King) board.getPieceAt(4, 4);
		// Diagonal down-right from e4 to f3 corresponds to (5,5).
		assertTrue(whiteKing.isValidMovement(5, 5), "King should be able to move diagonally down-right.");
	}

	@Test
	@DisplayName("White King invalid move: more than one square away")
	void testWhiteKingInvalidMoveTwoSquares() {
		boardLoad("8/8/8/8/4K3/8/8/8 w - - 0 1");
		King whiteKing = (King) board.getPieceAt(4, 4);
		// Moves more than one square away should be invalid.
		assertFalse(whiteKing.isValidMovement(4, 6), "King should not move two squares vertically.");
		assertFalse(whiteKing.isValidMovement(6, 4), "King should not move two squares horizontally.");
		assertFalse(whiteKing.isValidMovement(6, 6), "King should not move two squares diagonally.");
	}

	@Test
	@DisplayName("White King move to same position is invalid")
	void testWhiteKingMoveToSamePosition() {
		boardLoad("8/8/8/8/4K3/8/8/8 w - - 0 1");
		King whiteKing = (King) board.getPieceAt(4, 4);
		assertFalse(whiteKing.isValidMovement(4, 4), "King cannot move to the same square.");
	}

	@Test
	@DisplayName("White King move out of board is invalid")
	void testWhiteKingMoveOutOfBoard() {
		boardLoad("8/8/8/8/4K3/8/8/8 w - - 0 1");
		King whiteKing = (King) board.getPieceAt(4, 4);
		assertFalse(whiteKing.isValidMovement(4, -1), "Moving outside the board (negative row) should be invalid.");
		assertFalse(whiteKing.isValidMovement(-1, 4), "Moving outside the board (negative column) should be invalid.");
		assertFalse(whiteKing.isValidMovement(8, 4), "Moving outside the board (column too high) should be invalid.");
		assertFalse(whiteKing.isValidMovement(4, 8), "Moving outside the board (row too high) should be invalid.");
	}

	// ===================== Check Tests for White King =====================

	@Test
	@DisplayName("White King cannot move into check (attacked by enemy Rook)")
	void testWhiteKingCannotMoveIntoCheck_Rook() {
		// FEN: White King on e4 and enemy rook on h4 attacking horizontally.
		boardLoad("8/8/8/8/4K2r/8/8/8 w - - 0 1");
		King whiteKing = (King) board.getPieceAt(4, 4);
		// Moving to f4 (5,4) would put the king into the rook's line.
		assertFalse(whiteKing.isValidMovement(5, 4),
			"King should not move into a square attacked by an enemy rook.");
	}

	@Test
	@DisplayName("White King cannot move into check (attacked by enemy Bishop)")
	void testWhiteKingCannotMoveIntoCheck_Bishop() {
		// FEN: White King on e4 and enemy bishop on a8 attacking along the diagonal.
		boardLoad("b7/8/8/8/4K3/8/8/8 w - - 0 1");
		// Square d5 (3,3) is along the diagonal from a8 to e4 and should be attacked.
		// We check that the king's legal moves do not include (3,3).
		assertFalse(board.getPieceAt(4, 4).getLegalMoves(board).stream()
			            .anyMatch(move -> move.getNewColumn() == 3 && move.getNewRow() == 3),
			"King should not move into a square attacked by an enemy bishop.");

		assertFalse(board.getPieceAt(4, 4).getLegalMoves(board).stream()
			            .anyMatch(move -> move.getNewColumn() == 4 && move.getNewRow() == 4),
			"King should not move into a square attacked by an enemy bishop.");
	}

	@Test
	@DisplayName("White King cannot move into check (attacked by enemy Knight)")
	void testWhiteKingCannotMoveIntoCheck_Knight() {
		// FEN: White King on e4 and enemy knight on c4 attacking (4,5).
		boardLoad("8/8/8/8/2n1K3/8/8/8 w - - 0 1");
		King whiteKing = (King) board.getPieceAt(4, 4);
		// The enemy knight at c4 attacks square e3 (4,5); thus, the king should not move there.
		assertFalse(whiteKing.isValidMovement(4, 5),
			"King should not move into a square attacked by an enemy knight.");
	}

	@Test
	@DisplayName("White King in check by enemy Queen (diagonal)")
	void testWhiteKingCheckByQueenDiagonal() {
		// FEN: White King on e4 and enemy queen on h8 attacking diagonally.
		boardLoad("8/8/8/8/4K3/8/8/7q w - - 0 1");
		King whiteKing = (King) board.getPieceAt(4, 4);
		// The enemy queen on h8 attacks the diagonal including f3, e2, etc.
		// Here we test that the king cannot move to f3 (5,5) because it remains in check.
		assertFalse(whiteKing.isValidMovement(5, 5),
			"King should not move into a square attacked by an enemy queen.");
	}

	@Test
	@DisplayName("White King escapes check only by capturing the checking piece")
	void testWhiteKingEscapeByCapturingCheckingPiece() {
		// FEN: A complex position where the white king on e4 is in check from an enemy rook on f4,
		// and all adjacent squares except f4 are covered by enemy pieces.
		boardLoad("6k1/8/8/3q1q2/3rKr2/8/8/8 w - - 0 1");
		// The white king is at e4. The only legal move should be capturing the enemy rook on f4.
		List<Move> legalMoves = board.getPieceAt(4, 4).getLegalMoves(board);
		assertEquals(1, legalMoves.size(), "King should have exactly one legal move when in check.");

		Move move = legalMoves.getFirst();
		assertEquals(4, move.getNewColumn(), "The only legal move should be to column 4.");
		assertEquals(5, move.getNewRow(), "The only legal move should be to row 5 (capturing the checking piece).");
	}

	@Test
	@DisplayName("White King is checkmated (no legal moves)")
	void testWhiteKingCheckmate() {
		// FEN: White King is in the top-left corner (a8) and is checkmated by enemy pieces.
		boardLoad("Kb6/rqn5/8/8/8/8/8/8 w - - 0 1");
		// The white king is at a8 (0,0) and has no legal moves.
		King whiteKing = (King) board.getPieceAt(0, 0);
		assertTrue(whiteKing.getLegalMoves(board).isEmpty(),
			"White king should have no legal moves when checkmated.");
	}

	// ===================== Basic Movement Tests for Black King =====================

	@Test
	@DisplayName("Black King valid move: one square down")
	void testBlackKingMoveDown() {
		// FEN: Black King on e8; all other squares empty.
		boardLoad("4k3/8/8/8/8/8/8/8 b - - 0 1");
		// The black king is at e8 (4,0) in our 0-indexed board (row 0 is top).
		King blackKing = (King) board.getPieceAt(4, 0);
		// Moving one square down (from e8 to e7) corresponds to (4,1).
		assertTrue(blackKing.isValidMovement(4, 1), "Black king should be able to move one square down.");
	}

	@Test
	@DisplayName("Black King move out of board is invalid")
	void testBlackKingMoveOutOfBoard_Black() {
		boardLoad("4k3/8/8/8/8/8/8/8 b - - 0 1");
		King blackKing = (King) board.getPieceAt(4, 0);
		assertFalse(blackKing.isValidMovement(4, -1), "Moving outside the board should be invalid.");
		assertFalse(blackKing.isValidMovement(-1, 0), "Moving outside the board should be invalid.");
		assertFalse(blackKing.isValidMovement(8, 0), "Moving outside the board should be invalid.");
		assertFalse(blackKing.isValidMovement(4, 8), "Moving outside the board should be invalid.");
	}

	// ===================== Checkmate Test for Black King =====================

	@Test
	@DisplayName("Black King is checkmated")
	void testBlackKingCheckmate() {
		// FEN: Black King is in the top-left corner (a8) and is checkmated by white pieces.
		boardLoad("kB6/RQN5/8/8/8/8/8/8 b - - 0 1");
		// The black king is at a8 (0,0) and should have no legal moves.
		assertTrue(board.getPieceAt(0, 0).getLegalMoves(board).isEmpty(),
			"Black king should be checkmated with no legal moves.");
	}

	// Helper method to load board position from FEN
	private void boardLoad(String fen) {
		board = new Board(new Human(), new Human(), true);
		board.loadPositionFromFen(fen);
	}
}
