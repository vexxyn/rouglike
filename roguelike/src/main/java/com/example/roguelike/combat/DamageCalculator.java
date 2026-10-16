package com.example.roguelike.combat;

public final class DamageCalculator {
    private DamageCalculator() {}

    /**
     * Damage = attack - defense + variasi (-1, 0, atau +1), minimal 1 supaya serangan selalu berarti.
     * @param roll variasi, dibatasi ke -1..+1
     */
    public static int calculate(int attack, int defense, int roll) {
        int variance = Math.max(-1, Math.min(1, roll));
        return Math.max(1, attack - defense + variance);
    }
}
