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

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.StatusBarManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private class Toggle(
        val sw: Switch,
        val gated: Boolean,               // protected by Strict mode when switching OFF
        val get: () -> Boolean,
        val set: (Boolean) -> Unit
    )

    private val toggles = mutableListOf<Toggle>()

    private lateinit var statusDot: View
    private lateinit var statusTitle: TextView
    private lateinit var statusDesc: TextView
    private lateinit var enableButton: Button
    private lateinit var ytCount: TextView
    private lateinit var igCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Keep content clear of system bars (needed when the app draws edge-to-edge)
        findViewById<View>(R.id.root).setOnApplyWindowInsetsListener { v, insets ->
            val top: Int
            val bottom: Int
            if (Build.VERSION.SDK_INT >= 30) {
                val bars = insets.getInsets(WindowInsets.Type.systemBars())
                top = bars.top; bottom = bars.bottom
            } else {
                @Suppress("DEPRECATION")
                top = insets.systemWindowInsetTop
                @Suppress("DEPRECATION")
                bottom = insets.systemWindowInsetBottom
            }
            v.setPadding(0, top, 0, bottom)
            insets
        }

        statusDot = findViewById(R.id.statusDot)
        statusTitle = findViewById(R.id.statusTitle)
        statusDesc = findViewById(R.id.statusDesc)
        enableButton = findViewById(R.id.enableButton)
        ytCount = findViewById(R.id.ytCount)
        igCount = findViewById(R.id.igCount)

        val container = findViewById<LinearLayout>(R.id.togglesContainer)
        addToggle(container, R.string.toggle_youtube, R.string.toggle_youtube_desc, true,
            { Prefs.isEnabled(this, Targets.YOUTUBE) },
            { Prefs.setEnabled(this, Targets.YOUTUBE, it) })
        addToggle(container, R.string.toggle_instagram, R.string.toggle_instagram_desc, true,
            { Prefs.isEnabled(this, Targets.INSTAGRAM) },
            { Prefs.setEnabled(this, Targets.INSTAGRAM, it) })
        addToggle(container, R.string.toggle_strict, R.string.toggle_strict_desc, true,
            { Prefs.strictOn(this) },
            { Prefs.setStrict(this, it) })
        addToggle(container, R.string.toggle_reminders, R.string.toggle_reminders_desc, false,
            { Prefs.remindersOn(this) },
            { Prefs.setReminders(this, it) })

        enableButton.setOnClickListener {
            if (ServiceState.isEnabled(this)) openAccessibilitySettings() else showDisclosure()
        }
        findViewById<Button>(R.id.addTileButton).setOnClickListener { addTile() }
        findViewById<Button>(R.id.sourceButton).setOnClickListener { openUrl(R.string.url_source) }
        findViewById<Button>(R.id.privacyButton).setOnClickListener { openUrl(R.string.url_privacy) }

        askNotificationPermission()
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun addToggle(
        parent: LinearLayout, title: Int, desc: Int, gated: Boolean,
        get: () -> Boolean, set: (Boolean) -> Unit
    ) {
        val row = layoutInflater.inflate(R.layout.item_toggle, parent, false)
        row.findViewById<TextView>(R.id.toggleTitle).setText(title)
        row.findViewById<TextView>(R.id.toggleDesc).setText(desc)
        toggles += Toggle(row.findViewById(R.id.toggleSwitch), gated, get, set)
        parent.addView(row)
    }

    private fun refresh() {
        for (t in toggles) {
            t.sw.setOnCheckedChangeListener(null)
            t.sw.isChecked = t.get()
            t.sw.setOnCheckedChangeListener { _, checked ->
                if (t.gated && !checked && Prefs.strictOn(this)) {
                    // Strict mode: snap back and make the user pass the gate first
                    showGate { t.set(false); refresh() }
                    refresh()
                } else {
                    t.set(checked)   // the running service reacts to this automatically
                    refresh()
                }
            }
        }

        ytCount.text = Prefs.blockedCount(this, Targets.YOUTUBE).toString()
        igCount.text = Prefs.blockedCount(this, Targets.INSTAGRAM).toString()

        val serviceOn = ServiceState.isEnabled(this)
        val anyOn = Prefs.anyEnabled(this)
        val color: Int
        when {
            !serviceOn && Prefs.bankingPause(this) -> {
                color = getColor(R.color.sb_paused)
                statusTitle.setText(R.string.status_bank)
                statusDesc.setText(R.string.status_bank_desc)
                enableButton.setText(R.string.btn_enable)
            }
            !serviceOn -> {
                color = getColor(R.color.sb_off)
                statusTitle.setText(R.string.status_off)
                statusDesc.setText(R.string.status_off_desc)
                enableButton.setText(R.string.btn_enable)
            }
            !anyOn -> {
                color = getColor(R.color.sb_paused)
                statusTitle.setText(R.string.status_paused)
                statusDesc.setText(R.string.status_paused_desc)
                enableButton.setText(R.string.btn_manage)
            }
            else -> {
                color = getColor(R.color.sb_on)
                statusTitle.setText(R.string.status_on)
                statusDesc.setText(R.string.status_on_desc)
                enableButton.setText(R.string.btn_manage)
            }
        }
        statusDot.background.mutate().setTint(color)
    }

    /** Strict mode gate: a 30-second wait AND a typed phrase before switching a blocker off. */
    private fun showGate(onConfirm: () -> Unit) {
        val phrase = getString(R.string.gate_phrase)
        val seconds = 30
        val input = EditText(this).apply {
            hint = phrase
            setSingleLine()
            setPadding(48, 32, 48, 32)
        }
        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.gate_title)
            .setMessage(getString(R.string.gate_message, phrase, seconds))
            .setView(input)
            .setPositiveButton(R.string.gate_confirm, null)
            .setNegativeButton(R.string.gate_cancel, null)
            .create()
        dialog.show()

        val ok = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        var secondsLeft = seconds
        var phraseOk = false
        fun update() {
            ok.isEnabled = secondsLeft <= 0 && phraseOk
            ok.text = if (secondsLeft > 0) getString(R.string.gate_wait, secondsLeft)
            else getString(R.string.gate_confirm)
        }
        update()

        val timer = object : CountDownTimer(seconds * 1000L, 1000L) {
            override fun onTick(ms: Long) { secondsLeft = ((ms + 999) / 1000).toInt(); update() }
            override fun onFinish() { secondsLeft = 0; update() }
        }.start()

        input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun afterTextChanged(s: Editable?) {
                phraseOk = s.toString().trim().equals(phrase, ignoreCase = true)
                update()
            }
        })
        ok.setOnClickListener { timer.cancel(); dialog.dismiss(); onConfirm() }
        dialog.setOnDismissListener { timer.cancel() }
    }

    /** Clear explanation shown BEFORE sending the user to Accessibility settings. */
    private fun showDisclosure() {
        AlertDialog.Builder(this)
            .setTitle(R.string.disclosure_title)
            .setMessage(R.string.disclosure_body)
            .setPositiveButton(R.string.disclosure_agree) { _, _ -> openAccessibilitySettings() }
            .setNegativeButton(R.string.disclosure_cancel, null)
            .show()
    }

    private fun addTile() {
        if (Build.VERSION.SDK_INT >= 33) {
            getSystemService(StatusBarManager::class.java).requestAddTileService(
                ComponentName(this, PauseTileService::class.java),
                getString(R.string.tile_label),
                Icon.createWithResource(this, R.drawable.ic_stat_shield),
                mainExecutor
            ) { /* result ignored */ }
        } else {
            Toast.makeText(this, R.string.tile_manual, Toast.LENGTH_LONG).show()
        }
    }

    private fun openAccessibilitySettings() {
        startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
    }

    private fun openUrl(res: Int) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(res))))
        } catch (_: Exception) { /* no browser installed */ }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
        }
    }

    override fun onRequestPermissionsResult(code: Int, perms: Array<out String>, results: IntArray) {
        super.onRequestPermissionsResult(code, perms, results)
        if (ServiceState.isEnabled(this) && results.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            StatusNotifier.showStatus(this)
        }
    }
}
