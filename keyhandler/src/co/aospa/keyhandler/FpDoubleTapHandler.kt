/*
 * Copyright (C) 2023 Paranoid Android
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.aospa.keyhandler

import android.app.SearchManager
import android.app.StatusBarManager
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.os.SystemClock
import android.os.UserHandle
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.MediaStore
import android.provider.Settings
import android.view.KeyEvent
import android.view.WindowManager.ScreenshotSource.SCREENSHOT_VENDOR_GESTURE
import android.util.Log

import com.android.internal.app.AssistUtils
import com.android.internal.util.ScreenshotHelper

class FpDoubleTapHandler(
    private val context: Context
) {
    private val audioManager = context.getSystemService(AudioManager::class.java)
    private val cameraManager = context.getSystemService(CameraManager::class.java)
    private val powerManager = context.getSystemService(PowerManager::class.java)
    private val statusBarManager = context.getSystemService(StatusBarManager::class.java)
    private val vibrator = context.getSystemService(Vibrator::class.java)

    private val handler = Handler(Looper.getMainLooper())
    private val screenshotHelper = ScreenshotHelper(context)

    private val vibrationEffect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
    private val vibrationAttrs =
            VibrationAttributes.createForUsage(VibrationAttributes.USAGE_HARDWARE_FEEDBACK)

    private val isFpDoubleTapEnabled: Boolean
        get() =
            Settings.System.getIntForUser(context.contentResolver, SETTING_KEY_ENABLE,
                0, UserHandle.USER_CURRENT) == 1

    private val fpDoubleTapAction: Int
        get() =
            Settings.System.getIntForUser(context.contentResolver, SETTING_KEY_ACTION,
                0, UserHandle.USER_CURRENT)

    private var torchOn = false

    init {
        cameraManager.registerTorchCallback(
            object: CameraManager.TorchCallback() {
                override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                    if (cameraId == REAR_CAMERA_ID) {
                        torchOn = enabled
                    }
                }
            },
            handler
        )
    }

    fun handleEvent(event: KeyEvent) {
        val enabled = isFpDoubleTapEnabled
        val action = fpDoubleTapAction
        val interactive = powerManager.isInteractive() // TODO: support screen off?
        Log.d(TAG, "handleEvent: enabled=$enabled action=$action interactive=$interactive")
        if (!enabled || !interactive || event.action != KeyEvent.ACTION_UP) {
            Log.d(TAG, "wont handle")
            return
        }
        when (action) {
            1 -> takeScreenshot()
            2 -> launchAssist(event.eventTime)
            3 -> playPauseMedia()
            4 -> showNotifications()
            5 -> launchCamera()
            6 -> toggleFlashlight()
            7 -> toggleRingerMode(AudioManager.RINGER_MODE_SILENT)
            8 -> toggleRingerMode(AudioManager.RINGER_MODE_VIBRATE)
            9 -> showVolumePanel()
            10 -> goToSleep()
            else -> Log.e(TAG, "unsupported action: $action")
        }
    }

    private fun takeScreenshot() {
        Log.d(TAG, "takeScreenshot")
        screenshotHelper.takeScreenshot(SCREENSHOT_VENDOR_GESTURE, handler, null)
    }

    private fun launchAssist(eventTime: Long) {
        Log.d(TAG, "launchAssist: eventTime=$eventTime")
        val searchManager = context.getSystemService(SearchManager::class.java)
        if (searchManager == null) {
            Log.d(TAG, "launchAssist: searchManager is null!")
            return
        }
        vibrate()
        val args = Bundle()
        args.putLong(Intent.EXTRA_TIME, eventTime)
        args.putInt(AssistUtils.INVOCATION_TYPE_KEY, AssistUtils.INVOCATION_TYPE_PHYSICAL_GESTURE)
        searchManager.launchAssist(args)
    }

    private fun playPauseMedia() {
        Log.d(TAG, "playPauseMedia")
        vibrate()
        val time = SystemClock.uptimeMillis()
        var event = KeyEvent(time, time, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0)
        audioManager.dispatchMediaKeyEvent(event)
        event = KeyEvent.changeAction(event, KeyEvent.ACTION_UP)
        audioManager.dispatchMediaKeyEvent(event)
    }

    private fun showNotifications() {
        Log.d(TAG, "showNotifications")
        statusBarManager.expandNotificationsPanel()
    }

    private fun launchCamera() {
        Log.d(TAG, "launchCamera")
        vibrate()
        context.startActivity(
            Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    private fun toggleFlashlight() {
        Log.d(TAG, "toggleFlashlight: torchOn=$torchOn")
        vibrate()
        cameraManager.setTorchMode(REAR_CAMERA_ID, !torchOn)
    }

    private fun toggleRingerMode(ringerMode: Int) {
        val currentMode = audioManager.getRingerModeInternal()
        Log.d(TAG, "toggleRingerMode: $ringerMode currentMode=$currentMode")
        vibrate()
        audioManager.setRingerModeInternal(
            if (currentMode != ringerMode) ringerMode else AudioManager.RINGER_MODE_NORMAL
        )
    }

    private fun showVolumePanel() {
        Log.d(TAG, "showVolumePanel")
        audioManager.adjustVolume(AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI)
    }

    private fun goToSleep() {
        Log.d(TAG, "goToSleep")
        vibrate()
        powerManager.goToSleep(SystemClock.uptimeMillis())
    }

    private fun vibrate() {
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(vibrationEffect, vibrationAttrs)
        }
    }

    companion object {
        private const val TAG = "FpDoubleTapHandler"
        private const val REAR_CAMERA_ID = "0"
        private const val SETTING_KEY_ENABLE = "fp_double_tap_enable"
        private const val SETTING_KEY_ACTION = "fp_double_tap_action"
    }
}
