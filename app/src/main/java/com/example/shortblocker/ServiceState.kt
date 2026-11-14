/**
 * Application: Shorts Blocker
 * Copyright (c) 2026 Atharva Saraf
 * Original Developer: Atharva Saraf
 * GitHub: https://github.com/YOUR_USERNAME
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.example.shortblocker

import android.content.ComponentName
import android.content.Context
import android.provider.Settings

object ServiceState {
    /** True when the user has the Accessibility service switched on in Android settings. */
    fun isEnabled(ctx: Context): Boolean {
        val enabled = Settings.Secure.getString(
            ctx.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        val me = ComponentName(ctx, ShortsBlockerService::class.java)
        return enabled.split(':').any {
            it.equals(me.flattenToString(), true) || it.equals(me.flattenToShortString(), true)
        }
    }
}
