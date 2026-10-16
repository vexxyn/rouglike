package com.example.roguelike;

import static org.junit.jupiter.api.Assertions.*;

import com.example.roguelike.world.Dungeon;
import com.example.roguelike.world.Point;
import com.example.roguelike.world.generator.BspGenerator;
import com.example.roguelike.world.generator.DungeonGenerator;
import com.example.roguelike.world.generator.RoomCorridorGenerator;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DungeonGeneratorTest {
    private static final int W = 60, H = 40;
    private static final int[][] DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    private static int walkableCount(Dungeon d) {
        int n = 0;
        for (int x = 0; x < d.width(); x++) for (int y = 0; y < d.height(); y++) if (d.isWalkable(x, y)) n++;
        return n;
    }

    private static Set<Point> reachableFromStart(Dungeon d) {
        Set<Point> seen = new HashSet<>();
        Deque<Point> queue = new ArrayDeque<>();
        queue.add(d.start());
        seen.add(d.start());
        while (!queue.isEmpty()) {
            Point p = queue.poll();
            for (int[] dir : DIRS) {
                Point n = new Point(p.x() + dir[0], p.y() + dir[1]);
                if (d.isWalkable(n.x(), n.y()) && seen.add(n)) queue.add(n);
            }
        }
        return seen;
    }

    @Test
    void everyWalkableTileIsConnectedForManySeeds() {
        for (DungeonGenerator g : List.of(new BspGenerator(), new RoomCorridorGenerator())) {
            for (long seed = 0; seed < 200; seed++) {
                Dungeon d = g.generate(W, H, new Random(seed));
                assertEquals(walkableCount(d), reachableFromStart(d).size(),
                        g.getClass().getSimpleName() + " seed " + seed);
            }
        }
    }

    @Test
    void stairsAreReachableAndDifferentFromStart() {
        for (DungeonGenerator g : List.of(new BspGenerator(), new RoomCorridorGenerator())) {
            for (long seed = 0; seed < 100; seed++) {
                Dungeon d = g.generate(W, H, new Random(seed));
                assertNotEquals(d.start(), d.stairs());
                assertTrue(reachableFromStart(d).contains(d.stairs()));
            }
        }
    }

    @Test
    void mapBorderIsAlwaysWall() {
        Dungeon d = new BspGenerator().generate(W, H, new Random(7));
        for (int x = 0; x < W; x++) { assertFalse(d.isWalkable(x, 0)); assertFalse(d.isWalkable(x, H - 1)); }
        for (int y = 0; y < H; y++) { assertFalse(d.isWalkable(0, y)); assertFalse(d.isWalkable(W - 1, y)); }
    }

    @Test
    void sameSeedGivesSameDungeon() {
        Dungeon a = new BspGenerator().generate(W, H, new Random(99));
        Dungeon b = new BspGenerator().generate(W, H, new Random(99));
        for (int x = 0; x < W; x++) for (int y = 0; y < H; y++) assertEquals(a.tile(x, y), b.tile(x, y));
    }
}
