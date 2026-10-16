package com.example.roguelike.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.example.roguelike.RoguelikeGame;

public class GameOverScreen extends ScreenAdapter {
    private final RoguelikeGame game;
    private final boolean victory;
    private final int score, floor;
    private final boolean newRecord;
    private final OrthographicCamera cam = new OrthographicCamera();
    private final Viewport viewport = new FitViewport(GameScreen.VW, GameScreen.VH, cam);

    public GameOverScreen(RoguelikeGame game, boolean victory, int score, int floor) {
        this.game = game;
        this.victory = victory;
        this.score = score;
        this.floor = floor;
        this.newRecord = game.saves.submitHighScore(score);
    }

    @Override
    public void resize(int w, int h) { viewport.update(w, h, true); }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.setScreen(new MenuScreen(game));
            return;
        }
        ScreenUtils.clear(0.06f, 0.02f, 0.02f, 1f);
        viewport.apply();
        game.batch.setProjectionMatrix(cam.combined);
        game.batch.begin();
        var font = game.font;
        font.getData().setScale(3f);
        font.setColor(victory ? Color.GOLD : Color.SCARLET);
        font.draw(game.batch, victory ? "VICTORY!" : "YOU DIED", victory ? 340 : 330, 470);
        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);
        font.draw(game.batch, "Reached floor " + floor, 370, 370);
        font.draw(game.batch, "Score: " + score, 400, 330);
        if (newRecord) { font.setColor(Color.GOLD); font.draw(game.batch, "New high score!", 370, 290); }
        else { font.setColor(Color.LIGHT_GRAY); font.draw(game.batch, "High score: " + game.saves.highScore(), 370, 290); }
        font.getData().setScale(1f);
        font.setColor(Color.LIGHT_GRAY);
        font.draw(game.batch, "Press Enter to return to menu", 370, 200);
        game.batch.end();
    }
}
