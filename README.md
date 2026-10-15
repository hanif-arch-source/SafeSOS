# 📱 SafeSOS — Android Emergency Alert App

**Kotlin + Jetpack Compose**  
SafeSOS allows a signed-in user to register emergency contacts and dispatch an instantaneous, real SMS emergency alert—complete with live coordinates and incident profiling details—in one tap or by shaking the device. 

Built for the **IBL23305 Mobile Application Development** module, this application implements strict production-grade components including secure session persistence, a relational Room database with CRUD operations, advanced sensory input pipelines, and robust Retrofit REST API integration.

### 👥 Authors
* **Hanif Ismail** (SCCJ/00648/2021)
* **Janet Chepkurui** (SCCJ/00648/2023)

---

## ✨ Features

* **🛡️ Secure Authentication** — Custom Register, Log In, and Password Reset screens backed by a local Room `users` database table. Passwords are fully salted and securely processed via SHA-256 cryptographic hashing. Active user states are persisted seamlessly across device restarts via `SessionManager`.
* **🗺️ Real-Time Map Interface** — Clean integration of Google Maps Compose directly onto the main workspace dashboard, providing explicit live user tracking coordinates. Safely falls back to static vector states if hardware geolocation access is restricted.
* **🚨 Sensor-Driven Emergency Alert Flow** — Triggers dynamically via an intuitive on-screen action button or a direct physical device shake. Broadcasters select from multiple granular Incident Vectors (*Medical, Fire, Accident, Crime/Security, Natural Disaster, Other*). Forms change dynamically based on the alert type; choosing a *Medical* alert auto-fills critical details from the user's stored medical profile.
* **💬 Native SMS Dispatch Engine** — Employs native Android `SmsManager` services to simultaneously route urgent alerts out to every assigned responder profile sequentially. Includes custom failure isolation logic to track and report delivery confirmations individually per contact on the tracking summary screen.
* **🗂️ Live Contact Management (CRUD)** — Seamless local relational contact storage allowing rapid additions, standard modifications, and record removals. The initial entry added defaults automatically to your "Primary Contact" profile status.
* **📜 Cascading Incident Logger** — Every active distress signal logs chronological meta-data payloads down to a dedicated Room `incidents` schema table. Entries link directly back to the active `ownerId` using explicit foreign key cascading rules. Logs viewable directly inside the settings hub showcase chronological error diagnostics, time-stamping metrics, and transmission status histories.
* **🩹 Persistent Medical Ledger** — A dedicated local configuration panel enabling users to preset critical wellness factors (Blood type, primary allergens, clinical conditions) to bypass typing forms during intense crises.
* **📡 Retrofit Security Infrastructure** — Asynchronous network communication wrapper pulling safety tips dynamically from a public REST API architecture. Features elegant layout provisions for buffering loading, structural exception errors, and manual refresh requests.
* **📳 Shake-to-Alert Sensor Matrix** — Monitors active device accelerometer configurations while operating on the homepage environment. Instantly bypasses the selection screens during sudden, heavy motion vectors to queue up absolute alert confirmation configurations within fractions of a second.

---

## 🏗️ Project Architecture

```text
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

### 🗄️ Relational Database Schema
The database engine runs three strictly synchronized local relational tables mapped via a single core `AppDatabase` controller configuration:
1. **`users`**: Manages primary account credentials, hashes, and persistent profile state IDs.
2. **`contacts`**: Keyed directly back to the source account identity using an explicit `ownerId` constraint.
3. **`incidents`**: Tracks historical alert history objects linked back to individual profiles.

> ℹ️ *Both child configurations (`contacts` and `incidents`) map explicitly to target user IDs using `CASCADE` deletion parameters. This prevents stale unlinked records and clears data profiles cleanly if an upstream account is deleted.*

---

## 🚀 How to Setup and Run

1. Extract the project archives locally and open Android Studio.
2. Navigate directly to **File > Open** and select the parent `SafeSOS` folder workspace root.
3. Allow Gradle to synchronize dependencies automatically (*Requires an active internet connection to cache Room libraries, Retrofit engines, Maps Compose environments, and Play Services dependencies initially*).
4. **API Initialization**: The repository includes preset development keys configured right inside the `local.properties` framework file (`MAPS_API_KEY=...`). To supply your custom keys, visit the [Google Cloud Console](https://google.com), activate the **"Maps SDK for Android"** API engine on your developer container profile, and place the key directly in your local environment workspace:
   ```properties
   MAPS_API_KEY=your_key_here
   ```
5. Deploy onto a virtual emulator targeted with native **Google Play Services** packages (*Standard basic images omitting active Play library binaries cannot draw vector Map instances*).
6. Provide system access authorization alerts for active **Device Location** and **SMS Services** when requested at initialization.

⚠️ **Important:** Never tracking-commit your modified `local.properties` definitions out into open Git structures. Keep this configuration nested within your target `.gitignore` setup patterns to safeguard active private integration credentials.

---

## 🕹️ Recommended Demonstration Flow

1. **Authentication** — Complete a new profile registration cycle to initialize an active session and land on the clean Maps Home hub dashboard.
2. **Contact Provisioning** — Access the **My Contacts** portal to append active phone lines to your responder matrix. Test row modification routines and remove dummy records cleanly.
3. **API Integrity Verification** — Fire up the **Safety Tips** layout component to view automated data fetching in real time. Switch into Airplane Mode temporarily to verify error catch layouts and offline retry components.
4. **Sensory Feedback Loop** — Activate your emulator's accelerometer simulator controls and cycle a sharp shake movement pattern to trigger the rapid incident selector switch instantly.
5. **Form Adaptation Verification** — Select a **Fire** vector pattern, leave mandatory form elements blank to show active input validation safeguards, and then advance.
6. **Live Alert Dispatches** — Advance past the confirmation panel layout to trigger live cellular messaging tasks out to targeted responder devices. Check receipt states using connected virtual targets.
7. **Audit Record Analysis** — Open **Settings > Incident History** to verify that logging instances track accurate timelines, structural alert classifications, and individual recipient status codes correctly.
8. **Automation Profiling** — Configure permanent personal tracking parameters via **Manage Medical Info** and verify that specialized forms fill out automatically during subsequent test cycles.
9. **Persistence Check** — Run a session **Logout** sequence, return to the workspace door, and log back in to show that local user credentials remain intact inside the Room engine database.

---

## 🛡️ Disclosed Technical Constraints

* **Simulated Password Resets** — Because a standalone university coursework project lacks a dedicated external mail-relay cluster infrastructure, password recovery codes display locally directly within the active UI window environment ("Demo mode — no email server is wired up").
* **Simplified Encryption Routines** — Account keys rely on localized salted SHA-256 algorithms. While a substantial upgrade over unencrypted cleartext structures, this should be swapped for high-iteration server-backed bcrypt or Argon2 configurations for larger production-scale platforms.
* **SMS Verification Hardware Requirements** — Real physical target environments or multiple interconnected target developer emulators are required to review physical data transmission pipelines effectively.
