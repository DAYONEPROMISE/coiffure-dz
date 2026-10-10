# CoiffureDZ — offline appointment book for Algerian barbershops (MIT)

100% offline Android app (Kotlin, Jetpack Compose Material 3, Room). No account, no network.
Features: appointment book (client, service, time, chair), daily takings in DZD, client history, French/Arabic with RTL.

## Build locally
1. Install Android Studio (or SDK 34 + JDK 17).
2. `cd coiffuredz && ./gradlew assembleDebug`
3. APK: `app/build/outputs/apk/debug/app-debug.apk`

## Tests
`./gradlew testDebugUnitTest` — 9 JVM unit tests on pure `ShopLogic` (takings, validation, formatting).

## Tech
- Room entities: Client, Service, Appointment (status BOOKED/DONE/CANCELLED; takings = DONE only).
- Seed services (DZD): Coupe 400, Barbe 200, Coupe+Barbe 600, Degrade 500, Enfant 300. Editable after first launch via code.
- RTL: layout direction switches on language toggle; `supportsRtl=true`.

## License
MIT — see LICENSE. Clean-room code, no copied assets.
