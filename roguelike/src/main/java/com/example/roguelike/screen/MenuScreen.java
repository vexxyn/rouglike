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
import com.example.roguelike.game.GameWorld;
import com.example.roguelike.world.generator.BspGenerator;

public class MenuScreen extends ScreenAdapter {
    private final RoguelikeGame game;
    private final OrthographicCamera cam = new OrthographicCamera();
    private final Viewport viewport = new FitViewport(GameScreen.VW, GameScreen.VH, cam);
    private String error = "";

    public MenuScreen(RoguelikeGame game) { this.game = game; }

    @Override
    public void resize(int w, int h) { viewport.update(w, h, true); }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            game.saves.deleteSave();
            game.setScreen(new GameScreen(game, GameWorld.newGame(System.nanoTime(), game.data, new BspGenerator())));
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.C) && game.saves.hasSave()) {
            try {
                game.setScreen(new GameScreen(game, game.saves.load(game.data)));
                return;
            } catch (Exception e) {                 // save rusak: jangan crash, hapus saja
                Gdx.app.error("Menu", "Gagal load save", e);
                game.saves.deleteSave();
                error = "Save file rusak dan dihapus.";
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) Gdx.app.exit();

        ScreenUtils.clear(0.04f, 0.04f, 0.07f, 1f);
        viewport.apply();
        game.batch.setProjectionMatrix(cam.combined);
        game.batch.begin();
        var font = game.font;
        font.getData().setScale(3f);
        font.setColor(Color.GOLD);
        font.draw(game.batch, "DUNGEON DELVER", 250, 500);
        font.getData().setScale(1.4f);
        font.setColor(Color.WHITE);
        font.draw(game.batch, "[N]  New game", 380, 380);
        font.setColor(game.saves.hasSave() ? Color.WHITE : Color.DARK_GRAY);
        font.draw(game.batch, "[C]  Continue", 380, 340);
        font.setColor(Color.WHITE);
        font.draw(game.batch, "[Esc]  Quit", 380, 300);
        font.getData().setScale(1f);
        font.setColor(Color.LIGHT_GRAY);
        font.draw(game.batch, "High score: " + game.saves.highScore(), 400, 230);
        font.draw(game.batch, "Arrows/WASD move  -  walk into enemies to attack  -  I inventory  -  Space wait", 190, 80);
        if (!error.isEmpty()) { font.setColor(Color.SCARLET); font.draw(game.batch, error, 380, 190); }
        game.batch.end();
    }
}
