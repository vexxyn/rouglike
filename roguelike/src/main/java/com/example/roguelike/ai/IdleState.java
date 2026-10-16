package com.example.roguelike.ai;

import com.example.roguelike.entity.Enemy;
import com.example.roguelike.game.GameWorld;

/** Diam sampai pemain terlihat, lalu mulai mengejar. */
public class IdleState implements EnemyState {
    @Override
    public void update(Enemy self, GameWorld world) {
        if (world.canSeePlayer(self)) self.setState(new ChaseState());
    }
}
