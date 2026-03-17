/*
 * RPE Calculator
 * Copyright (C) 2026
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.acantatore.rpecalc.utils

import androidx.compose.ui.graphics.Color

/**
 * Calculates plate breakdowns for barbell loading.
 * Uses IWF/IPF standard color coding for kg plates.
 *
 * Plate breakdown algorithm:
 *   1. Calculate weight per side: (total - bar) / 2
 *   2. Greedy largest-first: pick the largest plate that fits
 *   3. Repeat until remaining weight < smallest plate
 *   4. If remainder exists, round to nearest achievable weight
 */
object PlateCalculator {

    // IWF/IPF standard colors
    private val IWF_RED = Color(0xFFE53935)
    private val IWF_BLUE = Color(0xFF1E88E5)
    private val IWF_YELLOW = Color(0xFFFDD835)
    private val IWF_GREEN = Color(0xFF43A047)
    private val IWF_BLACK = Color(0xFF212121)
    private val IWF_WHITE = Color(0xFFF5F5F5)

    /**
     * Standard kg plates with IWF colors.
     * Sorted largest to smallest for greedy algorithm.
     */
    val PLATES_KG: List<Plate> = listOf(
        Plate(25.0, IWF_RED, "Red", UnitSystem.KG),
        Plate(20.0, IWF_BLUE, "Blue", UnitSystem.KG),
        Plate(15.0, IWF_YELLOW, "Yellow", UnitSystem.KG),
        Plate(10.0, IWF_GREEN, "Green", UnitSystem.KG),
        Plate(5.0, IWF_BLACK, "Black", UnitSystem.KG),
        Plate(2.5, IWF_RED, "Red", UnitSystem.KG),
        Plate(2.0, IWF_BLUE, "Blue", UnitSystem.KG),
        Plate(1.5, IWF_YELLOW, "Yellow", UnitSystem.KG),
        Plate(1.0, IWF_GREEN, "Green", UnitSystem.KG),
        Plate(0.5, IWF_WHITE, "White", UnitSystem.KG)
    )

    /**
     * Standard lbs plates (US gym standard).
     * Using similar color scheme for consistency.
     */
    val PLATES_LBS: List<Plate> = listOf(
        Plate(45.0, IWF_BLUE, "Blue", UnitSystem.LBS),
        Plate(35.0, IWF_YELLOW, "Yellow", UnitSystem.LBS),
        Plate(25.0, IWF_GREEN, "Green", UnitSystem.LBS),
        Plate(10.0, IWF_BLACK, "Black", UnitSystem.LBS),
        Plate(5.0, IWF_RED, "Red", UnitSystem.LBS),
        Plate(2.5, IWF_WHITE, "White", UnitSystem.LBS)
    )

    /**
     * Gets the available plates for a unit system.
     */
    fun getPlates(unit: UnitSystem): List<Plate> {
        return when (unit) {
            UnitSystem.KG -> PLATES_KG
            UnitSystem.LBS -> PLATES_LBS
        }
    }

    /**
     * Calculates the plates needed per side to achieve a target weight.
     *
     * @param totalWeight Total weight including bar
     * @param barWeight Weight of the bar
     * @param unit Unit system (kg or lbs)
     * @return Pair of (list of plates per side, actual achievable weight)
     */
    fun breakdown(
        totalWeight: Double,
        barWeight: Double,
        unit: UnitSystem
    ): Pair<List<Plate>, Double> {
        val weightPerSide = (totalWeight - barWeight) / 2.0

        if (weightPerSide <= 0) {
            return Pair(emptyList(), barWeight)
        }

        val availablePlates = getPlates(unit)
        val platesPerSide = mutableListOf<Plate>()
        var remaining = weightPerSide

        // Greedy algorithm: always pick the largest plate that fits
        for (plate in availablePlates) {
            while (remaining >= plate.weight) {
                platesPerSide.add(plate)
                remaining -= plate.weight
            }
        }

        // Calculate actual achieved weight
        val actualPerSide = platesPerSide.sumOf { it.weight }
        val actualTotal = barWeight + (actualPerSide * 2)

        return Pair(platesPerSide, actualTotal)
    }

    /**
     * Formats a plate breakdown as a human-readable string.
     * Example: "20 + 10 + 2.5 kg each side"
     */
    fun formatBreakdown(plates: List<Plate>, unit: UnitSystem): String {
        if (plates.isEmpty()) {
            return "Bar only"
        }

        // Group consecutive same-weight plates
        val grouped = mutableListOf<Pair<Double, Int>>()
        var currentWeight = plates[0].weight
        var count = 0

        for (plate in plates) {
            if (plate.weight == currentWeight) {
                count++
            } else {
                grouped.add(Pair(currentWeight, count))
                currentWeight = plate.weight
                count = 1
            }
        }
        grouped.add(Pair(currentWeight, count))

        // Format as string
        val parts = grouped.map { (weight, qty) ->
            if (qty > 1) "${weight.formatWeight()} x$qty" else weight.formatWeight()
        }

        return "${parts.joinToString(" + ")} ${unit.label} each side"
    }

    /**
     * Formats a weight value, removing unnecessary decimals.
     */
    private fun Double.formatWeight(): String {
        return if (this == this.toLong().toDouble()) {
            this.toLong().toString()
        } else {
            this.toString()
        }
    }
}
