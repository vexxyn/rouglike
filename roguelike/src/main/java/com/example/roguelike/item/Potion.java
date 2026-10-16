package com.example.roguelike.item;

import com.example.roguelike.entity.Player;

public class Potion extends Item {
    private final int heal;

    public Potion(String name, int heal) { super(name, '!'); this.heal = heal; }
    public int healAmount() { return heal; }

    @Override public String use(Player p) {
        int before = p.stats().hp();
        p.stats().heal(heal);
        p.inventory().remove(this);
        return "You drink " + name() + " (+" + (p.stats().hp() - before) + " HP).";
    }

    @Override public String describe() { return "heals " + heal + " HP"; }
}
