# 99 Names of Allah

A free, open-source, native Android app for reading and memorizing Al-Asma ul-Husna — the 99 Names of Allah — with Arabic, transliteration, and meanings.

Based on the lecture of Sheikh Ibn Uthaymeen (Rahimahullah), as presented in *"The Ninety Nine Names of Allah: A Memorisation Tool with Transliteration and Meanings"*. Content curated at [muntasimulhaque.bearblog.dev/99-names](https://muntasimulhaque.bearblog.dev/99-names/).

## Features

- **Read** — all 99 names with Arabic script set in the Amiri typeface, transliteration, and full meanings, plus scholarly notes (e.g. the distinction between Ar-Rahmaan and Ar-Raheem). Browse a classic list or an Arabic grid, swipe between names, and search by name, meaning, or number.
- **Share** — turn any name into a beautifully rendered card (Arabic, transliteration, meaning) and share it as an image.
- **Memorize** — flashcards with a flip animation and an "I know it / Still learning" loop, a ten-question quiz with a remembered best score, and a quiet progress count (no streaks, no gamification).
- **Daily** — a "Name of the Day" that rotates deterministically through all 99, shown on the home screen, as an optional notification at a time you choose, and as a day/night-aware home-screen widget.
- **Considered** — warm paper light theme, dark, and true-black AMOLED; adjustable text size; bundled Amiri (Arabic) and Spectral (Latin) typefaces (SIL Open Font License); predictive back; edge-to-edge.
- **Pure** — 100% offline. No ads, no analytics, no tracking, no network permission. The only permission is notifications, and only if you turn the daily name on.

## Building

1. Open the project in Android Studio (Ladybug or newer). It will download Gradle 8.7 and all dependencies on first sync — accept any suggested Gradle/AGP updates if prompted.
2. Run on a device or emulator (minimum Android 7.0, API 24).
3. For a release build: **Build → Generate Signed App Bundle** (see `PUBLISHING.md`).

The Gradle wrapper JAR is intentionally not committed; Android Studio regenerates it. From the command line, run `gradle wrapper` once (with any local Gradle ≥ 8.7) to restore `gradlew`.

Unit tests for the core logic (daily-name rotation, quiz generation, search) run with `gradle :app:testDebugUnitTest`.

## Architecture

Single-module Kotlin app. Jetpack Compose + Material 3 with a small design system (theme, type scale, shared components), Navigation Compose with activity- and screen-scoped ViewModels, DataStore for progress and settings, WorkManager for the daily schedule, Glance for the widget, kotlinx.serialization for the bundled `assets/names.json`. No DI framework, no database — the content is a static JSON asset, which also makes translations straightforward (swap the asset per locale).

## License

MIT — see [LICENSE](LICENSE). Bundled fonts (Amiri, Spectral) are under the SIL Open Font License (`app/src/main/assets/fonts/`). The content is presented for the benefit of anyone seeking to learn the names; please keep the attribution to the source lecture intact.
