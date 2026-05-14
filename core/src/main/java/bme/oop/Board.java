package bme.oop;

import java.util.ArrayList;
import java.util.Collections;

public class Board {
    public Cell[][] matrix;
    public int size;
    public int mineCount;
    public int flagsPlaced = 0;
    public boolean isGameOver = false;
    public boolean isWon = false;
    public float timer;

    public Board(int size, String difficulty) {
        this.size = size;
        this.matrix = new Cell[size][size];
        
        switch(difficulty) {
            case("easy"):
                mineCount = (int)(size * size * 0.1);
                break;
            case("medium"):
                mineCount = (int)(size * size * 0.15);
                break;
            case("hard"):
                mineCount = (int)(size * size * 0.2);
                break;
        }

        this.timer = mineCount * 10;

        // Mátrix feltöltése
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                matrix[x][y] = new Cell(x, y);
            }
        }
        generateMines();
        calculateNumbers();
    }

    private void generateMines() {
        ArrayList<Cell> allCells = new ArrayList<>();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) allCells.add(matrix[x][y]);
        }
        Collections.shuffle(allCells);
        for (int i = 0; i < mineCount; i++) {
            allCells.get(i).isMine = true;
        }
    }

    private void calculateNumbers() {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (!matrix[x][y].isMine) {
                    matrix[x][y].adjMines = countMinesAround(x, y);
                }
            }
        }
    }

    private int countMinesAround(int x, int y) {
        int count = 0;
        for (int i = Math.max(0, x-1); i <= Math.min(size-1, x+1); i++) {
            for (int j = Math.max(0, y-1); j <= Math.min(size-1, y+1); j++) {
                if (matrix[i][j].isMine) count++;
            }
        }
        return count;
    }

    public void reveal(int x, int y) {
        if (x < 0 || x >= size || y < 0 || y >= size || matrix[x][y].isRevealed || matrix[x][y].isFlagged)
            return;

        matrix[x][y].isRevealed = true;

        if (matrix[x][y].isMine) {
            isGameOver = true;
            revealAllMines();
            return;
        }

        if (matrix[x][y].adjMines == 0) {
            for (int i = Math.max(0, x-1); i <= Math.min(size-1, x+1); i++) {
                for (int j = Math.max(0, y-1); j <= Math.min(size-1, y+1); j++) {
                    reveal(i, j);
                }
            }
        }
        checkWin();
    }

    private void revealAllMines() {
        for (Cell[] row : matrix) {
            for (Cell c : row) if (c.isMine) c.isRevealed = true;
        }
    }

    private void checkWin() {
        int revealedCount = 0;
        for (Cell[] row : matrix) {
            for (Cell c : row) if (c.isRevealed) revealedCount++;
        }
        if (revealedCount == (size * size) - mineCount) isWon = true;
    }
}