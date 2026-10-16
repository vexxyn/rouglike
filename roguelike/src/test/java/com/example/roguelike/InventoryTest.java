package com.example.roguelike;

import static org.junit.jupiter.api.Assertions.*;

import com.example.roguelike.item.Inventory;
import com.example.roguelike.item.Potion;
import org.junit.jupiter.api.Test;

class InventoryTest {
    @Test
    void rejectsItemsWhenFull() {
        Inventory inv = new Inventory(2);
        assertTrue(inv.add(new Potion("A", 5)));
        assertTrue(inv.add(new Potion("B", 5)));
        assertFalse(inv.add(new Potion("C", 5)));
        assertEquals(2, inv.size());
    }

    @Test
    void removesBySameInstanceOnly() {
        Inventory inv = new Inventory(5);
        Potion p1 = new Potion("Same", 5), p2 = new Potion("Same", 5);
        inv.add(p1);
        inv.add(p2);
        inv.remove(p1);
        assertSame(p2, inv.get(0));
    }
}
