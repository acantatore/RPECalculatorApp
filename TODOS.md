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

## Future Phases

### Phase 2: Workout Logging
Track completed workouts with E1RM calculations over time.

### Phase 3: Progress Tracking
Graphs and trends for E1RM progression.

### Phase 4: Program Templates
Pre-built training programs with RPE targets.
