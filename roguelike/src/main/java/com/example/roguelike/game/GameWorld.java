package com.example.roguelike.game;

import com.example.roguelike.combat.CombatSystem;
import com.example.roguelike.core.EventBus;
import com.example.roguelike.core.GameEvents;
import com.example.roguelike.data.EnemyDef;
import com.example.roguelike.data.GameData;
import com.example.roguelike.entity.Enemy;
import com.example.roguelike.entity.EntityFactory;
import com.example.roguelike.entity.Player;
import com.example.roguelike.item.Item;
import com.example.roguelike.item.ItemFactory;
import com.example.roguelike.world.Dungeon;
import com.example.roguelike.world.FieldOfView;
import com.example.roguelike.world.Point;
import com.example.roguelike.world.Room;
import com.example.roguelike.world.TileType;
import com.example.roguelike.world.generator.DungeonGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Model game (tanpa LibGDX sama sekali, jadi bisa di-unit-test).
 *
 * Alur satu giliran (turn-based):
 *   pemain bertindak (gerak/serang/pakai item)  ->  semua musuh bertindak  ->  fog of war dihitung ulang.
 * Layar (GameScreen) hanya memanggil playerMove()/useItem()/... lalu menggambar hasilnya.
 */
public class GameWorld {
    public static final int WIDTH = 60, HEIGHT = 40;
    public static final int MAX_FLOOR = 5;
    public static final int FOV_RADIUS = 8;
    private static final double LOOT_CHANCE = 0.35;

    private final long seed;
    private final GameData data;
    private final DungeonGenerator generator;
    private final EventBus bus = new EventBus();
    private final Random rng;
    private final CombatSystem combat;

    private final List<Enemy> enemies = new ArrayList<>();
    private final Map<Point, Item> groundItems = new HashMap<>();
    private final List<String> messages = new ArrayList<>();

    private Dungeon dungeon;
    private Player player;
    private int floor, score, turn;
    private boolean gameOver, victory;

    public GameWorld(long seed, GameData data, DungeonGenerator generator) {
        this.seed = seed;
        this.data = data;
        this.generator = generator;
        this.rng = new Random(seed);
        this.combat = new CombatSystem(bus, rng);
        subscribeToEvents();
    }

    public static GameWorld newGame(long seed, GameData data, DungeonGenerator generator) {
        GameWorld w = new GameWorld(seed, data, generator);
        w.startFloor(1);
        w.log("You enter the dungeon. Find the stairs (>) on each floor.");
        return w;
    }

    // ------------------------------------------------------------------ event handlers (OBSERVER)

    private void subscribeToEvents() {
        bus.subscribe(GameEvents.EntityDamaged.class, e ->
            log(e.attacker().name() + " hit " + e.target().name() + " for " + e.amount() + "."));

        bus.subscribe(GameEvents.EntityDied.class, e -> {
            if (e.victim() instanceof Enemy enemy) {
                enemies.remove(enemy);
                score += enemy.scoreValue();
                log(enemy.name() + " dies. (+" + enemy.scoreValue() + ")");
                if (player.gainXp(enemy.scoreValue())) log("Level up! You are now level " + player.level() + ".");
                Point pos = new Point(enemy.x(), enemy.y());
                if (rng.nextDouble() < LOOT_CHANCE && !groundItems.containsKey(pos)
                        && dungeon.tile(pos.x(), pos.y()) != TileType.STAIRS) {
                    groundItems.put(pos, ItemFactory.random(data, floor, rng));
                }
            } else {
                gameOver = true;
                log("You died on floor " + floor + ".");
            }
        });

        bus.subscribe(GameEvents.ItemPickedUp.class, e -> log("You pick up " + e.item().name() + "."));
    }

    // ------------------------------------------------------------------ floors

    /** Bikin lantai baru. Seed per lantai = seed game + nomor lantai, jadi lantai yang sama selalu identik (penting untuk save/load). */
    public void startFloor(int newFloor) {
        floor = newFloor;
        dungeon = generator.generate(WIDTH, HEIGHT, new Random(seed * 31L + newFloor));
        enemies.clear();
        groundItems.clear();
        Point start = dungeon.start();
        if (player == null) player = EntityFactory.createPlayer(start.x(), start.y());
        else player.setPosition(start.x(), start.y());
        spawnContent(new Random(seed * 131L + newFloor * 7919L));
        refreshFov();
        bus.publish(new GameEvents.FloorChanged(floor));
    }

    private void spawnContent(Random r) {
        List<Room> rooms = dungeon.rooms();
        List<EnemyDef> pool = data.enemiesForFloor(floor);
        int enemyCount = Math.min(12, 3 + floor * 2);
        for (int i = 0; i < enemyCount; i++) {
            Room room = rooms.get(1 + r.nextInt(rooms.size() - 1));   // ruangan 0 = tempat start, aman
            Point p = randomFreeTile(room, r);
            if (p == null) continue;
            enemies.add(EntityFactory.createEnemy(pool.get(r.nextInt(pool.size())), p.x(), p.y(), floor));
        }
        int itemCount = 3 + r.nextInt(3);
        for (int i = 0; i < itemCount; i++) {
            Point p = randomFreeTile(rooms.get(r.nextInt(rooms.size())), r);
            if (p != null) groundItems.put(p, ItemFactory.random(data, floor, r));
        }
    }

    private Point randomFreeTile(Room room, Random r) {
        for (int attempt = 0; attempt < 20; attempt++) {
            int x = room.x() + r.nextInt(room.w()), y = room.y() + r.nextInt(room.h());
            if (dungeon.tile(x, y) == TileType.FLOOR && !isOccupied(x, y) && !groundItems.containsKey(new Point(x, y)))
                return new Point(x, y);
        }
        return null;
    }

    private void descend() {
        score += 50;
        if (floor >= MAX_FLOOR) {
            victory = true;
            score += 500;
            log("You escape the dungeon! Victory!");
            return;
        }
        startFloor(floor + 1);
        int heal = Math.max(1, player.stats().maxHp() * 3 / 10);     // bonus: pulih 30% HP tiap turun lantai
        player.stats().heal(heal);
        log("You descend to floor " + floor + " and catch your breath (+" + heal + " HP).");
    }

    // ------------------------------------------------------------------ player actions

    /** @return true kalau aksi memakan satu giliran (menabrak tembok = tidak). */
    public boolean playerMove(int dx, int dy) {
        if (isOver()) return false;
        int nx = player.x() + dx, ny = player.y() + dy;

        Enemy target = enemyAt(nx, ny);
        if (target != null) {                       // bergerak ke musuh = menyerang
            combat.attack(player, target);
            endTurn();
            return true;
        }
        if (!dungeon.isWalkable(nx, ny)) return false;

        player.setPosition(nx, ny);
        pickUpHere();
        if (dungeon.tile(nx, ny) == TileType.STAIRS) {
            descend();
            refreshFov();
            return true;
        }
        endTurn();
        return true;
    }

    public boolean playerWait() {
        if (isOver()) return false;
        endTurn();
        return true;
    }

    public boolean useItem(int index) {
        Item item = player.inventory().get(index);
        if (item == null || isOver()) return false;
        log(item.use(player));
        endTurn();
        return true;
    }

    public boolean dropItem(int index) {
        Item item = player.inventory().get(index);
        Point here = new Point(player.x(), player.y());
        if (item == null || isOver()) return false;
        if (groundItems.containsKey(here)) {
            log("There is already something here.");
            return false;
        }
        player.inventory().remove(item);
        groundItems.put(here, item);
        log("You drop " + item.name() + ".");
        endTurn();
        return true;
    }

    private void pickUpHere() {
        Point here = new Point(player.x(), player.y());
        Item item = groundItems.get(here);
        if (item == null) return;
        if (player.inventory().add(item)) {
            groundItems.remove(here);
            bus.publish(new GameEvents.ItemPickedUp(item));
        } else {
            log("Your inventory is full.");
        }
    }

    private void endTurn() {
        for (Enemy e : new ArrayList<>(enemies)) {          // salinan: musuh bisa dihapus saat iterasi
            if (!isOver() && e.isAlive()) e.takeTurn(this);
        }
        turn++;
        refreshFov();
    }

    // ------------------------------------------------------------------ helpers dipakai AI

    public void attackPlayer(Enemy e) { combat.attack(e, player); }

    public boolean canSeePlayer(Enemy e) {
        int dx = e.x() - player.x(), dy = e.y() - player.y();
        if (dx * dx + dy * dy > e.sightRange() * e.sightRange()) return false;
        return FieldOfView.hasLineOfSight(dungeon, e.x(), e.y(), player.x(), player.y());
    }

    public Enemy enemyAt(int x, int y) {
        for (Enemy e : enemies) if (e.x() == x && e.y() == y) return e;
        return null;
    }

    /** Ada musuh atau pemain di tile ini? */
    public boolean isOccupied(int x, int y) {
        return enemyAt(x, y) != null || (player != null && player.x() == x && player.y() == y);
    }

    public void refreshFov() { FieldOfView.compute(dungeon, player.x(), player.y(), FOV_RADIUS); }

    // ------------------------------------------------------------------ accessors

    public boolean isOver() { return gameOver || victory; }
    public boolean isGameOver() { return gameOver; }
    public boolean isVictory() { return victory; }
    public Dungeon dungeon() { return dungeon; }
    public Player player() { return player; }
    public int floor() { return floor; }
    public int score() { return score; }
    public void setScore(int score) { this.score = score; }
    public int turn() { return turn; }
    public long seed() { return seed; }
    public EventBus bus() { return bus; }
    public GameData data() { return data; }
    public List<String> messages() { return messages; }

    /** Daftar yang bisa diubah langsung: dipakai SaveMapper untuk memulihkan state dari file save. */
    public List<Enemy> enemies() { return enemies; }
    public Map<Point, Item> groundItems() { return groundItems; }

    public void log(String text) {
        messages.add(text);
        if (messages.size() > 50) messages.remove(0);
    }
}
