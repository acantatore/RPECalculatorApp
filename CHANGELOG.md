# Changelog

All notable changes to this project will be documented in this file.

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [1.2.0] - 2026-03-19

### Added
- **E1RM History Screen** — new screen accessible via the chart icon in the AppBar. Shows a Vico line chart of E1RM over time per lift, plus a scrollable list of all logged sessions. Five lift tabs (Squat, Bench, Deadlift, OHP, Other) in a `ScrollableTabRow`. Chart and list share a single `LazyColumn` to avoid nested scroll conflicts.
- **History navigation** — `sealed class Screen { Main, Settings, History }` replaces the `showSettings: Boolean` boolean nav. Enables clean three-destination navigation without Jetpack Navigation.
- **Vico chart integration** — `com.patrykandpatrick.vico:compose-m3:1.14.0` added; `E1rmChart` uses `ChartEntryModelProducer` + `FloatEntry` (1.x API) with custom bottom-axis date formatter.
- **About section** — moved from AppBar overlay to a dedicated section in Settings Screen with an `OutlinedButton` and `AlertDialog`.
- **History AppBar icon** — replaced the About icon in the AppBar with a History icon (`Icons.Default.History`). AppBar is now: History | Theme | Settings.
- **`SessionDao.getHistoryByLift`** — parameterized query returning sessions for a given lift in ascending date order; uses `readableDatabase`.
- **`AppDatabase.readableSessionDao()`** — convenience accessor wrapping `readableDatabase` for history queries.
- **`SessionRepository.getHistoryByLift`** — suspending function dispatching to `Dispatchers.IO`.

### Changed
- AppBar icon layout: About icon removed; History icon added as leftmost action.
- `DESIGN.md` — documented `HistoryScreen` and `SessionRow` component specs; 7 design decision log entries added.

## [1.1.0] - 2026-03-18

### Added
- **Session logging** — log completed sets directly from the calculator. Each log entry records lift type, weight, reps, RPE, and estimated 1RM; weights are stored in kg regardless of the active unit system.
- **Lift selector** — horizontal chip row on the main screen lets users tag each set with a lift type (Squat, Bench, Deadlift, OHP, Other) before logging.
- **Log Session button** — appears below the E1RM result with a debounced tap handler and a snackbar confirmation; disabled while a log write is in-flight to prevent duplicate entries.
- **Palette persistence** — the selected color theme now survives app restarts; the chosen palette name is saved to DataStore and restored on cold start.
- **SQLite data layer** — `SessionDao`, `AppDatabase` (SQLiteOpenHelper singleton), and `SessionRepository` power the session log without annotation-processor dependencies.
- `LiftTypeTest` — unit tests covering all five enum values, display names, and `valueOf` lookup.

### Changed
- `LiftType.values()` replaced with `LiftType.entries` throughout (Kotlin 1.9 best practice).

## [1.0.1] - 2026-03-17

### Changed
- Added warmup set calculator with IWF-colored plate breakdown.
- Secured release signing credentials in `local.properties`.
- Fixed launcher icon.
