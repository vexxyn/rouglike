package com.example.roguelike.data;

/** type: "potion" | "weapon" | "armor". value = jumlah heal / bonus ATK / bonus DEF. */
public record ItemDef(String type, String name, int value, int minFloor, int weight) {}
