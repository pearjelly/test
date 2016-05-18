package com.tencent.mm.test.common;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

/**
 * Created by xiaobinghan on 16/4/12.
 */
public class Util {


    /**
     * 唤醒设备的方法
     *
     * @param context
     * @return
     */
    public static WakeLock wakeScreen(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        WakeLock wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                        | PowerManager.FULL_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                Util.class.getSimpleName());
        wakeLock.acquire();
        return wakeLock;
    }

    /**
     * 解锁的方法
     *
     * @param context
     */
    public static void unlock(Context context) {
        try {
            KeyguardManager keyGuardManager = (KeyguardManager) context
                    .getSystemService(Context.KEYGUARD_SERVICE);
            KeyguardManager.KeyguardLock mLock = keyGuardManager.newKeyguardLock("");
            mLock.disableKeyguard();
        } catch (Throwable e) {
            Log.e(Constants.LOG_TAG, "unlock failed:", e);
        }
    }

}

