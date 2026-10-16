package com.example.roguelike.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import java.util.ArrayList;
import java.util.List;

/** Baca assets/data/enemies.json dan items.json. Mau tambah musuh/item? Edit JSON saja, tidak perlu ubah kode. */
public final class DataLoader {
    private DataLoader() {}

    public static GameData load() {
        try {
            List<EnemyDef> enemies = new ArrayList<>();
            for (JsonValue v : parse("data/enemies.json")) {
                enemies.add(new EnemyDef(v.getString("name"), v.getString("glyph"), v.getInt("hp"),
                        v.getInt("attack"), v.getInt("defense"), v.getInt("sight", 8),
                        v.getInt("score", 10), v.getInt("minFloor", 1)));
            }
            List<ItemDef> items = new ArrayList<>();
            for (JsonValue v : parse("data/items.json")) {
                items.add(new ItemDef(v.getString("type"), v.getString("name"), v.getInt("value"),
                        v.getInt("minFloor", 1), v.getInt("weight", 1)));
            }
            if (enemies.isEmpty() || items.isEmpty()) throw new IllegalStateException("data file kosong");
            return new GameData(enemies, items);
        } catch (Exception e) {
            Gdx.app.error("DataLoader", "Gagal baca JSON, pakai data bawaan", e);
            return GameData.defaults();
        }
    }

    private static JsonValue parse(String path) {
        FileHandle file = Gdx.files.internal(path);
        return new JsonReader().parse(file);
    }
}
