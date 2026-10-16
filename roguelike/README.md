rg# Dungeon Delver — Roguelike 2D (Java + LibGDX)

Roguelike turn-based dengan dungeon prosedural, musuh ber-AI (A\* + state machine), item/inventory, level-up, fog of war, dan save/load.
Seluruh grafis digambar dengan kode (kotak + huruf), jadi **tidak butuh file aset** untuk jalan.

## Cara menjalankan

Butuh **JDK 17+**.

```bash
gradle run          # atau ./gradlew run kalau kamu generate wrapper (gradle wrapper)
gradle test         # unit test
gradle dist         # bikin build/libs/roguelike-all.jar -> java -jar build/libs/roguelike-all.jar
```

Atau buka folder ini di **IntelliJ IDEA** (Open → pilih `build.gradle`), lalu run `DesktopLauncher`.
Di macOS, tambahkan VM option `-XstartOnFirstThread` kalau run dari IDE.

## Kontrol

| Tombol | Aksi |
|---|---|
| Panah / WASD | Gerak. Jalan ke arah musuh = menyerang |
| Space | Diam satu giliran |
| I | Buka/tutup inventory (↑↓ pilih, Enter pakai/equip, X buang) |
| Esc | Tutup inventory / kembali ke menu (auto-save) |

Tujuan: turun lewat tangga `>` sampai lantai 5, lalu keluar dari lantai 5. Item diambil otomatis saat diinjak.

## Cara kerja game (alur satu giliran)

```
Input (GameScreen) ──> GameWorld.playerMove()
                         1. pemain gerak / serang / pungut item / injak tangga
                         2. SEMUA musuh dapat giliran:  enemy.takeTurn() -> state.update()
                         3. fog of war dihitung ulang
                       <── GameScreen menggambar state terbaru
```

Game ini **turn-based**: dunia hanya berubah setelah pemain bertindak. Karena itu `GameWorld` bisa dites tanpa grafis sama sekali
(package `game`, `world`, `entity`, `ai`, `item`, `combat` tidak meng-import LibGDX).

**Pembuatan lantai.** `BspGenerator` membelah peta secara rekursif jadi area-area, menaruh satu ruangan di tiap area, lalu menyambung
anak-anak pohon dengan lorong. Karena strukturnya pohon, semua ruangan dijamin terhubung. Seed lantai = `seed game * 31 + nomor lantai`,
jadi lantai yang sama selalu identik. Inilah sebabnya save file cukup menyimpan seed, bukan seluruh peta.

**AI musuh.** Tiap `Enemy` punya objek *state*:
`IdleState` (diam sampai melihat pemain) → `ChaseState` (jalur A\* ke pemain) → `AttackState` (pukul kalau bersebelahan).
Kalau pemain kabur jauh, kembali ke Idle.

**Combat.** `damage = max(1, attack - defense + (-1..+1))`. `CombatSystem` tidak tahu soal skor, loot, atau pesan:
ia hanya menerbitkan event (`EntityDamaged`, `EntityDied`), dan `GameWorld` yang berlangganan untuk menambah skor, menjatuhkan loot (35%), dan menulis log.

**Progres.** Musuh makin kuat tiap lantai (HP +10%/lantai, attack +1 per 2 lantai). Membunuh musuh memberi XP; naik level = +10 max HP, +1 ATK.
Turun tangga memulihkan 30% HP.

## Urutan membaca kode (disarankan)

1. `world/Dungeon.java`, `Room.java`, `TileType.java` — struktur data peta.
2. `world/generator/BspGenerator.java` (+ `Carver`) — pembuatan lantai.
3. `world/FieldOfView.java` — fog of war (garis Bresenham).
4. `ai/AStarPathfinder.java` — pathfinding.
5. `ai/*State.java` + `entity/Enemy.java` — state machine.
6. `combat/*`, `core/EventBus.java`, `core/GameEvents.java` — combat + observer.
7. `item/*`, `data/*`, `assets/data/*.json` — item, factory, data-driven.
8. **`game/GameWorld.java`** — pusat semuanya; baca setelah komponen-komponen di atas.
9. `persistence/*` — save/load.
10. `screen/GameScreen.java`, `RoguelikeGame.java` — baru bagian LibGDX (render & input).

## Design pattern

| Pattern | Lokasi |
|---|---|
| Strategy | `DungeonGenerator` ← `BspGenerator`, `RoomCorridorGenerator` |
| State | `EnemyState` ← `IdleState`, `ChaseState`, `AttackState` |
| Factory | `ItemFactory`, `EntityFactory` (dibangun dari definisi JSON) |
| Observer | `EventBus` + `GameEvents` |
| Polymorphism | `Item.use()` — Potion/Weapon/Armor berperilaku beda lewat satu method |

## Struktur

```
src/main/java/com/example/roguelike
├── DesktopLauncher / RoguelikeGame   entry point LibGDX
├── screen/        MenuScreen, GameScreen, GameOverScreen      (LibGDX)
├── game/          GameWorld                                   (model, tanpa LibGDX)
├── world/         Dungeon, Room, TileType, FieldOfView, generator/
├── entity/        Entity, Player, Enemy, Stats, EntityFactory
├── ai/            AStarPathfinder, EnemyState + 3 state
├── combat/        CombatSystem, DamageCalculator
├── item/          Item, Potion, Weapon, Armor, Inventory, ItemFactory
├── data/          EnemyDef, ItemDef, GameData, DataLoader(LibGDX)
├── persistence/   SaveData, SaveMapper, SaveManager(LibGDX)
└── core/          EventBus, GameEvents
assets/data/       enemies.json, items.json   <- edit untuk menambah musuh/item
src/test/java/     25 unit test (generator, A*, combat, inventory, GameWorld, save/load)
```

## Ide pengembangan (kalau mau menambah sendiri)

- Ganti kotak berwarna dengan sprite (Kenney.nl) dan tambah suara/animasi hit.
- Minimap, status effect (poison), musuh jarak jauh, jebakan.
- Pakai `RoomCorridorGenerator` selang-seling dengan BSP per lantai.
- Tambah variasi AI (musuh yang kabur saat HP rendah = state baru).
- Balancing: ubah angka di `assets/data/*.json` dan `EntityFactory`.
