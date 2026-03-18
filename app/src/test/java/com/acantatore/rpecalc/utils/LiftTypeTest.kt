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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class LiftTypeTest {

    @Test
    fun `all lift types have non-empty display names`() {
        LiftType.entries.forEach { lift ->
            assertNotNull(lift.displayName)
            assert(lift.displayName.isNotEmpty()) { "${lift.name} has empty displayName" }
        }
    }

    @Test
    fun `lift type display names match expected values`() {
        assertEquals("Squat",     LiftType.SQUAT.displayName)
        assertEquals("Bench",     LiftType.BENCH.displayName)
        assertEquals("Deadlift",  LiftType.DEADLIFT.displayName)
        assertEquals("OHP",       LiftType.OHP.displayName)
        assertEquals("Other",     LiftType.OTHER.displayName)
    }

    @Test
    fun `lift type enum has exactly five values`() {
        assertEquals(5, LiftType.entries.size)
    }

    @Test
    fun `lift type can be retrieved by name`() {
        assertEquals(LiftType.SQUAT,    LiftType.valueOf("SQUAT"))
        assertEquals(LiftType.BENCH,    LiftType.valueOf("BENCH"))
        assertEquals(LiftType.DEADLIFT, LiftType.valueOf("DEADLIFT"))
        assertEquals(LiftType.OHP,      LiftType.valueOf("OHP"))
        assertEquals(LiftType.OTHER,    LiftType.valueOf("OTHER"))
    }
}
