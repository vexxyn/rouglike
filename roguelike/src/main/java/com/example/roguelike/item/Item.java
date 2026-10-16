package com.example.roguelike.item;

import com.example.roguelike.entity.Player;

public abstract class Item {
    private final String name;
    private final char glyph;

    protected Item(String name, char glyph) { this.name = name; this.glyph = glyph; }

    public String name() { return name; }
    public char glyph() { return glyph; }

    /** Dipakai dari inventory. @return pesan untuk message log. */
    public abstract String use(Player player);

    /** Teks singkat untuk UI inventory. */
    public abstract String describe();
}
