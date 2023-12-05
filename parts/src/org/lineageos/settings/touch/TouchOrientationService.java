/*
 * Copyright (C) 2023 Paranoid Android
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.settings.touch;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.os.UserHandle;
import android.util.Log;

public class TouchOrientationService extends Service {

    private static final String TAG = "TouchOrientationService";
    private static final boolean DEBUG = true;

    // from kernel drivers/input/touchscreen/xiaomi/xiaomi_touch.h
    private static final int MODE_TOUCH_PANEL_ORIENTATION = 8;

    public static void startService(Context context) {
        context.startServiceAsUser(new Intent(context, TouchOrientationService.class),
                UserHandle.CURRENT);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        dlog("onStartCommand");
        updateOrientation();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        dlog("onDestroy");
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        dlog("onConfigurationChanged");
        updateOrientation();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void updateOrientation() {
        final int rotation = getDisplay().getRotation();
        dlog("updateTpOrientation: rotation=" + rotation);

        // Lucky for us, Surface.ROTATION_* directly translates into touchpanel values
        TfWrapper.setModeValue(MODE_TOUCH_PANEL_ORIENTATION, rotation);
    }

    private static void dlog(String msg) {
        if (DEBUG) Log.d(TAG, msg);
    }

}
