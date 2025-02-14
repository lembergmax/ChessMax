package com.mlprograms.tests.engine.evaluation;

import com.mlprograms.chess.game.engine.state.GameState;
import com.mlprograms.chess.game.engine.state.PositionEvaluation;
import com.mlprograms.chess.game.ui.Board;
import com.mlprograms.chess.human.Human;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameStateTest {

	private Board board;
	private PositionEvaluation evaluation;

	/**
	 * Sets up a new chess board with two human players before each test.
	 */
	@BeforeEach
	public void setUp() {
		board = new Board(new Human(), new Human());
	}

	/**
	 * Loads a board position from a FEN string, evaluates the game state,
	 * and asserts that it matches the expected state.
	 *
	 * @param fen           the FEN string representing the board position.
	 * @param expectedState the expected game state.
	 */
	private void loadFenAndEvaluate(String fen, GameState expectedState) {
		board.loadPositionFromFen(fen);
		evaluation = new PositionEvaluation(board);
		assertEquals(expectedState, evaluation.evaluateGameState());
	}

	// Existing tests

	/**
	 * Test to ensure the game is evaluated as END_GAME due to queen count.
	 */
	@Test
	public void testEndGameDueToQueenCount() {
		loadFenAndEvaluate("8/8/8/3Q4/8/8/8/K6k w - - 0 1", GameState.END_GAME);
	}

	/**
	 * Test to ensure the game is evaluated as END_GAME due to few pieces on the board.
	 */
	@Test
	public void testEndGameDueToFewPieces() {
		loadFenAndEvaluate("8/8/8/3Q4/8/8/8/K6k w - - 0 1", GameState.END_GAME);
	}

	/**
	 * Test to ensure the game is evaluated as END_GAME when both kings are positioned centrally.
	 */
	@Test
	public void testEndGameDueToKingsCentral() {
		loadFenAndEvaluate("8/8/3k4/8/3K4/8/8/8 w - - 0 1", GameState.END_GAME);
	}

	/**
	 * Test to evaluate the opening state.
	 * Note: The starting position is typically evaluated as OPENING.
	 */
	@Test
	public void testMiddleGame() {
		loadFenAndEvaluate("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", GameState.OPENING);
	}

	/**
	 * Test to ensure the board is evaluated as OPENING.
	 */
	@Test
	public void testOpening() {
		loadFenAndEvaluate("r1bqkbnr/pppppppp/2n5/8/8/5N2/PPPPPPPP/RNBQKB1R w KQkq - 2 3", GameState.OPENING);
	}

	/**
	 * Test to ensure that a board with only kings is evaluated as END_GAME.
	 */
	@Test
	public void testOnlyKings() {
		loadFenAndEvaluate("8/8/8/8/8/8/3K4/4k3 w - - 0 1", GameState.END_GAME);
	}

	/**
	 * Test to ensure the game is evaluated as END_GAME when there is only one queen,
	 * even if the overall material is high.
	 */
	@Test
	public void testEndGameWithOneQueenEvenIfMaterialHigh() {
		loadFenAndEvaluate("rnb1kbnr/pppp1ppp/8/4p3/8/4Q3/PPPP1PPP/RNB1KBNR w KQkq - 0 1", GameState.END_GAME);
	}

	/**
	 * Test to ensure the full starting position is correctly evaluated as OPENING.
	 */
	@Test
	public void testFullOpeningState() {
		loadFenAndEvaluate("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", GameState.OPENING);
	}

	// Additional tests

	/**
	 * Test a typical middlegame position where both sides are developed
	 * and both kings are safe (usually via castling). This position is expected to be evaluated as MIDDLE_GAME.
	 */
	@Test
	public void testTypicalMiddlegame() {
		loadFenAndEvaluate("r1bq1rk1/ppp1bppp/2np1n2/2p1p3/2B1P3/2NP1N2/PPP2PPP/R1BQ1RK1 w - - 0 1", GameState.MIDDLE_GAME);
	}

	/**
	 * Test a queenless position where all other pieces (rooks, knights, bishops, and pawns) are still on the board.
	 * Expected to be evaluated as MIDDLE_GAME.
	 */
	@Test
	public void testQueenlessButFullMaterial() {
		loadFenAndEvaluate("rnb1kbnr/pppppppp/8/8/8/8/PPPPPPPP/RNB1KBNR w KQkq - 0 1", GameState.MIDDLE_GAME);
	}

	/**
	 * Test an endgame scenario with only kings and a rook, a typical winning endgame variant.
	 */
	@Test
	public void testEndGameWithRookAndKings() {
		loadFenAndEvaluate("8/8/8/8/8/2R5/1k6/1K6 w - - 0 1", GameState.END_GAME);
	}

	/**
	 * Test a pawn endgame where only kings and several pawns remain,
	 * a scenario that typically falls into the endgame phase.
	 */
	@Test
	public void testPawnEndgame() {
		loadFenAndEvaluate("8/3p4/3p4/3p4/3p4/3p4/3K4/4k3 w - - 0 1", GameState.END_GAME);
	}

	/**
	 * Test an endgame scenario with a king and a single pawn.
	 */
	@Test
	public void testKingAndPawnEndgame() {
		loadFenAndEvaluate("8/8/8/8/8/2P5/2K5/4k3 w - - 0 1", GameState.END_GAME);
	}

	/**
	 * Test a complex middlegame position with balanced material and several pieces on the board.
	 */
	@Test
	public void testComplexMiddlegame() {
		loadFenAndEvaluate("r3k2r/ppqn1ppp/2np1b2/4p3/4P3/2NP1B2/PPQN1PPP/R3K2R w KQkq - 0 1", GameState.MIDDLE_GAME);
	}

	/**
	 * Test a position after a queen exchange where plenty of material still remains,
	 * representing a typical transition into the middlegame.
	 */
	@Test
	public void testMiddlegameAfterQueenExchange() {
		loadFenAndEvaluate("rnb1k2r/ppp1bppp/3p1n2/4p3/4P3/1NN1P3/PPP1BPPP/R2BK2R w KQkq - 0 1", GameState.MIDDLE_GAME);
	}

	/**
	 * Test an endgame with insufficient material: a king and bishop versus a lone king.
	 */
	@Test
	public void testInsufficientMaterialBishop() {
		loadFenAndEvaluate("8/8/8/8/8/8/KB6/k7 w - - 0 1", GameState.END_GAME);
	}

	/**
	 * Test an endgame with insufficient material: a king and knight versus a lone king.
	 */
	@Test
	public void testInsufficientMaterialKnight() {
		loadFenAndEvaluate("8/8/8/8/8/8/KN6/k7 w - - 0 1", GameState.END_GAME);
	}

	/**
	 * Test a position with advanced pawn structures and centralized kings,
	 * which are typical features of many endgames.
	 */
	@Test
	public void testAdvancedPawnStructureEndgame() {
		loadFenAndEvaluate("8/8/8/3p4/3P4/8/3K4/4k3 w - - 0 1", GameState.END_GAME);
	}

	/**
	 * Test an early game position with only a single pawn move and minimal piece development.
	 * This is generally evaluated as an OPENING.
	 */
	@Test
	public void testEarlyGameWithMinorDevelopment() {
		loadFenAndEvaluate("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq - 0 1", GameState.OPENING);
	}
}
