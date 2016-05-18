package com.tencent.mm.test;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.robotium.solo.Solo;
import com.tencent.mm.test.common.Constants;
import com.tencent.mm.test.common.Util;

import junit.framework.AssertionFailedError;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


@SuppressWarnings("rawtypes")
public class ViewAtricleInMessage extends ActivityInstrumentationTestCase2 {
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
    public ViewAtricleInMessage() throws ClassNotFoundException {
        super(launcherActivityClass);
    }

    public void setUp() throws Exception {
        super.setUp();
        Context context = getActivity();
        solo = new Solo(getInstrumentation());
        Util.wakeScreen(context);
        Util.unlock(context);
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
        super.tearDown();
    }

    public void testRun() {
        String[] urls = {"http://mp.weixin.qq.com/s?__biz=MzIzMTI0NzcxMg==&mid=402220245&idx=2&sn=bd2ca7cc178811dcf1d04ecffa2b0e2f"
                , "http://mp.weixin.qq.com/s?__biz=MzA5NjM0MDkyNw==&mid=404325872&idx=1&sn=ec6bf0a77b7f5a3f792ce7d3f047f8f1"
                , "http://mp.weixin.qq.com/s?__biz=MzAxNjgwMDg3Ng==&mid=2649552846&idx=1&sn=a856bcf68f01caf8eeb3a72d49dab0e8"
                , "http://mp.weixin.qq.com/s?__biz=MzA4MzI3OTUxMg==&mid=405769539&idx=1&sn=0575192d1879de28c4dea92fc3ab039d"
                , "http://mp.weixin.qq.com/s?__biz=MjM5NjQ2NjcyMg==&mid=403936314&idx=1&sn=03b06acfdbdc64677b8d825dc1ced229"
                , "http://mp.weixin.qq.com/s?__biz=MjM5MTk1OTMwMQ==&mid=404736154&idx=4&sn=2a76afcd25a6ed132b6c4a6c83997d1b"
                , "http://mp.weixin.qq.com/s?__biz=MjM5NjQ4MjYwMQ==&mid=417075534&idx=1&sn=4e4aea7c3b1353593c845b611717db15"
                , "http://mp.weixin.qq.com/s?__biz=MzA5MDE5MjYwMw==&mid=402522652&idx=3&sn=89b639a6e0caaf06d8b8cefdbc328f2c"
                , "http://mp.weixin.qq.com/s?__biz=MzA3MzM4MDQyMA==&mid=402773395&idx=2&sn=4be65d263c11ecbe54b3ccf7a459123e"
        };
        openChat("甜甜");
        for (int i = 0; i < urls.length; i++) {
            sendAndClickUrl(urls[i], i);
        }
        solo.sleep(30 * 1000);
    }

    private void openChat(String nickname) {
        long startTime = System.currentTimeMillis();
        solo.waitForActivity("LauncherUI", Constants.LONG_TIMEOUT);
//        solo.sleep(Constants.LONG_PAUSE);
        assertTrue("Text 通讯录 is not found!", solo.waitForText("通讯录", 1, 5000));
        solo.clickOnView((View) solo.getText("通讯录").getParent());
//        solo.sleep(Constants.SHORT_PAUSE);
//        textLoaded = solo.waitForText(nickname, 1, 2000, true);
//        Log.d(Constants.LOG_TAG, String.valueOf(nickname) + " textLoaded " + String.valueOf(textLoaded));
//        Log.d(Constants.LOG_TAG, "TextView#b3 text " + String.valueOf(((TextView) solo.getView("b3", 0)).getText()));

        assertTrue("ListView is not found!", solo.waitForView(ListView.class, 1, 2000));
        for (int i = 1; i < 100; i++) {
            try {
                View addressView = solo.getView("b4", i);
                String addressViewText = getAddressViewText(addressView);
                Log.d(Constants.LOG_TAG, "AddressView#b4 text " + String.valueOf(addressViewText));
                if (nickname.equals(addressViewText)) {
                    solo.clickOnView((View) addressView.getParent().getParent());
                    solo.sleep(Constants.SHORT_PAUSE);
                    assertTrue("ContactInfoUI is not found!", solo.waitForActivity("ContactInfoUI", Constants.LONG_TIMEOUT));
                    solo.sleep(Constants.SHORT_PAUSE);
                    solo.clickOnView(solo.getView("ahu"));
                    assertTrue("ChattingUI is not found!", solo.waitForActivity("ChattingUI", Constants.LONG_TIMEOUT));
                    solo.sleep(Constants.SHORT_PAUSE);
                    Log.d(Constants.LOG_TAG, "openChat spends " + ((float) (System.currentTimeMillis() - startTime) / 1000) + " s");
                    break;
                }
            } catch (AssertionFailedError e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private String getAddressViewText(View addressView) {
        CharSequence nickName = "";
        try {
            Method getNickNameMethod = addressView.getClass().getDeclaredMethod("getNickName");
            if (getNickNameMethod != null) {
                nickName = (CharSequence) getNickNameMethod.invoke(addressView);
                Log.d(Constants.LOG_TAG, "getAddressViewText " + String.valueOf(nickName));
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return nickName.toString();
    }

    private void sendAndClickUrl(String url, int pos) {
        long startTime = System.currentTimeMillis();
        solo.clearEditText((android.widget.EditText) solo.getView("c50"));
        solo.enterText((android.widget.EditText) solo.getView("c50"), url);
        solo.clickOnView(solo.getView("c55"));
        solo.sleep(Constants.SHORT_PAUSE);
        solo.clickOnView(solo.getView("be", pos > 3 ? 3 : pos));
        boolean webOpened = solo.waitForView(solo.getView("cd1"), 2000, false);
        Log.d(Constants.LOG_TAG, String.valueOf(url) + " webOpened " + String.valueOf(webOpened));
        solo.sleep(Constants.LONG_PAUSE);
        solo.clickOnView(solo.getView("cd1"));
        solo.sleep(Constants.SHORT_PAUSE);
        Log.d(Constants.LOG_TAG, "click " + String.valueOf(url) + " spends " + ((float) (System.currentTimeMillis() - startTime) / 1000) + " s");
    }
}
