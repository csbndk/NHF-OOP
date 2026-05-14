package bme.oop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    public Screen menu;
    public Screen load;
    public Screen game;

    @Override
    public void create() {
        menu = new Menu(this);
        game = new bme.oop.Game(this);
        setScreen(menu);
    }
    public void gotoMenu(){
        setScreen(menu);
    }
    public void gotoGame(){
        setScreen(game);
    }
    public void gotoLoad(){

    }
}