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

    @Test
    fun `percentage returns 0 when x is 16 or more`() {
        // x = (10 - rpe) + (reps - 1); at rpe=4, reps=11 → x=16 → 0
        assertEquals(0.0, Calculator.percentage(11, 4.0), 0.01)
        // x=15 (rpe=4, reps=10) should still produce a nonzero result
        assertTrue(Calculator.percentage(10, 4.0) > 0.0)
    }

    @Test
    fun `percentage quadratic branch is used for x below 2_92`() {
        // x=1 (1@9): quadratic gives ~95.71
        val pct1at9 = Calculator.percentage(1, 9.0)
        assertTrue(pct1at9 > 94.0)
        assertTrue(pct1at9 < 97.0)
    }

    @Test
    fun `percentage produces exact value for known point`() {
        // 5@8: x = 2+4 = 6, linear = -2.64249*6 + 97.0955 ≈ 81.24
        assertEquals(81.24, Calculator.percentage(5, 8.0), 0.1)
    }

    // ── maxReps ────────────────────────────────────────────────────────────────

    @Test
    fun `maxReps returns 0 for non-positive rpe`() {
        assertEquals(0, Calculator.maxReps(0.0))
        assertEquals(0, Calculator.maxReps(-1.0))
    }

    @Test
    fun `maxReps returns correct values for standard rpe values`() {
        assertEquals(15, Calculator.maxReps(10.0))
        assertEquals(12, Calculator.maxReps(7.0))
        assertEquals(9,  Calculator.maxReps(4.0))
    }

    @Test
    fun `maxReps clamps rpe above 10 to 15`() {
        assertEquals(15, Calculator.maxReps(11.0))
        assertEquals(15, Calculator.maxReps(100.0))
    }

    @Test
    fun `maxReps clamps rpe below 4 to 9`() {
        // 3.0 > 0, goes through clamping path to 4 → 9
        assertEquals(9, Calculator.maxReps(3.0))
        assertEquals(9, Calculator.maxReps(1.0))
    }

    @Test
    fun `maxReps handles fractional rpe`() {
        // RPE 7.5: (10-7.5).toInt() = 2, 15-2 = 13
        assertEquals(13, Calculator.maxReps(7.5))
        // RPE 6.5: (10-6.5).toInt() = 3, 15-3 = 12
        assertEquals(12, Calculator.maxReps(6.5))
    }

    @Test
    fun `maxReps is consistent with percentage — at maxReps percentage is always nonzero`() {
        // The UI uses maxReps to cap user input; at that cap, E1RM must still be calculable.
        // maxReps is conservative (2 below the true x>=16 boundary) to keep the UX safe.
        for (rpe in listOf(4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0)) {
            val max = Calculator.maxReps(rpe)
            assertTrue(
                "percentage should be >0 at reps=$max, rpe=$rpe",
                Calculator.percentage(max, rpe) > 0.0
            )
        }
    }

    @Test
    fun `percentage returns 0 at the true x=16 zero boundary`() {
        // For integer RPEs the true zero boundary is reps = 17 - (10 - rpe).toInt()
        // which is always maxReps + 2.
        val cases = mapOf(10.0 to 17, 9.0 to 16, 8.0 to 15, 7.0 to 14, 6.0 to 13, 5.0 to 12, 4.0 to 11)
        for ((rpe, zeroBoundary) in cases) {
            assertEquals(
                "percentage should be 0 at reps=$zeroBoundary, rpe=$rpe",
                0.0,
                Calculator.percentage(zeroBoundary, rpe),
                0.01
            )
            assertTrue(
                "percentage should be >0 at reps=${zeroBoundary - 1}, rpe=$rpe",
                Calculator.percentage(zeroBoundary - 1, rpe) > 0.0
            )
        }
    }
}
