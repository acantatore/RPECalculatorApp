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

import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acantatore.rpecalc.data.UserPreferencesData
import com.acantatore.rpecalc.ui.theme.*
import com.acantatore.rpecalc.utils.BarWeight
import com.acantatore.rpecalc.utils.UnitSystem
import com.acantatore.rpecalc.utils.WarmupProtocol

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentPalette: AppPalette,
    preferences: UserPreferencesData,
    onUnitChange: (UnitSystem) -> Unit,
    onBarWeightChange: (Double) -> Unit,
    onProtocolChange: (WarmupProtocol) -> Unit,
    onPaletteChange: (AppPalette) -> Unit,
    onBack: () -> Unit
) {
    var showAboutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBackground)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = currentPalette.accent
                )
            }
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = currentPalette.accent
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Unit System Section
            SettingsSection(title = "Unit System", currentPalette = currentPalette) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    UnitSystem.values().forEach { unit ->
                        FilterChip(
                            selected = preferences.unitSystem == unit,
                            onClick = { onUnitChange(unit) },
                            label = { Text(unit.label.uppercase()) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = currentPalette.accent,
                                selectedLabelColor = CardBackground
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bar Weight Section
            SettingsSection(title = "Bar Weight", currentPalette = currentPalette) {
                var expanded by remember { mutableStateOf(false) }

                val displayWeight = if (preferences.unitSystem == UnitSystem.KG) {
                    "${preferences.barWeight.formatDisplay()} kg"
                } else {
                    "${(preferences.barWeight * 2.205).formatDisplay()} lbs"
                }

                Box {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextPrimary
                        )
                    ) {
                        Text(displayWeight)
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(CardBackground)
                    ) {
                        BarWeight.values().forEach { bar ->
                            val weight = if (preferences.unitSystem == UnitSystem.KG) bar.kg else bar.lbs
                            val label = if (preferences.unitSystem == UnitSystem.KG) {
                                "${bar.kg.formatDisplay()} kg"
                            } else {
                                "${bar.lbs.formatDisplay()} lbs"
                            }

                            DropdownMenuItem(
                                text = { Text(label, color = TextPrimary) },
                                onClick = {
                                    onBarWeightChange(bar.kg) // Always store in kg
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Theme Section
            SettingsSection(title = "Theme", currentPalette = currentPalette) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Palettes.forEach { palette ->
                        val isSelected = currentPalette.name == palette.name
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPaletteChange(palette) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) currentPalette.accent.copy(alpha = 0.2f) else CardBackground
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Palette preview swatch
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .background(
                                                brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                                    colors = listOf(palette.gradientStart, palette.gradientEnd)
                                                ),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                    )
                                    Text(
                                        text = palette.name,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) currentPalette.accent else TextPrimary
                                    )
                                }
                                if (isSelected) {
                                    Text(
                                        text = "Selected",
                                        fontSize = 12.sp,
                                        color = currentPalette.accent
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Warmup Protocol Section
            SettingsSection(title = "Warmup Protocol", currentPalette = currentPalette) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    WarmupProtocol.PRESETS.forEach { protocol ->
                        val isSelected = preferences.warmupProtocol.name == protocol.name

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onProtocolChange(protocol) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) currentPalette.accent.copy(alpha = 0.2f) else CardBackground
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = protocol.name,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) currentPalette.accent else TextPrimary
                                    )
                                    if (isSelected) {
                                        Text(
                                            text = "Selected",
                                            fontSize = 12.sp,
                                            color = currentPalette.accent
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = protocol.steps.joinToString(" → ") { step ->
                                        if (step.percentage == 0) "Bar x${step.reps}"
                                        else "${step.percentage}% x${step.reps}"
                                    },
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // About Section
            SettingsSection(title = "About", currentPalette = currentPalette) {
                OutlinedButton(
                    onClick = { showAboutDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                ) {
                    Text("About this app")
                }
            }
        }
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Text(text = "About", fontWeight = FontWeight.Bold, color = currentPalette.accent)
            },
            text = {
                Text(
                    text = "This app is based on the PLSource RPE Calculator by the OpenPowerlifting project.\n\n" +
                           "Original project: https://gitlab.com/openpowerlifting/plsource\n\n" +
                           "Licensed under GNU AGPL v3.",
                    color = TextPrimary
                )
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Close", color = currentPalette.accent)
                }
            },
            containerColor = CardBackground
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    currentPalette: AppPalette,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = currentPalette.accent,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        content()
    }
}

private fun Double.formatDisplay(): String {
    return if (this == this.toLong().toDouble()) {
        this.toLong().toString()
    } else {
        String.format("%.1f", this)
    }
}
