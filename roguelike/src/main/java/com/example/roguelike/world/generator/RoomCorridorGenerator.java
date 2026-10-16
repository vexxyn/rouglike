package com.example.roguelike.world.generator;

import com.example.roguelike.world.Dungeon;
import com.example.roguelike.world.Room;
import java.util.Random;

/** Algoritma sederhana: lempar ruangan acak, buang yang bertabrakan, sambung tiap ruangan ke ruangan sebelumnya. */
public class RoomCorridorGenerator implements DungeonGenerator {
    private static final int ATTEMPTS = 40;

    @Override
    public Dungeon generate(int w, int h, Random rng) {
        while (true) {
            Dungeon d = new Dungeon(w, h);
            Room prev = null;
            for (int i = 0; i < ATTEMPTS; i++) {
                int rw = 5 + rng.nextInt(7), rh = 4 + rng.nextInt(5);
                int rx = 1 + rng.nextInt(w - rw - 2), ry = 1 + rng.nextInt(h - rh - 2);
                Room r = new Room(rx, ry, rw, rh);
                if (d.rooms().stream().anyMatch(o -> o.intersects(r, 1))) continue;
                Carver.carveRoom(d, r);
                if (prev != null) Carver.carveCorridor(d, prev.centerX(), prev.centerY(), r.centerX(), r.centerY(), rng);
                prev = r;
            }
            if (d.rooms().size() >= 2) {
                Carver.finish(d);
                return d;
            }
        }
    }
}
