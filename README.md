# Unit Converter

An offline-first Android unit converter: 14 categories, ~180 units, exact
BigDecimal arithmetic, natural-language input, a home-screen widget and a
Quick Settings tile.

## Why the numbers are trustworthy

Every conversion runs through `BigDecimal` at 50-digit precision (`core/math/MC.kt`)
and every factor is written as a **string literal**, never a `Double`. Factors are
either exact by definition (1 in = 0.0254 m) or derived from exact constants at
startup (`core/math/PhysicalConstants.kt`), so nothing is a hand-typed rounding of
a rounding.

A test suite of 132 unit tests locks this down:

| Suite | What it guards |
|---|---|
| `ConversionAccuracyTest` | ~60 reference conversions checked against NIST / BIPM / IAU values to 10 significant figures |
| `UnitRegistryIntegrityTest` | Structural invariants: unique ids, base unit is the identity, lossless round trips, no alias collisions |
| `NlpParserTest` | Natural-language parsing, plus fuzz input that must never throw |
| `ExpressionEvaluatorTest` | Arithmetic grammar, precedence, and clean rejection of garbage |
| `NumberFormatterTest` | Display formatting; no silent precision loss |
| `CookingConverterTest` | Density-based mass ↔ volume, both directions |

Run them:

```bash
./gradlew :app:testDebugUnitTest
```

## Architecture

Standard Android clean architecture — Compose UI, Hilt DI, Room persistence.

```
core/
  math/       BigDecimal engine, expression parser, formatting, constants
  registry/   UnitCategory + UnitDef tables — the single source of truth
  nlp/        Natural-language input parser
  cooking/    Density-based mass <-> volume conversion
data/         Room DAOs/entities, DataStore prefs, repository impls
domain/       Models, repository interfaces, use cases
feature/      One package per screen (Compose screen + ViewModel + UiState)
widget/       Glance home-screen widget
tile/         Quick Settings tile service
```

### Adding a unit category

Add one file under `core/registry/units/` and add its object to the `builtIn`
list in `UnitRegistry`. Nothing else changes — the engine and UI are driven off
the registry. The integrity tests will immediately fail if the new table has a
duplicate id, a bad base unit, or a colliding alias.

### Alias collisions

`NlpParser` resolves aliases first-match-wins, so an alias appearing twice in the
same category is always a bug and the test suite fails the build on it. A few
aliases legitimately collide *across* categories (`c` = Celsius and century, `min`
= minute and arcminute). Those are enumerated in the `accepted` set in
`UnitRegistryIntegrityTest` — if you add a new one, you must document it there
deliberately.

## Building

Requires JDK 21 (bundled with Android Studio) and the Android SDK.

```bash
./gradlew :app:assembleDebug      # debug APK, installs alongside release
./gradlew :app:testDebugUnitTest  # unit tests
./gradlew :app:assembleRelease    # minified + shrunk release APK
./gradlew :app:bundleRelease      # AAB for Play upload
```

The debug build uses the `.debug` application id suffix, so debug and release
can be installed on the same device at once.

## Release signing

Signing credentials are **never** committed. Create `keystore.properties` in the
project root (it is git-ignored):

```properties
storeFile=release.keystore
storePassword=<store password>
keyAlias=<alias>
keyPassword=<key password>
```

If that file is absent the release build still assembles — unsigned — so a fresh
clone or a CI job without secrets does not break.

> **Note:** `release.keystore` is the only thing that can sign updates to a
> published app. Losing it means the app can never be updated again. Keep an
> offline backup.

## Before publishing to Google Play

- [ ] **Application id.** Currently `com.example.unit_coverter`. Google Play
      **rejects** any id beginning with `com.example`. This must be changed
      before the first upload, and it can never be changed afterwards.
- [ ] Set a real `versionCode` / `versionName`.
- [ ] Install and smoke-test the *release* APK on a physical device. R8 strips
      code aggressively; `proguard-rules.pro` keeps what Room, Hilt, Glance and
      kotlinx.serialization reach reflectively, but only a real run proves it.
- [ ] Confirm the billing product ids match the Play Console configuration.
