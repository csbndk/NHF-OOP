package bme.oop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import bme.oop.Displays.*;

public class Game implements Screen {

    private SpriteBatch batch;
    private Board board;
    private Texture cell_Deafult;
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


    @Override
    public void show() {
        batch = new SpriteBatch();

        cell_Deafult = new Texture("cell_graphics/cella.png");
        cell_Revealed = new Texture("cell_graphics/akna_0.png");
        cell_Mine = new Texture("cell_graphics/akna.png");
        cell_Flag = new Texture("cell_graphics/cella_megjelolt.png");

        numbers = new Texture[9];
        for (int i = 0; i < 9; i++) {
            numbers[i] = new Texture("cell_graphics/akna_" + i + ".png");
        }

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
                // 1. Dátum és fájlnév generálása
                String datum = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH_mm_ss"));
                FileHandle mentesFajl = Gdx.files.local("savedGames/" + datum + ".json");

                // 2. A tábla (board) JSON szöveggé alakítása
                String jsonSzoveg = gson.toJson(board);

                // 3. A szöveg kiírása a fájlba (a 'false' azt jelenti, hogy felülírja, nem pedig hozzáfűz)
                mentesFajl.writeString(jsonSzoveg, false);
                main.gotoMenu();
            }
        });

        stage.addActor(menu);
        Gdx.input.setInputProcessor(stage);

        int width = board.size * cellSize;
        int height = board.size * cellSize + topBarHeight;

        Gdx.graphics.setWindowedMode(width, height);

        batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
        stage.getViewport().update(width, height, true);
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.7f, 0.7f, 0.7f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput();

        if (timerEnabled) {
            updateTimer();
        }

        batch.begin();

        drawBoard();

        counter.draw(batch, board.mineCount - board.flagsPlaced);

        if (timerEnabled) {
            timer.draw(batch, (int) board.timer);
        }

        batch.end();

        stage.act(delta);
        stage.draw();
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
                        batch.draw(cell_Deafult, drawX, drawY, cellSize, cellSize);
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

    @Override
    public void resize(int width, int height) {
        if (batch != null) {
            batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
        }

        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
    }

    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }

        if (cell_Deafult != null) {
            cell_Deafult.dispose();
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