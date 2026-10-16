package com.example.roguelike.world;

/** Fog of war: tile terlihat kalau ada garis lurus tanpa dinding dari pemain dalam radius tertentu. */
public final class FieldOfView {
    private FieldOfView() {}

    public static void compute(Dungeon d, int ox, int oy, int radius) {
        d.clearVisible();
        for (int x = ox - radius; x <= ox + radius; x++) {
            for (int y = oy - radius; y <= oy + radius; y++) {
                int dx = x - ox, dy = y - oy;
                if (dx * dx + dy * dy > radius * radius) continue;
                if (d.inBounds(x, y) && hasLineOfSight(d, ox, oy, x, y)) d.markVisible(x, y);
            }
        }
    }

    /** Garis Bresenham dari (x0,y0) ke (x1,y1). Dinding menghalangi, tapi dinding itu sendiri tetap terlihat. */
    public static boolean hasLineOfSight(Dungeon d, int x0, int y0, int x1, int y1) {
        int dx = Math.abs(x1 - x0), dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1, sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;
        int x = x0, y = y0;
        while (true) {
            if (x == x1 && y == y1) return true;
            if (!(x == x0 && y == y0) && d.tile(x, y) == TileType.WALL) return false;
            int e2 = 2 * err;
            if (e2 > -dy) { err -= dy; x += sx; }
            if (e2 < dx) { err += dx; y += sy; }
        }
    }
}
