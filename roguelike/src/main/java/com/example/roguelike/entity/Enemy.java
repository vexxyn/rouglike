package com.example.roguelike.entity;

import com.example.roguelike.ai.EnemyState;
import com.example.roguelike.ai.IdleState;
import com.example.roguelike.game.GameWorld;

public class Enemy extends Entity {
    private final int sightRange;
    private final int scoreValue;
    private EnemyState state = new IdleState();

    public Enemy(String name, char glyph, int x, int y, Stats stats, int sightRange, int scoreValue) {
        super(name, glyph, x, y, stats);
        this.sightRange = sightRange;
        this.scoreValue = scoreValue;
    }

    public int sightRange() { return sightRange; }
    public int scoreValue() { return scoreValue; }
    public EnemyState state() { return state; }
    public void setState(EnemyState s) { this.state = s; }

    /** Satu giliran musuh: serahkan ke state yang aktif. */
    public void takeTurn(GameWorld world) { state.update(this, world); }
}
