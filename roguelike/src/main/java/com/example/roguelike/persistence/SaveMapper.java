package com.example.roguelike.persistence;

import com.example.roguelike.data.EnemyDef;
import com.example.roguelike.data.GameData;
import com.example.roguelike.entity.Enemy;
import com.example.roguelike.entity.EntityFactory;
import com.example.roguelike.entity.Player;
import com.example.roguelike.game.GameWorld;
import com.example.roguelike.item.Armor;
import com.example.roguelike.item.Item;
import com.example.roguelike.item.ItemFactory;
import com.example.roguelike.item.Weapon;
import com.example.roguelike.world.Point;
import com.example.roguelike.world.generator.BspGenerator;
import java.util.Map;

/** Konversi GameWorld <-> SaveData. Murni Java (tanpa file I/O), jadi mudah dites. */
public final class SaveMapper {
    private SaveMapper() {}

    public static SaveData snapshot(GameWorld w) {
        SaveData s = new SaveData();
        Player p = w.player();
        s.seed = w.seed();
        s.floor = w.floor();
        s.score = w.score();
        s.px = p.x();
        s.py = p.y();
        s.level = p.level();
        s.xp = p.xp();
        s.maxHp = p.stats().maxHp();
        s.hp = p.stats().hp();
        s.baseAttack = p.stats().attack();
        s.weapon = p.weapon() == null ? null : p.weapon().name();
        s.armor = p.armor() == null ? null : p.armor().name();
        for (Item i : p.inventory().items()) s.inventory.add(i.name());
        for (Enemy e : w.enemies()) {
            SaveData.EnemySave es = new SaveData.EnemySave();
            es.name = e.name(); es.x = e.x(); es.y = e.y(); es.hp = e.stats().hp();
            s.enemies.add(es);
        }
        for (Map.Entry<Point, Item> e : w.groundItems().entrySet()) {
            SaveData.ItemSave is = new SaveData.ItemSave();
            is.name = e.getValue().name(); is.x = e.getKey().x(); is.y = e.getKey().y();
            s.groundItems.add(is);
        }
        return s;
    }

    public static GameWorld restore(SaveData s, GameData data) {
        GameWorld w = new GameWorld(s.seed, data, new BspGenerator());
        w.startFloor(s.floor);                       // dungeon identik karena seed sama
        w.enemies().clear();                         // buang spawn baru, ganti dengan yang tersimpan
        w.groundItems().clear();
        w.setScore(s.score);

        Player p = w.player();
        p.setPosition(s.px, s.py);
        p.restoreProgress(s.level, s.xp, s.maxHp, s.hp, s.baseAttack);
        for (String name : s.inventory) p.inventory().add(ItemFactory.create(data.itemByName(name)));
        if (s.weapon != null) p.setWeapon((Weapon) ItemFactory.create(data.itemByName(s.weapon)));
        if (s.armor != null) p.setArmor((Armor) ItemFactory.create(data.itemByName(s.armor)));

        for (SaveData.EnemySave es : s.enemies) {
            EnemyDef def = data.enemyByName(es.name);
            Enemy e = EntityFactory.createEnemy(def, es.x, es.y, s.floor);
            e.stats().restore(e.stats().maxHp(), es.hp, e.stats().attack());
            w.enemies().add(e);
        }
        for (SaveData.ItemSave is : s.groundItems)
            w.groundItems().put(new Point(is.x, is.y), ItemFactory.create(data.itemByName(is.name)));

        w.refreshFov();
        w.log("Game loaded.");
        return w;
    }
}
