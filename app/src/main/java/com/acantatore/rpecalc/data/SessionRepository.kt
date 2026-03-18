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

import com.acantatore.rpecalc.utils.LiftType
import com.acantatore.rpecalc.utils.UnitSystem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SessionRepository(private val db: AppDatabase) {

    /**
     * Logs a completed set. Weights are converted to kg before storage.
     * Runs on [Dispatchers.IO].
     */
    suspend fun logSession(
        lift: LiftType,
        weight: Double,
        reps: Int,
        rpe: Double,
        e1rm: Double,
        unit: UnitSystem
    ) = withContext(Dispatchers.IO) {
        val toKg = if (unit == UnitSystem.LBS) 1.0 / 2.205 else 1.0
        db.sessionDao().insert(
            SessionEntity(
                date   = System.currentTimeMillis(),
                lift   = lift.name,
                weight = weight * toKg,
                reps   = reps,
                rpe    = rpe,
                e1rm   = e1rm * toKg
            )
        )
    }
}
