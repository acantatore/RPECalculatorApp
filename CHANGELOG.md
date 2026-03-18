# Changelog

All notable changes to this project will be documented in this file.

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

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
