package com.acantatore.rpecalc.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlateCalculatorTest {

    @Test
    fun `breakdown returns empty for bar only weight`() {
        val (plates, actualWeight) = PlateCalculator.breakdown(
            totalWeight = 20.0,
            barWeight = 20.0,
            unit = UnitSystem.KG
        )
        assertTrue(plates.isEmpty())
        assertEquals(20.0, actualWeight, 0.01)
    }

    @Test
    fun `breakdown returns empty for weight less than bar`() {
        val (plates, actualWeight) = PlateCalculator.breakdown(
            totalWeight = 15.0,
            barWeight = 20.0,
            unit = UnitSystem.KG
        )
        assertTrue(plates.isEmpty())
        assertEquals(20.0, actualWeight, 0.01)
    }

    @Test
    fun `breakdown calculates correct plates for 60kg total`() {
        // 60kg total with 20kg bar = 40kg on bar = 20kg per side
        val (plates, actualWeight) = PlateCalculator.breakdown(
            totalWeight = 60.0,
            barWeight = 20.0,
            unit = UnitSystem.KG
        )

        assertEquals(60.0, actualWeight, 0.01)

        val totalPlateWeight = plates.sumOf { it.weight }
        assertEquals(20.0, totalPlateWeight, 0.01) // 20kg per side
    }

    @Test
    fun `breakdown calculates correct plates for 100kg total`() {
        // 100kg total with 20kg bar = 80kg on bar = 40kg per side
        // Expected: 25 + 15 = 40 or 20 + 20 = 40
        val (plates, actualWeight) = PlateCalculator.breakdown(
            totalWeight = 100.0,
            barWeight = 20.0,
            unit = UnitSystem.KG
        )

        assertEquals(100.0, actualWeight, 0.01)

        val totalPlateWeight = plates.sumOf { it.weight }
        assertEquals(40.0, totalPlateWeight, 0.01)
    }

    @Test
    fun `breakdown handles odd weights by rounding`() {
        // 73kg total with 20kg bar = 53kg on bar = 26.5kg per side
        // Can't achieve exactly - should round to nearest achievable
        val (plates, actualWeight) = PlateCalculator.breakdown(
            totalWeight = 73.0,
            barWeight = 20.0,
            unit = UnitSystem.KG
        )

        // Actual weight should be achievable
        val achievedPerSide = plates.sumOf { it.weight }
        assertEquals(actualWeight, 20.0 + (achievedPerSide * 2), 0.01)
    }

    @Test
    fun `breakdown uses greedy algorithm correctly`() {
        // 90kg total with 20kg bar = 70kg on bar = 35kg per side
        // Should use 25 + 10 = 35, not 20 + 15 or other combinations
        val (plates, _) = PlateCalculator.breakdown(
            totalWeight = 90.0,
            barWeight = 20.0,
            unit = UnitSystem.KG
        )

        val totalPlateWeight = plates.sumOf { it.weight }
        assertEquals(35.0, totalPlateWeight, 0.01)

        // First plate should be largest (25kg)
        assertEquals(25.0, plates.first().weight, 0.01)
    }

    @Test
    fun `breakdown works with lbs plates`() {
        // 135 lbs with 45 lb bar = 90 lbs on bar = 45 lbs per side
        val (plates, actualWeight) = PlateCalculator.breakdown(
            totalWeight = 135.0,
            barWeight = 45.0,
            unit = UnitSystem.LBS
        )

        assertEquals(135.0, actualWeight, 0.01)

        val totalPlateWeight = plates.sumOf { it.weight }
        assertEquals(45.0, totalPlateWeight, 0.01)

        // Should be a single 45 lb plate
        assertEquals(1, plates.size)
        assertEquals(45.0, plates.first().weight, 0.01)
    }

    @Test
    fun `breakdown handles complex weight`() {
        // 142.5kg total with 20kg bar = 122.5kg on bar = 61.25kg per side
        // 25 + 25 + 10 + 1 + 0.5 = 61.5 (closest achievable)
        val (plates, actualWeight) = PlateCalculator.breakdown(
            totalWeight = 142.5,
            barWeight = 20.0,
            unit = UnitSystem.KG
        )

        // Should be close to target
        assertTrue(actualWeight >= 142.0)
        assertTrue(actualWeight <= 144.0)
    }

    @Test
    fun `getPlates returns correct plates for each unit`() {
        val kgPlates = PlateCalculator.getPlates(UnitSystem.KG)
        val lbsPlates = PlateCalculator.getPlates(UnitSystem.LBS)

        assertTrue(kgPlates.isNotEmpty())
        assertTrue(lbsPlates.isNotEmpty())

        // KG plates should include 25, 20, 15, 10, 5, 2.5, 2, 1.5, 1, 0.5
        assertEquals(10, kgPlates.size)
        assertEquals(25.0, kgPlates.first().weight, 0.01)
        assertEquals(0.5, kgPlates.last().weight, 0.01)

        // LBS plates should include 45, 35, 25, 10, 5, 2.5
        assertEquals(6, lbsPlates.size)
        assertEquals(45.0, lbsPlates.first().weight, 0.01)
    }

    @Test
    fun `plates have correct colors`() {
        val kgPlates = PlateCalculator.getPlates(UnitSystem.KG)

        // 25kg should be red
        val plate25 = kgPlates.find { it.weight == 25.0 }!!
        assertEquals("Red", plate25.colorName)

        // 20kg should be blue
        val plate20 = kgPlates.find { it.weight == 20.0 }!!
        assertEquals("Blue", plate20.colorName)

        // 15kg should be yellow
        val plate15 = kgPlates.find { it.weight == 15.0 }!!
        assertEquals("Yellow", plate15.colorName)

        // 10kg should be green
        val plate10 = kgPlates.find { it.weight == 10.0 }!!
        assertEquals("Green", plate10.colorName)
    }

    @Test
    fun `formatBreakdown returns bar only for empty plates`() {
        val result = PlateCalculator.formatBreakdown(emptyList(), UnitSystem.KG)
        assertEquals("Bar only", result)
    }

    @Test
    fun `formatBreakdown formats single plate correctly`() {
        val plates = listOf(PlateCalculator.PLATES_KG.find { it.weight == 20.0 }!!)
        val result = PlateCalculator.formatBreakdown(plates, UnitSystem.KG)
        assertEquals("20 kg each side", result)
    }

    @Test
    fun `formatBreakdown formats multiple plates correctly`() {
        val plates = listOf(
            PlateCalculator.PLATES_KG.find { it.weight == 25.0 }!!,
            PlateCalculator.PLATES_KG.find { it.weight == 10.0 }!!
        )
        val result = PlateCalculator.formatBreakdown(plates, UnitSystem.KG)
        assertEquals("25 + 10 kg each side", result)
    }

    @Test
    fun `formatBreakdown groups duplicate plates`() {
        val plate20 = PlateCalculator.PLATES_KG.find { it.weight == 20.0 }!!
        val plates = listOf(plate20, plate20)
        val result = PlateCalculator.formatBreakdown(plates, UnitSystem.KG)
        assertEquals("20 x2 kg each side", result)
    }
}
