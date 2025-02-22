/*
 * Copyright (c) 2025 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.tests.ui;

import com.mlprograms.chess.game.ui.Board;
import com.mlprograms.chess.human.Human;
import com.mlprograms.chess.utils.ConfigFetcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoardTest {

	private Board board;

	@BeforeEach
	public void setUp() {
		board = new Board(new Human(), new Human(), true);
	}

	@Test
	public void testBoardInitialization() {
		assertEquals(
			ConfigFetcher.fetchStringConfig("ChessGame", "STARTING_POSITION_" + ((board.isWhiteAtBottom()) ? "WHITE" : "BLACK")),
			board.getCurrentPositionsFenNotation().toString()
		);

		board = new Board(new Human(), new Human(), false);
		assertEquals(
			ConfigFetcher.fetchStringConfig("ChessGame", "STARTING_POSITION_" + ((board.isWhiteAtBottom()) ? "WHITE" : "BLACK")),
			board.getCurrentPositionsFenNotation().toString()
		);
	}

	@Test
	public void testLoadPositionFromFen() {
		String randomFen1 = "r3QbR1/Pp6/1npP4/P1pP4/3Pp3/2K2k2/3P2b1/N7 w - - 0 1"; // 19 Pieces
		String randomFen2 = "8/8/8/2k5/8/8/4K3/8 w - - 0 1"; // 2 Pieces (King and King)
		String randomFen3 = "7q/Pp2P3/bp2Npkp/1Q1PP1p1/P1rBK2R/pBP1RN1p/p1rbPP2/1n1n4 w - - 0 1"; // 32 Pieces

		board.loadPositionFromFen(randomFen1);
		assertEquals(19, board.getPieceList().size());

		board.loadPositionFromFen(randomFen2);
		assertEquals(2, board.getPieceList().size());

		board.loadPositionFromFen(randomFen3);
		assertEquals(32, board.getPieceList().size());
	}

	@Test
	public void testRotateBoardWithTogglingFlag() {
		boolean initialOrientation = board.isWhiteAtBottom();
		board.rotateBoardWithTogglingFlag();
		assertEquals(!initialOrientation, board.isWhiteAtBottom());
	}

	@Test
	public void testRotateCoordinates() {
		int[] rotatedCoords = board.rotateCoordinates(0, 0);
		assertEquals(board.getColumns() - 1, rotatedCoords[ 0 ]);
		assertEquals(board.getRows() - 1, rotatedCoords[ 1 ]);
	}

	@Test
	public void testRotateAllFenNotations() {
		String initialFen = board.getCurrentPositionsFenNotation().toString();
		board.rotateAllFenNotations();
		board.rotateAllFenNotations(); // Should restore the original position
		assertEquals(initialFen, board.getCurrentPositionsFenNotation().toString());
	}

	@Test
	public void testRotate() {
		String initialFen = board.getCurrentPositionsFenNotation().toString();
		board.rotate();
		board.rotate(); // Should restore the original position
		assertEquals(initialFen, board.getCurrentPositionsFenNotation().toString());
	}

	@Test
	public void testRotateBoard() {
		String initialFen = board.getCurrentPositionsFenNotation().toString();
		board.rotateBoard();
		board.rotateBoard(); // Should restore the original position
		assertEquals(initialFen, board.getCurrentPositionsFenNotation().toString());
	}

	@Test
	public void testRotateEnPassantTile() {
		board.setEnPassantTile(12); // Example tile
		board.rotateEnPassantTile();
		int expectedRotatedTile = board.rotateCoordinates(12 % board.getColumns(), 12 / board.getColumns())[ 1 ] * board.getColumns() + board.rotateCoordinates(12 % board.getColumns(), 12 / board.getColumns())[ 0 ];
		assertEquals(expectedRotatedTile, board.getEnPassantTile());
	}

}