package com.example.roguelike.item;

import com.example.roguelike.data.GameData;
import com.example.roguelike.data.ItemDef;
import java.util.List;
import java.util.Random;

/** FACTORY: bikin Item dari definisi data. Menambah item baru = tambah baris di items.json, tanpa ubah kode. */
public final class ItemFactory {
    private ItemFactory() {}

    public static Item create(ItemDef d) {
        return switch (d.type()) {
            case "potion" -> new Potion(d.name(), d.value());
            case "weapon" -> new Weapon(d.name(), d.value());
            case "armor" -> new Armor(d.name(), d.value());
            default -> throw new IllegalArgumentException("Unknown item type: " + d.type());
        };
    }

    /** Pilih item acak (berbobot) yang boleh muncul di lantai ini. */
    public static Item random(GameData data, int floor, Random rng) {
        List<ItemDef> pool = data.itemsForFloor(floor);
        int total = pool.stream().mapToInt(ItemDef::weight).sum();
        int r = rng.nextInt(total);
        for (ItemDef d : pool) {
            r -= d.weight();
            if (r < 0) return create(d);
        }
        return create(pool.get(0));
    }
}
