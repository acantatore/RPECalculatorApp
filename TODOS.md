# TODOS

## Deferred Features

### Delete Session Entry
**Priority:** P3 | **Effort:** S | **Status:** Deferred — build History Screen first

**What:** Long-press on a session row in the History screen → confirm dialog → permanently deletes that entry.

**Why:** Once users can see their history, they'll notice accidental log entries (wrong weight, fat-finger reps). There's currently no way to remove them, which corrupts the E1RM trend.

**Pros:**
- Closes the "oh no I logged 200kg on bench" escape hatch
- Small: one `DELETE FROM sessions WHERE id = ?` DAO method + one Compose dialog
- Natural fit alongside the History Screen

**Cons:**
- Permanent deletion with no undo — confirmation dialog is non-negotiable
- Requires row IDs to surface from the data layer to the UI

**Context:** Surfaced during eng review 2026-03-19 while planning the E1RM History Screen. The DAO and Screen will both be in place once History ships, making this a natural follow-on.

**Implementation notes:**
- Add `SessionDao.delete(id: Long)` using `writableDatabase`
- Add `SessionRepository.deleteSession(id: Long)` wrapping on `Dispatchers.IO`
- History row: `Modifier.combinedClickable(onLongClick = { showDeleteDialog = true })`
- Use `AlertDialog` with "Delete" (destructive) and "Cancel" buttons

**Depends on:** E1RM History Screen must ship first
**Blocked by:** Nothing once History Screen is implemented

---

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


## Future Phases

### Phase 3: Progress Tracking
Graphs and trends for E1RM progression (superseded by E1RM History Screen above).

### Phase 4: Program Templates
Pre-built training programs with RPE targets.

---

## Completed

### E1RM History Screen
**Completed:** v1.2.0 (2026-03-19)
Vico line chart of E1RM over time per lift, scrollable session list, `ScrollableTabRow` with 5 lift tabs, sealed class Screen navigation, `readableDatabase` history query.

### Session Logging
**Completed:** v1.1.0 (2026-03-18)
Lift selector, Log Session button, snackbar confirmation, SQLite data layer (SessionDao/AppDatabase/SessionRepository). Weights stored in kg regardless of active unit system.

### Persist Theme Selection
**Completed:** v1.1.0 (2026-03-18)
Palette name persisted to DataStore; restored on cold start with graceful fallback to Palettes[0].
