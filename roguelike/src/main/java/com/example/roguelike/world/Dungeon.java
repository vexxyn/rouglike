package com.example.roguelike.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Grid tile satu lantai. Indeks selalu [x][y]. Juga menyimpan status "pernah dilihat" & "sedang terlihat" (fog of war). */
public class Dungeon {
    private final int width, height;
    private final TileType[][] tiles;
    private final boolean[][] explored, visible;
    private final List<Room> rooms = new ArrayList<>();
    private Point start, stairs;

    public Dungeon(int width, int height) {
        this.width = width;
        this.height = height;
        tiles = new TileType[width][height];
        explored = new boolean[width][height];
        visible = new boolean[width][height];
        for (TileType[] col : tiles) Arrays.fill(col, TileType.WALL);
    }

    public int width() { return width; }
    public int height() { return height; }
    public boolean inBounds(int x, int y) { return x >= 0 && y >= 0 && x < width && y < height; }

    public TileType tile(int x, int y) { return inBounds(x, y) ? tiles[x][y] : TileType.WALL; }
    public void setTile(int x, int y, TileType t) { if (inBounds(x, y)) tiles[x][y] = t; }
    public boolean isWalkable(int x, int y) { return tile(x, y).walkable(); }

    public boolean explored(int x, int y) { return inBounds(x, y) && explored[x][y]; }
    public boolean visible(int x, int y) { return inBounds(x, y) && visible[x][y]; }
    public void clearVisible() { for (boolean[] col : visible) Arrays.fill(col, false); }
    public void markVisible(int x, int y) {
        if (!inBounds(x, y)) return;
        visible[x][y] = true;
        explored[x][y] = true;
    }

    public List<Room> rooms() { return rooms; }
    public Point start() { return start; }
    public Point stairs() { return stairs; }
    public void setStart(Point p) { start = p; }
    public void setStairs(Point p) { stairs = p; }
}
