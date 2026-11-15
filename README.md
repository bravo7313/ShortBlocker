# Shorts Blocker

An open-source Android app that blocks **YouTube Shorts** and **Instagram Reels** while leaving the rest of YouTube and Instagram working normally.

**Latest release: [v1.2.2](https://github.com/bravo7313/ShortBlocker/releases/latest)** · License: [MIT](LICENSE) · Developer: Atharva Saraf

> **Availability:** A Google Play version is being prepared and is **not yet available**. Until then, install the signed APK from [GitHub Releases](https://github.com/bravo7313/ShortBlocker/releases).

> Shorts Blocker is an independent project. It is not affiliated with, endorsed by, or sponsored by Google, YouTube, Instagram or Meta. All product names and trademarks belong to their respective owners.

## Features

- **Separate switches** for blocking YouTube Shorts and Instagram Reels
- **Status notification** that shows when protection is on and what it is blocking
- **Optional reminder pop-ups** when a Short or Reel is blocked (can be switched off)
- **Strict mode** (optional): switching a blocker off requires a 30-second wait and a typed phrase
- **Quick Settings tile** to pause protection completely, for example before opening a banking or UPI app
- **Shorts links** (`youtube.com/shorts/...`) can open as normal videos if you choose Shorts Blocker to handle them
- **Local counters** of how many Shorts and Reels were blocked, stored only on your phone
- Light and dark themes
- No ads, no analytics, no tracking, and **no internet permission**

## How it works

Android does not let one app see which screen another app is showing, except through the **Accessibility Service** API. Shorts Blocker uses it in a deliberately narrow way:

1. The service is declared for only two apps: YouTube (`com.google.android.youtube`) and Instagram (`com.instagram.android`). Android does not deliver events from any other app to it. See [`accessibility_service_config.xml`](app/src/main/res/xml/accessibility_service_config.xml).
2. When an event arrives, the service checks whether the Shorts player or the Reels screen is showing. It does this by looking for specific internal view identifiers (and, for Instagram, whether the Reels tab is selected). See [`Targets.kt`](app/src/main/java/com/example/shortblocker/Targets.kt).
3. If it finds one and that blocker is switched on, it presses **Back**.

The service is allowed to read window content in those two apps, because that is how view identifiers are found. The code does not read, log, store or transmit text, messages, passwords or anything else. It does not perform gestures and does not observe key presses. The service is declared with `isAccessibilityTool="false"`: Shorts Blocker is not an assistive technology.

## Permissions

| Permission                                                | Why it is needed                                                         |
| --------------------------------------------------------- | ------------------------------------------------------------------------ |
| Accessibility Service (you enable it in Android settings) | To detect the Shorts and Reels screens in the YouTube and Instagram apps |
| `POST_NOTIFICATIONS` (Android 13+)                        | To show the status notification and the optional reminders               |
| _No `INTERNET` permission_                                | The app makes no network requests                                        |

See [PRIVACY.md](PRIVACY.md) for the full privacy policy.

## Install

1. Download `ShortsBlocker-v1.2.2.apk` and `SHA256SUMS.txt` from the [latest release](https://github.com/bravo7313/ShortBlocker/releases/latest).
2. **Verify the download** (see below).
3. Open the APK and allow installing from your browser or file manager if Android asks.
4. Open **Shorts Blocker**, tap **Turn on protection**, read the explanation, then enable **Shorts Blocker** in Android's Accessibility settings.

**If Google Play Protect blocks the install** ("App blocked to protect your device"): in some regions, including India, Play Protect blocks apps installed from a browser or file manager when they use sensitive permissions such as Accessibility. This is an automatic rule, not a verdict about this app. Options:

- Install from a computer with `adb install ShortsBlocker-v1.2.2.apk`, or
- Temporarily turn off _Scan apps with Play Protect_ in the Play Store's Play Protect settings, install the app, and turn it back on.

**If the Accessibility toggle is greyed out** (Android 13+, apps installed outside the Play Store): open Settings → Apps → Shorts Blocker → ⋮ (top right) → **Allow restricted settings**, then try again.

### Verify your download

Compare the APK's SHA-256 with `SHA256SUMS.txt` from the release:

- Windows (PowerShell): `Get-FileHash .\ShortsBlocker-v1.2.2.apk -Algorithm SHA256`
- Linux: `sha256sum ShortsBlocker-v1.2.2.apk`
- macOS: `shasum -a 256 ShortsBlocker-v1.2.2.apk`

Official releases are signed with the same key. The signing certificate's SHA-256

Check it with the Android SDK tool: `apksigner verify --print-certs ShortsBlocker-v1.2.2.apk`. If the fingerprint differs, do not install the file.

## Banking and UPI apps

Some banking and UPI apps refuse to open while **any** accessibility service is switched on. That is a security feature of those apps. Shorts Blocker does not try to hide from it or bypass it.

Use the **Quick Settings tile**:

1. Before opening a banking app, tap the **Shorts Blocker** tile. The service switches itself off completely, and a notification reminds you that protection is paused.
2. When you are done, tap the tile or the notification to open Accessibility settings and turn Shorts Blocker back on.

Re-enabling the service is always done by you in Android's Accessibility settings.

## Strict mode

Strict mode is an optional self-control feature. When it is on, switching a blocker off inside the app requires a 30-second wait and typing a short phrase. It adds friction at the app level only: it cannot stop you from switching the service off in Android's Accessibility settings or uninstalling the app, and the banking pause tile is not gated.

## Shorts links

To open Shorts links as normal videos, choose Shorts Blocker for them: Settings → Apps → Shorts Blocker → _Open by default_ → add the links. Depending on your Android version, Android may ask you to approve this.

## Known limitations

- **Detection depends on internal identifiers** in the YouTube and Instagram apps. They can change in an app update, which can stop blocking until the identifiers in `Targets.kt` are updated. Instagram detection in particular may not work on every version.
- The Shorts or Reels screen may appear for a moment before Back is pressed.
- Reels opened from other places in Instagram, such as direct messages, may also be blocked.
- Some banking and UPI apps will not run while an accessibility service is on (see above).
- Google Play Protect may block installing the APK from outside the Play Store in some regions (see above).
- Strict mode cannot prevent you from disabling the service in Android settings.

## Build from source

Requirements: Android Studio (recent stable) and JDK 17.

```
git clone https://github.com/bravo7313/ShortBlocker.git
cd ShortBlocker
./gradlew assembleDebug        # Windows: gradlew.bat assembleDebug
```

The debug APK is created in `app/build/outputs/apk/debug/`. A GitHub Actions workflow builds the project on every push.

### Where things are

| File                            | Purpose                                                              |
| ------------------------------- | -------------------------------------------------------------------- |
| `ShortsBlockerService.kt`       | The Accessibility Service that detects Shorts/Reels and presses Back |
| `Targets.kt`                    | Package names and view identifiers for YouTube and Instagram         |
| `StatusNotifier.kt`             | Status, reminder and "paused" notifications                          |
| `PauseTileService.kt`           | Quick Settings tile                                                  |
| `Prefs.kt`                      | The settings and counters stored on the device                       |
| `MainActivity.kt`               | The app screen                                                       |
| `ShortsLinkRedirectActivity.kt` | Rewrites Shorts links to normal video links                          |

## Reporting problems

If blocking stops working after an update, [open an issue](https://github.com/bravo7313/ShortBlocker/issues) and include your phone model, Android version, and the YouTube or Instagram app version. For security problems, see [SECURITY.md](SECURITY.md).

## Contributing

Issues and pull requests are welcome. Please keep the project free of advertising, analytics and tracking code, and do not add network access.

## Credits and licence

Built and maintained by **Atharva Saraf**.

© 2026 Atharva Saraf. Released under the [MIT License](LICENSE). Redistributions must keep the copyright and licence notice.
