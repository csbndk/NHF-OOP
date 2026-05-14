package bme.oop;


public class Cell {
    public int x, y;
    public boolean isMine = false;
    public boolean isRevealed = false;
    public boolean isFlagged = false;
    public int adjMines = 0;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }
}