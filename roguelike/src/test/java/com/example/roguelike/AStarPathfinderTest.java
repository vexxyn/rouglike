package com.example.roguelike;

import static org.junit.jupiter.api.Assertions.*;

import com.example.roguelike.ai.AStarPathfinder;
import com.example.roguelike.world.Point;
import java.util.List;
import org.junit.jupiter.api.Test;

class AStarPathfinderTest {
    @Test
    void straightLineInOpenGrid() {
        List<Point> path = AStarPathfinder.findPath(new Point(0, 0), new Point(4, 0), 10, 10, (x, y) -> true);
        assertEquals(4, path.size());
        assertEquals(new Point(1, 0), path.get(0));
        assertEquals(new Point(4, 0), path.get(3));
    }

    @Test
    void goesAroundWallThroughTheOnlyGap() {
        // dinding vertikal di x=2, satu-satunya celah di y=5
        List<Point> path = AStarPathfinder.findPath(new Point(0, 0), new Point(4, 0), 10, 10, (x, y) -> x != 2 || y == 5);
        assertEquals(14, path.size());                      // 5 naik + 4 kanan + 5 turun
        assertTrue(path.contains(new Point(2, 5)));
    }

    @Test
    void returnsEmptyWhenNoPathExists() {
        assertTrue(AStarPathfinder.findPath(new Point(0, 0), new Point(4, 0), 10, 10, (x, y) -> x != 2).isEmpty());
    }

    @Test
    void returnsEmptyWhenStartEqualsGoal() {
        assertTrue(AStarPathfinder.findPath(new Point(3, 3), new Point(3, 3), 10, 10, (x, y) -> true).isEmpty());
    }

    @Test
    void goalTileIsAllowedEvenIfNotPassable() {
        // tujuan biasanya ditempati target (pemain), jadi tetap harus bisa dicapai
        List<Point> path = AStarPathfinder.findPath(new Point(0, 0), new Point(2, 0), 10, 10, (x, y) -> !(x == 2 && y == 0));
        assertEquals(2, path.size());
    }
}
