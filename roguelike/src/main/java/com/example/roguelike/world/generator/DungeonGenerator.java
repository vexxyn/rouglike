package com.example.roguelike.world.generator;

import com.example.roguelike.world.Dungeon;
import java.util.Random;

/** STRATEGY pattern: GameWorld tidak peduli algoritma apa yang dipakai, cukup panggil generate(). */
public interface DungeonGenerator {
    Dungeon generate(int width, int height, Random rng);
}
