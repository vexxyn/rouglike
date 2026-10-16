package com.example.roguelike.combat;

import com.example.roguelike.core.EventBus;
import com.example.roguelike.core.GameEvents;
import com.example.roguelike.entity.Entity;
import java.util.Random;

public class CombatSystem {
    private final EventBus bus;
    private final Random rng;

    public CombatSystem(EventBus bus, Random rng) {
        this.bus = bus;
        this.rng = rng;
    }

    /** Satu serangan. Hasilnya diumumkan lewat event, bukan di-handle di sini. */
    public int attack(Entity attacker, Entity target) {
        int dmg = DamageCalculator.calculate(attacker.attackPower(), target.defensePower(), rng.nextInt(3) - 1);
        target.stats().damage(dmg);
        bus.publish(new GameEvents.EntityDamaged(attacker, target, dmg));
        if (!target.isAlive()) bus.publish(new GameEvents.EntityDied(target, attacker));
        return dmg;
    }
}
