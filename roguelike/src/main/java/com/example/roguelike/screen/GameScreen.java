package com.example.roguelike.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.example.roguelike.RoguelikeGame;
import com.example.roguelike.entity.Enemy;
import com.example.roguelike.entity.Player;
import com.example.roguelike.game.GameWorld;
import com.example.roguelike.item.Inventory;
import com.example.roguelike.item.Item;
import com.example.roguelike.world.Dungeon;
import com.example.roguelike.world.Point;
import com.example.roguelike.world.TileType;
import java.util.List;
import java.util.Map;

/**
 * Layar permainan. Tugasnya HANYA: baca input -> panggil GameWorld -> gambar hasilnya.
 * Semua aturan game ada di GameWorld, bukan di sini.
 */
public class GameScreen extends ScreenAdapter {
    static final int VW = 960, VH = 640;     // ukuran "dunia virtual" layar
    static final int T = 24;                  // ukuran satu tile dalam piksel

    private static final Color WALL_LIT = new Color(0.45f, 0.42f, 0.38f, 1f);
    private static final Color WALL_DIM = new Color(0.16f, 0.15f, 0.14f, 1f);
    private static final Color FLOOR_LIT = new Color(0.24f, 0.22f, 0.20f, 1f);
    private static final Color FLOOR_DIM = new Color(0.10f, 0.10f, 0.11f, 1f);
    private static final Color STAIRS_LIT = new Color(0.25f, 0.55f, 0.85f, 1f);
    private static final Color STAIRS_DIM = new Color(0.12f, 0.2f, 0.3f, 1f);
    private static final Color ENEMY = new Color(0.85f, 0.25f, 0.25f, 1f);
    private static final Color PLAYER = new Color(0.3f, 0.9f, 0.9f, 1f);
    private static final Color ITEM = new Color(0.95f, 0.8f, 0.2f, 1f);

    private final RoguelikeGame game;
    private final GameWorld world;
    private final OrthographicCamera worldCam = new OrthographicCamera();
    private final OrthographicCamera hudCam = new OrthographicCamera();
    private final Viewport worldView = new FitViewport(VW, VH, worldCam);
    private final Viewport hudView = new FitViewport(VW, VH, hudCam);

    private boolean showInventory;
    private int invCursor;

    public GameScreen(RoguelikeGame game, GameWorld world) {
        this.game = game;
        this.world = world;
    }

    @Override
    public void resize(int w, int h) {
        worldView.update(w, h);
        hudView.update(w, h, true);
    }

    /** Dipanggil saat pindah layar ATAU saat jendela ditutup: auto-save kalau permainan belum selesai. */
    @Override
    public void hide() {
        if (!world.isOver()) game.saves.save(world);
    }

    @Override
    public void render(float delta) {
        handleInput();
        if (world.isOver()) {
            game.saves.deleteSave();
            game.setScreen(new GameOverScreen(game, world.isVictory(), world.score(), world.floor()));
            return;
        }
        ScreenUtils.clear(0.02f, 0.02f, 0.03f, 1f);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        game.font.getData().setScale(1f);
        drawWorld();
        drawHud();
        if (showInventory) drawInventory();
    }

    // ------------------------------------------------------------------ input

    private static boolean pressed(int... keys) {
        for (int k : keys) if (Gdx.input.isKeyJustPressed(k)) return true;
        return false;
    }

    private void handleInput() {
        if (pressed(Input.Keys.ESCAPE)) {
            if (showInventory) showInventory = false;
            else game.setScreen(new MenuScreen(game));       // hide() akan auto-save
            return;
        }
        if (pressed(Input.Keys.I)) {
            showInventory = !showInventory;
            invCursor = 0;
            return;
        }
        if (showInventory) { handleInventoryInput(); return; }

        if (pressed(Input.Keys.UP, Input.Keys.W)) world.playerMove(0, 1);
        else if (pressed(Input.Keys.DOWN, Input.Keys.S)) world.playerMove(0, -1);
        else if (pressed(Input.Keys.LEFT, Input.Keys.A)) world.playerMove(-1, 0);
        else if (pressed(Input.Keys.RIGHT, Input.Keys.D)) world.playerMove(1, 0);
        else if (pressed(Input.Keys.SPACE)) world.playerWait();
    }

    private void handleInventoryInput() {
        Inventory inv = world.player().inventory();
        if (pressed(Input.Keys.UP, Input.Keys.W)) invCursor = Math.max(0, invCursor - 1);
        else if (pressed(Input.Keys.DOWN, Input.Keys.S)) invCursor = Math.min(Math.max(0, inv.size() - 1), invCursor + 1);
        else if (pressed(Input.Keys.ENTER, Input.Keys.E)) world.useItem(invCursor);
        else if (pressed(Input.Keys.X)) world.dropItem(invCursor);
        invCursor = Math.min(invCursor, Math.max(0, inv.size() - 1));
    }

    // ------------------------------------------------------------------ rendering: dunia

    private void drawWorld() {
        Dungeon d = world.dungeon();
        Player p = world.player();

        // kamera mengikuti pemain
        worldCam.position.set(p.x() * T + T / 2f, p.y() * T + T / 2f, 0);
        worldView.apply();
        worldCam.update();

        int rx = VW / T / 2 + 2, ry = VH / T / 2 + 2;       // hanya gambar tile yang masuk layar

        ShapeRenderer sr = game.shapes;
        sr.setProjectionMatrix(worldCam.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (int x = p.x() - rx; x <= p.x() + rx; x++) {
            for (int y = p.y() - ry; y <= p.y() + ry; y++) {
                if (!d.explored(x, y)) continue;             // fog of war: belum pernah dilihat = hitam
                boolean lit = d.visible(x, y);
                TileType t = d.tile(x, y);
                sr.setColor(switch (t) {
                    case WALL -> lit ? WALL_LIT : WALL_DIM;
                    case FLOOR -> lit ? FLOOR_LIT : FLOOR_DIM;
                    case STAIRS -> lit ? STAIRS_LIT : STAIRS_DIM;
                });
                sr.rect(x * T, y * T, T - 1, T - 1);
            }
        }
        sr.setColor(ITEM);
        for (Map.Entry<Point, Item> e : world.groundItems().entrySet()) {
            if (d.visible(e.getKey().x(), e.getKey().y())) sr.rect(e.getKey().x() * T + 7, e.getKey().y() * T + 7, 10, 10);
        }
        sr.setColor(ENEMY);
        for (Enemy e : world.enemies()) {
            if (d.visible(e.x(), e.y())) sr.rect(e.x() * T + 2, e.y() * T + 2, T - 5, T - 5);
        }
        sr.setColor(PLAYER);
        sr.rect(p.x() * T + 2, p.y() * T + 2, T - 5, T - 5);
        sr.end();

        // huruf (glyph) di atas kotak berwarna
        var batch = game.batch;
        var font = game.font;
        batch.setProjectionMatrix(worldCam.combined);
        batch.begin();
        Point st = d.stairs();
        if (d.explored(st.x(), st.y())) {
            font.setColor(Color.WHITE);
            font.draw(batch, ">", st.x() * T + 8, st.y() * T + T - 5);
        }
        font.setColor(Color.BLACK);
        for (Map.Entry<Point, Item> e : world.groundItems().entrySet()) {
            if (d.visible(e.getKey().x(), e.getKey().y()))
                font.draw(batch, String.valueOf(e.getValue().glyph()), e.getKey().x() * T + 8, e.getKey().y() * T + T - 5);
        }
        for (Enemy e : world.enemies()) {
            if (d.visible(e.x(), e.y())) font.draw(batch, String.valueOf(e.glyph()), e.x() * T + 8, e.y() * T + T - 5);
        }
        font.draw(batch, "@", p.x() * T + 7, p.y() * T + T - 5);
        batch.end();
    }

    // ------------------------------------------------------------------ rendering: HUD

    private void drawHud() {
        Player p = world.player();
        hudView.apply();
        hudCam.update();

        ShapeRenderer sr = game.shapes;
        sr.setProjectionMatrix(hudCam.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0, 0, 0, 0.65f);
        sr.rect(0, 0, VW, 120);
        sr.rect(0, VH - 34, VW, 34);
        float ratio = p.stats().hp() / (float) p.stats().maxHp();
        sr.setColor(0.3f, 0.05f, 0.05f, 1f);
        sr.rect(10, VH - 26, 200, 14);
        sr.setColor(0.85f, 0.15f, 0.15f, 1f);
        sr.rect(10, VH - 26, 200 * ratio, 14);
        sr.end();

        var batch = game.batch;
        var font = game.font;
        batch.setProjectionMatrix(hudCam.combined);
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "HP " + p.stats().hp() + "/" + p.stats().maxHp(), 70, VH - 12);
        font.draw(batch, "Floor " + world.floor() + "/" + GameWorld.MAX_FLOOR
                + "   Lv " + p.level() + " (" + p.xp() + "/" + p.xpToNext() + ")"
                + "   ATK " + p.attackPower() + "   DEF " + p.defensePower()
                + "   Score " + world.score(), 230, VH - 12);

        List<String> msgs = world.messages();
        for (int j = 0; j < 5 && j < msgs.size(); j++) {      // j = 0 pesan terbaru, paling bawah
            font.setColor(j == 0 ? Color.WHITE : new Color(0.7f, 0.7f, 0.7f, 1f - j * 0.15f));
            font.draw(batch, msgs.get(msgs.size() - 1 - j), 10, 24 + j * 20);
        }
        font.setColor(Color.GRAY);
        font.draw(batch, "I inventory  Space wait  Esc menu", VW - 250, 24);
        batch.end();
    }

    private void drawInventory() {
        Player p = world.player();
        Inventory inv = p.inventory();

        ShapeRenderer sr = game.shapes;
        sr.setProjectionMatrix(hudCam.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.02f, 0.02f, 0.05f, 0.92f);
        sr.rect(180, 90, 600, 460);
        sr.end();

        var batch = game.batch;
        var font = game.font;
        batch.setProjectionMatrix(hudCam.combined);
        batch.begin();
        font.setColor(Color.GOLD);
        font.draw(batch, "INVENTORY  (" + inv.size() + "/" + inv.capacity() + ")", 200, 530);
        font.setColor(Color.CYAN);
        font.draw(batch, "Weapon: " + (p.weapon() == null ? "-" : p.weapon().name() + "  (+" + p.weapon().bonus() + " ATK)"), 200, 500);
        font.draw(batch, "Armor:  " + (p.armor() == null ? "-" : p.armor().name() + "  (+" + p.armor().bonus() + " DEF)"), 200, 478);
        for (int i = 0; i < inv.size(); i++) {
            Item it = inv.get(i);
            font.setColor(i == invCursor ? Color.YELLOW : Color.WHITE);
            font.draw(batch, (i == invCursor ? "> " : "  ") + it.name() + "   - " + it.describe(), 200, 440 - i * 22);
        }
        if (inv.size() == 0) { font.setColor(Color.GRAY); font.draw(batch, "(empty)", 200, 440); }
        font.setColor(Color.GRAY);
        font.draw(batch, "Up/Down select   Enter use/equip   X drop   I or Esc close", 200, 118);
        batch.end();
    }
}
