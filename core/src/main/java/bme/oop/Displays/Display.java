package bme.oop.Displays;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

abstract public class Display {
    protected Rectangle bounds;
    protected Texture[] textures;
    protected Integer[] numbers;

    public Display(float x, float y){
        this.bounds = new Rectangle(x, y, 32, 2*32);
        textures = new Texture[10];
        numbers = new Integer[3];

        for (int i = 0; i < textures.length; i++) {
            textures[i] = new Texture("displays/display_" + i + ".png");
        }
    }
    public void calculateNumbers(int number) throws Exception {
        if (number > 999 || number < 0) {
            throw new Exception("Hibás szám!");
        }
        for (int i = 0; i < 3; i++) {
            numbers[i] = (int)(number / (Math.pow(10, 2-i)))%10;
        }
    }

    abstract public void draw(SpriteBatch batch, int number);
}
