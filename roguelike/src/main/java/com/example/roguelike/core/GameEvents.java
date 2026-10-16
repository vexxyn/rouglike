package com.example.roguelike.core;

import com.example.roguelike.entity.Entity;
import com.example.roguelike.item.Item;

/** Semua event yang beredar di EventBus. */
public final class GameEvents {
    private GameEvents() {}

    public record EntityDamaged(Entity attacker, Entity target, int amount) {}
    public record EntityDied(Entity victim, Entity killer) {}
    public record ItemPickedUp(Item item) {}
    public record FloorChanged(int floor) {}
}
