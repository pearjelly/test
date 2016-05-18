package com.tencent.mm.test;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.robotium.solo.Solo;
import com.tencent.mm.test.common.Constants;
import com.tencent.mm.test.common.Util;
//Testing started at 上午12:02 ...
//
//04/11 00:02:10: Launching LoginWithMobile
//$ adb push /Users/xiaobinghan/workspace/RobotiumCase/MMApplicationTest/build/outputs/apk/MMApplicationTest-debug.apk /data/local/tmp/com.tencent.mm
//$ adb shell pm install -r "/data/local/tmp/com.tencent.mm"
//pkg: /data/local/tmp/com.tencent.mm
//Success
//
//
//$ adb push /Users/xiaobinghan/workspace/RobotiumCase/MMApplicationTest/build/outputs/apk/MMApplicationTest-debug-androidTest-unaligned.apk /data/local/tmp/com.tencent.mm.test
//$ adb shell pm install -r "/data/local/tmp/com.tencent.mm.test"
//pkg: /data/local/tmp/com.tencent.mm.test
//Success
//
//
//Running tests
//
//$ adb shell am instrument -w -r   -e debug false -e class com.tencent.mm.test.LoginWithMobile com.tencent.mm.test/android.test.InstrumentationTestRunner
//Client not ready yet..Waiting for process to come online
//Test running started
//Timed out waiting for process to appear on samsung-gt_i9300-4d00cd64b490a0e1

@SuppressWarnings("rawtypes")
public class LoginWithMobile extends ActivityInstrumentationTestCase2 {
    private Solo solo;

    private static final String LAUNCHER_ACTIVITY_FULL_CLASSNAME = "com.tencent.mm.ui.LauncherUI";

    private static Class<?> launcherActivityClass;

    static {
        try {
            launcherActivityClass = Class.forName(LAUNCHER_ACTIVITY_FULL_CLASSNAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private String mobile;
    private String password;

    @SuppressWarnings("unchecked")
    public LoginWithMobile() throws ClassNotFoundException {
        super(launcherActivityClass);
    }

    public void setUp() throws Exception {
        super.setUp();
        Context context = getActivity();
        solo = new Solo(getInstrumentation());
        Util.wakeScreen(context);
//        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        Log.d(Constants.LOG_TAG, "setUp getActivity():" + String.valueOf(getActivity()));
        Util.unlock(context);
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(Uri.parse("content://me.pearjelly.wxrobot.data.RobotContentProvider/account"), null, null, null, "id desc");
        Log.d(Constants.LOG_TAG, "query cursor:" + String.valueOf(cursor));
        if (cursor != null && cursor.moveToNext()) {
            mobile = cursor.getString(cursor.getColumnIndex("mobile"));
            password = cursor.getString(cursor.getColumnIndex("password"));
            Log.d(Constants.LOG_TAG, "query mobile:" + String.valueOf(mobile));
            cursor.close();
        }
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
        super.tearDown();
    }

    public void testRun() {

        solo.waitForActivity("LauncherUI", Constants.SHORT_TIMEOUT);
        solo.sleep(Constants.LONG_PAUSE);
        solo.clickOnView(solo.getView("al7"));
        if (TextUtils.isEmpty(mobile) || TextUtils.isEmpty(password)) {
            Log.e(Constants.LOG_TAG, "no mobile or password");
        } else if (solo.waitForActivity("MobileInputUI", Constants.SHORT_TIMEOUT)) {
            solo.clearEditText((android.widget.EditText) solo.getView("cdp"));
            solo.enterText((android.widget.EditText) solo.getView("cdp"), mobile);
            solo.clearEditText((android.widget.EditText) solo.getView("a6t"));
            solo.enterText((android.widget.EditText) solo.getView("a6t"), password);
            solo.sleep(Constants.SHORT_PAUSE);
            solo.clickOnView(solo.getView("aje"));
            View view = solo.getView("bm1");
            if (solo.waitForView(view, Constants.SHORT_TIMEOUT, false)) {
                solo.clickOnView(view);
                solo.sleep(Constants.LONG_PAUSE);
            } else {
                solo.sleep(Constants.LONG_TIMEOUT);
            }
            solo.sleep(Constants.LONG_TIMEOUT);
        } else {
            Log.e(Constants.LOG_TAG, "no mobile input ui");
        }
    }
}
