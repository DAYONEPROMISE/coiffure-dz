# DESIGN — CoiffureDZ 2026 UI direction (Penpot-mappable)

No shared Design-Lead system was published at build time, so this file is the
single source of truth for CoiffureDZ tokens. It maps 1:1 to a Penpot M3 library
(screens: Book / Cash / Clients / Add-sheet; components below).

## Tokens (M3 baseline, dynamic color on Android 12+)
- Color: `dynamicDarkColorScheme` / `dynamicLightColorScheme` (API 31+),
  fallback `darkColorScheme()` / `lightColorScheme()`. Primary used only for
  the takings figure; error color for form validation.
- Type: M3 type scale only — displaySmall (takings), titleLarge (sheet/app bar),
  titleMedium (section headings), bodyLarge, labelLarge.
- Shape: M3 default shapes (medium cards/sheets).
- RTL: whole-tree `LocalLayoutDirection` (Rtl in AR, Ltr in FR);
  `android:supportsRtl="true"`. No mirrored custom drawables.

## Components
- Top: `CenterAlignedTopAppBar` + language `IconButton` (contentDescription = lang name).
- Nav: M3 `NavigationBar` (Book / Cash / Clients) with icon+label (a11y labels free).
- Book: day pager (< date >), `ElevatedCard` + `ListItem` per appointment,
  DONE / CANCELLED / delete actions right-aligned, 88dp FAB clearance.
- Add: `ModalBottomSheet` (skipPartiallyExpanded), `FilterChip` service picker
  with price, inline validation error in error color, full-width save.
- Cash: `ElevatedCard` hero with displaySmall takings + done count.
- Clients: adaptive list-detail — side-by-side ≥600dp (280dp list pane),
  stacked with back button on phones; selectable `Surface` rows (tonal 4dp).
- Edge-to-edge: `enableEdgeToEdge()` + `Scaffold` window insets.
- Dark mode: system-following, verified via dynamic/fallback schemes.

## Accessibility checklist
- Every screen title marked `heading()`; nav items have labels; FAB/chips are
  48dp+ targets; no color-only status (status shown as text label).
