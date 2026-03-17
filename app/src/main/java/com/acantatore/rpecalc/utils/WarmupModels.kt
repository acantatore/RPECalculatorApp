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

/**
 * A single step in a warmup protocol.
 * @param percentage Percentage of working weight (0 = bar only)
 * @param reps Number of reps for this warmup set
 */
data class ProtocolStep(
    val percentage: Int,
    val reps: Int
)

/**
 * A warmup protocol defining the progression to working weight.
 */
data class WarmupProtocol(
    val name: String,
    val steps: List<ProtocolStep>
) {
    companion object {
        val DEFAULT = WarmupProtocol(
            name = "Standard",
            steps = listOf(
                ProtocolStep(0, 10),   // Bar only x 10
                ProtocolStep(40, 5),   // 40% x 5
                ProtocolStep(60, 3),   // 60% x 3
                ProtocolStep(80, 2)    // 80% x 2
            )
        )

        val POWERLIFTING = WarmupProtocol(
            name = "Powerlifting",
            steps = listOf(
                ProtocolStep(0, 8),    // Bar only x 8
                ProtocolStep(50, 5),   // 50% x 5
                ProtocolStep(70, 3),   // 70% x 3
                ProtocolStep(85, 2),   // 85% x 2
                ProtocolStep(92, 1)    // 92% x 1
            )
        )

        val MINIMAL = WarmupProtocol(
            name = "Minimal",
            steps = listOf(
                ProtocolStep(0, 10),   // Bar only x 10
                ProtocolStep(60, 5),   // 60% x 5
                ProtocolStep(80, 2)    // 80% x 2
            )
        )

        val PRESETS = listOf(DEFAULT, POWERLIFTING, MINIMAL)
    }
}

/**
 * A single warmup set with weight, reps, and plate breakdown.
 * @param weight Total weight including bar
 * @param reps Number of reps
 * @param percentage Percentage of working weight (0 = bar only)
 * @param plates List of plates needed per side
 * @param isBarOnly True if this set uses only the bar
 * @param actualWeight The rounded weight that can be achieved with available plates
 * @param wasRounded True if the weight was rounded from the target
 */
data class WarmupSet(
    val weight: Double,
    val reps: Int,
    val percentage: Int,
    val plates: List<Plate>,
    val isBarOnly: Boolean = false,
    val actualWeight: Double = weight,
    val wasRounded: Boolean = false
)
