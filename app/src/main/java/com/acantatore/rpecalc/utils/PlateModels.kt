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
 * Unit system for weight display and calculations.
 */
enum class UnitSystem(val label: String, val smallestPlate: Double) {
    KG("kg", 0.5),
    LBS("lbs", 2.5)
}

/**
 * Represents a weight plate with IWF/IPF standard color coding.
 *
 * Color standard (kg):
 *   25 kg - Red
 *   20 kg - Blue
 *   15 kg - Yellow
 *   10 kg - Green
 *   5 kg  - Black (White in competition)
 *   2.5 kg - Red
 *   2 kg  - Blue
 *   1.5 kg - Yellow
 *   1 kg  - Green
 *   0.5 kg - White
 */
data class Plate(
    val weight: Double,
    val color: Color,
    val colorName: String,
    val unit: UnitSystem
)

/**
 * Standard bar weights.
 */
enum class BarWeight(val kg: Double, val lbs: Double, val label: String) {
    OLYMPIC_20KG(20.0, 44.0, "Olympic (20 kg)"),
    WOMENS_15KG(15.0, 33.0, "Women's (15 kg)"),
    STANDARD_45LBS(20.4, 45.0, "Standard (45 lbs)"),
    STANDARD_35LBS(15.9, 35.0, "Women's (35 lbs)")
}
