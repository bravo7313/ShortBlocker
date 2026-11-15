# Shorts Blocker

An open-source Android app that blocks **YouTube Shorts** and **Instagram Reels** while leaving the rest of YouTube and Instagram untouched. Each one has its own on/off switch.

**Coming soon to Google Play.**

- No ads, no tracking, no analytics
- **No internet permission**: it cannot send data anywhere
- Zero third-party libraries; the whole app is a few small Kotlin files
- Persistent "Shorts Blocker is ON" notification, plus optional creative reminder pop-ups when something is blocked
- Separate blockers for YouTube Shorts and Instagram Reels, and a local-only counter of what was blocked

> Not affiliated with, endorsed by, or sponsored by YouTube, Google, Instagram or Meta. All trademarks belong to their owners.

## How it works

Android gives apps no other way to know which screen another app is showing, so this uses an **Accessibility Service**:

1. The service is limited to the YouTube and Instagram packages (`res/xml/accessibility_service_config.xml`).
2. When the Shorts or Reels screen is on screen, it presses **Back**.
3. Nothing is read, saved, or transmitted. See [`ShortsBlockerService.kt`](app/src/main/java/com/example/shortblocker/ShortsBlockerService.kt) and [`Targets.kt`](app/src/main/java/com/example/shortblocker/Targets.kt).

Shorts links shared from other apps (`youtube.com/shorts/ID`) are rewritten to normal `watch?v=ID` links by `ShortsLinkRedirectActivity`.

## Permissions

| Permission                           | Why                                                                   |
| ------------------------------------ | --------------------------------------------------------------------- |
| Accessibility Service (user-enabled) | To detect the Shorts / Reels screen in the YouTube and Instagram apps |
| `POST_NOTIFICATIONS`                 | To show the status notification and reminder pop-ups                  |
| _(no INTERNET)_                      | The app has no network access at all                                  |

## Install

1. Download the latest APK from **Releases**.
2. Verify the checksum: `sha256sum ShortsBlocker.apk` should match `SHA256SUMS.txt`.
3. Open the app, tap **Turn on protection**, and enable **Shorts Blocker**.
4. If the toggle is greyed out (common for apps installed outside the Play Store): Settings → Apps → Shorts Blocker → ⋮ → **Allow restricted settings**.

## Build from source

```
git clone https://github.com/bravo7313/ShortBlocker
cd ShortsBlocker
./gradlew assembleDebug
```

## Banking and UPI apps

Many banking and UPI apps refuse to open while **any** accessibility service is switched on. That is a security feature, and Shorts Blocker does **not** try to hide from it or bypass it.

Instead, the app has a **Quick Settings tile**:

1. Before opening a banking app, tap the **Shorts Blocker** tile. The service switches itself fully off, so the banking app sees no accessibility service, and a reminder notification appears.
2. When you're done, tap the tile or the notification to open Accessibility settings and switch protection back on.

## Strict mode

YouTube's built-in Shorts limit can be overridden with a tap. Strict mode adds real friction: to switch a blocker off, you must wait 30 seconds **and** type a phrase. It is a self-control tool that you choose to enable. It cannot stop you from switching the service off in Android's own Accessibility settings, and the banking pause is deliberately not gated.

## Known limitations

- Detection relies on the apps' internal view IDs, which can change in updates. If blocking stops working, open an issue or update the IDs in `Targets.kt`.
- The Shorts/Reels screen may flash for a split second before Back is pressed.
- Reels shared inside Instagram DMs may also be blocked.

## Contributing

Issues and pull requests are welcome. Please keep the project dependency-free and never add network access.

## Credits and license

Built and maintained by **Atharva Saraf**.

© 2026 Atharva Saraf. Released under the MIT License, see [LICENSE](LICENSE). Redistributions must keep the copyright and licence notice.
