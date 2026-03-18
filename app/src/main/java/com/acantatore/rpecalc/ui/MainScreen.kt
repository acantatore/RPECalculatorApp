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
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
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

    // Validation — only shown when field is non-empty and out of range
    val haveRpeError  = validateRpe(haveRpeInput)
    val haveRepsError = validateReps(haveRepsInput, haveRpeInput)
    val wantRpeError  = validateRpe(wantRpeInput)
    val wantRepsError = validateReps(wantRepsInput, wantRpeInput)

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
                        RpeInputField("Weight", haveWeightInput, currentPalette, placeholder = "e.g. 100") { haveWeightInput = it }
                        Spacer(modifier = Modifier.height(10.dp))
                        RpeInputField("Reps", haveRepsInput, currentPalette, placeholder = "1 – 15", error = haveRepsError) { haveRepsInput = it }
                        Spacer(modifier = Modifier.height(10.dp))
                        RpeInputField("RPE", haveRpeInput, currentPalette, placeholder = "4 – 10", error = haveRpeError) { haveRpeInput = it }

                        Divider(modifier = Modifier.padding(vertical = 16.dp), color = BorderColor)

                        ResultRow("E1RM", e1rmDisplay ?: "", currentPalette)
                    }
                )

                Spacer(modifier = Modifier.height(26.dp))

                // WANT CARD
                InputCard(
                    title = "Want",
                    currentPalette = currentPalette,
                    content = {
                        RpeInputField("Reps", wantRepsInput, currentPalette, placeholder = "1 – 15", error = wantRepsError) { wantRepsInput = it }
                        Spacer(modifier = Modifier.height(10.dp))
                        RpeInputField("RPE", wantRpeInput, currentPalette, placeholder = "4 – 10", error = wantRpeError, imeAction = ImeAction.Done) { wantRpeInput = it }

                        Divider(modifier = Modifier.padding(vertical = 16.dp), color = BorderColor)

                        ResultRow("Weight", targetWeightDisplay ?: "", currentPalette)
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
                shape = RoundedCornerShape(8.dp),
                spotColor = CardShadow,
                ambientColor = CardShadow
            ),
        shape = RoundedCornerShape(8.dp),
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
    placeholder: String = "",
    error: String? = null,
    imeAction: ImeAction = ImeAction.Next,
    onValueChange: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    Column {
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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = imeAction),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) },
                onDone = { focusManager.clearFocus() }
            ),
            modifier = Modifier.width(110.dp),
            textStyle = LocalTextStyle.current.copy(
                fontSize = 16.sp,
                color = TextPrimary
            ),
            placeholder = if (placeholder.isNotEmpty()) ({
                Text(text = placeholder, fontSize = 12.sp, color = TextSecondary)
            }) else null,
            isError = error != null,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = currentPalette.accent,
                unfocusedBorderColor = BorderColor,
                cursorColor = currentPalette.accent
            ),
            shape = RoundedCornerShape(8.dp)
        )
    }
    if (error != null) {
        Text(
            text = error,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 4.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
    }
}

@Composable
fun ResultRow(label: String, result: String, currentPalette: AppPalette) {
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
                text = if (result.isEmpty()) "—" else result,
                fontSize = if (result.isEmpty()) 18.sp else 22.sp,
                fontWeight = FontWeight.Bold,
                color = if (result.isEmpty()) TextSecondary else currentPalette.accent
            )
        }
    }
}

// Validation helpers — internal so they are testable from the test source set.
// Only return an error string when the field is non-empty AND out of the valid range.

internal fun validateRpe(input: String): String? {
    val rpe = input.toDoubleOrNull() ?: return null
    return when {
        rpe < 4.0  -> "Min RPE is 4"
        rpe > 10.0 -> "Max RPE is 10"
        else       -> null
    }
}

internal fun validateReps(repsInput: String, rpeInput: String): String? {
    val reps = repsInput.toIntOrNull() ?: return null
    val rpe  = rpeInput.toDoubleOrNull()
    val max  = if (rpe != null) Calculator.maxReps(rpe) else 15
    return if (reps > max) "Max $max reps at this RPE" else null
}
