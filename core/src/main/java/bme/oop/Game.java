package bme.oop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import bme.oop.Displays.*;


public class Game implements Screen {
    private SpriteBatch batch;
    private Board board;
    private Texture tileClosed, tileOpen, tileMine, tileFlag;
    private Texture[] numbers;
    private int cellSize = 32;
    private int topBarHeight = 128;
    private Main main;
    private Display timer, counter;
    private Boolean timerEnabled=false;


    public Game(Main main, int size, String difficulty, boolean timer) {
        this.main = main;
        this.board = new Board(size, difficulty);
        this.timerEnabled = timer;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        tileClosed = new Texture("cell_graphics/cella.png");
        tileOpen = new Texture("cell_graphics/akna_0.png");
        tileMine = new Texture("cell_graphics/akna.png");
        tileFlag = new Texture("cell_graphics/cella_megjelolt.png");
        numbers = new Texture[9];
        timer = new Timer(32, board.size*cellSize  + topBarHeight - 3*cellSize);
        counter = new Counter(board.size*cellSize - 4 * cellSize, board.size*cellSize  + topBarHeight - 3*cellSize);
        for(int i = 0; i < 9; i++) {
            numbers[i] = new Texture("cell_graphics/akna_" + i + ".png");
        }
        Gdx.graphics.setWindowedMode(board.size*cellSize, board.size*cellSize + topBarHeight);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.7f, 0.7f, 0.7f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput();
        updateTimer();

        batch.begin();
        for (int x = 0; x < board.size; x++) {
            for (int y = 0; y < board.size; y++) {
                Cell cell = board.matrix[x][y];

                float drawY = y * cellSize; 
                float drawX = x * cellSize;

                if (!cell.isRevealed) {
                    batch.draw(cell.isFlagged ? tileFlag : tileClosed, drawX, drawY, cellSize, cellSize);
                } else {
                    if (cell.isMine)
                        batch.draw(tileMine, drawX, drawY, cellSize, cellSize);
                    else
                        batch.draw(numbers[cell.adjMines], drawX, drawY, cellSize, cellSize);
                }
            }
        }
        if (timerEnabled)
            timer.draw(batch, (int)board.timer);
        counter.draw(batch, board.mineCount-board.flagsPlaced);
        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            int mouseX = Gdx.input.getX();
            int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

            int cellX = mouseX / cellSize;
            int cellY = mouseY / cellSize;

            if (cellX < board.size && cellY < board.size && !board.isGameOver) {
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                    board.reveal(cellX, cellY);
                } else if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                    if (!board.matrix[cellX][cellY].isFlagged){
                        board.matrix[cellX][cellY].isFlagged = true;
                        board.flagsPlaced++;
                    }
                    else {
                        board.matrix[cellX][cellY].isFlagged = false;
                        board.flagsPlaced--;
                    }
                     
                }
            }
        }
    }

    private void updateTimer() {
        if (!board.isGameOver && !board.isWon) {
            board.timer -= Gdx.graphics.getDeltaTime();
            if (board.timer <= 0) board.isGameOver = true;
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        tileClosed.dispose();
        tileOpen.dispose();
        tileMine.dispose();
        tileFlag.dispose();
        
        for (Texture t : numbers) {
            if (t != null) t.dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
    }

    @Override
    public void pause() {
        throw new UnsupportedOperationException("Unimplemented method 'pause'");
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'resume'");
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'hide'");
    }
}