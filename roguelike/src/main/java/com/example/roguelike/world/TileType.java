package com.example.roguelike.world;

public enum TileType {
    WALL(false), FLOOR(true), STAIRS(true);

    private final boolean walkable;
    TileType(boolean walkable) { this.walkable = walkable; }
    public boolean walkable() { return walkable; }
}
