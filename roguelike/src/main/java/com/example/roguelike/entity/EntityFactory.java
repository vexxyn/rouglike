package com.example.roguelike.entity;

import com.example.roguelike.data.EnemyDef;

/** FACTORY: bikin Enemy dari definisi data (JSON), dengan stat yang naik tiap lantai. */
public final class EntityFactory {
    private EntityFactory() {}

    public static Enemy createEnemy(EnemyDef d, int x, int y, int floor) {
        double scale = 1.0 + 0.10 * (floor - 1);
        int hp = (int) Math.round(d.hp() * scale);
        int attack = d.attack() + (floor - 1) / 2;
        return new Enemy(d.name(), d.glyph().charAt(0), x, y, new Stats(hp, attack, d.defense()), d.sight(), d.score());
    }

    public static Player createPlayer(int x, int y) {
        return new Player(x, y);
    }
}
