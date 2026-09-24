# PrintXpress — Setup Guide

This is a complete, real Android Studio project. Unlike a web app you can
run instantly, an Android app needs two things set up first: **Android
Studio itself**, and **your own Firebase project** for it to talk to.
Neither of these could be done for you in advance — here's exactly how to
do both.

## 1. Install Android Studio

Download and install [Android Studio](https://developer.android.com/studio)
(free). First launch will prompt you to install the Android SDK — accept
the defaults.

## 2. Create your Firebase project

1. Go to the [Firebase console](https://console.firebase.google.com) and
   click **Add project**. Name it anything (e.g. "PrintXpress").
2. Once created, click the **Android icon** to add an Android app to the
   project.
3. For the **package name**, enter exactly: `com.printxpress.app`
   (this must match `applicationId` in `app/build.gradle` or the app won't
   connect).
4. Download the `google-services.json` file it offers you.
5. In this project folder, delete `app/google-services.json.EXAMPLE` and
   place your real downloaded file at `app/google-services.json` (exact
   path and filename matter).

## 3. Turn on the Firebase products the app uses

Still in the Firebase console, in the left sidebar:

- **Authentication** → Get started → enable the **Email/Password** sign-in method.
- **Firestore Database** → Create database → start in **production mode** →
  pick any region close to you.
- **Storage** → Get started → start in **production mode**.
- **Cloud Messaging** → nothing to enable here, it works automatically
  once the app is connected.

## 4. Deploy the security rules

The app's data access is protected by rules already written for you
(`firestore.rules` and `storage.rules` at the project root) — Firestore
and Storage reject everything by default until these are deployed.

Easiest way (via the console, no command line needed):
1. Firestore Database → **Rules** tab → paste in the contents of
   `firestore.rules` → **Publish**.
2. Storage → **Rules** tab → paste in the contents of `storage.rules` →
   **Publish**.

(If you're comfortable with a terminal, the
[Firebase CLI](https://firebase.google.com/docs/cli) can deploy both with
one command: `firebase deploy --only firestore:rules,storage`.)

## 5. Add some sample products

The app reads from a `products` collection but nothing writes to it from
inside the app (see `firestore.rules` — that's deliberate, matching the
brief's scope). Add a few by hand in the console:

1. Firestore Database → **Start collection** → collection ID: `products`.
2. Add a document (auto-ID is fine) with these fields:

   | Field | Type | Example |
   |---|---|---|
   | name | string | A5 Flyers (Gloss) |
   | category | string | Flyers |
   | description | string | Full-colour double-sided flyers, gloss finish. |
   | basePrice | number | 8 |
   | paperTypes | array of strings | Gloss, Matte, Recycled |
   | sizes | array of strings | A5, A4, A6 |
   | sampleImageUrl | string | (a public image URL, e.g. from Unsplash, for testing) |

3. Repeat for a few more (Business Cards, Banners, Stickers, T-Shirts,
   Mugs) so the Home screen's category grid has something to show.

## 6. Open and run the project

1. Open Android Studio → **Open** → select this project folder.
2. Let Gradle sync (the first sync downloads dependencies and can take a
   few minutes — watch the progress bar at the bottom).
3. Create a virtual device (Device Manager → Create device → pick any
   phone → pick a system image) or plug in a real Android phone with USB
   debugging enabled.
4. Press **Run** (the green triangle).

## What to expect the first time

- You'll land on Login. Tap **Sign Up** and register a normal account —
  there's no pre-seeded demo login this time, since accounts live in your
  own Firebase project, not a bundled database file.
- The category grid works immediately; tapping a category shows whatever
  products you added in Step 5.
- Placing an order writes a real document to your Firestore console — open
  the **Firestore Database** tab there to watch it appear live.
- Order status changes (Processing → Printing → Ready for pickup) are
  **not** triggerable from inside the app itself, by design (see
  `firestore.rules` — only a trusted server-side process should move an
  order forward). To test this, manually edit an order's `status` field
  directly in the Firestore console to `PRINTING` and watch **My Orders**
  update instantly on the device — this is the real-time listener from
  `OrderRepository.observeOrders()` working live.

## Known limitations (documented honestly, not hidden)

- **No Cloud Function is included.** The order-confirmation push
  notification described in Task B (Figure 4) requires a small
  server-side Cloud Function that isn't part of this Android Studio
  project — Cloud Functions are Node.js code deployed separately to
  Firebase, outside an Android app's own codebase. `PrintXpressMessagingService.kt`
  is ready to *receive* that push the moment such a function exists; until
  then, in-app notifications simply won't appear (the Notifications tab
  will just stay empty).
- **Phone number sign-in isn't implemented**, only email — see the
  comment in `AuthRepository.kt` for why (it requires Firebase's separate
  SMS-verification flow).
- **No automated tests are included** in this Android Studio project —
  see the Task E test plan document for the manual test cases this app
  was checked against instead.
- **This code has not been compiled** in the environment it was written
  in (no Android SDK is available there — see the chat for that
  explanation). It's been checked carefully for correctness, but you are
  the first person to actually build it. If Android Studio reports an
  error, most first-time errors are one of: a missing/misplaced
  `google-services.json`, a Gradle sync that hasn't finished, or an
  Android Studio version older than what `build.gradle` expects — check
  those three before anything else.
