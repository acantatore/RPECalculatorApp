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
package com.acantatore.rpecalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.acantatore.rpecalc.data.UserPreferences
import com.acantatore.rpecalc.data.UserPreferencesData
import com.acantatore.rpecalc.ui.MainScreen
import com.acantatore.rpecalc.ui.SettingsScreen
import com.acantatore.rpecalc.ui.theme.Palettes
import com.acantatore.rpecalc.ui.theme.RPECalcTheme
import com.acantatore.rpecalc.utils.UnitSystem
import com.acantatore.rpecalc.utils.WarmupProtocol
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userPreferences = UserPreferences(this)

        setContent {
            var currentPalette by remember { mutableStateOf(Palettes[0]) }
            var showSettings by remember { mutableStateOf(false) }

            // Collect preferences from DataStore
            val preferences by userPreferences.preferencesFlow.collectAsState(
                initial = UserPreferencesData()
            )

            val coroutineScope = rememberCoroutineScope()

            RPECalcTheme(palette = currentPalette) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (showSettings) {
                        SettingsScreen(
                            currentPalette = currentPalette,
                            preferences = preferences,
                            onUnitChange = { unit ->
                                coroutineScope.launch {
                                    userPreferences.setUnitSystem(unit)
                                }
                            },
                            onBarWeightChange = { weight ->
                                coroutineScope.launch {
                                    userPreferences.setBarWeight(weight)
                                }
                            },
                            onProtocolChange = { protocol ->
                                coroutineScope.launch {
                                    userPreferences.setWarmupProtocol(protocol)
                                }
                            },
                            onPaletteChange = { currentPalette = it },
                            onBack = { showSettings = false }
                        )
                    } else {
                        MainScreen(
                            currentPalette = currentPalette,
                            preferences = preferences,
                            onNavigateToSettings = { showSettings = true }
                        )
                    }
                }
            }
        }
    }
}
