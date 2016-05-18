package com.tencent.mm.test;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;
import com.tencent.mm.test.common.Constants;
//Testing started at 上午12:17 ...
//
//04/11 00:17:46: Launching ViewAccountHistoryAr...
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
//$ adb shell am instrument -w -r   -e debug false -e class com.tencent.mm.test.ViewAccountHistoryArticles com.tencent.mm.test/android.test.InstrumentationTestRunner
//Client not ready yet..Waiting for process to come online
//Test running started

@SuppressWarnings("rawtypes")
public class ViewAccountHistoryArticles extends ActivityInstrumentationTestCase2 {
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

    @SuppressWarnings("unchecked")
    public ViewAccountHistoryArticles() throws ClassNotFoundException {
        super(launcherActivityClass);
    }

    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation());
        getActivity();
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
        super.tearDown();
    }

    public void testRun() {
        //Wait for activity: 'com.tencent.mm.ui.LauncherUI'
        solo.waitForActivity("LauncherUI", Constants.SHORT_TIMEOUT);
        //Sleep for 16013 milliseconds
        solo.sleep(Constants.LONG_PAUSE);
        solo.sleep(Constants.LONG_PAUSE);
        //Click on Empty Text View
        solo.clickOnView(solo.getView("0x1"));
        //Wait for activity: 'com.tencent.mm.plugin.search.ui.FTSMainUI'
        solo.waitForActivity("FTSMainUI", Constants.SHORT_TIMEOUT);
        //Sleep for 7284 milliseconds
        solo.sleep(Constants.LONG_PAUSE);
        //Click on ImageView
        solo.clickOnView(solo.getView(android.widget.ImageButton.class, 3));
        //Wait for activity: 'com.tencent.mm.plugin.profile.ui.ContactInfoUI'
        solo.waitForActivity("ContactInfoUI",Constants.SHORT_TIMEOUT);
        //Sleep for 2258 milliseconds
        solo.sleep(Constants.LONG_PAUSE);
        //Click on 查看历史消息  LinearLayout
        solo.clickInList(5, 0);
        //Sleep for 12123 milliseconds
        solo.sleep(12123);
//        //Click on LinearLayout
//        solo.clickOnView(solo.getView("c4d"));
//        //Wait for activity: 'com.tencent.mm.plugin.profile.ui.ContactInfoUI'
//        assertTrue("ContactInfoUI is not found!", solo.waitForActivity("ContactInfoUI"));
//        //Sleep for 1371 milliseconds
//        solo.sleep(1371);
//        //Click on 查看历史消息  LinearLayout
//        solo.clickInList(4, 0);
//        //Sleep for 13142 milliseconds
//        solo.sleep(13142);
//        //Click on LinearLayout
//        solo.clickOnView(solo.getView("c4d"));
    }
}
