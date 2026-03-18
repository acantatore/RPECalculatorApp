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

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.acantatore.rpecalc.utils.BarWeight
import com.acantatore.rpecalc.utils.ProtocolStep
import com.acantatore.rpecalc.utils.UnitSystem
import com.acantatore.rpecalc.utils.WarmupProtocol
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * User preferences data class.
 */
data class UserPreferencesData(
    val unitSystem: UnitSystem = UnitSystem.KG,
    val barWeight: Double = 20.0,
    val warmupProtocol: WarmupProtocol = WarmupProtocol.DEFAULT,
    val paletteName: String = "Original Purple"
)

/**
 * DataStore wrapper for user preferences.
 */
class UserPreferences(private val context: Context) {

    companion object {
        private val UNIT_SYSTEM = stringPreferencesKey("unit_system")
        private val BAR_WEIGHT = doublePreferencesKey("bar_weight")
        private val PROTOCOL_NAME = stringPreferencesKey("protocol_name")
        private val PROTOCOL_STEPS = stringPreferencesKey("protocol_steps")
        private val PALETTE_NAME = stringPreferencesKey("palette_name")
    }

    /**
     * Flow of user preferences, updated whenever settings change.
     */
    val preferencesFlow: Flow<UserPreferencesData> = context.dataStore.data.map { preferences ->
        val unitSystem = preferences[UNIT_SYSTEM]?.let {
            try { UnitSystem.valueOf(it) } catch (e: Exception) { UnitSystem.KG }
        } ?: UnitSystem.KG

        val barWeight = preferences[BAR_WEIGHT] ?: 20.0

        val protocol = parseProtocol(
            preferences[PROTOCOL_NAME],
            preferences[PROTOCOL_STEPS]
        )

        val paletteName = preferences[PALETTE_NAME] ?: "Original Purple"

        UserPreferencesData(
            unitSystem = unitSystem,
            barWeight = barWeight,
            warmupProtocol = protocol,
            paletteName = paletteName
        )
    }

    /**
     * Updates the unit system.
     */
    suspend fun setUnitSystem(unit: UnitSystem) {
        context.dataStore.edit { preferences ->
            preferences[UNIT_SYSTEM] = unit.name
        }
    }

    /**
     * Updates the bar weight.
     */
    suspend fun setBarWeight(weight: Double) {
        context.dataStore.edit { preferences ->
            preferences[BAR_WEIGHT] = weight
        }
    }

    /**
     * Updates the palette name.
     */
    suspend fun setPaletteName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[PALETTE_NAME] = name
        }
    }

    /**
     * Updates the warmup protocol.
     */
    suspend fun setWarmupProtocol(protocol: WarmupProtocol) {
        context.dataStore.edit { preferences ->
            preferences[PROTOCOL_NAME] = protocol.name
            preferences[PROTOCOL_STEPS] = serializeSteps(protocol.steps)
        }
    }

    /**
     * Serializes protocol steps to a string.
     * Format: "percentage:reps,percentage:reps,..."
     */
    private fun serializeSteps(steps: List<ProtocolStep>): String {
        return steps.joinToString(",") { "${it.percentage}:${it.reps}" }
    }

    /**
     * Parses protocol from stored strings.
     */
    private fun parseProtocol(name: String?, stepsString: String?): WarmupProtocol {
        if (name == null || stepsString == null) {
            return WarmupProtocol.DEFAULT
        }

        // Check if it's a preset
        WarmupProtocol.PRESETS.find { it.name == name }?.let { return it }

        // Parse custom protocol
        val steps = try {
            stepsString.split(",").mapNotNull { part ->
                val (pct, reps) = part.split(":").map { it.toInt() }
                ProtocolStep(pct, reps)
            }
        } catch (e: Exception) {
            return WarmupProtocol.DEFAULT
        }

        if (steps.isEmpty()) {
            return WarmupProtocol.DEFAULT
        }

        return WarmupProtocol(name, steps)
    }

    /**
     * Gets the bar weight in the specified unit.
     */
    fun getBarWeightInUnit(barWeight: Double, targetUnit: UnitSystem, storedUnit: UnitSystem): Double {
        if (targetUnit == storedUnit) return barWeight

        return when (targetUnit) {
            UnitSystem.KG -> barWeight / 2.205 // lbs to kg
            UnitSystem.LBS -> barWeight * 2.205 // kg to lbs
        }
    }
}
