/*
 * Copyright (c) 2025 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.tests.engine;

import com.mlprograms.chess.game.engine.GameEnding;
import com.mlprograms.chess.game.engine.Move;
import com.mlprograms.chess.game.pieces.*;
import com.mlprograms.chess.game.ui.Board;
import com.mlprograms.chess.human.Human;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MoveAlgebraicNotationTest {

	private Board board;

	@BeforeEach
	public void setUp() {
		board = new Board(new Human(), new Human());
		board.setWhiteAtBottom(true);
	}

	// Tests for normal moves (without capture) and capture moves

	@Test
	public void testPawnMoveNoCapture() {
		// Using the real Pawn constructor: (Board, column, row, isWhite)
		Pawn pawn = new Pawn(board, 4, 6, true);
		Move move = new Move(board, pawn, 4, 4, null);
		assertEquals("e4", move.toAlgebraicNotation());
	}

	@Test
	public void testPawnCapture() {
		Pawn pawn = new Pawn(board, 4, 6, true);
		Pawn enemyPawn = new Pawn(board, 3, 5, false);
		Move move = new Move(board, pawn, 3, 5, enemyPawn);
		assertEquals("exd3", move.toAlgebraicNotation());
	}

	@Test
	public void testKnightMoveNoCapture() {
		Knight knight = new Knight(board, 6, 7, true); // g1: column 6, row 7
		Move move = new Move(board, knight, 5, 5, null); // f3: column 5, row 5
		assertEquals("Nf3", move.toAlgebraicNotation());
	}

	@Test
	public void testKnightCapture() {
		Knight knight = new Knight(board, 1, 7, true); // b1: column 1, row 7
		Pawn enemyPawn = new Pawn(board, 2, 5, false);   // c3: column 2, row 5
		Move move = new Move(board, knight, 2, 5, enemyPawn);
		assertEquals("Nxc3", move.toAlgebraicNotation());
	}

	@Test
	public void testBishopMoveNoCapture() {
		Bishop bishop = new Bishop(board, 2, 7, true); // c1: column 2, row 7
		Move move = new Move(board, bishop, 5, 4, null); // f4: column 5, row 4
		assertEquals("Bf4", move.toAlgebraicNotation());
	}

	@Test
	public void testRookMoveNoCapture() {
		Rook rook = new Rook(board, 0, 7, true); // a1: column 0, row 7
		Move move = new Move(board, rook, 0, 4, null); // a4: column 0, row 4
		assertEquals("Ra4", move.toAlgebraicNotation());
	}

	@Test
	public void testRookCapture() {
		Rook rook = new Rook(board, 0, 7, true); // a1
		Pawn enemyPawn = new Pawn(board, 0, 0, false); // a8: column 0, row 0
		Move move = new Move(board, rook, 0, 0, enemyPawn);
		assertEquals("Rxa8", move.toAlgebraicNotation());
	}

	@Test
	public void testQueenMoveNoCapture() {
		Queen queen = new Queen(board, 3, 7, true); // d1: column 3, row 7
		Move move = new Move(board, queen, 7, 3, null); // h5: column 7, row 3
		assertEquals("Qh5", move.toAlgebraicNotation());
	}

	@Test
	public void testQueenCapture() {
		Queen queen = new Queen(board, 3, 7, true); // d1
		Pawn enemyPawn = new Pawn(board, 7, 3, false); // h5
		Move move = new Move(board, queen, 7, 3, enemyPawn);
		assertEquals("Qxh5", move.toAlgebraicNotation());
	}

	@Test
	public void testKingMoveNoCastling() {
		King king = new King(board, 4, 7, true); // e1: column 4, row 7
		Move move = new Move(board, king, 4, 6, null); // e2: column 4, row 6
		assertEquals("Ke2", move.toAlgebraicNotation());
	}

	@Test
	public void testKingCapture() {
		King king = new King(board, 4, 7, true); // e1
		Pawn enemyPawn = new Pawn(board, 4, 6, false); // e2
		Move move = new Move(board, king, 4, 6, enemyPawn);
		assertEquals("Kxe2", move.toAlgebraicNotation());
	}

	// Tests for special cases: castling, checkmate annotation, board orientation

	@Test
	public void testKingsideCastling() {
		King king = new King(board, 4, 7, true); // e1
		Move move = new Move(board, king, 6, 7, null); // g1
		assertEquals("O-O", move.toAlgebraicNotation());
	}

	@Test
	public void testQueensideCastling() {
		King king = new King(board, 4, 7, true); // e1
		Move move = new Move(board, king, 2, 7, null); // c1
		assertEquals("O-O-O", move.toAlgebraicNotation());
	}

	@Test
	public void testCheckmateAnnotation() {
		board.setGameEnding(GameEnding.CHECKMATE);
		Pawn pawn = new Pawn(board, 4, 6, true);
		Move move = new Move(board, pawn, 4, 4, null);
		assertEquals("e4#", move.toAlgebraicNotation());
	}

	@Test
	public void testBoardOrientationFalse() {
		board.setWhiteAtBottom(false);
		Pawn pawn = new Pawn(board, 4, 1, true); // e2: column 4, row 1 (1+1 = 2)
		Move move = new Move(board, pawn, 4, 3, null); // e4: column 4, row 3 (3+1 = 4)
		assertEquals("e4", move.toAlgebraicNotation());
	}
}