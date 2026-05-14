package bme.oop;

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
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Menu implements Screen {

    private SpriteBatch batch;
    private TextButton newGameButton;
    private TextButton loadGameButton;
    private Main main;
    private Stage stage;
    private BitmapFont font;
    private Texture buttonTexture;
    private Skin skin;

    public Menu(Main main) {
        this.main = main;
    }

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

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.7f, 0.7f, 0.7f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    public Table initTable() {
        Table table = new Table();
        table.setFillParent(true);

        TextButtonStyle style = new TextButtonStyle();
        style.up = new TextureRegionDrawable(buttonTexture);
        style.font = font;

        //Elemek létrehozása
        newGameButton = new TextButton("Uj Jatek", style);
        loadGameButton = new TextButton("Betoltes", style);

        SelectBox<String> selectBox = new SelectBox<>(skin);
        selectBox.setItems("easy", "medium", "hard");

        TextField textField = new TextField(null, skin);

        CheckBox checkBox = new CheckBox("Timer", skin);

        //Kattintásfigyelők
        newGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    main.gotoGame();
                    ((bme.oop.Game) main.game).setBoard(Integer.parseInt(textField.getText()), selectBox.getSelected());
                    ((bme.oop.Game) main.game).setTimer(checkBox.isChecked());
                } catch (Exception e) {
                    // TODO: handle exception
                } 
                
            }
        });

        loadGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                main.gotoLoad();
            }
        });

        table.add(selectBox).width(80).height(30).pad(10);
        table.add(textField).width(80).height(30).pad(10);
        table.add(checkBox).width(80).height(30).pad(10);
        table.row();

        table.add(newGameButton).colspan(3).width(150).height(30).pad(10);
        table.row();

        table.add(loadGameButton).colspan(3).width(150).height(30).pad(10);

        return table;
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;

        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (font != null) font.dispose();
        if (batch != null) batch.dispose();
        if (buttonTexture != null) buttonTexture.dispose();
        if (skin != null) skin.dispose();
    }
}