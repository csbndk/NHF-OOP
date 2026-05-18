package bme.oop;

import java.io.File;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * A játék főmenü képernyője.
 * Innen indítható új játék és innen tölthető be mentett játékállás.
 */
public class Menu implements Screen {

    private SpriteBatch batch;
    private Main main;
    private Stage stage;
    private BitmapFont font;
    private Texture buttonTexture;
    private Skin skin;

    /**
     * Létrehoz egy új menü képernyőt a fő alkalmazás hivatkozásával.
     * A Main objektumon keresztül történik a képernyőváltás.
     */
    public Menu(Main main) {
        this.main = main;
    }

    /**
     * A menü megjelenésekor lefutó inicializáló metódus.
     * Létrehozza a grafikus elemeket, a Stage-et és a menütáblát.
     */
    @Override
    public void show() {
        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport(), batch);
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));
        font = new BitmapFont();
        buttonTexture = new Texture("buttons/button1.png");

        stage.addActor(initTable());
        Gdx.graphics.setWindowedMode(300, 200);
    }

    /**
     * Kirajzolja a menü aktuális állapotát.
     * Minden képkockában frissíti és megjeleníti a Stage-et.
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.7f, 0.7f, 0.7f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    /**
     * Létrehozza a menüben látható táblázatos elrendezést.
     * Tartalmazza az új játék, betöltés, méret, nehézség és időzítő elemeket.
     */
    public Table initTable() {
        Table table = new Table();
        table.setFillParent(true);

        TextButtonStyle style = new TextButtonStyle();
        style.up = new TextureRegionDrawable(buttonTexture);
        style.font = font;

        TextButton newGameButton = new TextButton("Uj Jatek", style);
        TextButton loadGameButton = new TextButton("Betoltes", style);

        SelectBox<String> difficultySelectBox = new SelectBox<>(skin);
        difficultySelectBox.setItems("easy", "medium", "hard");

        TextField sizeTextField = new TextField("10", skin);
        CheckBox timerCheckBox = new CheckBox("Timer", skin);

        SelectBox<String> saveSelectBox = new SelectBox<>(skin);
        saveSelectBox.setItems(loadSaveNames());

        newGameButton.addListener(new ChangeListener() {
            /**
             * Új játék indításakor lefutó eseménykezelő.
             * Beolvassa a méretet, nehézséget és időzítő beállítást.
             */
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    int size = Integer.parseInt(sizeTextField.getText().trim());

                    if (size < 3) {
                        System.out.println("A pályaméret legyen legalább 3.");
                        return;
                    }

                    String difficulty = difficultySelectBox.getSelected();
                    boolean timerEnabled = timerCheckBox.isChecked();

                    main.gotoGame(size, difficulty, timerEnabled);
                } catch (NumberFormatException e) {
                    System.out.println("Hibás pályaméret! Számot adj meg.");
                }
            }
        });

        loadGameButton.addListener(new ChangeListener() {
            /**
             * Mentett játék betöltésekor lefutó eseménykezelő.
             * A kiválasztott fájl nevét továbbítja a Main osztálynak.
             */
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                main.loadGame(saveSelectBox.getSelected());
            }
        });

        table.add(difficultySelectBox).width(80).height(30).pad(10);
        table.add(sizeTextField).width(80).height(30).pad(10);
        table.add(timerCheckBox).width(80).height(30).pad(10);

        table.row();
        table.add(newGameButton).colspan(3).width(150).height(30).pad(10);

        table.row();
        table.add(saveSelectBox).colspan(3).width(150).height(30).pad(10);

        table.row();
        table.add(loadGameButton).colspan(3).width(150).height(30).pad(10);

        return table;
    }

    /**
     * Beolvassa a savedGames mappában található mentések neveit.
     * Ha nincs mentés, egy jelző szöveget ad vissza.
     */
    private Array<String> loadSaveNames() {
        File path = new File("savedGames");

        if (!path.exists()) {
            path.mkdirs();
        }

        File[] saves = path.listFiles();
        Array<String> names = new Array<>();

        if (saves != null) {
            for (File file : saves) {
                if (file.isFile()) {
                    names.add(file.getName());
                }
            }
        }

        if (names.isEmpty()) {
            names.add("No saves available");
        }

        return names;
    }

    /**
     * Az ablak átméretezésekor frissíti a Stage viewportját.
     * Nulla vagy negatív méret esetén nem végez frissítést.
     */
    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }

        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
    }

    /**
     * A játék szüneteltetésekor meghívódó metódus.
     * Jelenleg nincs külön szüneteltetési logika.
     */
    @Override
    public void pause() {
    }

    /**
     * A játék folytatásakor meghívódó metódus.
     * Jelenleg nincs külön folytatási logika.
     */
    @Override
    public void resume() {
    }

    /**
     * A képernyő elrejtésekor meghívódó metódus.
     * Jelenleg nem végez külön műveletet.
     */
    @Override
    public void hide() {
    }

    /**
     * Felszabadítja a menü által használt grafikus erőforrásokat.
     * A Stage, font, batch, textúra és skin objektumokat is lezárja.
     */
    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }

        if (font != null) {
            font.dispose();
        }

        if (batch != null) {
            batch.dispose();
        }

        if (buttonTexture != null) {
            buttonTexture.dispose();
        }

        if (skin != null) {
            skin.dispose();
        }
    }
}