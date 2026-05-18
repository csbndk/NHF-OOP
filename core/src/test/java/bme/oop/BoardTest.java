package bme.oop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class BoardTest {

    @Test
    void constructorCreatesCorrectSizeMatrix() {
        Board board = new Board(5, "easy");

        assertEquals(5, board.size);
        assertEquals(5, board.matrix.length);
        assertEquals(5, board.matrix[0].length);
    }

    @Test
    void easyDifficultySetsCorrectMineCount() {
        Board board = new Board(10, "easy");

        assertEquals(10, board.mineCount);
    }

    @Test
    void mediumDifficultySetsCorrectMineCount() {
        Board board = new Board(10, "medium");

        assertEquals(15, board.mineCount);
    }

    @Test
    void hardDifficultySetsCorrectMineCount() {
        Board board = new Board(10, "hard");

        assertEquals(20, board.mineCount);
    }

    @Test
    void revealRevealsSelectedCell() {
        Board board = createEmptyBoard(3);

        board.reveal(1, 1);

        assertTrue(board.matrix[1][1].isRevealed);
    }

    @Test
    void revealDoesNotRevealFlaggedCell() {
        Board board = createEmptyBoard(3);
        board.matrix[1][1].isFlagged = true;

        board.reveal(1, 1);

        assertFalse(board.matrix[1][1].isRevealed);
    }

    @Test
    void revealingMineSetsGameOver() {
        Board board = createEmptyBoard(3);
        board.mineCount = 1;
        board.matrix[1][1].isMine = true;

        board.reveal(1, 1);

        assertTrue(board.isGameOver);
    }

    private Board createEmptyBoard(int size) {
        Board board = new Board(size, "easy");

        for (int x = 0; x < board.size; x++) {
            for (int y = 0; y < board.size; y++) {
                board.matrix[x][y].isMine = false;
                board.matrix[x][y].isRevealed = false;
                board.matrix[x][y].isFlagged = false;
                board.matrix[x][y].adjMines = 1;
            }
        }

        board.mineCount = 0;
        board.flagsPlaced = 0;
        board.isGameOver = false;
        board.isWon = false;

        return board;
    }
}