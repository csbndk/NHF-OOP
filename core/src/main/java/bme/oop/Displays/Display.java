package bme.oop.Displays;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Absztrakt kijelző osztály háromjegyű számok megjelenítéséhez.
 * Ebből származik a bomba számláló és az időzítő kijelző.
 */
abstract public class Display {

    protected Rectangle bounds;
    protected Texture[] textures;
    protected Integer[] numbers;

    /**
     * Létrehoz egy kijelzőt a megadott pozíción.
     * Betölti a számjegyekhez tartozó textúrákat.
     */
    public Display(float x, float y) {
        this.bounds = new Rectangle(x, y, 32, 2 * 32);
        textures = new Texture[10];
        numbers = new Integer[3];

        for (int i = 0; i < textures.length; i++) {
            textures[i] = new Texture("displays/display_" + i + ".png");
        }
    }

    /**
     * Egy számot három számjegyre bont.
     * A negatív számokat 0-ra, a 999 feletti számokat 999-re korlátozza.
     */
    public static int[] calculateDigits(int number) {
        if (number < 0) {
            number = 0;
        }

        if (number > 999) {
            number = 999;
        }

        return new int[] {
                number / 100,
                (number / 10) % 10,
                number % 10
        };
    }

    /**
     * Beállítja a kijelzőn megjelenítendő három számjegyet.
     * A számjegyek kiszámításához a calculateDigits metódust használja.
     */
    public void calculateNumbers(int number) {
        int[] digits = calculateDigits(number);

        for (int i = 0; i < 3; i++) {
            numbers[i] = digits[i];
        }
    }

    /**
     * Kirajzolja a kijelzőt a megadott SpriteBatch segítségével.
     * A konkrét kirajzolási logikát a leszármazott osztályok valósítják meg.
     */
    abstract public void draw(SpriteBatch batch, int number);
}