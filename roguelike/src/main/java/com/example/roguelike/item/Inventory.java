package com.example.roguelike.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Inventory {
    private final int capacity;
    private final List<Item> items = new ArrayList<>();

    public Inventory(int capacity) { this.capacity = capacity; }

    public boolean add(Item item) {
        if (isFull()) return false;
        items.add(item);
        return true;
    }

    public boolean remove(Item item) { return items.remove(item); }
    public Item get(int index) { return index >= 0 && index < items.size() ? items.get(index) : null; }
    public int size() { return items.size(); }
    public int capacity() { return capacity; }
    public boolean isFull() { return items.size() >= capacity; }
    public List<Item> items() { return Collections.unmodifiableList(items); }
}
