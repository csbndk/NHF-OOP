package bme.oop;

/**
 * Egy mezőt reprezentál az aknakereső táblán.
 * Tárolja a mező koordinátáit és aktuális állapotát.
 */
public class Cell {

    public int x, y;
    public boolean isMine = false;
    public boolean isRevealed = false;
    public boolean isFlagged = false;
    public int adjMines = 0;

    /**
     * Létrehoz egy új cellát a megadott koordinátákon.
     * Az új cella alapértelmezetten nem akna, nincs felfedve és nincs megjelölve.
     */
    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }
}