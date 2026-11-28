# SeoRoute Android

SeoRoute is an Android app for discovering events in Seoul, reserving tickets, and managing events and venue check-in. This repository contains the Android frontend. The SeoRoute backend maintains the API contract according to the documentation in the backend repository.

## Current progress

| Role | Implemented in the frontend | In progress |
| --- | --- | --- |
| **Attendee** | Event discovery and details, saved interests, free reservations, simulated paid bookings, My Tickets, and an in-app notification inbox. Booking confirmation shows success after the backend responds. The digital ticket screen renders a rotating QR from backend ticket data. | End-to-end QR check-in needs device verification. Event maps are illustrative, and some screens retain sample data when backend data is unavailable. |
| **Organizer** | Event listing, creation and editing, native date and time pickers, and an API-backed cancellation action. The People screen requests event attendees and check-in stats. The Profile screen loads gate staff, grants ticketing access to an existing account by email, and removes access after backend confirmation. | The Overview reservation total is hardcoded. Some event and attendee views still have demo fallbacks or limited pagination. Full organizer profile editing is unfinished. Gate staff access is removed manually. |
| **Ticketing** | Event selection and check-in screens, with API integration for ticket manifests, stats, and online check-in. | The scanner currently simulates a scan. Camera QR decoding and offline scan sync are pending. |
| **Admin** | Overview requests platform stats. The Console loads events across organizers and opens the selected event for editing. The Agencies screen can create an agency through the API. | Verify the stats field mapping against the deployed response. The Agencies list still mixes seeded and API data, and edit/remove are session-local. Admin event creation and persistent agency edits/removals require backend support. |

## To-dos
- [ ] Implement Firebase Authentication. Connect email/password sign-up and sign-in, plus Google sign-in. Exchange the Firebase ID token through /auth/sync, then route using the role returned by the backend. Keep dev/demo login separate from the normal sign-in flow.
- [ ] Implement real camera-based check-in. Replace the simulated scanner with camera QR decoding. Submit the scanned token to the existing check-in endpoint and show success only when the backend accepts it. The attendee-side rotating QR is implemented in code and still needs device verification.
- [ ] Test the full flow on devices. Sign in → book a free or simulated paid ticket → open My Tickets → scan the QR → verify check-in. Also test expired and already-used tickets, failed bookings, and a staff account after access is granted or removed.
- [ ] Organizer Overview: Replace the hardcoded 142 with an API-backed reservation total. Decide whether the metric counts current bookings, checked-in attendees, or all-time reservations.
- [ ] Organizer events and People: Handle pagination and clear stale data when the API returns an empty list. Remove fake check-in totals. Make event cancellation reflect the backend response before updating the list.
- [ ] Organizer Profile: Replace the placeholder edit action and hardcoded phone toggle with an actual profile form.
- [ ] Admin Agencies and Overview: Show only backend agencies, remove fabricated active-event counts, and verify the stats fields against a real API response. Hide unsupported admin actions until the necessary routes are available.
- [ ] Dev login: Confirm whether /auth/dev rewrites existing roles. If the team keeps dev login for QA, have it preserve an existing user’s stored role and organizer affiliation.
- [ ] Admin operations: Agree on endpoints for creating events on behalf of an agency and editing or removing agencies.
- [ ] Gate staff rules: Confirm whether manual promotion and removal are sufficient or whether access should be event-specific and expire automatically.

## Tech stack

- **Android UI** — Kotlin, Jetpack Compose, and Material 3. Minimum SDK 34, compile and target SDK 37.
- **Networking and sessions** — Retrofit, OkHttp, Kotlin coroutines, JSON mapping, and AndroidX DataStore for the backend session.
- **Ticket QR** — ZXing Core for encoding and HMAC-SHA256 for the rotating ticket token.
- **Firebase** — Authentication and Cloud Messaging dependencies are configured. Interactive login and sign-up still use the dev/demo flow, and push notifications are pending.
- **Build** — Gradle Kotlin DSL, the Gradle wrapper, and a version catalog.

## Project structure

```text
SeoRoute/
├── app/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/example/seoroute/
│       │   │   ├── MainActivity.kt         # App entry point and role-based navigation
│       │   │   ├── model/                 # Screen models, repositories, and demo data
│       │   │   ├── network/               # Retrofit service, API client, and token storage
│       │   │   └── ui/
│       │   │       ├── components/        # Shared controls and navigation bars
│       │   │       ├── screens/           # Attendee, organizer, ticketing, and admin screens
│       │   │       └── theme/             # Colors, typography, and theme
│       │   └── res/                       # App resources and artwork
│       ├── test/                          # Local JVM tests
│       └── androidTest/                   # Device and emulator tests
├── gradle/                               # Version catalog, daemon settings, and wrapper config
├── gradlew / gradlew.bat
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## API integration

The configured API base URL is [`https://api.smbgarasibmw.my.id/api/v1/`](https://api.smbgarasibmw.my.id/api/v1/), set in [`app/build.gradle.kts`](app/build.gradle.kts). [`SeoRouteRetrofitService.kt`](app/src/main/java/com/example/seoroute/network/SeoRouteRetrofitService.kt) declares the routes, and [`SeoRouteApiClient.kt`](app/src/main/java/com/example/seoroute/network/SeoRouteApiClient.kt) attaches the backend bearer token and maps responses to app models.

The active sign-in flow uses `/auth/dev` and can fall back to local demo accounts. Android sends a demo role hint, and the dev endpoint can return an attendee session when the role is omitted. The Firebase ID-token exchange via `/auth/sync` has a client method and still needs wiring to the login UI. Endpoint payloads and server behavior are documented in the backend repository.

## Run locally

1. Open the repository in Android Studio and install Android SDK 37. The Gradle daemon configuration targets JDK 25.
2. Obtain the team's Firebase Android configuration for `com.example.seoroute` and place it at `app/google-services.json`. Git ignores this file.
3. Sync Gradle and run the `app` configuration on an emulator or device running API 34 or later. Backend-dependent flows require network access and an account supported by the deployed environment.

To build a debug APK from the repository root, run `./gradlew :app:assembleDebug` on macOS/Linux or `.\gradlew.bat :app:assembleDebug` in Windows PowerShell.
