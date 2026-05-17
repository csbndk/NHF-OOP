package bme.oop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.google.gson.Gson;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import bme.oop.Displays.*;

public class Game implements Screen {

    private SpriteBatch batch;
    private Board board;
    private Texture cell_Default;
    private Texture cell_Revealed;
    private Texture cell_Mine;
    private Texture cell_Flag;
    private Texture menu_Texture;
    private Texture[] numbers;
    private BitmapFont menuFont;
    private final int cellSize = 32;
    private final int topBarHeight = 128;
    private TextButton menu;
    private Stage stage;
    private Main main;
    private Display timer;
    private Display counter;
    private boolean timerEnabled = false;

    public Game(Board board) {
        this.board = board;
    }
    public Game(Main main, int size, String difficulty, boolean timer) {
        this.main = main;
        this.board = new Board(size, difficulty);
        this.timerEnabled = timer;
    }

    public int getWidth(){
        return board.size * cellSize;
    }
    public int getHeight(){
        return board.size * cellSize + topBarHeight;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        loadTextures();
        loadEdgeTextures();
        drawDisplays();
        addMenuButton();
        setWindowSize(getWidth(), getHeight());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.7f, 0.7f, 0.7f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput();
        checkGameStatus();

        if (timerEnabled) {
            updateTimer();
        }

        batch.begin();

        drawBoard();

        counter.draw(batch, board.mineCount - board.flagsPlaced);
        timer.draw(batch, (int) board.timer);

        drawEdges();

        batch.end();

        stage.act(delta);
        stage.draw();
    }

    private void checkGameStatus() {
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        Texture win = new Texture("popups/game_win.png");
        Texture lose = new Texture("popups/game_lose.png");
        Popup popup = new Popup("Menu", skin, lose);
        Boolean triggered = false;

        if (board.isGameOver){
            popup = new Popup("Menu", skin, lose);
            triggered = true;
        }
        else if (board.isWon) {
            popup = new Popup("Menu", skin, win);
            triggered = true;
        }
        if (triggered) {
            popup.show(stage);
        }
    }
    private void drawBoard() {
        for (int x = 0; x < board.size; x++) {
            for (int y = 0; y < board.size; y++) {
                Cell cell = board.matrix[x][y];

                float drawX = x * cellSize;
                float drawY = y * cellSize;

                if (!cell.isRevealed) {
                    if (cell.isFlagged) {
                        batch.draw(cell_Flag, drawX, drawY, cellSize, cellSize);
                    } else {
                        batch.draw(cell_Default, drawX, drawY, cellSize, cellSize);
                    }
                } else {
                    if (cell.isMine) {
                        batch.draw(cell_Mine, drawX, drawY, cellSize, cellSize);
                    } else {
                        batch.draw(numbers[cell.adjMines], drawX, drawY, cellSize, cellSize);
                    }
                }
            }
        }
    }

    private void drawDisplays() {
        // Bal oldal: counter
        counter = new Counter(
            32,
            board.size * cellSize + topBarHeight - 3 * cellSize
        );

        // Jobb oldal: timer
        timer = new Timer(
            board.size * cellSize - 4 * cellSize,
            board.size * cellSize + topBarHeight - 3 * cellSize
        );
    }

    private void loadTextures(){
        cell_Default = new Texture("cell_graphics/cella.png");
        cell_Revealed = new Texture("cell_graphics/akna_0.png");
        cell_Mine = new Texture("cell_graphics/akna.png");
        cell_Flag = new Texture("cell_graphics/cella_megjelolt.png");

        numbers = new Texture[9];
        for (int i = 0; i < 9; i++) {
            numbers[i] = new Texture("cell_graphics/akna_" + i + ".png");
        }
    }

    private void addMenuButton(){
        stage = new Stage(new ScreenViewport());

        TextButtonStyle style = new TextButtonStyle();
        style.up = new TextureRegionDrawable(new Texture("buttons/menu.png"));
        style.down = style.up;
        style.font = new BitmapFont();

        menu = new TextButton("", style);
        menu.setSize(32, 32);
        menu.setPosition(
            board.size * cellSize / 2f - 16,
            board.size * cellSize + 64
        );

        menu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gson gson = new Gson();
                String datum = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH_mm_ss"));
                FileHandle mentesFajl = Gdx.files.local("savedGames/" + datum + ".json");

                String jsonSzoveg = gson.toJson(board);

                mentesFajl.writeString(jsonSzoveg, false);
                main.gotoMenu();
            }
        });
        stage.addActor(menu);
        Gdx.input.setInputProcessor(stage);
    }
   
    private Texture[] loadEdgeTextures() {
        Texture[] textures = new Texture[2];
        Pixmap lightPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        lightPixmap.setColor(0.745f, 0.745f, 0.745f, 1f);
        lightPixmap.fill();
        Texture lightEdge = new Texture(lightPixmap);
        lightPixmap.dispose();
        textures[0] = lightEdge;

        Pixmap darkPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        darkPixmap.setColor(0.529f, 0.529f, 0.529f, 1f);
        darkPixmap.fill();
        Texture darkEdge = new Texture(darkPixmap);
        darkPixmap.dispose();
        textures[1] = darkEdge;

        return textures;
    }

    private void drawEdges() {
        int boardWidth = board.size * cellSize;
        int boardHeight = board.size * cellSize;
        int thickness = 4;
        int displayY = boardHeight + topBarHeight - 3 * cellSize - thickness;
        int displayWidth = 3 * cellSize + 2*thickness;
        int displayHeight = cellSize*2 + 2*thickness;

        // TopBar szegélye
        drawBeveledBorder(0, boardHeight, boardWidth, topBarHeight);

        // Counter display szegélye
        drawBeveledBorder(32 - thickness, displayY, displayWidth, displayHeight);

        // Timer display szegélye
        drawBeveledBorder(boardWidth - 4 * cellSize - thickness, displayY, displayWidth, displayHeight);
    }

    private void drawBeveledBorder(float x, float y, float width, float height) {
        float thickness = 4;
        Texture[] textures = loadEdgeTextures();
        Texture lightEdge = textures[0];
        Texture darkEdge = textures[1];

        // Bal oldal - világos
        batch.draw(lightEdge, x, y, thickness, height);

        // Felső oldal - világos
        batch.draw(lightEdge, x, y + height - thickness, width, thickness);

        // Jobb oldal - sötét
        batch.draw(darkEdge, x + width - thickness, y, thickness, height);

        // Alsó oldal - sötét
        batch.draw(darkEdge, x, y, width, thickness);
    }

    private void setWindowSize(int width, int height) {
        Gdx.graphics.setWindowedMode(width, height);

        batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
        stage.getViewport().update(width, height, true);
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            int mouseX = Gdx.input.getX();
            int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

            int cellX = mouseX / cellSize;
            int cellY = mouseY / cellSize;

            boolean insideBoard =
                cellX >= 0 &&
                cellY >= 0 &&
                cellX < board.size &&
                cellY < board.size;

            if (insideBoard && !board.isGameOver) {
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                    board.reveal(cellX, cellY);
                } else if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                    toggleFlag(cellX, cellY);
                }
                board.checkWin();
            }
        }
    }

    private void toggleFlag(int cellX, int cellY) {
        Cell cell = board.matrix[cellX][cellY];

        if (!cell.isFlagged) {
            cell.isFlagged = true;
            board.flagsPlaced++;
        } else {
            cell.isFlagged = false;
            board.flagsPlaced--;
        }
    }

    private void updateTimer() {
        if (!board.isGameOver && !board.isWon) {
            board.timer -= Gdx.graphics.getDeltaTime();

            if (board.timer <= 0) {
                board.isGameOver = true;
            }
        }
    }

    private class Popup extends Dialog {
        public Popup(String title, Skin skin, Texture popupImageTexture) {
            super("", skin);

            setMovable(false);
            setResizable(false);
            // Elrejtjük a címsort (ha a skinben alapból lenne)
            getContentTable().defaults().space(10); // Beállítjuk a tartalom térközeit

            Image image = new Image(new TextureRegionDrawable(popupImageTexture));
            getContentTable().add(image).padTop(20).center().row(); 

            TextButton menu = new TextButton(title, skin, "default");
            getButtonTable().add(menu).padBottom(20);

            menu.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    hide();
                    main.gotoMenu();
                }
            });
        }
    }

    @Override
    public void resize(int width, int height) {
        if (batch != null) {
            batch.getProjectionMatrix().setToOrtho2D(0, 0, getWidth(), getHeight());
        }

        if (stage != null) {
            stage.getViewport().update(getWidth(), getHeight(), true);
        }
    }

    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }

        if (cell_Default != null) {
            cell_Default.dispose();
        }

        if (cell_Revealed != null) {
            cell_Revealed.dispose();
        }

        if (cell_Mine != null) {
            cell_Mine.dispose();
        }

        if (cell_Flag != null) {
            cell_Flag.dispose();
        }

        if (menu_Texture != null) {
            menu_Texture.dispose();
        }

        if (menuFont != null) {
            menuFont.dispose();
        }

        if (numbers != null) {
            for (Texture t : numbers) {
                if (t != null) {
                    t.dispose();
                }
            }
        }

        if (stage != null) {
            stage.dispose();
        }
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
}