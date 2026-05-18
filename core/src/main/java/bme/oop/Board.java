package bme.oop;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Az aknakereső játék logikai tábláját reprezentálja.
 * Tárolja a cellamátrixot, az aknák számát és a játék állapotát.
 */
public class Board {

    public Cell[][] matrix;
    public int size;
    public int mineCount;
    public int flagsPlaced = 0;
    public boolean isGameOver = false;
    public boolean isWon = false;
    public float timer;

    /**
     * Létrehoz egy új táblát a megadott mérettel és nehézséggel.
     * Inicializálja a cellákat, elhelyezi az aknákat és kiszámolja a szomszédos aknák számát.
     */
    public Board(int size, String difficulty) {
        this.size = size;
        this.matrix = new Cell[size][size];

        switch (difficulty) {
            case "easy":
                mineCount = (int) (size * size * 0.1);
                break;
            case "medium":
                mineCount = (int) (size * size * 0.15);
                break;
            case "hard":
                mineCount = (int) (size * size * 0.2);
                break;
            default:
                mineCount = (int) (size * size * 0.1);
                break;
        }

        this.timer = mineCount * 10;

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                matrix[x][y] = new Cell(x, y);
            }
        }

        generateMines();
        calculateNumbers();
    }

    /**
     * Véletlenszerűen elhelyezi az aknákat a táblán.
     * Ehhez Java gyűjteményt és keverést használ.
     */
    private void generateMines() {
        ArrayList<Cell> allCells = new ArrayList<>();

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                allCells.add(matrix[x][y]);
            }
        }

        Collections.shuffle(allCells);

        for (int i = 0; i < mineCount; i++) {
            allCells.get(i).isMine = true;
        }
    }

    /**
     * Minden nem aknás cellához kiszámolja a körülötte lévő aknák számát.
     * Az eredményt az adott cella adjMines mezőjében tárolja.
     */
    public final void calculateNumbers() {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (!matrix[x][y].isMine) {
                    matrix[x][y].adjMines = countMinesAround(x, y);
                }
            }
        }
    }

    /**
     * Megszámolja, hogy egy cella körül hány akna található.
     * A szélső és sarokcellákat is biztonságosan kezeli.
     */
    private int countMinesAround(int x, int y) {
        int count = 0;

        for (int i = Math.max(0, x - 1); i <= Math.min(size - 1, x + 1); i++) {
            for (int j = Math.max(0, y - 1); j <= Math.min(size - 1, y + 1); j++) {
                if (i == x && j == y) {
                    continue;
                }

                if (matrix[i][j].isMine) {
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * Felfedi a megadott koordinátájú cellát.
     * Üres cella esetén rekurzívan felfedi a szomszédos cellákat is.
     */
    public void reveal(int x, int y) {
        if (x < 0 || x >= size || y < 0 || y >= size) {
            return;
        }

        Cell cell = matrix[x][y];

        if (cell.isRevealed || cell.isFlagged || isGameOver || isWon) {
            return;
        }

        cell.isRevealed = true;

        if (cell.isMine) {
            isGameOver = true;
            revealAllMines();
            return;
        }

        if (cell.adjMines == 0) {
            for (int i = Math.max(0, x - 1); i <= Math.min(size - 1, x + 1); i++) {
                for (int j = Math.max(0, y - 1); j <= Math.min(size - 1, y + 1); j++) {
                    if (!(i == x && j == y)) {
                        reveal(i, j);
                    }
                }
            }
        }

        checkWin();
    }

    /**
     * Felfedi az összes aknát a táblán.
     * Ezt akkor használja a játék, amikor a játékos aknára lép.
     */
    private void revealAllMines() {
        for (Cell[] row : matrix) {
            for (Cell c : row) {
                if (c.isMine) {
                    c.isRevealed = true;
                }
            }
        }
    }

    /**
     * Ellenőrzi, hogy a játékos megnyerte-e a játékot.
     * A győzelem feltétele, hogy minden nem aknás cella fel legyen fedve.
     */
    public void checkWin() {
        int revealedCount = 0;

        for (Cell[] row : matrix) {
            for (Cell c : row) {
                if (c.isRevealed) {
                    revealedCount++;
                }
            }
        }

        if (revealedCount == (size * size) - mineCount) {
            isWon = true;
        }
    }
}