package com.example.roguelike.entity;

import com.example.roguelike.item.Armor;
import com.example.roguelike.item.Inventory;
import com.example.roguelike.item.Weapon;

public class Player extends Entity {
    private final Inventory inventory = new Inventory(12);
    private Weapon weapon;
    private Armor armor;
    private int level = 1;
    private int xp = 0;

    public Player(int x, int y) {
        super("You", '@', x, y, new Stats(40, 5, 1));
    }

    public Inventory inventory() { return inventory; }
    public Weapon weapon() { return weapon; }
    public Armor armor() { return armor; }
    public int level() { return level; }
    public int xp() { return xp; }
    public int xpToNext() { return 30 * level; }

    @Override public int attackPower() { return super.attackPower() + (weapon == null ? 0 : weapon.bonus()); }
    @Override public int defensePower() { return super.defensePower() + (armor == null ? 0 : armor.bonus()); }

    /** Pasang senjata dari inventory; senjata lama dikembalikan ke inventory. */
    public void equipWeapon(Weapon w) {
        inventory.remove(w);
        if (weapon != null) inventory.add(weapon);
        weapon = w;
    }

    public void equipArmor(Armor a) {
        inventory.remove(a);
        if (armor != null) inventory.add(armor);
        armor = a;
    }

    /** @return true kalau naik level. */
    public boolean gainXp(int amount) {
        xp += amount;
        boolean leveled = false;
        while (xp >= xpToNext()) {
            xp -= xpToNext();
            level++;
            stats().raiseMaxHp(10);
            stats().addAttack(1);
            leveled = true;
        }
        return leveled;
    }

    // --- dipakai saat load save ---
    public void restoreProgress(int level, int xp, int maxHp, int hp, int baseAttack) {
        this.level = level;
        this.xp = xp;
        stats().restore(maxHp, hp, baseAttack);
    }
    public void setWeapon(Weapon w) { this.weapon = w; }
    public void setArmor(Armor a) { this.armor = a; }
}
