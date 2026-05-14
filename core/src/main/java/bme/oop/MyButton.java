package bme.oop;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class MyButton {
    private Rectangle bounds;
    private Texture texture;

    public MyButton(float x, float y, float width, float height, Texture texture) {
        this.bounds = new Rectangle(x, y, width, height);
        this.texture = texture;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public boolean isClicked(float touchX, float touchY) {
        return bounds.contains(touchX, touchY);
    }
}