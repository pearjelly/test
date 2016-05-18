package com.tencent.mm.test;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;

import com.robotium.solo.By;
import com.robotium.solo.Solo;
import com.tencent.mm.test.common.Constants;


@SuppressWarnings("rawtypes")
public class ViewAccountHistroyAtricles2 extends ActivityInstrumentationTestCase2 {
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
    public ViewAccountHistroyAtricles2() throws ClassNotFoundException {
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
        solo.waitForActivity("LauncherUI", Constants.LONG_TIMEOUT);
        //Sleep for 21501 milliseconds
        solo.sleep(Constants.LONG_PAUSE);
        solo.sleep(Constants.LONG_PAUSE);
        solo.clickOnView( solo.getText("通讯录"));
        View searchView = solo.getView("0x2");
        Log.d(Constants.LOG_TAG, "searchView:" + String.valueOf(searchView));
        //Click on Empty Text View
        solo.clickOnView(solo.getView(android.widget.TextView.class, 23));
        //Wait for activity: 'com.tencent.mm.plugin.search.ui.FTSMainUI'
        assertTrue("FTSMainUI is not found!", solo.waitForActivity("FTSMainUI", Constants.LONG_TIMEOUT));
        //Sleep for 18826 milliseconds
        solo.sleep(Constants.SHORT_PAUSE);
        //Enter the text: 'wonderfulpocket'
        solo.clearEditText((android.widget.EditText) solo.getView("a6t"));
        solo.enterText((android.widget.EditText) solo.getView("a6t"), "WonderfulPocket");
        //Sleep for 4616 milliseconds
        solo.sleep(Constants.SHORT_PAUSE);
        //Click on 搜一搜  wonderfulpocket 朋友圈 公众号 文章等
        solo.clickOnView(solo.getView("c0z"));
        By by = By.textContent("微信号");
        Log.d(Constants.LOG_TAG, "webview search " + String.valueOf(solo.searchText("微信号")) + " by:" + String.valueOf(by));
        solo.sleep(Constants.LONG_TIMEOUT);
        solo.takeScreenshot("search_result");
        solo.sleep(Constants.SHORT_PAUSE);

        solo.clickOnWebElement(by);

        //Wait for activity: 'com.tencent.mm.plugin.profile.ui.ContactInfoUI'
        assertTrue("ContactInfoUI is not found!", solo.waitForActivity("ContactInfoUI", Constants.LONG_TIMEOUT));
        solo.takeScreenshot("contact");
        //Sleep for 10266 milliseconds
        solo.sleep(Constants.SHORT_PAUSE);
        //Click on 查看历史消息  LinearLayout
        solo.clickInList(4, 0);
        solo.sleep(Constants.LONG_TIMEOUT);
        solo.takeScreenshot("history");
        solo.sleep(60 * 1000);
    }
}
