package bme.oop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

class CellTest {

    @Test
    void constructorSetsXCoordinate() {
        Cell cell = new Cell(2, 5);

        assertEquals(2, cell.x);
    }

    @Test
    void constructorSetsYCoordinate() {
        Cell cell = new Cell(2, 5);

        assertEquals(5, cell.y);
    }

    @Test
    void newCellIsNotMineByDefault() {
        Cell cell = new Cell(0, 0);

        assertFalse(cell.isMine);
    }

    @Test
    void newCellIsNotRevealedByDefault() {
        Cell cell = new Cell(0, 0);

        assertFalse(cell.isRevealed);
    }

    @Test
    void newCellIsNotFlaggedByDefault() {
        Cell cell = new Cell(0, 0);

        assertFalse(cell.isFlagged);
    }
}