package com.example.roguelike.ai;

import com.example.roguelike.world.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.BiPredicate;

/**
 * A* di grid 4 arah.
 * f = g + h, g = langkah dari start, h = tebakan jarak ke tujuan (Manhattan). Selalu perluas node dengan f terkecil.
 * Karena Manhattan tidak pernah melebihi jarak sebenarnya, hasilnya dijamin jalur terpendek.
 */
public final class AStarPathfinder {
    private AStarPathfinder() {}

    private static final int[][] DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    private record Node(Point p, int g, int f) {}

    /**
     * @param passable apakah tile (x,y) boleh dilewati. Tile tujuan selalu dianggap boleh (biasanya ditempati target).
     * @return langkah-langkah dari SETELAH start sampai tujuan; kosong kalau tidak ada jalur atau start == goal.
     */
    public static List<Point> findPath(Point start, Point goal, int width, int height, BiPredicate<Integer, Integer> passable) {
        if (start.equals(goal)) return List.of();
        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingInt(Node::f));
        Map<Point, Point> cameFrom = new HashMap<>();
        Map<Point, Integer> best = new HashMap<>();
        best.put(start, 0);
        open.add(new Node(start, 0, heuristic(start, goal)));

        while (!open.isEmpty()) {
            Node cur = open.poll();
            if (cur.p().equals(goal)) return reconstruct(cameFrom, start, goal);
            if (cur.g() > best.getOrDefault(cur.p(), Integer.MAX_VALUE)) continue;   // entri usang di antrean
            for (int[] d : DIRS) {
                int nx = cur.p().x() + d[0], ny = cur.p().y() + d[1];
                if (nx < 0 || ny < 0 || nx >= width || ny >= height) continue;
                Point np = new Point(nx, ny);
                if (!np.equals(goal) && !passable.test(nx, ny)) continue;
                int ng = cur.g() + 1;
                if (ng < best.getOrDefault(np, Integer.MAX_VALUE)) {
                    best.put(np, ng);
                    cameFrom.put(np, cur.p());
                    open.add(new Node(np, ng, ng + heuristic(np, goal)));
                }
            }
        }
        return List.of();
    }

    private static int heuristic(Point a, Point b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }

    private static List<Point> reconstruct(Map<Point, Point> cameFrom, Point start, Point goal) {
        List<Point> path = new ArrayList<>();
        for (Point p = goal; !p.equals(start); p = cameFrom.get(p)) path.add(p);
        Collections.reverse(path);
        return path;
    }
}
