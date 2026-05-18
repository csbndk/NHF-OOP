package bme.oop.Displays;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Az aknaszámláló kijelző osztálya.
 * A hátralévő zászlózható aknák számát jeleníti meg.
 */
public class Counter extends Display {

    /**
     * Létrehoz egy új aknaszámlálót a megadott pozíción.
     * A pozíció a kijelző bal alsó sarkát jelenti.
     */
    public Counter(float x, float y) {
        super(x, y);
    }

    /**
     * Kirajzolja az aknaszámlálót három számjeggyel.
     * A megadott számot először három számjegyre bontja.
     */
    @Override
    public void draw(SpriteBatch batch, int number) {
        calculateNumbers(number);

        for (int i = 0; i < 3; i++) {
            batch.draw(
                    textures[numbers[i]],
                    bounds.x + (i * 32),
                    bounds.y,
                    bounds.width,
                    bounds.height
            );
        }
    }
}