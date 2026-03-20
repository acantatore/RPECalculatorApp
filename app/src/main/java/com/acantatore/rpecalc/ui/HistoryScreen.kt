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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.m3.style.m3ChartStyle
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.acantatore.rpecalc.data.SessionEntity
import com.acantatore.rpecalc.data.SessionRepository
import com.acantatore.rpecalc.data.UserPreferencesData
import com.acantatore.rpecalc.ui.theme.*
import com.acantatore.rpecalc.utils.LiftType
import com.acantatore.rpecalc.utils.UnitSystem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    currentPalette: AppPalette,
    sessionRepository: SessionRepository,
    preferences: UserPreferencesData,
    onBack: () -> Unit
) {
    var selectedLift by remember { mutableStateOf(LiftType.SQUAT) }
    // Sessions ordered ASC (oldest first) — chart needs ascending order for trend
    var sessions by remember { mutableStateOf<List<SessionEntity>>(emptyList()) }
    var isError by remember { mutableStateOf(false) }

    LaunchedEffect(selectedLift) {
        isError = false
        try {
            sessions = sessionRepository.getHistoryByLift(selectedLift)
        } catch (e: Exception) {
            isError = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        // AppBar — same chrome as SettingsScreen
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
                text = "E1RM History",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = currentPalette.accent
            )
        }

        // Lift tab row — ScrollableTabRow handles 5 tabs on narrow screens
        val selectedIndex = LiftType.entries.indexOf(selectedLift)
        ScrollableTabRow(
            selectedTabIndex = selectedIndex,
            containerColor = CardBackground,
            contentColor = currentPalette.accent,
            divider = { Divider(color = BorderColor, thickness = 0.5.dp) }
        ) {
            LiftType.entries.forEachIndexed { _, lift ->
                Tab(
                    selected = selectedLift == lift,
                    onClick = { selectedLift = lift },
                    text = {
                        Text(
                            text = lift.displayName,
                            fontSize = 14.sp,
                            fontWeight = if (selectedLift == lift) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedLift == lift) currentPalette.accent else TextSecondary
                        )
                    }
                )
            }
        }

        // Single LazyColumn — chart card is the header item, session rows are list items
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                ChartCard(
                    sessions = sessions,
                    isError = isError,
                    liftName = selectedLift.displayName,
                    palette = currentPalette,
                    unitSystem = preferences.unitSystem
                )
            }

            if (!isError && sessions.isNotEmpty()) {
                // List shows most-recent-first (reversed from ASC query order)
                items(sessions.reversed()) { session ->
                    SessionHistoryRow(session = session, unitSystem = preferences.unitSystem)
                    Divider(color = BorderColor, thickness = 0.5.dp)
                }
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun ChartCard(
    sessions: List<SessionEntity>,
    isError: Boolean,
    liftName: String,
    palette: AppPalette,
    unitSystem: UnitSystem
) {
    val a11yDesc = when {
        isError -> "E1RM chart for $liftName. Could not load data."
        sessions.isEmpty() -> "E1RM chart for $liftName. No sessions logged yet."
        else -> "E1RM chart for $liftName. ${sessions.size} sessions. " +
                "Latest: ${formatE1rm(sessions.last().e1rm, unitSystem)} ${unitSystem.label}."
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
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
            // Gradient header — same as InputCard
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(palette.gradientEnd, palette.gradientStart)
                        )
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = "E1RM Progress",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .semantics { contentDescription = a11yDesc }
            ) {
                when {
                    isError -> {
                        Text(
                            text = "Couldn't load history.",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    sessions.isEmpty() -> {
                        EmptyChartState(liftName = liftName)
                    }
                    else -> {
                        E1rmChart(
                            sessions = sessions,
                            unitSystem = unitSystem,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyChartState(liftName: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "No $liftName sessions yet.",
            fontSize = 14.sp,
            color = TextSecondary
        )
        Text(
            text = "Log a set from the main screen to start tracking your E1RM.",
            fontSize = 12.sp,
            color = TextSecondary
        )
    }
}

@Composable
private fun E1rmChart(
    sessions: List<SessionEntity>,
    unitSystem: UnitSystem,
    modifier: Modifier = Modifier
) {
    val dateLabels = remember(sessions) { sessions.map { formatSessionDate(it.date) } }

    val modelProducer = remember { ChartEntryModelProducer() }

    LaunchedEffect(sessions, unitSystem) {
        val entries = sessions.mapIndexed { index, entry ->
            val yVal = if (unitSystem == UnitSystem.LBS) (entry.e1rm * 2.205).toFloat()
                       else entry.e1rm.toFloat()
            FloatEntry(x = index.toFloat(), y = yVal)
        }
        modelProducer.setEntries(entries)
    }

    val bottomAxisFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
        dateLabels.getOrElse(value.toInt()) { "" }
    }

    val chart = lineChart()

    ProvideChartStyle(m3ChartStyle()) {
        Chart(
            chart = chart,
            chartModelProducer = modelProducer,
            modifier = modifier,
            startAxis = rememberStartAxis(),
            bottomAxis = rememberBottomAxis(valueFormatter = bottomAxisFormatter)
        )
    }
}

@Composable
private fun SessionHistoryRow(
    session: SessionEntity,
    unitSystem: UnitSystem
) {
    val weightDisplay = formatE1rm(session.weight, unitSystem)
    val e1rmDisplay = formatE1rm(session.e1rm, unitSystem)
    val unitLabel = unitSystem.label
    val rpeDisplay = if (session.rpe == session.rpe.toLong().toDouble())
        session.rpe.toLong().toString()
    else
        String.format(Locale.getDefault(), "%.1f", session.rpe)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatSessionDate(session.date),
                fontSize = 14.sp,
                color = TextSecondary
            )
            Text(
                text = "$e1rmDisplay $unitLabel",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
        Text(
            text = "$weightDisplay $unitLabel × ${session.reps} @ RPE $rpeDisplay",
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

private fun formatSessionDate(epochMs: Long): String {
    val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
    return sdf.format(Date(epochMs))
}

private fun formatE1rm(kgValue: Double, unit: UnitSystem): String {
    val value = if (unit == UnitSystem.LBS) kgValue * 2.205 else kgValue
    return String.format(Locale.getDefault(), "%.1f", value)
}
