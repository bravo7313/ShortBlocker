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

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.content.SharedPreferences
import android.os.SystemClock
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
/**
 * Watches ONLY the YouTube and Instagram apps (see res/xml/accessibility_service_config.xml).
 * When the Shorts / Reels screen is on screen it presses Back.
 * Nothing is read, logged or sent anywhere.
 */

class ShortsBlockerService : AccessibilityService() {

    companion object {
        private const val CONTENT_THROTTLE_MS = 100L  // content events fire constantly
        private const val BLOCK_COOLDOWN_MS = 600L    // avoid repeated Back presses

        /** Lets the Quick Settings tile pause the service (banking mode). */
        @Volatile var instance: ShortsBlockerService? = null
    }

    private var lastCheck = 0L
    private var lastBlock = 0L

    private val prefListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (Prefs.isStatusKey(key)) StatusNotifier.showStatus(this)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Prefs.setBankingPause(this, false)
        StatusNotifier.cancelPaused(this)
        Prefs.get(this).registerOnSharedPreferenceChangeListener(prefListener)
        StatusNotifier.showStatus(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        if (event == null) return
        val pkg = event.packageName?.toString() ?: return
        val target = Targets.forPackage(pkg) ?: return
        if (!Prefs.isEnabled(this, target)) return

        val now = SystemClock.uptimeMillis()
        // Window changes are always checked; noisy content changes are throttled.
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            if (now - lastCheck < CONTENT_THROTTLE_MS) return
        }
        lastCheck = now
        if (now - lastBlock < BLOCK_COOLDOWN_MS) return

        val root = rootInActiveWindow ?: return
        if (root.packageName?.toString() != pkg) return

        if (isTargetScreen(root, target)) {
            lastBlock = now
            performGlobalAction(GLOBAL_ACTION_BACK)
            Prefs.incrementBlocked(this, target)
            StatusNotifier.showStatus(this)
            StatusNotifier.remind(this, target)
        }
    }
    private fun isTargetScreen(root: AccessibilityNodeInfo, target: Target): Boolean {
        for (id in target.viewIds) {
            if (!root.findAccessibilityNodeInfosByViewId(id).isNullOrEmpty()) return true
        }
        for (id in target.selectedTabIds) {
            val nodes = root.findAccessibilityNodeInfosByViewId(id)
            if (nodes != null && nodes.any { it.isSelected }) return true
        }
        return false
    }

    private fun cleanup() {
        instance = null
        Prefs.get(this).unregisterOnSharedPreferenceChangeListener(prefListener)
        StatusNotifier.cancelAll(this)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        cleanup()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        cleanup()
        super.onDestroy()
    }

    override fun onInterrupt() {}
}
