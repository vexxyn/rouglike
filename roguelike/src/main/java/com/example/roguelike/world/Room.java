package com.example.roguelike.world;

public record Room(int x, int y, int w, int h) {
    public int centerX() { return x + w / 2; }
    public int centerY() { return y + h / 2; }

    /** true kalau dua ruangan bertabrakan (dengan jarak minimal pad). */
    public boolean intersects(Room o, int pad) {
        return x - pad < o.x + o.w && x + w + pad > o.x
            && y - pad < o.y + o.h && y + h + pad > o.y;
    }

    public boolean contains(int px, int py) {
        return px >= x && px < x + w && py >= y && py < y + h;
    }
}
