package bme.oop;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import bme.oop.Displays.Counter;
import bme.oop.Displays.Display;
import bme.oop.Displays.Timer;

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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.google.gson.Gson;

/**
 * Az aknakereső játék képernyője.
 * Kezeli a tábla kirajzolását, a játékos kattintásait, az időzítőt és a mentést.
 */
public class Game implements Screen {

    private SpriteBatch batch;
    private Board board;

    private Texture cellDefault;
    private Texture cellMine;
    private Texture cellFlag;
    private Texture[] numbers;

    private Texture lightEdge;
    private Texture darkEdge;

    private Texture popupWinTexture;
    private Texture popupLoseTexture;
    private Skin popupSkin;

    private final int cellSize = 32;
    private final int topBarHeight = 128;

    private TextButton menu;
    private Stage stage;
    private Main main;

    private Display timer;
    private Display counter;

    private boolean timerEnabled = false;
    private boolean popupShown = false;

    /**
     * Mentett tábla alapján hoz létre játék képernyőt.
     * Betöltéskor ezt a konstruktort használja a program.
     */
    public Game(Main main, Board board) {
        this.main = main;
        this.board = board;
    }

    /**
     * Új játékot hoz létre a megadott beállítások alapján.
     * A konstruktor létrehozza a Board objektumot is.
     */
    public Game(Main main, int size, String difficulty, boolean timerEnabled) {
        this.main = main;
        this.board = new Board(size, difficulty);
        this.timerEnabled = timerEnabled;
    }

    /**
     * Visszaadja a játékablak szélességét pixelben.
     * A szélesség a tábla méretétől és a cellamérettől függ.
     */
    public int getWidth() {
        return board.size * cellSize;
    }

    /**
     * Visszaadja a játékablak magasságát pixelben.
     * A magasság tartalmazza a felső információs sávot is.
     */
    public int getHeight() {
        return board.size * cellSize + topBarHeight;
    }

    /**
     * A képernyő megjelenésekor inicializálja a játék grafikus elemeit.
     * Betölti a textúrákat, létrehozza a Stage-et és beállítja az ablakméretet.
     */
    @Override
    public void show() {
        batch = new SpriteBatch();

        loadTextures();
        loadEdgeTextures();
        loadPopupResources();

        stage = new Stage(new ScreenViewport(), batch);

        drawDisplays();
        addMenuButton();

        setWindowSize(getWidth(), getHeight());
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Minden képkockában frissíti és kirajzolja a játékot.
     * Kezeli az inputot, az időzítőt, a játékállapotot és a Stage elemeit.
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.7f, 0.7f, 0.7f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput();

        if (timerEnabled) {
            updateTimer();
        }

        checkGameStatus();

        batch.begin();
        drawBoard();
        counter.draw(batch, board.mineCount - board.flagsPlaced);
        timer.draw(batch, (int) board.timer);
        drawEdges();
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    /**
     * Betölti a cellákhoz és számokhoz tartozó textúrákat.
     * Ezek alapján történik a játéktábla kirajzolása.
     */
    private void loadTextures() {
        cellDefault = new Texture("cell_graphics/cella.png");
        cellMine = new Texture("cell_graphics/akna.png");
        cellFlag = new Texture("cell_graphics/cella_megjelolt.png");

        numbers = new Texture[9];

        for (int i = 0; i < 9; i++) {
            numbers[i] = new Texture("cell_graphics/akna_" + i + ".png");
        }
    }

    /**
     * Létrehozza a szegélyekhez használt egyszínű textúrákat.
     * Ezekkel rajzolja a játék a klasszikus aknakereső stílusú kereteket.
     */
    private void loadEdgeTextures() {
        Pixmap lightPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        lightPixmap.setColor(0.745f, 0.745f, 0.745f, 1f);
        lightPixmap.fill();
        lightEdge = new Texture(lightPixmap);
        lightPixmap.dispose();

        Pixmap darkPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        darkPixmap.setColor(0.529f, 0.529f, 0.529f, 1f);
        darkPixmap.fill();
        darkEdge = new Texture(darkPixmap);
        darkPixmap.dispose();
    }

    /**
     * Betölti a popup ablakhoz szükséges erőforrásokat.
     * A győzelem és vereség képeit, valamint a skint is itt inicializálja.
     */
    private void loadPopupResources() {
        popupSkin = new Skin(Gdx.files.internal("uiskin.json"));
        popupWinTexture = new Texture("popups/game_win.png");
        popupLoseTexture = new Texture("popups/game_lose.png");
    }

    /**
     * Létrehozza a felső sávban látható számlálókat.
     * A bal oldalon az aknaszámláló, jobb oldalon az időzítő jelenik meg.
     */
    private void drawDisplays() {
        counter = new Counter(
                32,
                board.size * cellSize + topBarHeight - 3 * cellSize
        );

        timer = new Timer(
                board.size * cellSize - 4 * cellSize,
                board.size * cellSize + topBarHeight - 3 * cellSize
        );
    }

    /**
     * Létrehozza a felső menü gombot.
     * A gomb lenyomásakor menti a játékállást és visszatér a főmenübe.
     */
    private void addMenuButton() {
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
            /**
             * A menü gombra kattintáskor menti a játékot.
             * A mentés után visszavált a főmenü képernyőre.
             */
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                saveGame();
                main.gotoMenu();
            }
        });

        stage.addActor(menu);
    }

    /**
     * Elmenti az aktuális játékállást JSON fájlba.
     * A mentés fájlneve az aktuális dátum és idő alapján készül.
     */
    private void saveGame() {
        Gson gson = new Gson();

        String datum = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH_mm_ss")
        );

        FileHandle mentesFajl = Gdx.files.local("savedGames/" + datum + ".json");
        String jsonSzoveg = gson.toJson(board);

        mentesFajl.writeString(jsonSzoveg, false);
    }

    /**
     * Kirajzolja a teljes játéktáblát.
     * A cella állapotától függően zárt, zászlós, aknás vagy számozott textúrát rajzol.
     */
    private void drawBoard() {
        for (int x = 0; x < board.size; x++) {
            for (int y = 0; y < board.size; y++) {
                Cell cell = board.matrix[x][y];

                float drawX = x * cellSize;
                float drawY = y * cellSize;

                if (!cell.isRevealed) {
                    if (cell.isFlagged) {
                        batch.draw(cellFlag, drawX, drawY, cellSize, cellSize);
                    } else {
                        batch.draw(cellDefault, drawX, drawY, cellSize, cellSize);
                    }
                } else {
                    if (cell.isMine) {
                        batch.draw(cellMine, drawX, drawY, cellSize, cellSize);
                    } else {
                        batch.draw(numbers[cell.adjMines], drawX, drawY, cellSize, cellSize);
                    }
                }
            }
        }
    }

    /**
     * Kezeli a játékos egérkattintásait a táblán.
     * Bal kattintásra felfed, jobb kattintásra zászlót helyez vagy vesz le.
     */
    private void handleInput() {
        if (popupShown) {
            return;
        }

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

            if (insideBoard && !board.isGameOver && !board.isWon) {
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                    board.reveal(cellX, cellY);
                } else if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                    toggleFlag(cellX, cellY);
                }

                board.checkWin();
            }
        }
    }

    /**
     * Zászlót helyez el vagy távolít el egy celláról.
     * Felfedett cellára nem lehet zászlót rakni, és nem lehet több zászló, mint akna.
     */
    private void toggleFlag(int cellX, int cellY) {
        Cell cell = board.matrix[cellX][cellY];

        if (cell.isRevealed) {
            return;
        }

        if (!cell.isFlagged) {
            if (board.flagsPlaced >= board.mineCount) {
                return;
            }

            cell.isFlagged = true;
            board.flagsPlaced++;
        } else {
            cell.isFlagged = false;
            board.flagsPlaced--;
        }
    }

    /**
     * Frissíti az időzítőt, ha az engedélyezve van.
     * Ha az idő lejár, a játék vereséggel véget ér.
     */
    private void updateTimer() {
        if (!board.isGameOver && !board.isWon) {
            board.timer -= Gdx.graphics.getDeltaTime();

            if (board.timer <= 0) {
                board.timer = 0;
                board.isGameOver = true;
            }
        }
    }

    /**
     * Ellenőrzi, hogy a játék véget ért-e.
     * Győzelem vagy vereség esetén popup ablakot jelenít meg.
     */
    private void checkGameStatus() {
        if (popupShown) {
            return;
        }

        if (!board.isGameOver && !board.isWon) {
            return;
        }

        Texture popupTexture = board.isWon ? popupWinTexture : popupLoseTexture;

        Popup popup = new Popup("Menu", popupSkin, popupTexture);
        popup.show(stage);

        popupShown = true;
    }

    /**
     * Kirajzolja a felső sáv és a kijelzők szegélyeit.
     * A szegélyek világos és sötét oldallal térhatásúak.
     */
    private void drawEdges() {
        int boardWidth = board.size * cellSize;
        int boardHeight = board.size * cellSize;
        int thickness = 4;

        int displayY = boardHeight + topBarHeight - 3 * cellSize - thickness;
        int displayWidth = 3 * cellSize + 2 * thickness;
        int displayHeight = cellSize * 2 + 2 * thickness;

        drawBeveledBorder(0, boardHeight, boardWidth, topBarHeight);
        drawBeveledBorder(32 - thickness, displayY, displayWidth, displayHeight);
        drawBeveledBorder(boardWidth - 4 * cellSize - thickness, displayY, displayWidth, displayHeight);
    }

    /**
     * Egy térhatású keretet rajzol a megadott pozícióra.
     * A világos és sötét textúrák segítségével klasszikus aknakereső keretet hoz létre.
     */
    private void drawBeveledBorder(float x, float y, float width, float height) {
        float thickness = 4;

        batch.draw(lightEdge, x, y, thickness, height);
        batch.draw(lightEdge, x, y + height - thickness, width, thickness);

        batch.draw(darkEdge, x + width - thickness, y, thickness, height);
        batch.draw(darkEdge, x, y, width, thickness);
    }

    /**
     * Beállítja az ablakméretet és a vetítési mátrixot.
     * A Stage viewportját is az új mérethez igazítja.
     */
    private void setWindowSize(int width, int height) {
        Gdx.graphics.setWindowedMode(width, height);
        batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);

        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
    }

    /**
     * A játék végi popup ablak belső osztálya.
     * Egy képet és egy menübe visszatérő gombot jelenít meg.
     */
    private class Popup extends Dialog {

        /**
         * Létrehozza a popup ablakot a megadott gombfelirattal és képpel.
         * A gomb megnyomásakor a result metódus fut le.
         */
        public Popup(String title, Skin skin, Texture popupImageTexture) {
            super("", skin);

            this.setBackground((Drawable) null);
            setMovable(false);
            setResizable(false);

            Image image = new Image(new TextureRegionDrawable(popupImageTexture));
            getContentTable().add(image).padTop(20).center().row();

            button(title, "menu");
        }

        /**
         * A popup gomb eredményét kezeli.
         * Menü választás esetén visszavált a főmenü képernyőre.
         */
        @Override
        protected void result(Object object) {
            if ("menu".equals(object)) {
                hide();
                main.gotoMenu();
            }
        }

        /**
         * Visszaadja a popup ajánlott szélességét.
         * A szélesség a Stage szélességének 90%-a.
         */
        @Override
        public float getPrefWidth() {
            if (getStage() != null) {
                return getStage().getWidth() * 0.9f;
            }

            return super.getPrefWidth();
        }

        /**
         * Visszaadja a popup ajánlott magasságát.
         * A magasság a Stage magasságának 80%-a.
         */
        @Override
        public float getPrefHeight() {
            if (getStage() != null) {
                return getStage().getHeight() * 0.8f;
            }

            return super.getPrefHeight();
        }
    }

    /**
     * Az ablak átméretezésekor frissíti a kamera és a Stage méretét.
     * Null objektumok esetén biztonságosan nem végez műveletet.
     */
    @Override
    public void resize(int width, int height) {
        if (batch != null) {
            batch.getProjectionMatrix().setToOrtho2D(0, 0, getWidth(), getHeight());
        }

        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
    }

    /**
     * Felszabadítja a játék által használt grafikus erőforrásokat.
     * A textúrákat, Stage-et, Skin-t és Batch-et is lezárja.
     */
    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }

        if (cellDefault != null) {
            cellDefault.dispose();
        }

        if (cellMine != null) {
            cellMine.dispose();
        }

        if (cellFlag != null) {
            cellFlag.dispose();
        }

        if (numbers != null) {
            for (Texture t : numbers) {
                if (t != null) {
                    t.dispose();
                }
            }
        }

        if (lightEdge != null) {
            lightEdge.dispose();
        }

        if (darkEdge != null) {
            darkEdge.dispose();
        }

        if (popupWinTexture != null) {
            popupWinTexture.dispose();
        }

        if (popupLoseTexture != null) {
            popupLoseTexture.dispose();
        }

        if (popupSkin != null) {
            popupSkin.dispose();
        }

        if (stage != null) {
            stage.dispose();
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
}