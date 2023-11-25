/*
 * Copyright (C) 2023 Paranoid Android
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.settings.touch;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;

import org.lineageos.settings.utils.FileUtils;

public class HighTouchPollingService extends Service {

    private static final String TAG = "HighTouchPollingService";
    private static final boolean DEBUG = true;

    private static final String SETTING_KEY = "touch_polling_enabled";
    private static final String TS_NODE = "/sys/devices/platform/goodix_ts.0/goodix_ts_report_rate";

    private boolean mEnabled;
    private boolean mScreenOn = true;
    private PowerManager mPowerManager;

    private final ContentObserver mSettingObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            dlog("SettingObserver: onChange");
            writeCurrentValue(true);
        }
    };

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            dlog("onReceive: " + intent.getAction());
            switch (intent.getAction()) {
                case Intent.ACTION_SCREEN_ON:
                    mScreenOn = true;
                    // fallthrough
                case PowerManager.ACTION_POWER_SAVE_MODE_CHANGED:
                    writeCurrentValue(false);
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    mScreenOn = false;
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        dlog("onCreate");
        mPowerManager = getSystemService(PowerManager.class);
        getContentResolver().registerContentObserver(Settings.Secure.getUriFor(SETTING_KEY),
                    false, mSettingObserver);
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED);
        registerReceiver(mIntentReceiver, filter);
        writeCurrentValue(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        dlog("onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        dlog("onDestroy");
        getContentResolver().unregisterContentObserver(mSettingObserver);
        unregisterReceiver(mIntentReceiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void startService(Context context) {
        context.startServiceAsUser(new Intent(context, HighTouchPollingService.class),
                UserHandle.CURRENT);
    }

    private void writeCurrentValue(boolean readSetting) {
        if (readSetting)
            mEnabled = Settings.Secure.getInt(getContentResolver(), SETTING_KEY, 0) == 1;

        final boolean isPowerSave = mPowerManager.isPowerSaveMode();
        dlog("writeCurrentValue: mEnabled=" + mEnabled + " mScreenOn=" + mScreenOn
                + " isPowerSave=" + isPowerSave);

        if (mScreenOn)
            FileUtils.writeLine(TS_NODE, mEnabled && !isPowerSave ? "1" : "0");
    }

    private static void dlog(String msg) {
        if (DEBUG) Log.d(TAG, msg);
    }
    
}
