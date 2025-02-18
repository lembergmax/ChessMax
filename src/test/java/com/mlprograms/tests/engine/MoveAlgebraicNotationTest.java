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
		Move move = new Move(board, pawn, 4, 2);
		assertEquals("e6", move.toAlgebraicNotation());
	}

	@Test
	void testPawnCapture() {
		Piece pawn = new Pawn(board, 4, 4, true);
		Piece capturedPiece = new Pawn(board, 5, 5, false);

		Move move = new Move(board, pawn, 5, 5, capturedPiece);
		assertEquals("exf3", move.toAlgebraicNotation());
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
		assertEquals("a1=Q", move.toAlgebraicNotation("Q"));
	}

	@Test
	void testPawnDoubleMove() {
		Piece pawn = new Pawn(board, 4, 1, true);
		Move move = new Move(board, pawn, 4, 3);
		assertEquals("e5", move.toAlgebraicNotation());
	}

	@Test
	void testKnightMove() {
		Piece knight = new Knight(board, 1, 0, true);
		Move move = new Move(board, knight, 2, 2);
		assertEquals("Nc6", move.toAlgebraicNotation());
	}

	@Test
	void testKnightCapture() {
		Piece knight = new Knight(board, 1, 0, true);
		Piece capturedPiece = new Pawn(board, 2, 2, false);
		Move move = new Move(board, knight, 2, 2, capturedPiece);
		assertEquals("Nxc6", move.toAlgebraicNotation());
	}

	@Test
	void testPawnMove_Black() {
		board.setWhiteAtBottom(false);
		Piece pawn = new Pawn(board, 4, 6, false);
		Move move = new Move(board, pawn, 4, 5);
		assertEquals("d6", move.toAlgebraicNotation());
	}

	@Test
	void testPawnCapture_Black() {
		board.setWhiteAtBottom(false);
		Piece pawn = new Pawn(board, 4, 3, false);
		Piece capturedPiece = new Pawn(board, 3, 2, true);
		Move move = new Move(board, pawn, 3, 2, capturedPiece);
		assertEquals("dxe3", move.toAlgebraicNotation());
	}

	@Test
	void testKingsideCastling_Black() {
		board.setWhiteAtBottom(false);
		Piece king = new King(board, 4, 7, false);
		Move move = new Move(board, king, 6, 7);
		assertEquals("O-O", move.toAlgebraicNotation());
	}

	@Test
	void testQueensideCastling_Black() {
		board.setWhiteAtBottom(false);
		Piece king = new King(board, 4, 7, false);
		Move move = new Move(board, king, 2, 7);
		assertEquals("O-O-O", move.toAlgebraicNotation());
	}

	@Test
	void testPawnPromotion_Black() {
		board.setWhiteAtBottom(false);
		Piece pawn = new Pawn(board, 0, 1, false);
		Move move = new Move(board, pawn, 0, 0);
		assertEquals("h1=Q", move.toAlgebraicNotation("Q"));
	}

	@Test
	void testPawnDoubleMove_Black() {
		board.setWhiteAtBottom(false);
		Piece pawn = new Pawn(board, 4, 6, false);
		Move move = new Move(board, pawn, 4, 4);
		assertEquals("d5", move.toAlgebraicNotation());
	}

	@Test
	void testKnightMove_Black() {
		board.setWhiteAtBottom(false);
		Piece knight = new Knight(board, 6, 7, false);
		Move move = new Move(board, knight, 5, 5);
		assertEquals("Nc6", move.toAlgebraicNotation());
	}

	@Test
	void testKnightCapture_Black() {
		board.setWhiteAtBottom(false);
		Piece knight = new Knight(board, 6, 7, false);
		Piece capturedPiece = new Pawn(board, 5, 5, true);
		Move move = new Move(board, knight, 5, 5, capturedPiece);
		assertEquals("Nxc6", move.toAlgebraicNotation());
	}
}
