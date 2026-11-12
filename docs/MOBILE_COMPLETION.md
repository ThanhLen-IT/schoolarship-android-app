# Completed mobile flows

Use the `feat/mobile-completion` branch of both repositories. The matching backend
must be deployed and `npm run migrate:mobile` must succeed before running this app.
Backend setup and new API contracts are in its `docs/MOBILE_COMPLETION.md`.

Implemented:

- Restore a valid session using `/auth/me` on launch. HTTP 401 or
  `403/ACCOUNT_INACTIVE` clears local profile/session and opens sign-in. Offline
  restoration offers retry without silently discarding the saved token.
- Country, type, major and study-level options come from published server records.
  Search supports server paging and explicit load-more controls. Ranking rows open
  mentor profiles; ranking also has a visible load-more button.
- Scholarship discovery uses one full-height RecyclerView for the filter header,
  result cards and loading/empty/load-more footer. A ConcatAdapter joins the three
  sections; the result list is not nested inside a ScrollView. Header/footer
  references remain available while their holders are offscreen, so changing
  filters and receiving API callbacks still update the original controls.
  Its LayoutManager is assigned before inflating the header/footer against that
  RecyclerView, because inflation asks the parent to generate LayoutParams.
- Mentor profile → choose PDF from the device → submit an application. Private CVs
  are downloaded with Bearer auth to app cache and opened via FileProvider. The
  server authorizes access; private storage is not exposed publicly.
- Application lists show viewer role and actual state: mentor accept/reject/start,
  mentee confirm completion, explicit support requests/replies and mentor reviews.
- Conversations are created only after acceptance. Chat uses 5-second foreground
  polling/cursors, read markers and UUID retry keys; retrying an unconfirmed send
  does not create duplicate server messages. Pending sends remain available for
  retry; polling stops when the screen is no longer visible.
- In-app notification list/read/read-all, with navigation to conversation,
  mentoring, verification and support screens. These are not background push alerts.
- Support tickets/feedback are submitted and stored; users see status/admin replies.
  FAQ buttons open answers. The contact cards lead to actual support instead of a
  simulated email action.
- Avatar picker uploads a resized JPEG from the device. The server validates and
  re-encodes it. Updating an avatar is immediate and explicitly separate from draft
  text profile edits; canceling text edits does not undo a confirmed avatar upload.
- Persisted system/light/dark preference with a night palette. Unconfigured
  Google/Facebook sign-in buttons/separators are hidden; email/password auth is real.
- Closing an account requires a second confirmation/current password. It closes and
  anonymizes the account, removes personal content and signs out; mentoring/payment
  history retains an anonymized ID. See backend retention details before testing.

Build with Android Studio or JDK 25 / SDK 37:

```bash
./gradlew :app:assembleDebug :app:testDebugUnitTest
```

The default debug/release URL is `https://scholarship-uit.duckdns.org/`. To exercise
local backend fixtures from an emulator, a debug build can override the origin:

```bash
./gradlew :app:assembleDebug -PapiBaseUrl=http://10.0.2.2:18880/
```

Only the debug manifest permits cleartext local development traffic. Release keeps
HTTPS and the production origin. Account backup is disabled to avoid restoring a
saved session onto a different device. No API credentials or test user tokens are
embedded in the app.

Validation includes model/Retrofit contract tests and a successful debug build.
Robolectric tests also launch discovery with its actual Android resources, open
it through Home's view-all action and dispatch swipe gestures across six mocked
results and back to the filters. These are simulated Android view tests, not a
physical-device/emulator pass.
Final validation results are recorded with the PR. Real email deep links depend on
backend sender configuration (`scholarship://reset-password` is supported); no
production email was sent during development.

Final validation:

- `assembleDebug` and `testDebugUnitTest` passed with the default production origin;
  16 tests passed (13 API contract tests, the existing example and two Robolectric
  navigation/scrolling regressions on simulated SDK 35). Both new tests reproduced
  `RecyclerView has no LayoutManager` on the earlier initialization order and pass
  after installing the LayoutManager before header/footer inflation.
- An isolated debug APK was also built for local HTTP testing. No local URL is
  embedded in the default/release configuration.
- Backend tests passed against PostgreSQL 18.6/PGlite through real HTTP, including
  CV permissions, mentoring transitions, message retries, notifications, support,
  image uploads and account closure.
- A headless Android emulator was attempted but could not finish booting in the
  hardware-unaccelerated environment before the deadline. No emulator screen-flow
  or physical-device pass is claimed. Run the final account/device checks after
  deploying the backend; real email delivery was not exercised here.

Scholarship discovery device check after pulling this branch:

1. With six published scholarships, open discovery and swipe upward on the cards
   until the sixth scholarship is visible. Swipe downward to return to the filter
   header; the result count should still be six.
2. Search or change a filter, then check the count and result cards together.
   Clear filters and check that all six return. Confirm the empty/retry message is
   reachable when there are no results or a request fails.
3. With more than twenty results, scroll to the last loaded card and check that
   the next page loads. The explicit load-more button uses the same paging state.
4. Rotate the device, return to the top and verify the selected filters/search
   text are retained. A device/emulator swipe pass is required before claiming
   this interaction has been verified on Android.
