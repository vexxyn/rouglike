package com.example.roguelike.entity;

/** Dasar semua makhluk di dungeon (Player dan Enemy). */
public abstract class Entity {
    private final String name;
    private final char glyph;
    private final Stats stats;
    private int x, y;

    protected Entity(String name, char glyph, int x, int y, Stats stats) {
        this.name = name;
        this.glyph = glyph;
        this.x = x;
        this.y = y;
        this.stats = stats;
    }

    public String name() { return name; }
    public char glyph() { return glyph; }
    public int x() { return x; }
    public int y() { return y; }
    public Stats stats() { return stats; }
    public void setPosition(int x, int y) { this.x = x; this.y = y; }
    public boolean isAlive() { return stats.hp() > 0; }

    /** Subclass boleh menambah bonus (misal dari senjata). */
    public int attackPower() { return stats.attack(); }
    public int defensePower() { return stats.defense(); }

    public int manhattanTo(Entity o) { return Math.abs(x - o.x) + Math.abs(y - o.y); }
}
