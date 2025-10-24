# Find My Path - Ready scaffold (Final)

This scaffold includes everything you asked for:
- Google Maps integration (placeholder for API key)
- Location tracking + Foreground TrackingService that saves to Room
- Room DB for route points and failed upload queue
- Retrofit skeleton for uploading routes to your API (supports Bearer Authorization header)
- UploadManager for retrying failed uploads
- WorkManager periodic worker (every 15 minutes) to retry failed uploads
- UI: list of saved routes, route viewer, progress UI for uploads
- Gradle task to copy logo from `assets_to_import/logo.png`
- GitHub Actions example workflow (uses secrets)
- Helper script `create_pr.sh` to push & open a PR using GitHub CLI

## How to finish setup (local steps)
1. Put your `logo.png` into `assets_to_import/logo.png` or `app/src/main/res/drawable/logo.png`.
2. Create `local.properties` (DO NOT COMMIT) with:
   ```
   MAPS_API_KEY=YOUR_REAL_MAPS_API_KEY
   API_BASE_URL=https://api.example.com/
   API_KEY=YOUR_API_KEY
   ```
3. Fill `app/keystore.properties` with keystore details.
4. (Optional) Install GitHub CLI and authenticate: `gh auth login` to enable `./create_pr.sh` script.
5. Open project in Android Studio and run on a real device. Grant location permissions. The app will schedule a periodic work every 15 minutes to retry failed uploads.

## GitHub Actions / CI
- Add secrets `MAPS_API_KEY`, `API_BASE_URL`, `API_KEY` to your repository Secrets.
- The workflow will pass them to Gradle during build.

## Creating a PR
- Run `./create_pr.sh "Your PR title" "PR body"` from the project root to push a branch and open a PR (requires `gh`).

## Automated signed release via GitHub Actions
You can build a signed release APK/AAB in GitHub Actions without sharing your keystore in the repo.
1. Encode your keystore to base64 locally:
   ```bash
   base64 -w 0 my-release-keystore.jks > keystore.jks.base64.txt
   ```
2. Add the following repository secrets (Settings → Secrets → Actions):
   - `KEYSTORE_BASE64` -> contents of keystore.jks.base64.txt
   - `KEYSTORE_PASSWORD`
   - `KEY_ALIAS`
   - `KEY_PASSWORD`
   - `MAPS_API_KEY`, `API_BASE_URL`, `API_KEY` (if not already added)
3. Run the workflow `Build Release APK` from the Actions tab → select `Run workflow` → click `Run workflow`.
4. After the job completes, download the artifact `FindMyPath-release-artifacts` which contains signed APK/AAB files.
