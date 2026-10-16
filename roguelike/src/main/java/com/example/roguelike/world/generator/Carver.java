package com.example.roguelike.world.generator;

import com.example.roguelike.world.Dungeon;
import com.example.roguelike.world.Point;
import com.example.roguelike.world.Room;
import com.example.roguelike.world.TileType;
import java.util.Random;

/** Helper bersama untuk semua generator: gali ruangan, gali lorong, lalu tentukan start & tangga. */
final class Carver {
    private Carver() {}

    static void carveRoom(Dungeon d, Room r) {
        for (int x = r.x(); x < r.x() + r.w(); x++)
            for (int y = r.y(); y < r.y() + r.h(); y++)
                d.setTile(x, y, TileType.FLOOR);
        d.rooms().add(r);
    }

    /** Lorong berbentuk L: horizontal dulu atau vertikal dulu (acak). */
    static void carveCorridor(Dungeon d, int x1, int y1, int x2, int y2, Random rng) {
        if (rng.nextBoolean()) {
            hLine(d, x1, x2, y1);
            vLine(d, y1, y2, x2);
        } else {
            vLine(d, y1, y2, x1);
            hLine(d, x1, x2, y2);
        }
    }

    private static void hLine(Dungeon d, int x1, int x2, int y) {
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) d.setTile(x, y, TileType.FLOOR);
    }

    private static void vLine(Dungeon d, int y1, int y2, int x) {
        for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) d.setTile(x, y, TileType.FLOOR);
    }

    /** Start = tengah ruangan pertama, tangga = tengah ruangan terjauh dari start. */
    static void finish(Dungeon d) {
        Room first = d.rooms().get(0);
        d.setStart(new Point(first.centerX(), first.centerY()));
        Room far = first;
        int best = -1;
        for (Room r : d.rooms()) {
            int dist = Math.abs(r.centerX() - first.centerX()) + Math.abs(r.centerY() - first.centerY());
            if (dist > best) { best = dist; far = r; }
        }
        Point s = new Point(far.centerX(), far.centerY());
        d.setStairs(s);
        d.setTile(s.x(), s.y(), TileType.STAIRS);
    }
}
