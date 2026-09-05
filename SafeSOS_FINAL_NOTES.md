# SafeSOS — Final Build Notes
### Read this before your demo. Supersedes the earlier progress doc.

## What changed in this final pass

1. **Real SMS sending.** Confirm & Send now requests the `SEND_SMS` permission at
   runtime and, on Send, actually dispatches a text message (via `SmsManager`) to
   every saved emergency contact — not a simulation. Long messages are split
   automatically. Per-contact failures (bad number, no SIM, radio off) are caught
   individually so one bad number doesn't block the rest, and the result screen
   reports exactly who was and wasn't reached.
2. **Generic "Emergency Alert" system.** The old Medical-only flow is now a
   picker (`Select Incident Type`) → **Medical, Fire, Accident, Crime/Security,
   Natural Disaster, Other** → a details form that adapts itself (blood
   type/allergies only appear for Medical; everything else gets a free-text
   "what's happening" field) → Confirm & Send → Sent. The SOS button, bottom nav,
   and shake gesture all lead into this same picker now instead of jumping
   straight to a medical form.
3. **Everything from the previous message is now wired in**, not just built:
   real Login/Register/Forgot Password flow, Room-backed Contacts (live list +
   Add/Edit/Delete), Settings showing the real signed-in user with a working
   Logout, a live Google Map on Home (falls back to a clean placeholder if no
   Maps key is set), and the shake sensor jumping into the alert picker.
4. **Session persistence** was added (not in the original build log): closing
   and reopening the app keeps you logged in, via a small SharedPreferences
   `SessionManager`. Logout clears it.

## Files touched in this pass

New: `model/IncidentType.kt`, `util/SmsSender.kt`, `util/SessionManager.kt`,
`ui/screens/SelectIncidentTypeScreen.kt`, `ui/screens/EmergencyDetailsScreen.kt`
(replaces `MedicalSosScreen.kt`).

Rewritten to use real data instead of placeholders: `MainActivity.kt`,
`HomeScreen.kt`, `ConfirmSendScreen.kt`, `SosSentScreen.kt`, `ContactsScreen.kt`,
`SettingsScreen.kt`, and `ui/components/BottomNavBar.kt` (the "Medical" tab is
now labelled "Alert" with a warning icon, to match the generic system).

`AndroidManifest.xml` gained the `SEND_SMS` permission (in addition to the
internet/location ones from the last pass).

## How to run it

1. Open the project in Android Studio, let Gradle sync (it will pull Room,
   Retrofit, Maps Compose, Play Services Location, and Accompanist Permissions).
2. **Google Maps key** (optional but recommended): get a free key from
   [Google Cloud Console](https://console.cloud.google.com/) → enable "Maps SDK
   for Android" → paste it into `local.properties` as `MAPS_API_KEY=...`.
   Without it, the Home screen shows a clean placeholder instead of crashing.
3. Run on an **emulator with Google Play services** (for the map) — a plain
   "No Play Services" AVD won't render Maps.
4. **Testing real SMS**: a physical device with a SIM is the most convincing
   demo. On two emulators, you can also send between them using each AVD's
   phone number (shown in the emulator's extended controls) — the emulator's
   modem is simulated but genuinely receives the SMS Intent.
5. Grant location + SMS permissions when prompted (or from Settings → Apps →
   SafeSOS → Permissions if you dismissed them).

## Suggested demo flow (maps to IBL23305's guide)

1. **Register** a new account → lands on Home. Show the live map (or the
   labelled placeholder if no key is set) and the "Hi, [name]" greeting.
2. **My Contacts** → add 1–2 real phone numbers you can check for the SMS
   arriving, edit one, delete one.
3. **Safety Tips** from Home → shows the Retrofit round-trip; toggle airplane
   mode first to also show the no-connection/Retry state.
4. **Shake the device** (or use the emulator's accelerometer panel under
   Extended Controls) → jumps into **Select Incident Type**.
5. Pick a type (e.g. **Fire**) → fill the adapted form, try leaving a required
   field blank first to show validation → **Continue**.
6. **Confirm & Send** → shows your real saved contacts → **Send** → grant the
   SMS permission when prompted → check the receiving phone/emulator for the
   actual text message → **Sent** screen reports delivery per contact.
7. **Settings** → shows your real name/phone → **Logout** → back to Login.
8. Log back in with the same credentials to show the account persisted in Room.
   Try **Forgot Password** once from the Login screen to show the demo
   reset-code flow.

## Still worth knowing about

- The demo "forgot password" reset code is generated and shown in-app rather
  than emailed, since there's no mail server in this coursework project — this
  is disclosed in the UI itself ("Demo mode — no email server is wired up").
- Password hashing is salted SHA-256: a real improvement over plaintext, but
  not production-grade (no bcrypt/Argon2, no server-side check).
- This has not been run through an actual Gradle build in this environment (no
  Android SDK here) — it's been checked file-by-file for matching signatures,
  consistent imports, and correct wiring, but a first real build may still
  surface something small like a dependency version mismatch.
