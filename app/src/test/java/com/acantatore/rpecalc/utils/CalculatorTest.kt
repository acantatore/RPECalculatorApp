package com.acantatore.rpecalc.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculatorTest {

    @Test
    fun `e1rm calculation is correct for standard input`() {
        val e1rm = Calculator.calculateE1RM(100.0, 5, 8.0)
        // Based on Tuchscherer's percentage chart
        assertTrue(e1rm > 100.0)
        assertTrue(e1rm < 150.0)
    }

    @Test
    fun `e1rm returns zero for invalid inputs`() {
        assertEquals(0.0, Calculator.calculateE1RM(0.0, 5, 8.0), 0.01)
        assertEquals(0.0, Calculator.calculateE1RM(100.0, 0, 8.0), 0.01)
        assertEquals(0.0, Calculator.calculateE1RM(100.0, 5, 0.0), 0.01)
        assertEquals(0.0, Calculator.calculateE1RM(-100.0, 5, 8.0), 0.01)
    }

    @Test
    fun `target weight calculation is correct`() {
        val e1rm = 100.0
        val targetWeight = Calculator.calculateTargetWeight(e1rm, 5, 8.0)
        assertTrue(targetWeight > 0)
        assertTrue(targetWeight < e1rm)
    }

    @Test
    fun `target weight returns zero for invalid inputs`() {
        assertEquals(0.0, Calculator.calculateTargetWeight(0.0, 5, 8.0), 0.01)
        assertEquals(0.0, Calculator.calculateTargetWeight(100.0, 0, 8.0), 0.01)
        assertEquals(0.0, Calculator.calculateTargetWeight(100.0, 5, 0.0), 0.01)
    }

    @Test
    fun `roundToNearestPlate rounds correctly with default increment`() {
        assertEquals(92.5, Calculator.roundToNearestPlate(92.3), 0.01)
        assertEquals(95.0, Calculator.roundToNearestPlate(93.8), 0.01)
        assertEquals(100.0, Calculator.roundToNearestPlate(100.0), 0.01)
    }

    @Test
    fun `roundToNearestPlate rounds correctly with custom increment`() {
        // kg plates with 0.5 increment
        assertEquals(92.5, Calculator.roundToNearestPlate(92.3, 0.5), 0.01)
        assertEquals(92.5, Calculator.roundToNearestPlate(92.6, 0.5), 0.01)
        assertEquals(93.0, Calculator.roundToNearestPlate(92.8, 0.5), 0.01)

        // 1.25 kg increment
        assertEquals(92.5, Calculator.roundToNearestPlate(92.3, 1.25), 0.01)
        assertEquals(93.75, Calculator.roundToNearestPlate(93.5, 1.25), 0.01)
    }

    @Test
    fun `generateWarmupSets returns empty for weight less than bar`() {
        val sets = Calculator.generateWarmupSets(
            workingWeight = 15.0,
            barWeight = 20.0,
            protocol = WarmupProtocol.DEFAULT,
            unit = UnitSystem.KG
        )
        assertTrue(sets.isEmpty())
    }

    @Test
    fun `generateWarmupSets returns sets for valid input`() {
        val sets = Calculator.generateWarmupSets(
            workingWeight = 100.0,
            barWeight = 20.0,
            protocol = WarmupProtocol.DEFAULT,
            unit = UnitSystem.KG
        )
        assertTrue(sets.isNotEmpty())
        assertEquals(WarmupProtocol.DEFAULT.steps.size, sets.size)
    }

    @Test
    fun `generateWarmupSets first set is bar only`() {
        val sets = Calculator.generateWarmupSets(
            workingWeight = 100.0,
            barWeight = 20.0,
            protocol = WarmupProtocol.DEFAULT,
            unit = UnitSystem.KG
        )
        assertTrue(sets.first().isBarOnly)
        assertEquals(20.0, sets.first().actualWeight, 0.01)
    }

    @Test
    fun `generateWarmupSets weights increase progressively`() {
        val sets = Calculator.generateWarmupSets(
            workingWeight = 100.0,
            barWeight = 20.0,
            protocol = WarmupProtocol.DEFAULT,
            unit = UnitSystem.KG
        )

        for (i in 1 until sets.size) {
            assertTrue(
                "Set $i should be >= previous set",
                sets[i].actualWeight >= sets[i - 1].actualWeight
            )
        }
    }

    @Test
    fun `generateWarmupSets skips duplicate weights`() {
        // With very light working weight, some percentage steps might round to same weight
        val sets = Calculator.generateWarmupSets(
            workingWeight = 25.0, // Just above 20kg bar
            barWeight = 20.0,
            protocol = WarmupProtocol.DEFAULT,
            unit = UnitSystem.KG
        )

        val weights = sets.map { it.actualWeight }
        val uniqueWeights = weights.distinct()
        assertEquals("No duplicate weights", weights.size, uniqueWeights.size)
    }

    @Test
    fun `percentage function returns valid percentages`() {
        val pct = Calculator.percentage(1, 10.0)
        assertEquals(100.0, pct, 0.01)

        val pct5x8 = Calculator.percentage(5, 8.0)
        assertTrue(pct5x8 > 70.0)
        assertTrue(pct5x8 < 90.0)
    }

    @Test
    fun `percentage function handles edge cases`() {
        // RPE > 10 should cap at 10
        val pctOver10 = Calculator.percentage(1, 11.0)
        assertEquals(100.0, pctOver10, 0.01)

        // RPE < 4 returns 0
        val pctLowRpe = Calculator.percentage(5, 3.0)
        assertEquals(0.0, pctLowRpe, 0.01)

        // Reps < 1 returns 0
        val pctNoReps = Calculator.percentage(0, 8.0)
        assertEquals(0.0, pctNoReps, 0.01)
    }
}
