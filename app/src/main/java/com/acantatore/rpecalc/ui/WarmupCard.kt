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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acantatore.rpecalc.ui.theme.*
import com.acantatore.rpecalc.utils.Plate
import com.acantatore.rpecalc.utils.UnitSystem
import com.acantatore.rpecalc.utils.WarmupSet

@Composable
fun WarmupCard(
    warmupSets: List<WarmupSet>,
    unit: UnitSystem,
    currentPalette: AppPalette
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
            // Header
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
                    text = "Warmup",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            // Warmup sets
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                warmupSets.forEachIndexed { index, set ->
                    WarmupSetRow(
                        setNumber = index + 1,
                        warmupSet = set,
                        unit = unit,
                        currentPalette = currentPalette
                    )

                    if (index < warmupSets.lastIndex) {
                        Divider(color = BorderColor.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

@Composable
private fun WarmupSetRow(
    setNumber: Int,
    warmupSet: WarmupSet,
    unit: UnitSystem,
    currentPalette: AppPalette
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Set header: number, weight, reps
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Set number badge
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(currentPalette.accent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = setNumber.toString(),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Weight display
                val weightDisplay = warmupSet.actualWeight.formatWeight()
                Text(
                    text = "$weightDisplay ${unit.label}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )

                // Rounding indicator
                if (warmupSet.wasRounded && !warmupSet.isBarOnly) {
                    Text(
                        text = "(rounded)",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
            }

            // Reps
            Text(
                text = "x ${warmupSet.reps}",
                fontSize = 14.sp,
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Plate breakdown
        if (warmupSet.isBarOnly) {
            Text(
                text = "Bar only",
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.padding(start = 32.dp)
            )
        } else {
            PlateBreakdown(
                plates = warmupSet.plates,
                unit = unit,
                modifier = Modifier.padding(start = 32.dp)
            )
        }
    }
}

@Composable
private fun PlateBreakdown(
    plates: List<Plate>,
    unit: UnitSystem,
    modifier: Modifier = Modifier
) {
    if (plates.isEmpty()) return

    // Group consecutive same-weight plates for cleaner display
    val grouped = groupPlates(plates)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        grouped.forEachIndexed { index, (plate, count) ->
            PlateChip(plate = plate, count = count)

            if (index < grouped.lastIndex) {
                Text(
                    text = "+",
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }
        }

        Text(
            text = "each side",
            fontSize = 10.sp,
            color = TextSecondary,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
private fun PlateChip(
    plate: Plate,
    count: Int
) {
    val isDark = plate.colorName == "Black"
    val textColor = if (isDark || plate.colorName == "Blue" || plate.colorName == "Red" || plate.colorName == "Green") {
        Color.White
    } else {
        Color.Black
    }

    Row(
        modifier = Modifier
            .background(plate.color, RoundedCornerShape(4.dp))
            .border(
                width = if (plate.colorName == "White") 1.dp else 0.dp,
                color = if (plate.colorName == "White") BorderColor else Color.Transparent,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = plate.weight.formatWeight(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
        if (count > 1) {
            Text(
                text = "x$count",
                fontSize = 9.sp,
                color = textColor.copy(alpha = 0.8f)
            )
        }
    }
}

/**
 * Groups consecutive same-weight plates.
 */
private fun groupPlates(plates: List<Plate>): List<Pair<Plate, Int>> {
    if (plates.isEmpty()) return emptyList()

    val result = mutableListOf<Pair<Plate, Int>>()
    var current = plates[0]
    var count = 1

    for (i in 1 until plates.size) {
        if (plates[i].weight == current.weight) {
            count++
        } else {
            result.add(Pair(current, count))
            current = plates[i]
            count = 1
        }
    }
    result.add(Pair(current, count))

    return result
}

private fun Double.formatWeight(): String {
    return if (this == this.toLong().toDouble()) {
        this.toLong().toString()
    } else {
        this.toString()
    }
}
