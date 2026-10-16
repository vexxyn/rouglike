package com.example.roguelike.persistence;

import java.util.ArrayList;
import java.util.List;

/**
 * Bentuk data yang ditulis ke save.json (field public + constructor kosong supaya bisa di-serialize LibGDX Json).
 * Dungeon TIDAK disimpan: cukup seed + nomor lantai, lalu dibuat ulang (generator deterministik).
 */
public class SaveData {
    public long seed;
    public int floor, score;
    public int px, py;
    public int level, xp, maxHp, hp, baseAttack;
    public String weapon, armor;                     // nama item, null kalau kosong
    public List<String> inventory = new ArrayList<>();
    public List<EnemySave> enemies = new ArrayList<>();
    public List<ItemSave> groundItems = new ArrayList<>();

    public static class EnemySave {
        public String name;
        public int x, y, hp;
    }

    public static class ItemSave {
        public String name;
        public int x, y;
    }
}
