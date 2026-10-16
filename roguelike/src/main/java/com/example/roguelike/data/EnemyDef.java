package com.example.roguelike.data;

public record EnemyDef(String name, String glyph, int hp, int attack, int defense, int sight, int score, int minFloor) {}
