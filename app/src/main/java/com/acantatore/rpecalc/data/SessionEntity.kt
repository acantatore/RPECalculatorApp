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
package com.acantatore.rpecalc.data

/**
 * Represents one logged training set.
 * All weights are stored in kg.
 *
 * Schema:
 *   sessions(id INTEGER PK, date INTEGER, lift TEXT, weight REAL, reps INTEGER, rpe REAL, e1rm REAL)
 *   INDEX: (lift, date)
 */
data class SessionEntity(
    val id: Long = 0,
    val date: Long,       // epoch milliseconds
    val lift: String,     // LiftType.name — "SQUAT" | "BENCH" | "DEADLIFT" | "OHP" | "OTHER"
    val weight: Double,   // always stored in kg
    val reps: Int,
    val rpe: Double,
    val e1rm: Double      // always stored in kg
)
