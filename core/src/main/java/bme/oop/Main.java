package bme.oop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;

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

    public void loadGame(String name){
        Gson gson = new Gson();
        
        FileHandle mentesFajl = Gdx.files.local("savedGames/" + name);
        String jsonSzoveg = mentesFajl.readString();
        game = new bme.oop.Game(gson.fromJson(jsonSzoveg, Board.class));

        setScreen(game);
    }
}