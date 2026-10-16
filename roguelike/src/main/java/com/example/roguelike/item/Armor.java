package com.example.roguelike.item;

import com.example.roguelike.entity.Player;

public class Armor extends Item {
    private final int bonus;

    public Armor(String name, int bonus) { super(name, '['); this.bonus = bonus; }
    public int bonus() { return bonus; }

    @Override public String use(Player p) { p.equipArmor(this); return "You wear " + name() + "."; }
    @Override public String describe() { return "armor, +" + bonus + " DEF"; }
}
