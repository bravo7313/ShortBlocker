/**
 * Application: Shorts Blocker
 * Copyright (c) 2026 Atharva Saraf
 * Original Developer: Atharva Saraf
 * GitHub: https://github.com/YOUR_USERNAME
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package `in`.sarafonline.shortblocker

import android.content.Context
import android.content.SharedPreferences

/** All data the app stores. Local only, never leaves the phone. */
object Prefs {
    private const val FILE = "shorts_blocker_prefs"
    private const val PREFIX_ENABLED = "enabled_"
    private const val PREFIX_COUNT = "count_"
    const val KEY_REMINDERS = "reminders_enabled"
    private const val KEY_BANKING_PAUSE = "banking_pause"
    private const val KEY_STRICT = "strict_mode"

    fun get(ctx: Context): SharedPreferences =
        ctx.applicationContext.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun isEnabled(ctx: Context, t: Target) = get(ctx).getBoolean(PREFIX_ENABLED + t.id, true)
    fun setEnabled(ctx: Context, t: Target, on: Boolean) =
        get(ctx).edit().putBoolean(PREFIX_ENABLED + t.id, on).apply()
    fun anyEnabled(ctx: Context) = Targets.ALL.any { isEnabled(ctx, it) }

    fun remindersOn(ctx: Context) = get(ctx).getBoolean(KEY_REMINDERS, true)
    fun setReminders(ctx: Context, on: Boolean) =
        get(ctx).edit().putBoolean(KEY_REMINDERS, on).apply()

    fun blockedCount(ctx: Context, t: Target) = get(ctx).getInt(PREFIX_COUNT + t.id, 0)
    fun totalBlocked(ctx: Context) = Targets.ALL.sumOf { blockedCount(ctx, it) }
    fun incrementBlocked(ctx: Context, t: Target) {
        get(ctx).edit().putInt(PREFIX_COUNT + t.id, blockedCount(ctx, t) + 1).apply()
    }

    fun bankingPause(ctx: Context) = get(ctx).getBoolean(KEY_BANKING_PAUSE, false)
    fun setBankingPause(ctx: Context, on: Boolean) =
        get(ctx).edit().putBoolean(KEY_BANKING_PAUSE, on).apply()

    fun strictOn(ctx: Context) = get(ctx).getBoolean(KEY_STRICT, false)
    fun setStrict(ctx: Context, on: Boolean) = get(ctx).edit().putBoolean(KEY_STRICT, on).apply()

    /** True for keys that should make the status notification refresh. */
    fun isStatusKey(key: String?) =
        key != null && (key.startsWith(PREFIX_ENABLED) || key == KEY_REMINDERS)
}
