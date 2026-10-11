# OpenPencil pass — coiffure-dz

## Design (Shared/OpenPencil/coiffure-dz/)
- `design.html` (booking card + takings card + add action + 3-tab nav, DZ tokens) → `design.fig`
- `lint --preset accessibility`: 0 errors, 0 warnings

## Code changes
- `MainActivity.kt`: 4 `Icon(..., null)` (nav DateRange/Wallet/People + add FAB) now use the
  localized strings map (`tab_book`/`tab_cash`/`tab_clients`/`add`) as `contentDescription`.
  FR/AR + RTL, adaptive list-detail, bottom sheet untouched.

## APKs
`android-build.yml` adds `assembleRelease` (unsigned) + release artifact + versioned
`v*-b*` GitHub Release with notes + both APKs.
