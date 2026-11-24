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

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

/**
 * Quick Settings tile. Tap while protection is ON  -> the service switches itself fully off
 * (so banking / UPI apps see no accessibility service) and a reminder notification appears.
 * Tap while OFF -> opens Accessibility settings so you can turn it back on.
 * Nothing is hidden from banking apps: while paused, the service genuinely is not running.
 */
class PauseTileService : TileService() {

    override fun onStartListening() {
        refreshTile(ServiceState.isEnabled(this))
    }

    override fun onClick() {
        val service = ShortsBlockerService.instance
        if (service != null && ServiceState.isEnabled(this)) {
            Prefs.setBankingPause(this, true)
            StatusNotifier.showPaused(this)
            service.disableSelf()
            refreshTile(false)
        } else {
            openAccessibilitySettings()
        }
    }

    private fun refreshTile(active: Boolean) {
        val tile = qsTile ?: return
        tile.state = if (active) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.label = getString(R.string.tile_label)
        if (Build.VERSION.SDK_INT >= 29) {
            tile.subtitle = getString(if (active) R.string.tile_on else R.string.tile_paused)
        }
        tile.updateTile()
    }

    private fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= 34) {
            startActivityAndCollapse(
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            )
        } else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
    }
}
