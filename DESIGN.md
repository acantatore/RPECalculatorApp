# Design System — RPE Calculator

## Product Context

**What this is:** A focused powerlifting/strength training tool for calculating E1RM, target weights, and warmup progressions using the RPE (Rate of Perceived Exertion) scale.

**Target users:** Intermediate-to-advanced strength athletes who understand RPE. Not a beginner app — users arrive with domain knowledge.

**Project type:** Android mobile app (Jetpack Compose, single-screen)

**Design philosophy:** Utility-first. Every pixel earns its place by helping the lifter calculate faster. No decorative chrome. Results should feel authoritative, not soft.

---

## Typography

**Font family:** System default (Roboto on Android)

| Role | Size | Weight | Usage |
|------|------|--------|-------|
| App title | 22sp (`titleLarge`) | Bold | AppBar heading |
| Card header | 18sp | Bold | "Have", "Want", "Warmup" section titles |
| Input label | 18sp | Regular | "Weight", "Reps", "RPE" |
| Input text | 16sp | Regular | User-typed values *(currently 14sp — known issue)* |
| Result value | 22sp | Bold | E1RM, target Weight outputs |
| Body / settings | 14sp | Regular | Settings labels, protocol descriptions |
| Caption | 12sp | Regular | Status bar, hints, "each side", "rounded" |
| Micro | 11sp / 9sp | Medium / Regular | Plate chip weight / count |

**Rules:**
- No font changes — Roboto only
- Input text must match or exceed 16sp so typed values don't look subordinate to their labels
- Result values must be visually larger than input values to signal output vs. input

---

## Color

### Static tokens (light mode only — no dark mode implemented)

| Token | Hex | Usage |
|-------|-----|-------|
| `CardBackground` | `#FFFFFF` | Card surfaces, AppBar, dropdown backgrounds |
| `BackgroundColor` | `#F6F6F6` | Screen background |
| `TextPrimary` | `#313131` | Primary labels, values, body text |
| `TextSecondary` | `#757575` | Secondary labels, hints, captions |
| `BorderColor` | `#CDCDCD` | Input outlines, dividers |
| `CardShadow` | `#A3A3A3` @ 45% | Card elevation shadow |

### Palette system (user-selectable, 5 built-in)

Each palette provides three tokens: `gradientStart`, `gradientEnd`, `accent`.

| Palette | gradientStart | gradientEnd | accent | Use |
|---------|--------------|-------------|--------|-----|
| Original Purple *(default)* | `#794A8F` | `#9A74AC` | `#5C2A73` | — |
| Ocean Blue | `#1E88E5` | `#64B5F6` | `#1565C0` | — |
| Forest Green | `#2E7D32` | `#81C784` | `#1B5E20` | — |
| Sunset Orange | `#F4511E` | `#FF8A65` | `#D84315` | — |
| Midnight Slate | `#37474F` | `#78909C` | `#263238` | — |

**Token roles:**
- `gradientStart` / `gradientEnd`: card header background (vertical gradient, end→start direction)
- `accent`: focused input borders, icon tints, result highlights, selected state color

**Palette rules:**
- Palette is in-memory only — not persisted across app restarts
- Static tokens (`CardBackground`, `TextPrimary`, etc.) never change with palette
- New palettes must maintain WCAG AA contrast for white text on `gradientStart`

### IWF/IPF Plate Colors (immutable)

| Weight (kg) | Color name | Hex |
|-------------|------------|-----|
| 25 kg | Red | `#E53935` |
| 20 kg | Blue | `#1E88E5` |
| 15 kg | Yellow | `#FDD835` |
| 10 kg | Green | `#43A047` |
| 5 kg | Black | `#212121` |
| 2.5 kg | Red (lighter) | `#EF9A9A` |
| 2 kg | Blue (lighter) | `#90CAF9` |
| 1.5 kg | Yellow (lighter) | `#FFF176` |
| 1 kg | Green (lighter) | `#A5D6A7` |
| 0.5 kg | White | `#FAFAFA` (with border) |

These are standardized competition colors and must not change.

---

## Spacing

**Base unit:** 4dp. All spacing values must be multiples of 4dp.

| Token | Value | Usage |
|-------|-------|-------|
| xs | 4dp | Inline gaps (plate chips, badge-to-text) |
| sm | 8dp | Between related elements |
| md | 12dp | Card header padding, warmup row gaps |
| lg | 16dp | Screen horizontal margins, AppBar padding |
| xl | 20dp | Card content vertical padding |
| 2xl | 24dp | Settings section gaps |
| 3xl | 26dp | Between cards, card area vertical padding |
| 4xl | 32dp | Bottom spacer |

**Card inner content:** `horizontal = 40dp, vertical = 20dp`
*(Note: 40dp horizontal is intentional to create visual inset — do not reduce below 32dp)*

**Between input fields:** 10dp *(exception to the 4dp scale — acceptable)*

---

## Border Radius

**Target system (two radii only):**

| Token | Value | Usage |
|-------|-------|-------|
| `radiusSmall` | 4dp | Plate chips, set number badges (or CircleShape for badges) |
| `radiusMedium` | 8dp | Cards, input fields, settings protocol cards, buttons |

**Known inconsistency:** Cards and inputs currently use 5dp; settings cards use 8dp. Normalize to 8dp as the standard card/input radius.

---

## Input Field Contract

All input fields (`RpeInputField`) must follow this contract:

| Property | Value |
|----------|-------|
| Width | 110dp fixed |
| Shape | `radiusMedium` (8dp) |
| Keyboard type | `KeyboardType.Number` |
| IME action | `Next` (all except last field), `Done` (last field) |
| Accepted characters | `^\d*\.?\d*$` (digits + optional decimal) |
| Focused border | `accent` color |
| Unfocused border | `BorderColor` (#CDCDCD) |
| Cursor | `accent` color |
| Text size | 16sp (not 14sp) |

**Placeholder hints (required):**

| Field | Placeholder |
|-------|-------------|
| Have → Weight | e.g. 100 |
| Have → Reps | 1 – 15 |
| Have → RPE | 4.0 – 10.0 |
| Want → Reps | 1 – 15 |
| Want → RPE | 4.0 – 10.0 |

---

## Input Validation & Valid Ranges

These are the Calculator's hard limits. The UI **must** surface them — silent failure is not acceptable.

| Field | Valid range | Notes |
|-------|-------------|-------|
| Weight | > 0 | No practical upper bound |
| Reps (any card) | 1 – 15 (conservative safe limit) | Exact failure threshold: `(10 - RPE) + (Reps - 1) ≥ 16` |
| RPE | 4.0 – 10.0 | Values > 10 silently clamped; values < 4 return 0 |

**States the result row must distinguish:**

| State | Display |
|-------|---------|
| Inputs not yet filled | `—` (em dash, TextSecondary color) |
| Inputs out of valid range | Inline hint, e.g. `"RPE must be 4–10"` (amber/red, small) |
| Valid result | Bold value + unit suffix at 22sp, TextPrimary or accent |

---

## Component Inventory

### AppBar
- Background: `CardBackground`
- Title: `titleLarge` Bold, `accent` color
- Right actions: Settings icon, Theme picker, About icon (all `accent` tint)
- Below AppBar: unit/bar status strip — `12sp`, `TextSecondary`, right-aligned
- **Rule:** Bar weight in status strip must display in the user's current unit system

### InputCard
- Shape: `radiusMedium`
- Shadow: elevation 20dp, `CardShadow`
- Header: vertical gradient (`gradientEnd` → `gradientStart`), 12dp padding, 18sp Bold White title
- Content: `horizontal = 40dp, vertical = 20dp` padding

### RpeInputField
- See Input Field Contract above

### ResultRow
- Label: 18sp Regular, `TextPrimary`
- Value: 22sp Bold, `TextPrimary` (or `accent` for emphasis)
- Empty state: `—` in `TextSecondary`
- Width: 110dp (matches input field width for column alignment)

### WarmupCard
- Identical card chrome to InputCard
- Set number badge: 24dp circle, `accent` fill, 12sp Bold White
- Weight display: 16sp Bold
- Reps: 14sp `TextSecondary`, right-aligned
- Rounded indicator: 10sp `TextSecondary`, `"(rounded)"`
- Plate chips: `radiusSmall`, IWF colors, 11sp weight text, 9sp count text
- "each side" label: 10sp `TextSecondary`

### SettingsScreen
- Section label: 14sp Medium, `accent` color, 8dp bottom padding
- Protocol cards: `radiusMedium`, tapped = `accent` @ 20% alpha background
- Selected badge: 12sp `accent`, text `"Selected"`, right-aligned

---

## Motion

| Interaction | Animation |
|-------------|-----------|
| WarmupCard appear/disappear | `AnimatedVisibility` (default fade+expand) |
| Dropdown menus | Material3 default |
| All others | Instant (no animation) |

**Rule:** Only `AnimatedVisibility` for the warmup card. Do not add transitions to card content, input focus states, or result updates — snappiness is a feature for a calculator tool.

---

## Decisions Log

| Date | Decision | Rationale |
|------|----------|-----------|
| 2026-03-18 | Baseline captured from live app + source | Inferred by `/plan-design-review` |
| 2026-03-18 | Target radius system: 4dp small / 8dp medium | Normalize current inconsistency (5dp cards, 8dp settings cards, 4dp chips) |
| 2026-03-18 | Input text size: 16sp (not 14sp) | Typed values should not appear subordinate to their 18sp labels |
| 2026-03-18 | Result rows must have 3 distinct states | Silent blank on invalid input is a trust-eroding UX failure |
| 2026-03-18 | Reps field max hint: 1–15 | Conservative safe ceiling; actual formula limit is RPE-dependent (fails at x≥16) |
| 2026-03-18 | Bar weight in status strip: dynamic unit conversion | Hardcoded "kg" is incorrect when user is in LBS mode |
