package com.example.roguelike.persistence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.example.roguelike.data.GameData;
import com.example.roguelike.game.GameWorld;

/** File I/O untuk save game (save.json di folder kerja) dan high score (Preferences LibGDX). */
public class SaveManager {
    private static final String FILE = "save.json";
    private final Json json = new Json(JsonWriter.OutputType.json);

    public boolean hasSave() { return Gdx.files.local(FILE).exists(); }

    public void save(GameWorld world) {
        Gdx.files.local(FILE).writeString(json.prettyPrint(SaveMapper.snapshot(world)), false);
    }

    public GameWorld load(GameData data) {
        SaveData s = json.fromJson(SaveData.class, Gdx.files.local(FILE));
        return SaveMapper.restore(s, data);
    }

    public void deleteSave() {
        if (hasSave()) Gdx.files.local(FILE).delete();
    }

    private Preferences prefs() { return Gdx.app.getPreferences("roguelike-portfolio"); }

    public int highScore() { return prefs().getInteger("highscore", 0); }

    /** @return true kalau skor ini rekor baru. */
    public boolean submitHighScore(int score) {
        if (score <= highScore()) return false;
        Preferences p = prefs();
        p.putInteger("highscore", score);
        p.flush();
        return true;
    }
}
