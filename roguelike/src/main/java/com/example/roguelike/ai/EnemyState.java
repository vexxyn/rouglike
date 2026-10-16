package com.example.roguelike.ai;

import com.example.roguelike.entity.Enemy;
import com.example.roguelike.game.GameWorld;

/** STATE pattern: perilaku musuh = objek state. Pindah perilaku = ganti objek state. */
public interface EnemyState {
    void update(Enemy self, GameWorld world);
}
