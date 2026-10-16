package com.example.roguelike.entity;

public class Stats {
    private int maxHp, hp, attack, defense;

    public Stats(int maxHp, int attack, int defense) {
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.attack = attack;
        this.defense = defense;
    }

    public int maxHp() { return maxHp; }
    public int hp() { return hp; }
    public int attack() { return attack; }
    public int defense() { return defense; }

    public void damage(int n) { hp = Math.max(0, hp - n); }
    public void heal(int n) { hp = Math.min(maxHp, hp + n); }
    public void raiseMaxHp(int n) { maxHp += n; hp = Math.min(maxHp, hp + n); }
    public void addAttack(int n) { attack += n; }

    /** Dipakai saat load save. */
    public void restore(int maxHp, int hp, int attack) {
        this.maxHp = maxHp;
        this.hp = Math.max(0, Math.min(maxHp, hp));
        this.attack = attack;
    }
}
