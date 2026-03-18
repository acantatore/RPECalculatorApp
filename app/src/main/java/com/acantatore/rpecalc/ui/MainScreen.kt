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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import com.acantatore.rpecalc.data.UserPreferencesData
import com.acantatore.rpecalc.ui.theme.*
import com.acantatore.rpecalc.utils.Calculator
import com.acantatore.rpecalc.utils.UnitSystem
import com.acantatore.rpecalc.utils.WarmupSet
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun MainScreen(
    currentPalette: AppPalette,
    preferences: UserPreferencesData,
    onPaletteChange: (AppPalette) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    var haveWeightInput by remember { mutableStateOf("") }
    var haveRepsInput by remember { mutableStateOf("") }
    var haveRpeInput by remember { mutableStateOf("") }

    var e1rmResult by remember { mutableStateOf<Double?>(null) }
    var e1rmDisplay by remember { mutableStateOf<String?>(null) }

    var wantRepsInput by remember { mutableStateOf("") }
    var wantRpeInput by remember { mutableStateOf("") }
    var targetWeightResult by remember { mutableStateOf<Double?>(null) }
    var targetWeightDisplay by remember { mutableStateOf<String?>(null) }

    var warmupSets by remember { mutableStateOf<List<WarmupSet>>(emptyList()) }

    var showAboutDialog by remember { mutableStateOf(false) }
    var showPaletteMenu by remember { mutableStateOf(false) }

    // Automatic re-calc when inputs or preferences change
    LaunchedEffect(
        haveWeightInput, haveRepsInput, haveRpeInput,
        wantRepsInput, wantRpeInput,
        preferences.unitSystem, preferences.barWeight, preferences.warmupProtocol
    ) {
        val w = haveWeightInput.toDoubleOrNull()
        val r = haveRepsInput.toIntOrNull()
        val rpe = haveRpeInput.toDoubleOrNull()

        if (w != null && w > 0 && r != null && r > 0 && rpe != null && rpe > 0) {
            val e1rm = Calculator.calculateE1RM(w, r, rpe)
            if (e1rm > 0) {
                e1rmResult = e1rm
                e1rmDisplay = String.format(Locale.getDefault(), "%.1f", e1rm)

                val wr = wantRepsInput.toIntOrNull()
                val wrpe = wantRpeInput.toDoubleOrNull()
                if (wr != null && wr > 0 && wrpe != null && wrpe > 0) {
                    val tw = Calculator.calculateTargetWeight(e1rm, wr, wrpe)
                    if (tw > 0) {
                        targetWeightResult = tw
                        targetWeightDisplay = String.format(Locale.getDefault(), "%.1f", tw)

                        // Generate warmup sets
                        warmupSets = Calculator.generateWarmupSets(
                            workingWeight = tw,
                            barWeight = preferences.barWeight,
                            protocol = preferences.warmupProtocol,
                            unit = preferences.unitSystem
                        )
                    } else {
                        targetWeightResult = null
                        targetWeightDisplay = null
                        warmupSets = emptyList()
                    }
                } else {
                    targetWeightResult = null
                    targetWeightDisplay = null
                    warmupSets = emptyList()
                }
            } else {
                e1rmResult = null
                e1rmDisplay = null
                targetWeightResult = null
                targetWeightDisplay = null
                warmupSets = emptyList()
            }
        } else {
            e1rmResult = null
            e1rmDisplay = null
            targetWeightResult = null
            targetWeightDisplay = null
            warmupSets = emptyList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(BackgroundColor)
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBackground)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "RPE Calculator",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = currentPalette.accent
            )

            Row {
                // Settings icon
                IconButton(onClick = onNavigateToSettings) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = currentPalette.accent
                    )
                }

                // Theme picker
                Box {
                    IconButton(onClick = { showPaletteMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Change Theme",
                            tint = currentPalette.accent
                        )
                    }

                    DropdownMenu(
                        expanded = showPaletteMenu,
                        onDismissRequest = { showPaletteMenu = false },
                        modifier = Modifier.background(CardBackground)
                    ) {
                        Palettes.forEach { palette ->
                            DropdownMenuItem(
                                text = { Text(palette.name, color = TextPrimary) },
                                onClick = {
                                    onPaletteChange(palette)
                                    showPaletteMenu = false
                                }
                            )
                        }
                    }
                }

                // About
                IconButton(onClick = { showAboutDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "About",
                        tint = currentPalette.accent
                    )
                }
            }
        }

        // Unit indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBackground)
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.End
        ) {
            val barDisplay = if (preferences.unitSystem == UnitSystem.KG)
                "${preferences.barWeight.toInt()} kg"
            else
                "${(preferences.barWeight * 2.205).roundToInt()} lbs"
            Text(
                text = "Unit: ${preferences.unitSystem.label} | Bar: $barDisplay",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        // Gradient background area for cards
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(currentPalette.gradientStart, currentPalette.gradientEnd)
                    )
                )
                .padding(vertical = 26.dp, horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // HAVE CARD
                InputCard(
                    title = "Have",
                    currentPalette = currentPalette,
                    content = {
                        RpeInputField("Weight", haveWeightInput, currentPalette) { haveWeightInput = it }
                        Spacer(modifier = Modifier.height(10.dp))
                        RpeInputField("Reps", haveRepsInput, currentPalette) { haveRepsInput = it }
                        Spacer(modifier = Modifier.height(10.dp))
                        RpeInputField("RPE", haveRpeInput, currentPalette) { haveRpeInput = it }

                        Divider(modifier = Modifier.padding(vertical = 16.dp), color = BorderColor)

                        ResultRow("E1RM", e1rmDisplay ?: "")
                    }
                )

                Spacer(modifier = Modifier.height(26.dp))

                // WANT CARD
                InputCard(
                    title = "Want",
                    currentPalette = currentPalette,
                    content = {
                        RpeInputField("Reps", wantRepsInput, currentPalette) { wantRepsInput = it }
                        Spacer(modifier = Modifier.height(10.dp))
                        RpeInputField("RPE", wantRpeInput, currentPalette) { wantRpeInput = it }

                        Divider(modifier = Modifier.padding(vertical = 16.dp), color = BorderColor)

                        ResultRow("Weight", targetWeightDisplay ?: "")
                    }
                )

                // WARMUP CARD - shows when target weight is calculated
                AnimatedVisibility(visible = warmupSets.isNotEmpty()) {
                    Column {
                        Spacer(modifier = Modifier.height(26.dp))
                        WarmupCard(
                            warmupSets = warmupSets,
                            unit = preferences.unitSystem,
                            currentPalette = currentPalette
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
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
fun InputCard(
    title: String,
    currentPalette: AppPalette,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(5.dp),
                spotColor = CardShadow,
                ambientColor = CardShadow
            ),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(currentPalette.gradientEnd, currentPalette.gradientStart)
                        )
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Column(
                modifier = Modifier.padding(horizontal = 40.dp, vertical = 20.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun RpeInputField(
    label: String,
    value: String,
    currentPalette: AppPalette,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 18.sp,
            color = TextPrimary,
            modifier = Modifier.padding(end = 16.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = {
                if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                    onValueChange(it)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(110.dp),
            textStyle = LocalTextStyle.current.copy(
                fontSize = 14.sp,
                color = TextPrimary
            ),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = currentPalette.accent,
                unfocusedBorderColor = BorderColor,
                cursorColor = currentPalette.accent
            ),
            shape = RoundedCornerShape(5.dp)
        )
    }
}

@Composable
fun ResultRow(label: String, result: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 18.sp,
            color = TextPrimary
        )

        Box(
            modifier = Modifier.width(110.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = result,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
    }
}
