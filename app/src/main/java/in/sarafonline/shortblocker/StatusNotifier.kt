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

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.Settings
import kotlin.random.Random

/**
 * Two notifications:
 *  1. A silent, persistent "Shorts Blocker is ON" status notification.
 *  2. A short-lived, creative reminder shown when a Short/Reel is blocked.
 */
object StatusNotifier {
    private const val CHANNEL_STATUS = "status"
    private const val CHANNEL_REMINDERS = "reminders"
    private const val ID_STATUS = 1
    private const val ID_REMINDER = 2
    private const val ID_PAUSED = 3
    private const val REMINDER_TIMEOUT_MS = 6000L

    private var lastReminderIndex = -1

    private fun manager(ctx: Context) = ctx.getSystemService(NotificationManager::class.java)

    private fun openAppIntent(ctx: Context) = PendingIntent.getActivity(
        ctx, 0, Intent(ctx, MainActivity::class.java),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    /** Persistent "ON" notification, built from the current settings. */
    fun showStatus(ctx: Context) {
        val nm = manager(ctx) ?: return
        nm.createNotificationChannel(
            NotificationChannel(
                CHANNEL_STATUS,
                ctx.getString(R.string.notif_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply { setShowBadge(false) }
        )

        val active = Targets.ALL.filter { Prefs.isEnabled(ctx, it) }
        val paused = active.isEmpty()
        val names = active.joinToString(" + ") { ctx.getString(it.labelRes) }

        val n = Notification.Builder(ctx, CHANNEL_STATUS)
            .setSmallIcon(R.drawable.ic_stat_shield)
            .setContentTitle(
                ctx.getString(if (paused) R.string.notif_title_paused else R.string.notif_title_on)
            )
            .setContentText(
                if (paused) ctx.getString(R.string.notif_text_paused)
                else ctx.getString(R.string.notif_text, names, Prefs.totalBlocked(ctx))
            )
            .setContentIntent(openAppIntent(ctx))
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        nm.notify(ID_STATUS, n)
    }

    /** Friendly pop-up nudge when a Short/Reel was just blocked. */
    fun remind(ctx: Context, target: Target) {
        if (!Prefs.remindersOn(ctx)) return
        val nm = manager(ctx) ?: return
        nm.createNotificationChannel(
            NotificationChannel(
                CHANNEL_REMINDERS,
                ctx.getString(R.string.notif_channel_reminders),
                NotificationManager.IMPORTANCE_HIGH   // heads-up pop-up
            ).apply {
                setSound(null, null)                  // silent, no vibration
                enableVibration(false)
                setShowBadge(false)
            }
        )

        val messages = ctx.resources.getStringArray(target.remindersRes)
        if (messages.isEmpty()) return
        var i = Random.nextInt(messages.size)
        if (messages.size > 1 && i == lastReminderIndex) i = (i + 1) % messages.size
        lastReminderIndex = i

        val parts = messages[i].split("|", limit = 2)
        val title = parts[0]
        val body = parts.getOrElse(1) { "" }

        val n = Notification.Builder(ctx, CHANNEL_REMINDERS)
            .setSmallIcon(R.drawable.ic_stat_shield)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(Notification.BigTextStyle().bigText(body))
            .setContentIntent(openAppIntent(ctx))
            .setAutoCancel(true)
            .setTimeoutAfter(REMINDER_TIMEOUT_MS)
            .setCategory(Notification.CATEGORY_STATUS)
            .build()
        nm.notify(ID_REMINDER, n)
    }

    /** Shown while paused for banking, so you never forget to turn protection back on. */
    fun showPaused(ctx: Context) {
        val nm = manager(ctx) ?: return
        nm.createNotificationChannel(
            NotificationChannel(
                CHANNEL_STATUS,
                ctx.getString(R.string.notif_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply { setShowBadge(false) }
        )
        val open = PendingIntent.getActivity(
            ctx, 1,
            Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val n = Notification.Builder(ctx, CHANNEL_STATUS)
            .setSmallIcon(R.drawable.ic_stat_shield)
            .setContentTitle(ctx.getString(R.string.notif_title_bank))
            .setContentText(ctx.getString(R.string.notif_text_bank))
            .setContentIntent(open)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setCategory(Notification.CATEGORY_STATUS)
            .build()
        nm.notify(ID_PAUSED, n)
    }

    fun cancelPaused(ctx: Context) {
        manager(ctx)?.cancel(ID_PAUSED)
    }

    fun cancelAll(ctx: Context) {
        manager(ctx)?.apply {
            cancel(ID_STATUS)
            cancel(ID_REMINDER)
        }
    }
}
