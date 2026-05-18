package bme.oop.Displays;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Az időzítő kijelző osztálya.
 * A játékból hátralévő időt jeleníti meg három számjeggyel.
 */
public class Timer extends Display {

    /**
     * Létrehoz egy új időzítő kijelzőt a megadott pozíción.
     * A pozíció a kijelző bal alsó sarkát jelenti.
     */
    public Timer(float x, float y) {
        super(x, y);
    }

    /**
     * Kirajzolja az időzítő aktuális értékét három számjeggyel.
     * A számot először biztonságosan 0 és 999 közé korlátozza.
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