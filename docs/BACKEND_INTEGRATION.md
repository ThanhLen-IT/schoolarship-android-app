# Android ↔ backend integration

The app uses `https://scholarship-uit.duckdns.org/` (`ApiClient.java`).
Backend contract reviewed: `ThanhLen-IT/schoolarship-backend-app` at
`4151e5807ee87d67ba7831925a1e4ca0b201218f`.

## Server-backed screens

| Screen / action | API |
| --- | --- |
| Register, sign in | `POST /api/auth/register`, `/api/auth/login` |
| Profile and account role | `GET /api/auth/me` |
| Save profile, skills array, avatar URL | `PUT /api/users/profile` |
| Forgot / reset password | `POST /api/auth/forgot-password`, `/api/auth/reset-password` |
| Home, search, scholarship picker | `GET /api/scholarships` |
| Scholarship details | `GET /api/scholarships/:id` |
| Save / remove / list bookmarks | `POST/DELETE /api/users/saved/:id`, `GET /api/users/saved` |
| Scholarship-specific ranking | `GET /api/mentors/ranking?scholarship_id=...` |
| Mentor evidence PDF, latest verification status | `POST /api/mentors/verify`, `GET /api/mentors/verify/status` |
| My mentoring applications (backend caps at 100) | `GET /api/mentoring/applications/mine` |
| Community feed, post, like, comments | `/api/posts` routes |

## Behavior

- No fallback fixture records. An empty server response stays empty.
- Mutations report success only after both HTTP and `success: true` succeed.
- Authenticated HTTP 401 clears the session and opens sign-in. A stale request
  made with a previous account's token cannot overwrite the current account.
- Profile cache is per account; all edits are submitted to the server first.
- Search sends `study_level` (`bachelor`, `master`, `doctoral`, `other`, `unknown`),
  country, `type`, `major`, `deadline_from`, `deadline_to`, and server sort keys.
  Search is debounced; obsolete requests are cancelled. Scroll to load more,
  or tap the result count. The ranking picker also loads more scholarships;
  ranking scrolls load additional mentors (long-press title to load more).
- Country, type and major filters are exact values in the backend, not fuzzy
  translations. Published records must use matching values.
- Scholarship detail always fetches its actual ID. Apply opens
  `application_link`; bookmark is a separate action. HTML is rendered as text
  formatting without executing scripts. Embedded web tables/images are not a
  full browser preview.
- Mentor role comes from the server; the UI cannot grant itself verification.
- PDF uploads check the actual byte length (5 MB maximum) and PDF header, then
  send multipart `scholarship_id` and `evidence_file`. Only the backend response
  can mark a submission successful. Status API returns the latest submission,
  optionally scoped to one scholarship; it is not a full history API.

## Backend capabilities that remain unavailable

- No chat or notification API exists in this backend. Those screens explicitly
  show that the feature is unavailable, rather than fabricated conversations.
- No image-upload API exists. Profile and post images use a public image URL;
  use HTTPS images. Phone/birthdate are not accepted by registration and are
  hidden instead of collecting unsaved fields.
- No delete-account or post-bookmark API exists. Delete-account explains the
  limitation; post bookmark controls are hidden. Logging out clears the token.
- Fake global ranking/XP/streak cards are hidden; ranking uses scholarship data.

## Password reset setup

The current backend emails a 64-character hexadecimal token (not a 5-digit OTP).
The app accepts either that token or the full emailed URL on the new-password
screen and calls `reset-password` with `token` and `new_password`.

The server must have working `RESEND_API_KEY`, `PASSWORD_RESET_FROM` and
`PASSWORD_RESET_URL`. To open the app from the email, configure
`PASSWORD_RESET_URL=scholarship://reset-password`. If the server uses a web URL,
users can paste the emailed URL into the token field instead. No backend
configuration was changed by this frontend branch.

## Verification

`ApiContractTest` exercises Gson decoding and Retrofit over a local HTTP server:
canonical scholarship fields; numeric IDs; nullable data; skill arrays;
nullable ranking metrics; all search filters and page/sort binding; empty
responses; failed bookmarks; profile payloads; reset-token requests; optional
registration username omission. No real user credentials are stored in tests.

Verification completed in this branch:

- Gradle `:app:assembleDebug :app:testDebugUnitTest`: passed (10 tests, 9 new
  API contract regressions plus the existing example test).
- Resource XML parsing and Java view-ID references: checked.
- Live public API: health 200; scholarship list 200 with zero published records;
  unauthenticated `/api/auth/me` 401.
- No emulator/device or real-account write flow has been exercised. Email
  delivery, account-to-account sync and admin verification still need the manual
  release checks below.

Run in Android Studio or a configured SDK environment:

```sh
./gradlew :app:assembleDebug :app:testDebugUnitTest
```

The repository specifies JDK 25, Gradle 9.8.2 and Android API 37. Install the
required SDK packages through Android Studio when prompted.

Manual release checks with a real account/device:

1. Register/sign in; check that profile skills decode, and relaunch the app.
2. Publish a scholarship in Web Admin, search/filter it, open details, save it,
   open bookmarks, remove it, and confirm the server state.
3. Save profile fields/avatar URL, sign in on another device, confirm sync.
4. Request email reset; use a valid token, then try an expired/reused token.
5. Check ranking for a real published scholarship, including no mentors/null
   scores; compare the names and metrics with API JSON.
6. Submit a real PDF in a test account; compare pending/approved/rejected status
   with admin decisions. Ensure a rejected HTTP response never reports success.
7. Publish a community post, like/unlike, add a comment, then refresh.
8. Disable network and try mutations. Verify an error is shown and the app does
   not claim the write succeeded. Restore network and retry.
