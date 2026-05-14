package bme.oop.Displays;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Counter extends Display{

    public Counter(float x, float y) {
        super(x, y);
    }

    @Override
    public void draw(SpriteBatch batch, int number) {
        try {
            calculateNumbers(number);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 3; i++) {
            batch.draw(textures[numbers[i]], bounds.x + (i * 32), bounds.y, bounds.width, bounds.height);
        }
    }

}
