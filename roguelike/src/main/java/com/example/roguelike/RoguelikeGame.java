package com.example.roguelike;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.example.roguelike.data.DataLoader;
import com.example.roguelike.data.GameData;
import com.example.roguelike.persistence.SaveManager;
import com.example.roguelike.screen.MenuScreen;

/**
 * Entry point LibGDX. Memegang resource yang dipakai bersama oleh semua layar
 * (batch, shape renderer, font, data, save manager) supaya dibuat sekali dan di-dispose sekali.
 */
public class RoguelikeGame extends Game {
    public SpriteBatch batch;
    public ShapeRenderer shapes;
    public BitmapFont font;
    public GameData data;
    public SaveManager saves;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapes = new ShapeRenderer();
        font = new BitmapFont();            // font bawaan LibGDX, tidak perlu file aset
        data = DataLoader.load();
        saves = new SaveManager();
        setScreen(new MenuScreen(this));
    }

    @Override
    public void dispose() {
        super.dispose();                    // memanggil hide() di layar aktif (GameScreen auto-save di sini)
        batch.dispose();
        shapes.dispose();
        font.dispose();
    }
}
