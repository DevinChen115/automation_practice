package com.ileopard.cmqe.cm_productions;

import android.os.Build;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static java.lang.Thread.sleep;

/**
 * Created by User2 on 2016/2/17.
 */

@RunWith(AndroidJUnit4.class)
public class Applocker{
    private UiDevice mDevice;
    Util util;

    @Before
    public void setUp() throws RemoteException {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        util = new Util(mDevice);

    }


    @Test  //applock 初始化
    public void verify_initialize() throws RemoteException, InterruptedException {
        String tc = "AppLock 初始化驗證";
        mDevice.pressHome();
        util.launchAppInHomeScreen(Define.appLock);
        if (android.os.Build.VERSION.SDK_INT >= 23){
            sleep(1000);
            try {
                UiObject protectBTN = mDevice.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/applock_lock_recommended_btn"));
                protectBTN.click(); //開啟保護
                sleep(1500);
                mDevice.swipe(util.getSwipePwd(), 50);
                sleep(1000);
                mDevice.swipe(util.getSwipePwd(), 50);
                sleep(1500);
                UiObject Access_permission = mDevice.findObject(new UiSelector().resourceId("com.android.packageinstaller:id/permission_allow_button"));
                Access_permission.click(); //開啟聯絡人權限
                sleep(1000);
                protectBTN.click(); //開啟完成
                sleep(3000);
                Access_permission.click(); //開啟電話權限
                sleep(2000);
                UiObject Tab_Advanced = mDevice.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/applock_item_subname"));
                Tab_Advanced.click();
                sleep(1000);
                Access_permission.click(); //開啟相機權限
                mDevice.waitForIdle();
                Access_permission.click(); //開啟儲存權限
                mDevice.pressHome();
            } catch (UiObjectNotFoundException e) {
                Assert.assertTrue(tc+"失敗",false);
            }
        }
        else {
            sleep(1000);
            try{
                UiObject protectBTN = mDevice.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/applock_lock_recommended_btn"));
                protectBTN.click(); //開啟保護
                sleep(1500);
                mDevice.swipe(util.getSwipePwd(), 50);
                sleep(1000);
                mDevice.swipe(util.getSwipePwd(), 50);
                sleep(1500);
                if (mDevice.findObject(new UiSelector().enabled(true))){
                    UiObject protectComplete = mDevice.findObject((new UiSelector().resourceId("com.cleanmaster.applock:id/btn_finish")));
                    sleep(3000);
                    mDevice.pressHome();
                }
                else {
                    protectBTN.click(); //開啟完成
                    sleep(3000);
                    mDevice.pressHome();
                }
            } catch (UiObjectNotFoundException e) {
                Assert.assertTrue(tc+"失敗",false);
            }

        }
    }


    //  @Test //applock TC 1-19 Patrick
    public void verifyIntruderselfie() throws RemoteException, InterruptedException {
        String tc = "applock_1-19 首次解鎖應用，輸錯一次密碼就會拍照，成功解鎖進入後出現拍下入侵者照片";
    //    util.resetAppLocke();
    //    mDevice.waitForIdle();
    //    util.launchAppInHomeScreen(Define.appLock);
        util.launchAppInHomeScreen(Define.app_line);
        sleep(2000);
        mDevice.swipe(util.getWrongSwipePwdV2(), 40);
        sleep(1000);
        mDevice.swipe(util.getSwipePwdV2(), 40);
        sleep(1000);

        sleep(3000);
        try {
            UiObject successwindow = mDevice.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/btn_ok"));
            successwindow.click();
            UiObject checkPhoto = mDevice.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/item_image"));
            UiObject confirm = mDevice.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/photo_btn_cancel"));
            confirm.click();
            mDevice.waitForIdle();
        } catch (UiObjectNotFoundException e) {
            Assert.assertTrue(tc+"失敗",false);
        }

    }

   //@Test
    public void verifyInputWrongPwdNeedToWaitTenSec() throws RemoteException {
        mDevice.wakeUp();
        mDevice.swipe(550, 1770, 550, 1280, 30);
        mDevice.waitForIdle();
        util.launchAppInHomeScreen(Define.appLock);
        //launchAppInHTCM8("CM AppLock");
        for (int i =3;i>0;i--){
            util.unLockBySwipe(false);
            UiObject subTitleObj = mDevice.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/lockpattern_subtitle"));
            try {
                if(i>1){
                    String subTitle = subTitleObj.getText();
                    Assert.assertEquals("輸入錯誤（還可以嘗試"+ Integer.toString(i-1) +"次）",subTitle);
                }
            } catch (UiObjectNotFoundException e) {
                Assert.assertTrue("輸入錯誤密碼時Exception", false);
            }

        }

        UiObject verifyText = mDevice.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/lock_count_time_text_tip2"));
        try {
            String verifyString = verifyText.getText();
            Assert.assertEquals("禁止輸入！",verifyString );
            try {
                sleep(10000);
            } catch (InterruptedException e) {
                Assert.assertTrue("等十秒時Exception",false);
            }
            //UiObject obj = mDevice.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/lockpattern_title"));
        } catch (UiObjectNotFoundException e) {
            Assert.assertTrue("輸入錯誤三次, 沒有等待十秒", false);
        }
    }

   //@Test
    public void verifyDetectLaunchApp() throws RemoteException, UiObjectNotFoundException {
        util.launchAppInHTCM8(Define.app_youtube);
        UiObject logo = mDevice.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/cms_logo"));
        UiObject lockPattern = mDevice.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/applock_pattern_layout"));
        Assert.assertTrue("App Lock Icon 不存在", logo.exists());
        Assert.assertTrue("圖形鎖沒有顯示", lockPattern.exists());
    }

   // @Test
    public void verifyShootAfterInputWrongPwd() throws UiObjectNotFoundException {
        util.setLockAppFrequency(Define.freq_everytime);
        util.setShootFrequency(1);
        util.launchAppInHomeScreen("Facebook");
        mDevice.swipe(util.getWrongSwipePwdV2(), 40);
        mDevice.swipe(util.getSwipePwdV2(), 40);
        UiObject checkTitle = mDevice.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/photo_title"));
        Assert.assertEquals("這個人想偷看你的 Facebook ", checkTitle.getText());
        UiObject checkPhoto = mDevice.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/item_image"));
        Assert.assertTrue("沒有拍照",checkPhoto.exists());
    }

   // @Test
    public void verifyGoToLockSetting() {
        String tc = "applock_1-11 點擊解鎖畫面\"上鎖設定\"選項進入上鎖設定頁面";
        try {
            util.goToAppLockSetting();
            UiObject lockSeting = mDevice.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/setting_temp_unlock_layout"));
            lockSeting.click();
            mDevice.waitForIdle();
        } catch (UiObjectNotFoundException e) {
            Assert.assertTrue(tc+"失敗",false);
        }
    }

    //@Test
    public void verifyLockFreq_3() throws InterruptedException {
        String result;
        String tc ="applock_1-14 選擇每次打開app都上鎖，解鎖離開app後再開啟App，確認App會成功上鎖";
        UiObject applookTitleBar;
        try {
            util.setLockAppFrequency(Define.freq_everytime);
            for(int i=0; i<2; i++){
                util.launchAppInHTCM8(Define.app_youtube);
                //util.launchAppInHomeScreen(Define.app_youtube);
                applookTitleBar = mDevice.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/applock_up_layout"));
                if(applookTitleBar.exists()){
                    mDevice.swipe(util.getSwipePwdV2(), 40);
                }else {

                    result = Define.app_youtube+"似乎沒有上鎖喲!!"
                            + System.getProperty("line.separator")
                            + tc;
                    Assert.assertTrue(result, false);
                }
            }
           //mDevice.waitForWindowUpdate(Define.pkgName_youtube,1000);
            sleep(1000);
            UiObject appTitle = mDevice.findObject(new UiSelector().resourceId("com.google.android.youtube:id/view_pager"));
            Boolean isExist = appTitle.exists();
            Assert.assertTrue(tc, isExist);
        } catch (UiObjectNotFoundException e){
            Assert.assertTrue(e.toString(), false);
        }
    }


    @Test
    public void verifyUnLockAllApp() {
        String tc = "applock_1-15 選擇解鎖全部App，確認解鎖任一app一次後上鎖的App全部解鎖";
        String rId = "com.cleanmaster.applock:id/applock_pattern_layout";
        try {
            util.setLockAppFrequency(Define.freq_screenLock);
            util.setUnLockAllApp(true);
            util.launchAppInHomeScreen(Define.app_youtube);
            sleep(1000);
            if(util.checkViewByResourceId(rId)){
                mDevice.swipe(util.getSwipePwdV2(),40);
            }else {
                Assert.assertTrue(tc+" 第一次開app",false);
            }
            util.launchAppInHomeScreen(Define.app_facebook);
            if(util.checkViewByResourceId(rId)){
                Assert.assertTrue(tc+" 第二次開app還要打密碼",false);
            }else {
                Assert.assertTrue(true);
            }
        } catch (UiObjectNotFoundException e) {
            Assert.assertTrue(e.toString(),false);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
