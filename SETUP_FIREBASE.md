# Getting new versions onto your phone (Firebase App Distribution)

Every time I push a change to the `claude/android-app-edits-50tyeh` branch, GitHub
Actions builds a signed APK and uploads it to **Firebase App Distribution**. Your
phone gets a notification and you tap **Install** to get the new version. Because
every build is signed with the same key, updates install *over* the old app and
keep your data.

The code side is done. You need to do the **one-time account setup** below. It
takes ~15 minutes and you only do it once.

---

## 1. Create a Firebase project & register the app

1. Go to <https://console.firebase.google.com> and click **Add project** (any
   name, e.g. "Unit Converter"). Google Analytics is optional.
2. In the project, click the **Android** icon to add an Android app.
3. For **Android package name** enter exactly:
   ```
   com.example.unit_coverter
   ```
4. Finish. You can skip downloading `google-services.json` for now (it's only
   needed for the optional in-app update prompt — see the bottom of this file).
5. Open **Project settings → General**, scroll to **Your apps**, and copy the
   **App ID**. It looks like `1:1234567890:android:abcdef123456`.
   → This is your `FIREBASE_APP_ID` secret.

## 2. Turn on App Distribution & add yourself as a tester

1. In the Firebase console left menu: **Run → App Distribution** → **Get started**.
2. Open the **Testers & Groups** tab → create a group named exactly `testers`.
3. Add your email (`sks3022005@gmail.com`) to the `testers` group.
4. On your phone, watch for the Firebase App Distribution invite email and accept
   it. (Optional: install the "App Tester" app from that email for the smoothest
   update experience — it shows every new build and installs with one tap.)

## 3. Create a service account (lets CI upload builds)

1. Go to <https://console.cloud.google.com>, pick the **same project**.
2. **IAM & Admin → Service Accounts → Create service account**. Name it e.g.
   `firebase-distributor`.
3. Grant it the role **Firebase App Distribution Admin**.
4. Open the created account → **Keys → Add key → Create new key → JSON**. A
   `.json` file downloads. Keep it private — this is your upload credential.

## 4. Create a signing keystore

Run this on any computer with Java installed (fill in your own passwords). Keep
the resulting file **safe and forever** — losing it means future builds can't
update the installed app.

```bash
keytool -genkey -v -keystore release.keystore \
  -alias unitconverter -keyalg RSA -keysize 2048 -validity 10000
```

It asks for a keystore password, some name/org fields (anything is fine), and a
key password (you can reuse the keystore password).

## 5. Add the GitHub secrets

In your repo on GitHub: **Settings → Secrets and variables → Actions → New
repository secret**. Add each of these:

| Secret name                        | Value |
|------------------------------------|-------|
| `FIREBASE_APP_ID`                  | The App ID from step 1.5 |
| `ANDROID_KEYSTORE_PASSWORD`        | The keystore password from step 4 |
| `ANDROID_KEY_ALIAS`                | `unitconverter` (the alias from step 4) |
| `ANDROID_KEY_PASSWORD`             | The key password from step 4 |
| `ANDROID_KEYSTORE_BASE64`          | The keystore file, base64-encoded (see below) |
| `FIREBASE_SERVICE_ACCOUNT_BASE64`  | The service-account JSON, base64-encoded (see below) |

Encode the two files to base64 first (the secret value is the command's output):

```bash
# macOS / Linux
base64 -i release.keystore | tr -d '\n'
base64 -i firebase-distributor-key.json | tr -d '\n'
```
```powershell
# Windows PowerShell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("release.keystore"))
[Convert]::ToBase64String([IO.File]::ReadAllBytes("firebase-distributor-key.json"))
```

## 6. Trigger the first build

Push any change (I'll do this), **or** go to the repo's **Actions** tab, pick
**"Build & distribute to Firebase"**, and click **Run workflow**. When it
finishes green, the build appears in Firebase App Distribution and you get the
install notification on your phone.

---

## After setup — how updates flow

1. You ask me for a change.
2. I edit the code and push to `claude/android-app-edits-50tyeh`.
3. GitHub Actions builds a signed APK (version `1.0.<run number>`) and uploads it.
4. Your phone gets a notification → tap **Update** → new version installed. Done.

If a build fails, open the **Actions** tab on GitHub and check the log — the most
common cause is a mistyped secret. The workflow also attaches the APK as a
downloadable **artifact** as a backup, so you can always grab it manually.

---

## Optional: in-app "new build available" pop-up

If you want the app itself to prompt you to update (instead of relying on the
email/App Tester notification):

1. In Firebase console, download **`google-services.json`** for the app.
2. Place it at `app/google-services.json`.

The build automatically links the Firebase App Distribution SDK when that file is
present (the Gradle config already handles this). I can then wire the small bit of
code that checks for and shows the update prompt on app launch — just ask.
