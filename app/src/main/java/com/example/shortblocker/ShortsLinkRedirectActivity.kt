/**
 * Application: Shorts Blocker
 * Copyright (c) 2026 Atharva Saraf
 * Original Developer: Atharva Saraf
 * GitHub: https://github.com/bravo7313
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.example.shortblocker

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle

/** Turns https://youtube.com/shorts/ID into https://www.youtube.com/watch?v=ID */
class ShortsLinkRedirectActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = intent?.data?.pathSegments?.getOrNull(1)
        if (!id.isNullOrBlank()) {
            val watch = Uri.parse("https://www.youtube.com/watch?v=${Uri.encode(id)}")
            val open = Intent(Intent.ACTION_VIEW, watch).setPackage("com.google.android.youtube")
            try {
                startActivity(open)
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, watch)) // fall back to browser
            }
        }
        finish()
    }
}
