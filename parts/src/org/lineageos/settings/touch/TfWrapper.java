/*
 * Copyright (C) 2023 Paranoid Android
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.settings.touch;

import android.os.IHwBinder.DeathRecipient;
import android.util.Log;

import vendor.xiaomi.hw.touchfeature.V1_0.ITouchFeature;

public class TfWrapper {

    private static final String TAG = "TouchFeatureWrapper";

    private static ITouchFeature mTouchFeature;

    private static DeathRecipient mDeathRecipient = (cookie) -> {
        Log.d(TAG, "serviceDied");
        mTouchFeature = null;
    };

    public static ITouchFeature getITouchFeature() {
        if (mTouchFeature == null) {
            Log.d(TAG, "getITouchFeature: mTouchFeature=null");
            try {
                mTouchFeature = ITouchFeature.getService();
                mTouchFeature.asBinder().linkToDeath(mDeathRecipient, 0);
            } catch (Exception e) {
                Log.e(TAG, "getITouchFeature failed!", e);
            }
        }
        return mTouchFeature;
    }

    public static void setModeValue(int mode, int value) {
        final ITouchFeature touchFeature = getITouchFeature();
        if (touchFeature == null) {
            Log.e(TAG, "setModeValue: touchFeature is null!");
            return;
        }
        Log.d(TAG, "set mode=" + mode + " value=" + value);
        try {
            touchFeature.setModeValue(0, mode, value);
        } catch (Exception e) {
            Log.e(TAG, "setModeValue failed!", e);
        }
    }

}
