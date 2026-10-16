package com.example.roguelike.ai;

import com.example.roguelike.entity.Enemy;
import com.example.roguelike.game.GameWorld;
import com.example.roguelike.world.Point;
import java.util.List;

/** Kejar pemain pakai A*. Menyerah (kembali Idle) kalau pemain sudah terlalu jauh. */
public class ChaseState implements EnemyState {
    @Override
    public void update(Enemy self, GameWorld world) {
        var player = world.player();
        if (self.manhattanTo(player) == 1) {
            self.setState(new AttackState());
            world.attackPlayer(self);
            return;
        }
        if (self.manhattanTo(player) > self.sightRange() * 2) {
            self.setState(new IdleState());
            return;
        }
        List<Point> path = AStarPathfinder.findPath(
                new Point(self.x(), self.y()), new Point(player.x(), player.y()),
                world.dungeon().width(), world.dungeon().height(),
                (x, y) -> world.dungeon().isWalkable(x, y) && world.enemyAt(x, y) == null);
        if (path.isEmpty()) return;                       // buntu (misal terhalang musuh lain): tunggu
        Point next = path.get(0);
        if (!world.isOccupied(next.x(), next.y())) self.setPosition(next.x(), next.y());
    }
}
