# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Debug build
./gradlew assembleDebug

# Release build (requires local.properties with RELEASE_STORE_PASSWORD and RELEASE_KEY_PASSWORD)
./gradlew assembleRelease

# Run all unit tests
./gradlew testDebugUnitTest

# Run a single test class
./gradlew testDebugUnitTest --tests "com.acantatore.rpecalc.utils.CalculatorTest"

# Run a single test method
./gradlew testDebugUnitTest --tests "com.acantatore.rpecalc.utils.CalculatorTest.e1rm calculation is correct for standard input"
```

Unit tests are JVM-only (no emulator needed). There are no instrumented tests that run.

## Architecture

Single-activity Jetpack Compose app. No ViewModel, no Jetpack Navigation.

**Navigation** uses a `sealed class Screen { Main, Settings, History }` state variable in `MainActivity`. Switching screens means setting `screen = Screen.X` — the top-level `when(screen)` renders the appropriate composable.

**State management** lives directly in composables via `remember`. The main calculation flow in `MainScreen` uses a single `LaunchedEffect` keyed on all inputs and preferences; any change triggers a full recalculation cascade: E1RM → target weight → warmup sets.

**Persistence** uses Jetpack DataStore (`data/UserPreferences.kt`). Warmup protocol steps are serialized as a `"percentage:reps,..."` string. Named presets are stored by name and looked up on read; unknown names fall back to custom step parsing.

### Core Calculation Layer (`utils/`)

- **`Calculator`** — pure-function object. The RPE→percentage formula is Tuchscherer's chart translated into a piecewise continuous function: quadratic for high percentages (x ≤ 2.92), linear otherwise, where `x = (10 - rpe) + (reps - 1)`. Returns 0 for x ≥ 16 or RPE < 4.
- **`PlateCalculator`** — greedy largest-first algorithm: subtract bar weight, divide by 2, greedily fill from largest plate down. Returns both the plate list per side and the actual achievable weight (which may differ from target due to rounding).
- **`PlateModels`** — `Plate` and `UnitSystem` (KG/LBS) data classes.
- **`WarmupModels`** — `WarmupProtocol` (name + steps), `ProtocolStep` (percentage, reps), `WarmupSet` (weight, plates, rounding metadata). Three built-in presets: `DEFAULT`, `POWERLIFTING`, `MINIMAL`.

### UI Layer (`ui/`)

- **`MainScreen`** — "Have" card (weight/reps/RPE → E1RM) + "Want" card (reps/RPE → target weight) + `WarmupCard` (animated, shown only when target weight is set) + lift selector chip row + Log Session button.
- **`WarmupCard`** — displays warmup sets with IWF/IPF color-coded plate circles.
- **`SettingsScreen`** — unit system, bar weight, warmup protocol selection, About section.
- **`HistoryScreen`** — E1RM trend chart (Vico) + scrollable session list per lift. `ScrollableTabRow` with 5 lift tabs; chart and list share a single `LazyColumn`.
- **`theme/Color.kt`** — defines `AppPalette` (gradientStart, gradientEnd, accent, name) and the 5 built-in palettes. Also defines global static colors (`CardBackground`, `TextPrimary`, etc.) used directly by composables.

### Theming

Palette is persisted to DataStore (`paletteName` key) and restored on cold start. The active `AppPalette` is passed down as a parameter to all composables that need it. Static colors (backgrounds, text, borders) are package-level constants in `theme/Color.kt`.

## gstack

Use the `/browse` skill from gstack for all web browsing. Never use `mcp__claude-in-chrome__*` tools.

Available skills:

- `/plan-ceo-review` — CEO/founder-mode plan review
- `/plan-eng-review` — Eng manager-mode plan review
- `/plan-design-review` — Designer's eye review of a live site (report only)
- `/design-consultation` — Full design system proposal
- `/review` — Pre-landing PR review
- `/ship` — Ship workflow: merge, test, bump version, create PR
- `/browse` — Headless browser for QA, testing, and dogfooding
- `/qa` — QA test and fix bugs iteratively
- `/qa-only` — QA report only, no fixes
- `/qa-design-review` — Designer's eye QA with fixes
- `/setup-browser-cookies` — Import real browser cookies into headless session
- `/retro` — Weekly engineering retrospective
- `/document-release` — Post-ship documentation update

## Release Signing

`local.properties` must contain:
```
RELEASE_STORE_PASSWORD=...
RELEASE_KEY_PASSWORD=...
```
The keystore file is `app/release.jks`. These are never committed.
