# NutriTrack

An Android app, written in Kotlin with Jetpack Compose, that turns a
patient's dietary-quality data (HEIFA scores) into a personal dashboard,
lets them log their eating habits, and coaches them with AI-generated tips.
A separate clinician view aggregates every patient's data and asks the model
to surface patterns across the cohort.

Built by Haruto Iriyama.

## What it does

| Screen | Purpose |
|---|---|
| **Welcome / Login / Register** | Patients are pre-loaded from a CSV; a user claims their account by matching ID + phone number, then sets a name and password. Session state is persisted so the app reopens on the home screen. |
| **Food-intake questionnaire** | Food categories, a "persona" that best describes the user's eating style, and meal / sleep / wake times. Answers are stored in Room and pre-filled on return. |
| **Home** | The user's total Food Quality Score with a quick explanation of what it means. |
| **Insights** | Per-category HEIFA score bars (vegetables, fruit, grains, dairy, sugar, sodium, …) against their maximum, plus a share button. |
| **NutriCoach** | Look up any fruit's nutrition facts via the [FruityVice](https://www.fruityvice.com/) API, and generate a motivational tip from Gemini that is tailored to the user's own scores. Tips are saved and can be reviewed later. |
| **Clinician dashboard** | Behind a clinician key: average HEIFA scores by sex and a "Find data patterns" button that sends the anonymised cohort data to Gemini for a written analysis. |
| **Settings** | Account details, logout, and the entry point to the clinician view. |

## Architecture

- **UI** — Jetpack Compose with Material 3 and a bottom navigation bar; one `@Composable` screen per feature under `ui/screens`.
- **State** — MVVM. Each screen has a `ViewModel` exposing `StateFlow`s; a single `NutriTrackRepository` is the only thing the view models talk to.
- **Persistence** — Room database with `Patient`, `FoodIntake` and `NutriCoachTip` entities and DAOs. The patient table is seeded from `assets/CustomerData.csv` on first launch.
- **Networking** — Retrofit + Gson for FruityVice; the Google Generative AI SDK (`gemini-1.5-flash`) for coaching tips and cohort analysis.
- **Auth** — `AuthManager` keeps the logged-in user in `SharedPreferences`; passwords are set at registration and checked at login.

```
app/src/main/java/com/nutritrack/
├── MainActivity.kt          NavHost and bottom bar
├── NutriTrackRepository.kt  single source of truth: Room + Retrofit + Gemini
├── AuthManager.kt           session persistence, CSV loader
├── data/                    Room database, entities, DAOs
├── network/                 FruityVice Retrofit service
├── ui/screens/              one Composable per screen
├── ui/theme/                Material 3 theme
└── viewModel/               one ViewModel per screen
```

## Running

Requires Android Studio (Ladybug or newer) and an emulator or device on
API 35.

1. Create `local.properties` in the project root (it is git-ignored) and add
   a Gemini API key:

   ```properties
   apiKey=YOUR_GEMINI_API_KEY
   ```

2. Open the project in Android Studio and run the `app` configuration.

Sample patients live in `app/src/main/assets/CustomerData.csv`; log in with
any `User_ID` / `PhoneNumber` pair from that file.
