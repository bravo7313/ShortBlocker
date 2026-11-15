# Privacy Policy

**Shorts Blocker** · Developer: Atharva Saraf · Applies to version 1.2.2 and later · Last updated: 15 November 2026

## Summary

Shorts Blocker does not collect, transmit, sell or share any personal data. The app has no internet permission and makes no network requests. The few settings it stores stay on your phone.

## Information we collect

None. The developer receives no data from the app: no personal information, no usage data, no device identifiers, no crash reports and no analytics.

## Data stored on your device

The app stores only the following in its private storage on your phone:

- Whether the YouTube blocker and the Instagram blocker are switched on
- Whether Strict mode and the reminder notifications are switched on
- Whether protection was paused using the Quick Settings tile
- A count of how many Shorts and Reels have been blocked

This data is never sent anywhere. App backup is disabled, and the data is deleted when you uninstall the app.

## How the app uses the Accessibility Service

Shorts Blocker uses Android's Accessibility Service API for one purpose: to detect when the YouTube Shorts screen or the Instagram Reels screen is showing, and then press the Back button.

- **Which apps:** The service is declared for only two apps, YouTube (`com.google.android.youtube`) and Instagram (`com.instagram.android`). Android does not deliver events from any other app to it.
- **What it looks at:** The service is permitted to read window content in those two apps. The app uses this only to check for specific internal screen elements (view identifiers) that exist while Shorts or Reels are showing, and whether the Reels tab is selected.
- **What it does not do:** It does not read, record, store or transmit text, messages, comments, search queries, passwords, personal information or what you watch. It does not perform gestures and does not observe key presses.
- **The action it takes:** It presses Back, and it updates a local counter.

The Accessibility Service is not used to collect data, and the app is not an assistive technology: it is declared with `isAccessibilityTool="false"`. You can switch the service off at any time in Android Settings → Accessibility, or use the pause tile.

## Permissions

| Permission                                        | Purpose                                                                          |
| ------------------------------------------------- | -------------------------------------------------------------------------------- |
| Accessibility Service (enabled by you)            | Detect the Shorts and Reels screens in YouTube and Instagram, as described above |
| Notifications (`POST_NOTIFICATIONS`, Android 13+) | Show the status notification and the optional reminders                          |

The app does not request the `INTERNET` permission or any permission to read contacts, location, files, camera, microphone, SMS or call logs.

## Sharing and third parties

The app contains no advertising, analytics or tracking code and does not share data with any third party. When you tap **Source code** or **Privacy policy** inside the app, your web browser opens the GitHub page. That visit is handled by your browser and by GitHub, outside this app, under their own policies.

If the app is distributed through Google Play, Google's own handling of installs and downloads is governed by Google's policies, not by this app.

## Children

Shorts Blocker is a general-purpose utility and does not knowingly collect information from anyone, including children.

## Security

Because the app stores almost nothing and has no network access, there is no server-side data to protect. Release APKs are signed, and each release publishes a SHA-256 checksum so you can verify your download. See [SECURITY.md](SECURITY.md).

## Changes to this policy

If the app's data practices ever change, this policy will be updated in the repository before the change is released, and the "Last updated" date above will change.

## Contact

Questions about privacy: [open an issue](https://github.com/bravo7313/ShortBlocker/issues) or email 'atharvasaraf2004@gmail.com'.

Developer: Atharva Saraf · Source code: <https://github.com/bravo7313/ShortBlocker> · License: MIT
