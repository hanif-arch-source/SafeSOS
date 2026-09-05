# SafeSOS — Android Emergency Alert App

**Kotlin + Jetpack Compose.** SafeSOS lets a signed-in user register emergency
contacts, then send a real SMS emergency alert — with their live location and
incident details — to all of them in one tap, triggered either from the app or
by shaking the phone.

Built for **IBL23305 Mobile Application Development** and covers every
required component: authentication, a related-table Room database with CRUD,
a Retrofit REST API integration, and a sensor that drives real app behaviour.

**Authors:** Hanif Ismail (SCCJ/00648/2021) · Janet Chepkurui (SCCJ/00648/2023)

---

## Features

- **Authentication** — Register / Log In / Forgot Password, backed by a local
  Room `users` table. Passwords are salted and SHA-256 hashed. The session
  persists across app restarts (via `SessionManager`) until you log out.
- **Live map on Home** — Google Maps Compose showing your current location
  (falls back to a clean placeholder if location permission is denied or no
  Maps key is configured).
- **Emergency Alert flow** — tap the SOS button (or shake the phone) →
  **Select Incident Type** (Medical, Fire, Accident, Crime/Security, Natural
  Disaster, Other) → an adaptive details form (Medical adds blood
  type/allergies fields, pre-filled from the saved medical profile; everything
  else gets a free-text description) → **Confirm & Send**, showing your real
  saved contacts.
- **Real SMS sending** — Confirm & Send dispatches an actual text message via
  `SmsManager` to every saved contact, not a simulation. Per-contact failures
  are caught individually, and the result screen reports who was and wasn't
  reached.
- **My Contacts** — full Room-backed CRUD: live list, Add, Edit, Delete, with
  the first contact you add automatically marked "Primary".
- **Incident History** — every alert that's sent is logged to a Room
  `incidents` table (foreign-keyed to the signed-in user, cascading on
  delete) and shown newest-first from Settings, with type, timestamp,
  location, details, and exactly who was and wasn't reached.
- **Profile** — view and edit your own name, email, and phone from Settings.
- **Manage Medical Info** — save a persisted blood type / condition /
  allergies profile once, so it auto-fills the Medical incident form instead
  of being retyped every time.
- **About screen** — a short in-app page describing what SafeSOS is, its key
  features, its privacy stance, and the coursework project it was built for.
- **Safety Tips** — a Retrofit-powered screen pulling a live tip from a public
  REST API, with loading, error, and retry states.
- **Shake-to-alert sensor** — the accelerometer is monitored while logged in
  and on the Home screen; a shake skips the incident picker entirely and goes
  straight to Confirm & Send as a general emergency alert, for speed.
- **Settings** — shows the real signed-in user's name and phone, with
  Incident History, Profile, Manage Medical Info, About, and a working
  Logout, all in one place.

---

## Project structure

```
app/src/main/java/com/example/safesos/
├── MainActivity.kt                    # NavHost (14 routes) + session + sensor wiring
├── data/
│   ├── local/                          # Room: User/Contact/Incident Entity+Dao, AppDatabase
│   ├── remote/                         # Retrofit: AdviceApi, RetrofitInstance, models
│   └── repository/                     # AuthRepository, ContactsRepository, IncidentRepository
├── location/
│   └── LocationHelper.kt               # Fused location provider wrapper for the Home map
├── model/
│   ├── Contact.kt                      # UI-facing contact model + sample data
│   └── IncidentType.kt                 # Medical/Fire/Accident/Crime/Disaster/Other definitions
├── sensor/
│   └── ShakeDetector.kt                # Accelerometer shake detection
├── util/
│   ├── PasswordUtil.kt                 # Salted SHA-256 hashing
│   ├── SessionManager.kt               # SharedPreferences-backed login persistence
│   └── SmsSender.kt                    # SmsManager wrapper, per-contact result reporting
└── ui/
    ├── theme/                          # Colors, typography, SafeSOSTheme
    ├── components/                     # RedTopBar, BottomNavBar, shared auth text fields
    └── screens/
        ├── LoginScreen.kt / RegisterScreen.kt / ForgotPasswordScreen.kt
        ├── HomeScreen.kt
        ├── SelectIncidentTypeScreen.kt / EmergencyDetailsScreen.kt
        ├── ConfirmSendScreen.kt / SosSentScreen.kt
        ├── ContactsScreen.kt
        ├── SettingsScreen.kt
        ├── IncidentHistoryScreen.kt / ProfileScreen.kt / MedicalInfoScreen.kt
        ├── AboutScreen.kt
        └── SafetyTipScreen.kt
```

### Database schema

Three related Room tables in `AppDatabase` (`users`, `contacts`, `incidents`):
`contacts.ownerId` and `incidents.ownerId` are both foreign keys onto
`users.userId` with `CASCADE` delete, which is what makes Contacts and
Incident History always show data scoped to the signed-in user, and cleans
that data up automatically if a user were ever deleted.

---

## How to open and run

1. Unzip the project, open Android Studio → **File > Open** → select the
   `SafeSOS` folder.
2. Let Gradle sync (pulls Room, Retrofit, Maps Compose, Play Services
   Location, and Accompanist Permissions — needs internet the first time).
3. **`local.properties` already has a Maps API key set** (`MAPS_API_KEY=...`),
   so the live map should work out of the box. If you ever need to replace it:
   get a free key from [Google Cloud Console](https://console.cloud.google.com/)
   (enable "Maps SDK for Android") and paste it in as
   `MAPS_API_KEY=your_key_here`. Leaving it blank shows a placeholder instead
   of crashing.
4. **Run on an emulator with Google Play services** (a "No Play Services" AVD
   won't render the map).
5. Grant location and SMS permissions when prompted (or later from
   Settings → Apps → SafeSOS → Permissions).

⚠️ **Do not commit `local.properties` to git or share it publicly** — it now
contains a real API key. If this project goes into a public repo, add
`local.properties` to `.gitignore` (Android Studio does this by default for
new projects) and rotate the key if it's ever been exposed.

---

## Suggested demo flow 

1. **Register** a new account → lands on Home with the live map and a
   "Hi, [name]" greeting.
2. **My Contacts** → add 1–2 real phone numbers you can check for the SMS
   arriving, edit one, delete one.
3. **Safety Tips** from Home → shows the Retrofit round-trip live; toggle
   airplane mode first to also show the no-connection/Retry state.
4. **Shake the device** (or the emulator's accelerometer panel under Extended
   Controls) → jumps into **Select Incident Type**.
5. Pick a type (e.g. **Fire**) → fill the adapted form, try leaving a required
   field blank first to show validation → **Continue**.
6. **Confirm & Send** → shows your real saved contacts → **Send** → grant the
   SMS permission if prompted → check the receiving phone for the actual text
   → **Sent** screen reports delivery per contact.
7. **Settings → Incident History** → shows the alert you just sent, logged
   with type, time, location, and per-contact delivery outcome.
8. **Settings → Profile** and **Manage Medical Info** → edit and save each,
   then send a new Medical alert to show the saved blood type/allergies
   pre-fill the form automatically.
9. **Settings → About** → shows the in-app description of the project.
10. **Settings → Logout** → back to Login, then log back in with the same
    credentials to show the account persisted in Room. Try **Forgot
    Password** once to show the demo reset-code flow.

---

## Known limitations (disclosed, not hidden)

- **Forgot Password** shows the reset code in-app rather than emailing it —
  there's no mail server in this coursework project, and the UI says so
  ("Demo mode — no email server is wired up").
- **Password hashing is salted SHA-256** — a real improvement over plaintext,
  but not production-grade (no bcrypt/Argon2, no server-side verification).
- **SMS requires a real SIM or two emulators** to demo convincingly — a
  physical device is the most reliable option; emulator-to-emulator SMS also
  works via each AVD's simulated phone number.
- This project has not been run through an actual Gradle build outside
  Android Studio — it's been checked file-by-file for matching signatures and
  consistent wiring, but a first build may still surface something small like
  a dependency version conflict.

---

## Ideas for further extension

A few genuine next steps if you want to keep building on this after the demo:

- **Real password reset via email** — wire up a mail-sending backend (e.g.
  Firebase Auth, or a small server endpoint) instead of the in-app demo code.
- **Offline queueing for SMS** — if `SmsManager` fails because the radio is
  off or there's no signal, queue the alert and retry automatically once
  connectivity returns, rather than just reporting the failure once.
- **Contact groups / priority tiers** — let some contacts be "notify
  immediately" and others "notify if unresolved after N minutes."
- **Incident history** — a simple Room table logging past alerts sent (type,
  time, contacts notified, outcome), viewable from Settings.
- **Push-based acknowledgement** — if a contact also has the app installed,
  let them tap "I'm on it" and reflect that back to the sender, instead of a
  one-way SMS.
- **Multi-language support** for the incident and details screens, useful if
  this is ever used outside an English-first user base.
- **Biometric unlock** (fingerprint/face) as a faster alternative to
  re-entering a password after the session expires.
- **Unit tests** for `AuthRepository`, `ContactsRepository`, and
  `IncidentType` logic — none exist yet, and they're the easiest, highest-value
  pieces to cover since they don't need Compose UI testing infrastructure.

---

## Project authors

- **Hanif Ismail** — SCCJ/00648/2021
- **Janet Chepkurui** — SCCJ/00648/2023
#   S a f e e S O S  
 