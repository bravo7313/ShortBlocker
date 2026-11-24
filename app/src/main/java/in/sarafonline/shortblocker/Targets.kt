/**
 * Application: Shorts Blocker
 * Copyright (c) 2026 Atharva Saraf
 * Original Developer: Atharva Saraf
 * GitHub: https://github.com/bravo7313
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package `in`.sarafonline.shortblocker

/** One app + short-video feature we know how to detect. */
class Target(
    val id: String,
    val packageName: String,
    val labelRes: Int,
    /** View IDs that only exist while the Shorts/Reels player is on screen. */
    val viewIds: Array<String>,
    /** Bottom-nav tab IDs that count as "Shorts/Reels" when currently selected. */
    val selectedTabIds: Array<String>,
    /** string-array of "Title|Body" reminder messages. */
    val selectedTabTexts: Array<String> = emptyArray(),
    val remindersRes: Int
)

object Targets {
    // IDs can change in app updates. Verify with Layout Inspector or
    // `adb shell uiautomator dump` and update them here if blocking stops working.
    val YOUTUBE = Target(
        id = "yt",
        packageName = "com.google.android.youtube",
        labelRes = R.string.label_youtube,
        viewIds = arrayOf(
            "com.google.android.youtube:id/reel_recycler",
            "com.google.android.youtube:id/reel_player_page_container",
            "com.google.android.youtube:id/reel_watch_player"
        ),
        selectedTabIds = emptyArray(),
        remindersRes = R.array.yt_reminders
    )

    val INSTAGRAM = Target(
        id = "ig",
        packageName = "com.instagram.android",
        labelRes = R.string.label_instagram,
        viewIds = arrayOf(
            "com.instagram.android:id/clips_viewer_view_pager"
        ),
        selectedTabIds = arrayOf(
            "com.instagram.android:id/clips_tab"
        ),
        remindersRes = R.array.ig_reminders
    )
    val FACEBOOK = Target(
        id = "fb",
        packageName = "com.facebook.katana",
        labelRes = R.string.label_facebook,
        viewIds = emptyArray(),                  // fill in after testing (step 7)
        selectedTabIds = emptyArray(),
        selectedTabTexts = arrayOf("Reels"),     // starting guess, must be tested
        remindersRes = R.array.fb_reminders
    )



    val ALL = listOf(YOUTUBE, INSTAGRAM, FACEBOOK)

    fun forPackage(pkg: String?): Target? = ALL.firstOrNull { it.packageName == pkg }
}
