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
		// Standardmäßig: Weiß am Boden → interne Zeilenumrechnung: rank = getRows() - row.
		board = new Board(new Human(), new Human());
		board.setWhiteAtBottom(true);
		// Angenommen, ein Standardbrett hat 8 Reihen und 8 Spalten.
	}

	@Test
	public void testPawnMoveNoCapture() {
		// Bei whiteAtBottom = true entspricht row 6 der algebraischen Rangzahl 2 (8-6 = 2)
		// und row 4 der Rangzahl 4.
		Pawn pawn = new Pawn(board, 4, 6, true);  // entspricht e2
		Move move = new Move(board, pawn, 4, 4, null); // entspricht e4
		assertEquals("e4", move.toAlgebraicNotation());
	}

	@Test
	public void testPawnCapture() {
		Pawn pawn = new Pawn(board, 4, 6, true);  // e2
		// Gegnerischer Bauer auf (3,5): entspricht d3 (8-5 = 3)
		Pawn enemyPawn = new Pawn(board, 3, 5, false);
		Move move = new Move(board, pawn, 3, 5, enemyPawn);
		// Bauernschlag: Ursprungsfile 'e' (4 → e) ergibt "exd3"
		assertEquals("exd3", move.toAlgebraicNotation());
	}

	@Test
	public void testKnightMoveNoCapture() {
		// Weißer Springer an (6,7) entspricht g1 (8-7 = 1)
		Knight knight = new Knight(board, 6, 7, true); // g1
		// Ziel (5,5) entspricht f3 (8-5 = 3)
		Move move = new Move(board, knight, 5, 5, null); // f3
		assertEquals("Nf3", move.toAlgebraicNotation());
	}

	@Test
	public void testKnightCapture() {
		// Weißer Springer an (1,7) entspricht b1
		Knight knight = new Knight(board, 1, 7, true); // b1
		// Gegnerischer Bauer an (2,5) entspricht c3
		Pawn enemyPawn = new Pawn(board, 2, 5, false);
		Move move = new Move(board, knight, 2, 5, enemyPawn);
		assertEquals("Nxc3", move.toAlgebraicNotation());
	}

	@Test
	public void testBishopMoveNoCapture() {
		// Weißer Läufer an (2,7) entspricht c1
		Bishop bishop = new Bishop(board, 2, 7, true); // c1
		// Ziel (5,4) entspricht f4
		Move move = new Move(board, bishop, 5, 4, null); // f4
		assertEquals("Bf4", move.toAlgebraicNotation());
	}

	@Test
	public void testRookMoveNoCapture() {
		// Weißer Turm an (0,7) entspricht a1
		Rook rook = new Rook(board, 0, 7, true); // a1
		// Ziel (0,4) entspricht a4
		Move move = new Move(board, rook, 0, 4, null); // a4
		assertEquals("Ra4", move.toAlgebraicNotation());
	}

	@Test
	public void testRookCapture() {
		Rook rook = new Rook(board, 0, 7, true); // a1
		// Gegnerischer Bauer an (0,0) entspricht a8
		Pawn enemyPawn = new Pawn(board, 0, 0, false); // a8
		Move move = new Move(board, rook, 0, 0, enemyPawn);
		assertEquals("Rxa8", move.toAlgebraicNotation());
	}

	@Test
	public void testQueenMoveNoCapture() {
		// Weiße Dame an (3,7) entspricht d1
		Queen queen = new Queen(board, 3, 7, true); // d1
		// Ziel (7,3) entspricht h5
		Move move = new Move(board, queen, 7, 3, null); // h5
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
		// Weißer König an (4,7) entspricht e1
		King king = new King(board, 4, 7, true); // e1
		// Ziel (4,6) entspricht e2
		Move move = new Move(board, king, 4, 6, null); // e2
		assertEquals("Ke2", move.toAlgebraicNotation());
	}

	@Test
	public void testKingCapture() {
		King king = new King(board, 4, 7, true); // e1
		Pawn enemyPawn = new Pawn(board, 4, 6, false); // e2
		Move move = new Move(board, king, 4, 6, enemyPawn);
		assertEquals("Kxe2", move.toAlgebraicNotation());
	}

	@Test
	public void testKingsideCastling() {
		King king = new King(board, 4, 7, true); // e1
		// Zwei Felder nach rechts
		Move move = new Move(board, king, 6, 7, null); // g1
		assertEquals("O-O", move.toAlgebraicNotation());
	}

	@Test
	public void testQueensideCastling() {
		King king = new King(board, 4, 7, true); // e1
		// Zwei Felder nach links
		Move move = new Move(board, king, 2, 7, null); // c1
		assertEquals("O-O-O", move.toAlgebraicNotation());
	}

	@Test
	public void testCheckmateAnnotation() {
		board.setGameEnding(GameEnding.CHECKMATE);
		Pawn pawn = new Pawn(board, 4, 6, true);  // e2
		Move move = new Move(board, pawn, 4, 4, null); // e4
		assertEquals("e4#", move.toAlgebraicNotation());
	}

	@Test
	public void testBoardOrientationFalse() {
		// Wenn whiteAtBottom false ist, erfolgt die Umrechnung so, dass
		// file = 'a' + column und rank = row + 1 (keine Umkehrung der Zeilen).
		board.setWhiteAtBottom(false);
		// Hier: (4,1) -> e2 und (4,3) -> e4
		Pawn pawn = new Pawn(board, 4, 1, true); // e2
		Move move = new Move(board, pawn, 4, 3, null); // e4
		assertEquals("e4", move.toAlgebraicNotation());
	}
}
