/*
 * Copyright (C) 2023 Paranoid Android
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lineageos.settings.gestures;

import android.content.Context;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;

import vendor.xiaomi.hardware.fingerprintextension.V1_0.IXiaomiFingerprint;

public class GestureUtils {

    private static final String TAG = "GestureUtils";
    static final String SETTING_KEY_ENABLE = "fp_double_tap_enable";
    static final String SETTING_KEY_ACTION = "fp_double_tap_action";

    private static final int FINGERPRINT_CMD_LOCKOUT_MODE = 12;
    private static final int POWERFP_DISABLE_NAVIGATION = 0;
    private static final int POWERFP_ENABLE_NAVIGATION = 2;

    public static void setFingerprintNavigation(boolean enable) {
        try {
            IXiaomiFingerprint xiaomiFp = IXiaomiFingerprint.getService();
            xiaomiFp.extCmd(FINGERPRINT_CMD_LOCKOUT_MODE,
                    enable ? POWERFP_ENABLE_NAVIGATION : POWERFP_DISABLE_NAVIGATION);
            Log.d(TAG, "setFingerprintNavigation: " + enable);
        } catch (Exception e) {
            Log.e(TAG, "setFingerprintNavigation failed: ", e);
        }
    }

    public static boolean isFpDoubleTapEnabled(Context context) {
        return Settings.System.getIntForUser(context.getContentResolver(), SETTING_KEY_ENABLE,
                0, UserHandle.USER_CURRENT) == 1;
    }

    public static int getFpDoubleTapAction(Context context) {
        return Settings.System.getIntForUser(context.getContentResolver(), SETTING_KEY_ACTION,
                1, UserHandle.USER_CURRENT);
    }

}
