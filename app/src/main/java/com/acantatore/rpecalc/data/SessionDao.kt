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

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase

/**
 * Data access object for session rows. Operates on an open [SQLiteDatabase].
 */
class SessionDao(private val db: SQLiteDatabase) {

    companion object {
        const val TABLE = "sessions"
        const val COL_ID     = "id"
        const val COL_DATE   = "date"
        const val COL_LIFT   = "lift"
        const val COL_WEIGHT = "weight"
        const val COL_REPS   = "reps"
        const val COL_RPE    = "rpe"
        const val COL_E1RM   = "e1rm"

        const val SQL_CREATE = """
            CREATE TABLE IF NOT EXISTS $TABLE (
                $COL_ID     INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_DATE   INTEGER NOT NULL,
                $COL_LIFT   TEXT NOT NULL,
                $COL_WEIGHT REAL NOT NULL,
                $COL_REPS   INTEGER NOT NULL,
                $COL_RPE    REAL NOT NULL,
                $COL_E1RM   REAL NOT NULL
            )
        """

        const val SQL_INDEX = "CREATE INDEX IF NOT EXISTS idx_sessions_lift_date ON $TABLE ($COL_LIFT, $COL_DATE)"
    }

    /** Inserts a session row and returns the new row ID, or -1 on failure. */
    fun insert(session: SessionEntity): Long {
        val values = ContentValues().apply {
            put(COL_DATE,   session.date)
            put(COL_LIFT,   session.lift)
            put(COL_WEIGHT, session.weight)
            put(COL_REPS,   session.reps)
            put(COL_RPE,    session.rpe)
            put(COL_E1RM,   session.e1rm)
        }
        return db.insertOrThrow(TABLE, null, values)
    }
}
