package bme.oop;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    public Menu menu;
    public bme.oop.Game game;

    @Override
    public void create() {
        menu = new Menu(this);
        setScreen(menu);
    }

    public void gotoMenu() {
        setScreen(menu);
    }

    public void gotoGame(int size, String difficulty, boolean timerEnabled) {
        game = new bme.oop.Game(this, size, difficulty, timerEnabled);
        setScreen(game);
    }

}