package bme.oop;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;

/**
 * A játék fő LibGDX alkalmazásosztálya.
 * Ez kezeli a képernyők közötti váltást, a menüt, az új játékot és a mentés betöltését.
 */
public class Main extends com.badlogic.gdx.Game {

    public Menu menu;
    public bme.oop.Game game;

    /**
     * Az alkalmazás indulásakor meghívódó metódus.
     * Létrehozza és megjeleníti a főmenüt.
     */
    @Override
    public void create() {
        gotoMenu();
    }

    /**
     * Biztonságosan képernyőt vált, majd felszabadítja az előző képernyő erőforrásait.
     * Így nem maradnak bent régi Stage, Texture vagy SpriteBatch objektumok.
     */
    private void switchScreen(Screen newScreen) {
        Screen oldScreen = getScreen();
        setScreen(newScreen);

        if (oldScreen != null) {
            oldScreen.dispose();
        }
    }

    /**
     * Visszavált a főmenüre.
     * Minden alkalommal új menüpéldányt hoz létre, hogy frissüljön a mentések listája.
     */
    public void gotoMenu() {
        menu = new Menu(this);
        switchScreen(menu);
    }

    /**
     * Új játékot indít a megadott méret, nehézség és időzítő beállítás alapján.
     *
     * @param size a pálya mérete
     * @param difficulty a játék nehézsége
     * @param timerEnabled igaz, ha az időzítő aktív
     */
    public void gotoGame(int size, String difficulty, boolean timerEnabled) {
        game = new bme.oop.Game(this, size, difficulty, timerEnabled);
        switchScreen(game);
    }

    /**
     * Betölt egy korábban elmentett játékállást JSON fájlból.
     * Ha nincs kiválasztott vagy létező mentés, akkor nem történik képernyőváltás.
     *
     * @param name a mentés fájlneve
     */
    public void loadGame(String name) {
        if (name == null || name.equals("No saves available")) {
            System.out.println("Nincs betölthető mentés.");
            return;
        }

        FileHandle saveFile = com.badlogic.gdx.Gdx.files.local("savedGames/" + name);

        if (!saveFile.exists()) {
            System.out.println("A mentés nem létezik: " + name);
            return;
        }

        Gson gson = new Gson();
        String jsonText = saveFile.readString();

        @SuppressWarnings("null")
        Board loadedBoard = gson.fromJson(jsonText, Board.class);
        game = new bme.oop.Game(this, loadedBoard);
        switchScreen(game);
    }
}