# TODOS

## Deferred Features

### Copy Warmup to Clipboard
**Priority:** P3 | **Effort:** S | **Status:** Deferred from v1

**What:** One-tap button to copy the warmup scheme as formatted text for sharing.

**Why:** Users often train with partners and want to share warmup plans. Currently would need to screenshot or manually type.

**Pros:**
- Low effort (~30 min implementation)
- Delightful feature that makes users smile
- Enables sharing without screenshots

**Cons:**
- Not essential for core functionality
- Adds another UI element to warmup card

**Context:** Deferred during CEO plan review for Warmup Calculator feature (2026-03-17). Ship core warmup functionality first, add clipboard feature based on user feedback.

**Implementation notes:**
- Use Android ClipboardManager
- Format as plain text with weights and plate breakdown
- Add small copy icon button to warmup card header

---

### Persist Theme Selection
**Priority:** P2 | **Effort:** S | **Status:** Deferred from design review

**What:** Save the selected color palette name to DataStore so the theme survives app restarts.

**Why:** Theme was moved from the AppBar overflow menu into Settings (design review 2026-03-18). Users strongly expect settings-screen choices to persist across launches. Currently resets to "Original Purple" on every cold start.

**Pros:**
- Removes a surprising inconsistency (setting visible in Settings but not durable)
- Low effort — DataStore already in use for unit system, bar weight, warmup protocol

**Cons:**
- Palette objects can't be stored directly — need to store by name string and look up in `Palettes` list on restore
- If a palette name changes or is removed in a future version, the stored name won't match and must fall back gracefully

**Context:** Deferred during eng review 2026-03-18. The in-memory approach is documented in DESIGN.md ("Palette is in-memory state only — not persisted"). That decision was made before theme moved to Settings.

**Implementation notes:**
- Add `paletteName: String` to `UserPreferencesData` with default `"Original Purple"`
- Add `setPaletteName(name: String)` to `UserPreferences`
- On MainActivity start: read `paletteName` from preferences, find matching palette in `Palettes` list, fall back to `Palettes[0]` if not found
- `onPaletteChange` in MainActivity saves name to DataStore alongside setting the in-memory state

**Depends on:** Nothing — DataStore infra already present

---

## Future Phases

### E1RM History Screen
**Priority:** P2 | **Effort:** M | **Status:** Deferred — build Session Log first

**What:** A scrollable screen listing all logged sessions, grouped by lift (Squat, Bench, Deadlift, OHP, Other), showing E1RM over time so the user can track strength progress.

**Why:** Without a history screen, the session log database accumulates data invisibly. Users need to see the payoff of logging — an E1RM trend line that validates their training is working.

**Pros:**
- Closes the loop on session logging: log → see progress → motivated to keep logging
- Establishes the data visualization foundation for future program templates (Phase 4)
- Room DB is already queried by lift in the DAO, so the data layer is ready

**Cons:**
- Requires a new screen + Jetpack Navigation or equivalent nav state (currently nav is a single boolean)
- Chart/trend-line library decision deferred (MPAndroidChart, Vico, or custom Canvas)
- Data is only useful after several sessions are logged — early users won't see a trend

**Context:** Deferred during CEO review (SELECTIVE EXPANSION) on 2026-03-18. The Room DB schema must be designed with this screen in mind: `date: Long` (epoch ms), `lift: String` (enum ordinal or name), `e1rm: Double`. Don't use a generic "value" column — name it `e1rm` now so the history query is legible later.

**Implementation notes:**
- Query: `SELECT date, e1rm FROM sessions WHERE lift = ? ORDER BY date ASC`
- Group sessions by lift into tabs or a dropdown selector
- Minimum viable chart: a simple line chart or table sorted by date with E1RM delta
- Navigation: add `showHistory: Boolean` nav state alongside `showSettings`

**Depends on:** Session Log feature (Room DB + SessionEntity) must ship first
**Blocked by:** Nothing once Session Log is implemented

---

### Phase 3: Progress Tracking
Graphs and trends for E1RM progression (superseded by E1RM History Screen above).

### Phase 4: Program Templates
Pre-built training programs with RPE targets.
