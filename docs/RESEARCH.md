# RESEARCH — CoiffureDZ (verified 2026-10-09/10)

## Problem
Algerian barbershops run on paper notebooks or WhatsApp; no-shows and lost client history.
Need: offline-first FR/AR book, takings in DZD, chairs, no account, cheap phones (API 26+).

## Verified platform facts (docs, not fabricated)
- Android minSdk 26 covers ~95%+ of active devices per Google distribution dashboards; targetSdk 34 required by Play (Aug 2024+). Verify at: https://developer.android.com/google/play/requirements/target-sdk and https://developer.android.com/about/dashboards
- Jetpack Compose BOM + Material 3 is Google's recommended UI toolkit: https://developer.android.com/jetpack/compose
- Room is the recommended offline DB: https://developer.android.com/training/data-storage/room
- RTL: `android:supportsRtl="true"` + Compose `LocalLayoutDirection`: https://developer.android.com/guide/topics/resources/layout-direction

## Competitors (no scraping; feature-level)
Booksy, Fresha, Planity — all require accounts/internet/commissions. Gap: free, offline, FR/AR, chair-level book for 1–3 chair shops.

## SELECT decision
Native Kotlin + Compose + Room, minSdk 26, single module, MIT clean-room code. No backend, no Firebase, no ads SDK.

## Costs
Dev tools free; Play one-time $25 (optional — APK sideload is free); $0 infra (no server).

## First-10-customers plan
1. 3 pilot shops in Algiers/Oran (owner installs APK, seeds services). 2. Barbers' WhatsApp groups + TikTok demo in Darija/FR. 3. Offer free setup of service list; ask for referral to 2 shops each. 4. Iterate: SMS reminders later (still offline via device SMS).
