/*
 * Copyright (c) 2025 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.tests.engine;

import com.mlprograms.chess.game.engine.Move;
import com.mlprograms.chess.game.pieces.King;
import com.mlprograms.chess.game.pieces.Knight;
import com.mlprograms.chess.game.pieces.Pawn;
import com.mlprograms.chess.game.pieces.Piece;
import com.mlprograms.chess.game.ui.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MoveAlgebraicNotationTest {

	private Board board;

	@BeforeEach
	void setUp() {
		board = new Board();
		board.setWhiteAtBottom(true);
	}

	@Test
	void testPawnMove() {
		Piece pawn = new Pawn(board, 4, 1, true);
		Move move = new Move(board, pawn, 4, 3);
		assertEquals("e4", move.toAlgebraicNotation());
	}

	@Test
	void testPawnCapture() {
		Piece pawn = new Pawn(board, 4, 4, true);
		Piece capturedPiece = new Pawn(board, 5, 5, false);

		Move move = new Move(board, pawn, 5, 5, capturedPiece);
		assertEquals("exf6", move.toAlgebraicNotation());
	}

	@Test
	void testKingsideCastling() {
		Piece king = new King(board, 4, 0, true);
		Move move = new Move(board, king, 6, 0);
		assertEquals("O-O", move.toAlgebraicNotation());
	}

	@Test
	void testQueensideCastling() {
		Piece king = new King(board, 4, 0, true);
		Move move = new Move(board, king, 2, 0);
		assertEquals("O-O-O", move.toAlgebraicNotation());
	}

	@Test
	void testPawnPromotion() {
		Piece pawn = new Pawn(board, 0, 6, true);
		Move move = new Move(board, pawn, 0, 7);
		assertEquals("a8=Q", move.toAlgebraicNotation("Q"));
	}

	@Test
	void testPawnDoubleMove() {
		Piece pawn = new Pawn(board, 4, 1, true);
		Move move = new Move(board, pawn, 4, 3);
		assertEquals("e4", move.toAlgebraicNotation());
	}

	@Test
	void testKnightMove() {
		Piece knight = new Knight(board, 1, 0, true);
		Move move = new Move(board, knight, 2, 2);
		assertEquals("Nc3", move.toAlgebraicNotation());
	}

	@Test
	void testKnightCapture() {
		Piece knight = new Knight(board, 1, 0, true);
		Piece capturedPiece = new Pawn(board, 2, 2, false);
		Move move = new Move(board, knight, 2, 2, capturedPiece);
		assertEquals("Nxc3", move.toAlgebraicNotation());
	}

}
