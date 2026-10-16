package com.example.roguelike;

import static org.junit.jupiter.api.Assertions.*;

import com.example.roguelike.combat.DamageCalculator;
import org.junit.jupiter.api.Test;

class DamageCalculatorTest {
    @Test void attackMinusDefensePlusRoll() { assertEquals(8, DamageCalculator.calculate(10, 3, 1)); }
    @Test void neverBelowOne() { assertEquals(1, DamageCalculator.calculate(1, 50, -1)); }
    @Test void rollIsClampedToPlusMinusOne() { assertEquals(8, DamageCalculator.calculate(10, 3, 99)); }
}
