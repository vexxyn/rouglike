package com.example.roguelike.item;

import com.example.roguelike.entity.Player;

public class Weapon extends Item {
    private final int bonus;

    public Weapon(String name, int bonus) { super(name, '/'); this.bonus = bonus; }
    public int bonus() { return bonus; }

    @Override public String use(Player p) { p.equipWeapon(this); return "You wield " + name() + "."; }
    @Override public String describe() { return "weapon, +" + bonus + " ATK"; }
}
