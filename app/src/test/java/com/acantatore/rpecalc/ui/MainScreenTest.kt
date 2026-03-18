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
package com.acantatore.rpecalc.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class MainScreenTest {

    // ── validateRpe ────────────────────────────────────────────────────────────

    @Test
    fun `validateRpe returns null for empty input`() {
        assertNull(validateRpe(""))
    }

    @Test
    fun `validateRpe returns null for non-numeric input`() {
        assertNull(validateRpe("abc"))
        assertNull(validateRpe("."))
    }

    @Test
    fun `validateRpe returns null for valid rpe values`() {
        assertNull(validateRpe("4"))
        assertNull(validateRpe("7"))
        assertNull(validateRpe("8.5"))
        assertNull(validateRpe("10"))
    }

    @Test
    fun `validateRpe returns error for rpe below 4`() {
        assertNotNull(validateRpe("3.9"))
        assertNotNull(validateRpe("0"))
        assertNotNull(validateRpe("3"))
        assertEquals("Min RPE is 4", validateRpe("1"))
    }

    @Test
    fun `validateRpe returns error for rpe above 10`() {
        assertNotNull(validateRpe("10.1"))
        assertNotNull(validateRpe("11"))
        assertEquals("Max RPE is 10", validateRpe("11"))
    }

    @Test
    fun `validateRpe boundary — exactly 4 and 10 are valid`() {
        assertNull(validateRpe("4.0"))
        assertNull(validateRpe("10.0"))
    }

    // ── validateReps ───────────────────────────────────────────────────────────

    @Test
    fun `validateReps returns null for empty reps input`() {
        assertNull(validateReps("", "8"))
    }

    @Test
    fun `validateReps returns null for non-numeric reps input`() {
        assertNull(validateReps("abc", "8"))
    }

    @Test
    fun `validateReps returns null for valid reps without rpe context`() {
        // No RPE given — falls back to 15 max
        assertNull(validateReps("15", ""))
        assertNull(validateReps("1", ""))
    }

    @Test
    fun `validateReps returns error when reps exceeds 15 with no rpe`() {
        assertNotNull(validateReps("16", ""))
        assertNotNull(validateReps("16", "abc"))
    }

    @Test
    fun `validateReps returns null for valid reps at given rpe`() {
        // RPE 10 → maxReps = 15
        assertNull(validateReps("15", "10"))
        // RPE 7 → maxReps = 12
        assertNull(validateReps("12", "7"))
        // RPE 4 → maxReps = 9
        assertNull(validateReps("9", "4"))
    }

    @Test
    fun `validateReps returns error when reps exceeds maxReps for given rpe`() {
        // RPE 7 → maxReps = 12; 13 should fail
        assertNotNull(validateReps("13", "7"))
        assertEquals("Max 12 reps at this RPE", validateReps("13", "7"))
        // RPE 4 → maxReps = 9; 10 should fail
        assertNotNull(validateReps("10", "4"))
        assertEquals("Max 9 reps at this RPE", validateReps("10", "4"))
    }

    @Test
    fun `validateReps boundary — exactly at maxReps limit is valid`() {
        // RPE 7 → maxReps = 12; 12 is the boundary — should be null
        assertNull(validateReps("12", "7"))
    }

    @Test
    fun `validateReps with rpe out of range falls back to 15`() {
        // RPE 3 is below minimum but user typed it; rpe parsing succeeds,
        // Calculator.maxReps(3.0) clamps to 4 → 9
        assertNotNull(validateReps("10", "3"))
    }
}
