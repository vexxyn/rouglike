package com.example.roguelike.world.generator;

import com.example.roguelike.world.Dungeon;
import com.example.roguelike.world.Room;
import java.util.Random;

/**
 * Binary Space Partitioning.
 * 1) Bagi peta jadi dua area, bagi lagi tiap area, dst. (pohon biner)
 * 2) Di setiap daun pohon, taruh satu ruangan.
 * 3) Di setiap node internal, sambung satu ruangan dari anak kiri dengan satu ruangan dari anak kanan.
 * Karena strukturnya pohon dan tiap node disambung, SEMUA ruangan dijamin terhubung.
 */
public class BspGenerator implements DungeonGenerator {
    private static final int MIN_LEAF = 8;
    private static final int MAX_DEPTH = 4;

    private static class Node {
        final int x, y, w, h;
        Node a, b;
        Room room;
        Node(int x, int y, int w, int h) { this.x = x; this.y = y; this.w = w; this.h = h; }
    }

    @Override
    public Dungeon generate(int w, int h, Random rng) {
        while (true) {
            Dungeon d = new Dungeon(w, h);
            Node root = new Node(0, 0, w, h);
            split(root, rng, MAX_DEPTH);
            placeRooms(root, d, rng);
            connect(root, d, rng);
            if (d.rooms().size() >= 2) {
                Carver.finish(d);
                return d;
            }
        }
    }

    private void split(Node n, Random rng, int depth) {
        if (depth == 0) return;
        boolean splitHeight = n.w > n.h ? false : (n.h > n.w ? true : rng.nextBoolean());
        int max = (splitHeight ? n.h : n.w) - MIN_LEAF;
        if (max < MIN_LEAF) return;                       // terlalu kecil, jadi daun
        int cut = MIN_LEAF + rng.nextInt(max - MIN_LEAF + 1);
        if (splitHeight) {
            n.a = new Node(n.x, n.y, n.w, cut);
            n.b = new Node(n.x, n.y + cut, n.w, n.h - cut);
        } else {
            n.a = new Node(n.x, n.y, cut, n.h);
            n.b = new Node(n.x + cut, n.y, n.w - cut, n.h);
        }
        split(n.a, rng, depth - 1);
        split(n.b, rng, depth - 1);
    }

    private void placeRooms(Node n, Dungeon d, Random rng) {
        if (n.a != null) {
            placeRooms(n.a, d, rng);
            placeRooms(n.b, d, rng);
            return;
        }
        int rw = 4 + rng.nextInt(n.w - 5);                // 4 .. n.w-2
        int rh = 4 + rng.nextInt(n.h - 5);
        int rx = n.x + 1 + rng.nextInt(n.w - rw - 1);     // sisakan 1 tile dinding di tepi area
        int ry = n.y + 1 + rng.nextInt(n.h - rh - 1);
        n.room = new Room(rx, ry, rw, rh);
        Carver.carveRoom(d, n.room);
    }

    private Room anyRoom(Node n, Random rng) {
        if (n.room != null) return n.room;
        return anyRoom(rng.nextBoolean() ? n.a : n.b, rng);
    }

    private void connect(Node n, Dungeon d, Random rng) {
        if (n.a == null) return;
        connect(n.a, d, rng);
        connect(n.b, d, rng);
        Room ra = anyRoom(n.a, rng), rb = anyRoom(n.b, rng);
        Carver.carveCorridor(d, ra.centerX(), ra.centerY(), rb.centerX(), rb.centerY(), rng);
    }
}
