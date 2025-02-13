/*
 * Copyright (c) 2025 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.tests.pieces;

import com.mlprograms.chess.game.pieces.Queen;
import com.mlprograms.chess.game.ui.Board;
import com.mlprograms.chess.human.Human;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueenTest {

	private Board board;
	private Queen whiteQueen;
	private Queen blackQueen;

	@BeforeEach
	void setUp() {
		board = new Board(new Human(), new Human(), true);
		// Clear the board's piece list.
		board.getPieceList().clear();
		// Initialize a white queen at (3,0) and a black queen at (3,7).
		whiteQueen = new Queen(board, 3, 0, true);
		blackQueen = new Queen(board, 3, 7, false);
		board.getPieceList().add(whiteQueen);
		board.getPieceList().add(blackQueen);
	}

	// ===================== White Queen Tests =====================

	@Test
	@DisplayName("White Queen valid horizontal move to right")
	void testWhiteQueenValidHorizontalMoveRight() {
		// From (3,0) to (7,0) is a valid horizontal move.
		assertTrue(whiteQueen.isValidMovement(7, 0));
	}

	@Test
	@DisplayName("White Queen valid horizontal move to left")
	void testWhiteQueenValidHorizontalMoveLeft() {
		// From (3,0) to (0,0) is a valid horizontal move.
		assertTrue(whiteQueen.isValidMovement(0, 0));
	}

	@Test
	@DisplayName("White Queen valid vertical move downward")
	void testWhiteQueenValidVerticalMoveDown() {
		// Reposition the queen to a central location.
		whiteQueen.setPosition(4, 4);
		// Move vertically downward from (4,4) to (4,7).
		assertTrue(whiteQueen.isValidMovement(4, 7));
	}

	@Test
	@DisplayName("White Queen valid vertical move upward")
	void testWhiteQueenValidVerticalMoveUp() {
		whiteQueen.setPosition(4, 4);
		// Move vertically upward from (4,4) to (4,0).
		assertTrue(whiteQueen.isValidMovement(4, 0));
	}

	@Test
	@DisplayName("White Queen valid diagonal move: down right")
	void testWhiteQueenValidDiagonalDownRight() {
		whiteQueen.setPosition(4, 4);
		// Diagonal move from (4,4) to (7,7) is valid.
		assertTrue(whiteQueen.isValidMovement(7, 7));
	}

	@Test
	@DisplayName("White Queen valid diagonal move: down left")
	void testWhiteQueenValidDiagonalDownLeft() {
		whiteQueen.setPosition(4, 4);
		// Diagonal move from (4,4) to (1,7) is valid.
		assertTrue(whiteQueen.isValidMovement(1, 7));
	}

	@Test
	@DisplayName("White Queen valid diagonal move: up right")
	void testWhiteQueenValidDiagonalUpRight() {
		whiteQueen.setPosition(4, 4);
		// Diagonal move from (4,4) to (7,1) is valid.
		assertTrue(whiteQueen.isValidMovement(7, 1));
	}

	@Test
	@DisplayName("White Queen valid diagonal move: up left")
	void testWhiteQueenValidDiagonalUpLeft() {
		whiteQueen.setPosition(4, 4);
		// Diagonal move from (4,4) to (0,0) is valid.
		assertTrue(whiteQueen.isValidMovement(0, 0));
	}

	@Test
	@DisplayName("White Queen invalid non-straight move")
	void testWhiteQueenInvalidNonStraightMove() {
		whiteQueen.setPosition(4, 4);
		// A move that is neither horizontal, vertical, nor diagonal is invalid.
		// For example, (4,4) to (5,6) does not lie on a straight line.
		assertFalse(whiteQueen.isValidMovement(5, 6));
	}

	@Test
	@DisplayName("White Queen move to same position is invalid")
	void testWhiteQueenMoveToSamePosition() {
		whiteQueen.setPosition(4, 4);
		// Moving to the same square is not allowed.
		assertFalse(whiteQueen.isValidMovement(4, 4));
	}

	@Test
	@DisplayName("White Queen move out of board is invalid")
	void testWhiteQueenMoveOutOfBoard() {
		whiteQueen.setPosition(4, 4);
		// Attempting to move to an out-of-bound square (e.g., (8,8)) should be invalid.
		assertFalse(whiteQueen.isValidMovement(8, 8));
	}

	@Test
	@DisplayName("White Queen can capture an opponent piece")
	void testWhiteQueenCaptureOpponentPiece() {
		whiteQueen.setPosition(4, 4);
		// Place an enemy piece at (4,7) along the vertical path.
		Queen enemyQueen = new Queen(board, 4, 7, false);
		board.getPieceList().add(enemyQueen);
		// The queen should be able to move to (4,7) capturing the enemy.
		assertTrue(whiteQueen.isValidMovement(4, 7));
	}

	@Test
	@DisplayName("White Queen cannot capture its own piece")
	void testWhiteQueenCannotCaptureOwnPiece() {
		whiteQueen.setPosition(4, 4);
		// Place a friendly piece at (4,7).
		Queen friendlyQueen = new Queen(board, 4, 7, true);
		board.getPieceList().add(friendlyQueen);
		// The queen should not be allowed to move to (4,7) because the square is occupied by a friendly piece.
		assertFalse(whiteQueen.isValidMovement(4, 7));
	}

	@Test
	@DisplayName("White Queen move blocked by friendly piece in path")
	void testWhiteQueenBlockedByFriendlyPiece() {
		whiteQueen.setPosition(4, 4);
		// Place a friendly piece directly in the horizontal path at (6,4).
		Queen blockingQueen = new Queen(board, 6, 4, true);
		board.getPieceList().add(blockingQueen);
		// The queen cannot move horizontally to (7,4) because the path is blocked.
		assertFalse(whiteQueen.isValidMovement(7, 4));
		// Also, moving directly into the blocking square is invalid.
		assertFalse(whiteQueen.isValidMovement(6, 4));
	}

	@Test
	@DisplayName("White Queen move blocked by enemy piece in path: capture allowed only at blocking square")
	void testWhiteQueenBlockedByEnemyPiece() {
		whiteQueen.setPosition(4, 4);
		// Place an enemy piece in the diagonal path at (5,5).
		Queen blockingEnemy = new Queen(board, 5, 5, false);
		board.getPieceList().add(blockingEnemy);
		// The queen can capture the enemy piece at (5,5).
		assertTrue(whiteQueen.isValidMovement(5, 5));
		// However, the queen cannot move beyond the enemy piece along the same diagonal.
		assertFalse(whiteQueen.isValidMovement(6, 6));
	}

	@Test
	@DisplayName("White Queen legal moves from center on an empty board")
	void testWhiteQueenLegalMovesFromCenterEmptyBoard() {
		// Clear the board and add only the white queen at the center (4,4).
		board.getPieceList().clear();
		whiteQueen.setPosition(4, 4);
		board.getPieceList().add(whiteQueen);
		/* Expected legal moves from (4,4) on an empty board:
		 * Horizontal left: (3,4), (2,4), (1,4), (0,4) => 4 moves
		 * Horizontal right: (5,4), (6,4), (7,4) => 3 moves
		 * Vertical up: (4,3), (4,2), (4,1), (4,0) => 4 moves
		 * Vertical down: (4,5), (4,6), (4,7) => 3 moves
		 * Diagonal up-left: (3,3), (2,2), (1,1), (0,0) => 4 moves
		 * Diagonal up-right: (5,3), (6,2), (7,1) => 3 moves
		 * Diagonal down-left: (3,5), (2,6), (1,7) => 3 moves
		 * Diagonal down-right: (5,5), (6,6), (7,7) => 3 moves
		 * Total expected moves = 4+3+4+3+4+3+3+3 = 27.
		 */
		assertEquals(27, whiteQueen.getLegalMoves(board).size());
	}

	// ===================== Black Queen Tests =====================

	@Test
	@DisplayName("Black Queen valid horizontal move to right")
	void testBlackQueenValidHorizontalMoveRight() {
		// From (3,7) to (7,7) is a valid horizontal move.
		assertTrue(blackQueen.isValidMovement(7, 7));
	}

	@Test
	@DisplayName("Black Queen valid horizontal move to left")
	void testBlackQueenValidHorizontalMoveLeft() {
		// From (3,7) to (0,7) is a valid horizontal move.
		assertTrue(blackQueen.isValidMovement(0, 7));
	}

	@Test
	@DisplayName("Black Queen valid vertical move upward")
	void testBlackQueenValidVerticalMoveUp() {
		// Reposition the queen to a central location.
		blackQueen.setPosition(4, 4);
		// Move vertically upward from (4,4) to (4,0).
		assertTrue(blackQueen.isValidMovement(4, 0));
	}

	@Test
	@DisplayName("Black Queen valid vertical move downward")
	void testBlackQueenValidVerticalMoveDown() {
		blackQueen.setPosition(4, 4);
		// Move vertically downward from (4,4) to (4,7).
		assertTrue(blackQueen.isValidMovement(4, 7));
	}

	@Test
	@DisplayName("Black Queen valid diagonal move: down right")
	void testBlackQueenValidDiagonalDownRight() {
		blackQueen.setPosition(4, 4);
		// Diagonal move from (4,4) to (7,7) is valid.
		assertTrue(blackQueen.isValidMovement(7, 7));
	}

	@Test
	@DisplayName("Black Queen valid diagonal move: down left")
	void testBlackQueenValidDiagonalDownLeft() {
		blackQueen.setPosition(4, 4);
		// Diagonal move from (4,4) to (1,7) is valid.
		assertTrue(blackQueen.isValidMovement(1, 7));
	}

	@Test
	@DisplayName("Black Queen valid diagonal move: up right")
	void testBlackQueenValidDiagonalUpRight() {
		blackQueen.setPosition(4, 4);
		// Diagonal move from (4,4) to (7,1) is valid.
		assertTrue(blackQueen.isValidMovement(7, 1));
	}

	@Test
	@DisplayName("Black Queen valid diagonal move: up left")
	void testBlackQueenValidDiagonalUpLeft() {
		blackQueen.setPosition(4, 4);
		// Diagonal move from (4,4) to (0,0) is valid.
		assertTrue(blackQueen.isValidMovement(0, 0));
	}

	@Test
	@DisplayName("Black Queen invalid non-straight move")
	void testBlackQueenInvalidNonStraightMove() {
		blackQueen.setPosition(4, 4);
		// A move that is not horizontal, vertical, or diagonal is invalid.
		assertFalse(blackQueen.isValidMovement(5, 6));
	}

	@Test
	@DisplayName("Black Queen move to same position is invalid")
	void testBlackQueenMoveToSamePosition() {
		blackQueen.setPosition(4, 4);
		// Moving to the same square is not allowed.
		assertFalse(blackQueen.isValidMovement(4, 4));
	}

	@Test
	@DisplayName("Black Queen move out of board is invalid")
	void testBlackQueenMoveOutOfBoard() {
		blackQueen.setPosition(4, 4);
		// Moving to a square outside the board (e.g., (8,8)) should be invalid.
		assertFalse(blackQueen.isValidMovement(8, 8));
	}

	@Test
	@DisplayName("Black Queen can capture an opponent piece")
	void testBlackQueenCaptureOpponentPiece() {
		blackQueen.setPosition(4, 4);
		// Place an enemy piece (white queen) at (4,0) along the vertical path.
		Queen enemyQueen = new Queen(board, 4, 0, true);
		board.getPieceList().add(enemyQueen);
		// The queen should be able to move to (4,0) to capture the enemy.
		assertTrue(blackQueen.isValidMovement(4, 0));
	}

	@Test
	@DisplayName("Black Queen cannot capture its own piece")
	void testBlackQueenCannotCaptureOwnPiece() {
		blackQueen.setPosition(4, 4);
		// Place a friendly piece (black queen) at (4,0).
		Queen friendlyQueen = new Queen(board, 4, 0, false);
		board.getPieceList().add(friendlyQueen);
		// The queen should not be allowed to move to (4,0) because it is occupied by a friendly piece.
		assertFalse(blackQueen.isValidMovement(4, 0));
	}

	@Test
	@DisplayName("Black Queen move blocked by friendly piece in path")
	void testBlackQueenBlockedByFriendlyPiece() {
		blackQueen.setPosition(4, 4);
		// Place a friendly piece in the horizontal path at (6,4).
		Queen blockingQueen = new Queen(board, 6, 4, false);
		board.getPieceList().add(blockingQueen);
		// The queen cannot move horizontally to (7,4) because the path is blocked.
		assertFalse(blackQueen.isValidMovement(7, 4));
		// Also, moving directly into the blocking square is invalid.
		assertFalse(blackQueen.isValidMovement(6, 4));
	}

	@Test
	@DisplayName("Black Queen move blocked by enemy piece in path: capture allowed only at blocking square")
	void testBlackQueenBlockedByEnemyPiece() {
		blackQueen.setPosition(4, 4);
		// Place an enemy piece in the diagonal path at (5,5).
		Queen blockingEnemy = new Queen(board, 5, 5, true);
		board.getPieceList().add(blockingEnemy);
		// The queen can capture the enemy at (5,5).
		assertTrue(blackQueen.isValidMovement(5, 5));
		// However, it cannot move beyond the enemy piece along the same diagonal.
		assertFalse(blackQueen.isValidMovement(6, 6));
	}

	@Test
	@DisplayName("Black Queen legal moves from center on an empty board")
	void testBlackQueenLegalMovesFromCenterEmptyBoard() {
		// Clear the board and place only the black queen at the center (4,4).
		board.getPieceList().clear();
		blackQueen.setPosition(4, 4);
		board.getPieceList().add(blackQueen);
		/* Expected legal moves from (4,4) on an empty board:
		 * Horizontal left: 4 moves: (3,4), (2,4), (1,4), (0,4)
		 * Horizontal right: 3 moves: (5,4), (6,4), (7,4)
		 * Vertical up: 4 moves: (4,3), (4,2), (4,1), (4,0)
		 * Vertical down: 3 moves: (4,5), (4,6), (4,7)
		 * Diagonal up-left: 4 moves: (3,3), (2,2), (1,1), (0,0)
		 * Diagonal up-right: 3 moves: (5,3), (6,2), (7,1)
		 * Diagonal down-left: 3 moves: (3,5), (2,6), (1,7)
		 * Diagonal down-right: 3 moves: (5,5), (6,6), (7,7)
		 * Total expected moves = 4+3+4+3+4+3+3+3 = 27.
		 */
		assertEquals(27, blackQueen.getLegalMoves(board).size());
	}
}
