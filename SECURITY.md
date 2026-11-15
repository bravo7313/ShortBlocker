# Security Policy

## Supported versions

| Version               | Supported |
| --------------------- | --------- |
| 1.2.x (latest: 1.2.2) | Yes       |
| Older than 1.2.0      | No        |

Please update to the [latest release](https://github.com/bravo7313/ShortBlocker/releases/latest) before reporting a problem.

## Reporting a vulnerability

Please **do not** post security problems in public issues.

1. Use GitHub's private reporting: open the repository's **Security** tab and choose **Report a vulnerability**.
2. If that is not available, email atharvasaraf2004@gmail.com with the subject "Shorts Blocker security".

Please include what you found, the steps to reproduce it, the app version, and your Android version and phone model. I will aim to acknowledge reports within 7 days and to publish a fix or an explanation as soon as reasonably possible. I'm happy to credit you in the release notes unless you prefer to stay anonymous.

## What counts as a security or privacy issue

- The app sending data off the device, or requesting a network permission
- The Accessibility Service acting on apps other than YouTube and Instagram
- The app reading, storing or leaking screen content beyond what [PRIVACY.md](PRIVACY.md) describes
- Another app being able to control the service or read its data
- A release file that does not match its published checksum or signing certificate

Normal bugs, such as blocking stopping after a YouTube or Instagram update, are not security issues. Please report them as regular [issues](https://github.com/bravo7313/ShortBlocker/issues).

## Verifying releases

- Each release includes `SHA256SUMS.txt`. Compare it with your downloaded APK's SHA-256.
- Official releases are signed with one key.

```

- Check an APK with `apksigner verify --print-certs ShortsBlocker-v1.2.2.apk`. If the fingerprint differs, do not install it.
- Only download releases from <https://github.com/bravo7313/ShortBlocker/releases>. Copies from other websites are not official.

## Design choices that limit risk

- No `INTERNET` permission
- Accessibility Service limited to the YouTube and Instagram packages, with no gesture or key-event capabilities
- No advertising, analytics or tracking code
- Only local settings and counters are stored, and app backup is disabled
- The signing keystore and its passwords are never committed to this repository

## Known limits

Android lets users turn off an accessibility service at any time, so Strict mode cannot prevent that. Some banking apps refuse to run while any accessibility service is on; the Quick Settings tile pauses the service completely for those moments.
```
