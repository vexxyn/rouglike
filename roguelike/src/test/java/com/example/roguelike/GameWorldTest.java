package com.example.roguelike;

import static org.junit.jupiter.api.Assertions.*;

import com.example.roguelike.data.GameData;
import com.example.roguelike.entity.Enemy;
import com.example.roguelike.entity.EntityFactory;
import com.example.roguelike.entity.Player;
import com.example.roguelike.entity.Stats;
import com.example.roguelike.game.GameWorld;
import com.example.roguelike.item.Potion;
import com.example.roguelike.item.Weapon;
import com.example.roguelike.persistence.SaveData;
import com.example.roguelike.persistence.SaveMapper;
import com.example.roguelike.world.TileType;
import com.example.roguelike.world.generator.BspGenerator;
import org.junit.jupiter.api.Test;

class GameWorldTest {
    private final GameData data = GameData.defaults();

    /** Dunia kosong (tanpa musuh & item) dengan petak lantai di kanan pemain, supaya tes deterministik. */
    private GameWorld emptyWorld() {
        GameWorld w = GameWorld.newGame(1234, data, new BspGenerator());
        w.enemies().clear();
        w.groundItems().clear();
        Player p = w.player();
        for (int dx = 1; dx <= 3; dx++) w.dungeon().setTile(p.x() + dx, p.y(), TileType.FLOOR);
        return w;
    }

    private Enemy addEnemy(GameWorld w, int dx, Stats stats) {
        Player p = w.player();
        Enemy e = new Enemy("Dummy", 'd', p.x() + dx, p.y(), stats, 8, 10);
        w.enemies().add(e);
        return e;
    }

    @Test
    void walkingIntoWallTakesNoTurn() {
        GameWorld w = emptyWorld();
        Player p = w.player();
        w.dungeon().setTile(p.x() + 1, p.y(), TileType.WALL);
        int turn = w.turn();
        assertFalse(w.playerMove(1, 0));
        assertEquals(turn, w.turn());
    }

    @Test
    void walkingIntoEnemyAttacksIt() {
        GameWorld w = emptyWorld();
        Enemy e = addEnemy(w, 1, new Stats(100, 0, 0));
        w.playerMove(1, 0);
        assertTrue(e.stats().hp() < 100);
        assertEquals(w.player().x() + 1, e.x());            // pemain tidak berpindah
    }

    @Test
    void killingEnemyGivesScoreAndRemovesIt() {
        GameWorld w = emptyWorld();
        addEnemy(w, 1, new Stats(1, 0, 0));
        w.playerMove(1, 0);
        assertTrue(w.enemies().isEmpty());
        assertEquals(10, w.score());
    }

    @Test
    void potionHealsAndIsConsumed() {
        GameWorld w = emptyWorld();
        w.player().stats().damage(20);
        w.player().inventory().add(new Potion("Test Potion", 15));
        int hp = w.player().stats().hp();
        assertTrue(w.useItem(0));
        assertEquals(hp + 15, w.player().stats().hp());
        assertEquals(0, w.player().inventory().size());
    }

    @Test
    void equippingWeaponRaisesAttackAndReturnsOldWeaponToInventory() {
        GameWorld w = emptyWorld();
        Player p = w.player();
        int base = p.attackPower();
        p.inventory().add(new Weapon("Dagger", 2));
        p.inventory().add(new Weapon("Sword", 4));
        w.useItem(0);
        assertEquals(base + 2, p.attackPower());
        w.useItem(0);                                       // Sword sekarang di indeks 0
        assertEquals(base + 4, p.attackPower());
        assertEquals("Dagger", p.inventory().get(0).name());
    }

    @Test
    void enemyNoticesChasesAndAttacksPlayer() {
        GameWorld w = emptyWorld();
        addEnemy(w, 3, new Stats(50, 8, 0));
        int hp = w.player().stats().hp();
        for (int i = 0; i < 6; i++) w.playerWait();
        assertTrue(w.player().stats().hp() < hp);
    }

    @Test
    void steppingOnStairsGoesToNextFloor() {
        GameWorld w = emptyWorld();
        Player p = w.player();
        w.dungeon().setTile(p.x() + 1, p.y(), TileType.STAIRS);
        w.playerMove(1, 0);
        assertEquals(2, w.floor());
    }

    @Test
    void stairsOnLastFloorWinTheGame() {
        GameWorld w = emptyWorld();
        w.startFloor(GameWorld.MAX_FLOOR);
        w.enemies().clear();
        Player p = w.player();
        w.dungeon().setTile(p.x() + 1, p.y(), TileType.STAIRS);
        w.playerMove(1, 0);
        assertTrue(w.isVictory());
    }

    @Test
    void dyingEndsTheGame() {
        GameWorld w = emptyWorld();
        addEnemy(w, 1, new Stats(50, 999, 0));
        for (int i = 0; i < 3; i++) w.playerWait();         // giliran 1: musuh menyadari pemain (Idle -> Chase), lalu menyerang
        assertTrue(w.isGameOver());
    }

    @Test
    void saveAndLoadRestoresState() {
        GameWorld w = GameWorld.newGame(42, data, new BspGenerator());
        for (int i = 0; i < 60; i++) w.playerMove(i % 2 == 0 ? 1 : 0, i % 2 == 0 ? 0 : 1);
        w.player().inventory().add(new Potion("Small Potion", 15));

        SaveData s = SaveMapper.snapshot(w);
        GameWorld loaded = SaveMapper.restore(s, data);

        assertEquals(w.floor(), loaded.floor());
        assertEquals(w.score(), loaded.score());
        assertEquals(w.player().x(), loaded.player().x());
        assertEquals(w.player().stats().hp(), loaded.player().stats().hp());
        assertEquals(w.enemies().size(), loaded.enemies().size());
        assertEquals(w.groundItems().size(), loaded.groundItems().size());
        assertEquals(w.player().inventory().size(), loaded.player().inventory().size());
    }

    @Test
    void enemiesGetStrongerOnDeeperFloors() {
        Enemy f1 = EntityFactory.createEnemy(data.enemyByName("Orc"), 0, 0, 1);
        Enemy f5 = EntityFactory.createEnemy(data.enemyByName("Orc"), 0, 0, 5);
        assertTrue(f5.stats().maxHp() > f1.stats().maxHp());
        assertTrue(f5.stats().attack() > f1.stats().attack());
    }
}
