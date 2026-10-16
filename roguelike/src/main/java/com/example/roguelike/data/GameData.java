package com.example.roguelike.data;

import java.util.List;

/** Kumpulan definisi musuh & item. Biasanya diisi dari assets/data/*.json (lihat DataLoader); defaults() sebagai cadangan & untuk test. */
public class GameData {
    private final List<EnemyDef> enemies;
    private final List<ItemDef> items;

    public GameData(List<EnemyDef> enemies, List<ItemDef> items) {
        this.enemies = List.copyOf(enemies);
        this.items = List.copyOf(items);
    }

    public List<EnemyDef> enemiesForFloor(int floor) {
        return enemies.stream().filter(e -> e.minFloor() <= floor).toList();
    }

    public List<ItemDef> itemsForFloor(int floor) {
        return items.stream().filter(i -> i.minFloor() <= floor).toList();
    }

    public ItemDef itemByName(String name) {
        return items.stream().filter(i -> i.name().equals(name)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown item: " + name));
    }

    public EnemyDef enemyByName(String name) {
        return enemies.stream().filter(e -> e.name().equals(name)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown enemy: " + name));
    }

    public static GameData defaults() {
        return new GameData(
            List.of(
                new EnemyDef("Rat",      "r",  6, 3, 0, 7,  10, 1),
                new EnemyDef("Goblin",   "g", 12, 5, 1, 8,  20, 1),
                new EnemyDef("Skeleton", "s", 18, 7, 2, 8,  35, 2),
                new EnemyDef("Orc",      "o", 28, 9, 3, 9,  60, 3),
                new EnemyDef("Troll",    "T", 45, 11, 4, 9, 120, 4)),
            List.of(
                new ItemDef("potion", "Small Potion", 15, 1, 6),
                new ItemDef("potion", "Big Potion",   30, 2, 4),
                new ItemDef("weapon", "Dagger",        2, 1, 2),
                new ItemDef("weapon", "Sword",         4, 2, 2),
                new ItemDef("weapon", "War Axe",       6, 3, 1),
                new ItemDef("armor",  "Leather Armor", 1, 1, 2),
                new ItemDef("armor",  "Chain Mail",    3, 2, 2),
                new ItemDef("armor",  "Plate Armor",   5, 4, 1)));
    }
}
