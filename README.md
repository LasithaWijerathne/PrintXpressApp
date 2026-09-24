# PrintXpress

A native Android app (Kotlin) for a digital printing service — browse print
products, customise an order with uploaded artwork, track order status in
real time, and manage delivery addresses and saved designs. Built as a full
Firebase-backed mobile application for CSE5011 Mobile Application Development.

<!-- Add 2-3 screenshots here once you're on GitHub, e.g.: -->
<!-- ![Home screen](docs/screenshots/home.png) ![My Orders](docs/screenshots/orders.png) -->

## Features

- **Authentication & profiles** — register/login with email, manage saved
  designs, delivery addresses and order history from a single Profile screen.
- **Product browsing & ordering** — browse by category, customise paper type,
  size and quantity, upload artwork or type instructions, live price preview.
- **Real-time order tracking** — order status updates on screen instantly via
  a Firestore snapshot listener, with no manual refresh.
- **Order management** — reschedule or cancel an order before printing
  begins, enforced both client-side and by Firestore Security Rules.
- **Design support** — print file guidelines (format, resolution, colour
  mode, bleed) and an expandable FAQ.

## Tech stack

- **Language:** Kotlin
- **Min SDK:** 24 (Android 7.0) · **Target SDK:** 34
- **Backend:** Firebase — Authentication, Firestore, Storage, Cloud Messaging
- **Architecture:** MVVM (ViewModel + LiveData), Repository pattern over Firebase
- **UI:** XML layouts + View Binding, Material Components
- **Async:** Kotlin Coroutines + Flow (`callbackFlow` wraps Firestore's
  `addSnapshotListener` for live data)
- **Image loading:** Glide, with a locally bundled vector-icon fallback per
  product so the UI never shows a broken image if a remote photo fails to load

## Architecture

Every screen follows the same three-layer shape:

| Layer | Responsibility | Example |
|---|---|---|
| UI (Activity/Fragment) | Draws the screen, forwards user actions | `CheckoutActivity`, `HomeFragment` |
| ViewModel | Holds UI state as LiveData; all validation logic lives here | `AuthViewModel`, `CheckoutViewModel` |
| Repository | The only layer that talks to Firebase; wraps every call in a `Result<T>` | `AuthRepository`, `OrderRepository` |
| Model | Plain Kotlin data classes mirroring Firestore documents | `Order`, `OrderItem`, `Product` |

Full system design — UML use case/class/activity diagrams, the Firestore
data model, and an equivalent 3NF relational schema with a step-by-step
normalization walkthrough — is written up separately (see the project's
Task B report).

## Project structure

```
app/src/main/java/com/printxpress/app/
├── PrintXpressApp.kt        Application class - Firebase init
├── data/
│   ├── model/                8 Kotlin data classes (Firestore documents)
│   └── repository/           6 repositories - all Firebase I/O lives here
├── ui/
│   ├── splash/  auth/  main/
│   ├── home/  product/  cart/  checkout/
│   ├── orders/  notifications/  profile/  support/
├── service/                  FCM push notification handling
└── util/                     Result wrapper, shared constants

firestore.rules   storage.rules   <- server-side security enforcement
```

## Running it yourself

1. Install [Android Studio](https://developer.android.com/studio) (free);
   first launch installs the Android SDK.
2. Create a Firebase project at [console.firebase.google.com](https://console.firebase.google.com),
   then add an Android app to it with package name exactly `com.printxpress.app`.
3. Download the generated `google-services.json` and place it at
   `app/google-services.json` (replacing `.EXAMPLE`).
4. In the Firebase console, enable **Authentication** (Email/Password),
   create a **Firestore** database (production mode), and enable **Storage**
   (production mode).
5. Publish `firestore.rules` and `storage.rules` (in this repo's root) from
   each product's **Rules** tab — both deny all access until this is done.
6. Add a few sample documents to the `products` collection so the Home
   screen has something to display.
7. Open the project in Android Studio, let Gradle sync, then **Run**.

Order status (Processing → Printing → Ready for Pickup) is intentionally not
changeable from inside the app — only a trusted server-side process should
move an order forward (enforced by `firestore.rules`). To see this working,
change an order's `status` field directly in the Firestore console and watch
**My Orders** update live on the device — that's `OrderRepository.observeOrders()`'s
real-time listener at work.

## Known limitations

- **No Cloud Function is included.** The order-status push notification
  needs a small piece of server-side code (Cloud Functions, Node.js) that
  sits outside this Android Studio project's scope. `PrintXpressMessagingService.kt`
  is ready to receive that push the moment such a function is deployed;
  until then, the Notifications tab stays empty.
- **Phone number sign-in isn't implemented**, only email — it requires
  Firebase's separate SMS-verification flow.
- **No automated tests** are included in this project; it was tested
  manually, end-to-end, on a physical device against the live Firebase
  project (28 test cases across every core flow — see the project's Task E
  report for the full test plan and results).
- **Product sample photos** depend on a remote image host; where that fails,
  a local vector icon is shown instead, so the UI never shows a broken image.

## Suggested future improvements

- Implement the order-status Cloud Function to complete the notifications
  feature end-to-end.
- Add phone-number authentication as a second sign-in option.
- Add automated unit tests for ViewModel validation logic, which is already
  structured to be testable independently of the UI.
- Add an admin-side tool for managing the products catalogue.

---

Built for CSE5011 Mobile Application Development.
