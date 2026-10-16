package com.example.roguelike.ai;

import com.example.roguelike.entity.Enemy;
import com.example.roguelike.game.GameWorld;

/** Menyerang selama pemain masih bersebelahan; kalau pemain kabur, kembali mengejar. */
public class AttackState implements EnemyState {
    @Override
    public void update(Enemy self, GameWorld world) {
        if (self.manhattanTo(world.player()) == 1) {
            world.attackPlayer(self);
        } else {
            ChaseState chase = new ChaseState();
            self.setState(chase);
            chase.update(self, world);
        }
    }
}
