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

import kotlin.math.roundToInt

object Calculator {

    /**
     * This is a translation of Tuchscherer's standard percentage chart into
     * a continuous function. This enables using real numbers for RPEs, like 8.75.
     */
    fun percentage(reps: Int, rpeInput: Double): Double {
        var rpe = rpeInput
        // Cap the RPE at 10.
        if (rpe > 10.0) {
            rpe = 10.0
        }

        // No prediction if failure occurred, or if RPE is unreasonably low.
        if (reps < 1 || rpe < 4.0) {
            return 0.0
        }

        // Handle the obvious case early to avoid bound errors.
        if (reps == 1 && rpe == 10.0) {
            return 100.0
        }

        // x is defined such that 1@10 = 0, 1@9 = 1, 1@8 = 2, etc.
        val x = (10.0 - rpe) + (reps - 1)

        // The logic breaks down for super-high numbers,
        // and it's too hard to extrapolate an E1RM from super-high-rep sets anyway.
        if (x >= 16) {
            return 0.0
        }

        val intersection = 2.92

        // The highest values follow a quadratic.
        if (x <= intersection) {
            val a = 0.347619
            val b = -4.60714
            val c = 99.9667
            return a * x * x + b * x + c
        }

        // Otherwise it's just a line
        val m = -2.64249
        val b = 97.0955
        return m * x + b
    }

    /**
     * Calculates the Estimated 1 Rep Max using the translated continuous function.
     */
    fun calculateE1RM(weight: Double, reps: Int, rpe: Double): Double {
        if (weight <= 0.0 || reps <= 0 || rpe <= 0.0) return 0.0
        val p = percentage(reps, rpe)
        if (p <= 0.0) return 0.0
        return weight / p * 100.0
    }

    /**
     * Calculates the target weight needed to hit a specific rep goal at a specific RPE,
     * given a known 1 Rep Max.
     */
    fun calculateTargetWeight(e1rm: Double, targetReps: Int, targetRpe: Double): Double {
        if (e1rm <= 0.0 || targetReps <= 0 || targetRpe <= 0.0) return 0.0
        val p2 = percentage(targetReps, targetRpe)
        if (p2 <= 0.0) return 0.0
        return e1rm / 100.0 * p2
    }

    /**
     * Generates standard weight percentages based on the 1RM.
     */
    fun generatePercentages(e1rm: Double): List<Pair<Int, Double>> {
        val percentages = listOf(100, 95, 90, 85, 80, 75, 70, 65, 60, 55, 50)
        return percentages.map { pct ->
            pct to (e1rm * (pct / 100.0))
        }
    }
    
    /**
     * Helper to round to nearest 2.5 (standard plate math).
     */
    fun roundToNearestPlate(weight: Double): Double {
        return Math.round(weight / 2.5) * 2.5
    }
}
