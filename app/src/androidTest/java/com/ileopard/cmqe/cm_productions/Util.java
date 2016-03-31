package com.ileopard.cmqe.cm_productions;

import android.graphics.Point;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiCollection;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;

import junit.framework.Assert;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import static java.lang.Thread.sleep;

/**
 * Created by User2 on 2016/2/26.
 */
public class Util {
     UiDevice device;
    public String pwdTypeNow;  //目前AppLock鎖的型態 (swipe or digital)

    public Util(UiDevice mDevice) {
        device = mDevice;
    }

    /**
     * 執行桌面上的App
     * //@param device 測項中抓到的Device
     * @param  appName 要Launch 的 app 名稱
     * */
    public  void launchAppInHomeScreen(String appName) {
        device.pressHome();
        UiObject appObj = device.findObject(new UiSelector().text(appName));
        try {
            appObj.click();
            device.waitForIdle();
        } catch (UiObjectNotFoundException e) {
            Assert.assertTrue("Launch " + appName + " Fail", false);
        }
    }

    public  void resetAppLocke() {
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("pm clear com.cleanmaster.applock");
    }

    public  void launchApp(String appName){
        goToAppsList();
        try{
            scrollAndClickByText(appName);
        }catch (UiObjectNotFoundException e){
            Assert.assertTrue("Launch AppLock 失敗, Exception:  找不到 " + appName,false);
        }
    }

    public  Boolean launchAppInHTCM8(String appName) throws UiObjectNotFoundException{
        goToAppsList();
        UiObject appBefore, appAfter, launchApp;
        String checkBefore, checkAfter;
//確定可以到第一頁
        do{
            UiObject appList = device.findObject(new UiSelector().resourceId("com.htc.launcher:id/all_apps_paged_view"));
            appBefore = appList.getChild(new UiSelector().index(0)).getChild(new UiSelector().index(0)).getChild(new UiSelector().index(0));
            checkBefore = appBefore.getContentDescription();
            device.swipe(700,600,700,1500,40);
            appAfter = appList.getChild(new UiSelector().index(0)).getChild(new UiSelector().index(0)).getChild(new UiSelector().index(0));
            checkAfter = appAfter.getContentDescription();
        }while(!checkAfter.equals(checkBefore));

        do{
            launchApp = device.findObject(new UiSelector().text(appName));
            try{
                if(launchApp.exists()){
                    launchApp.click();
                    device.waitForIdle();
                    return true;
                }else {
                    UiObject appList = device.findObject(new UiSelector().resourceId("com.htc.launcher:id/all_apps_paged_view"));
                    appBefore = appList.getChild(new UiSelector().index(0)).getChild(new UiSelector().index(0)).getChild(new UiSelector().index(0));
                    checkBefore = appBefore.getContentDescription();
                    device.swipe(700,1500,700,600,40);
                    appAfter = appList.getChild(new UiSelector().index(0)).getChild(new UiSelector().index(0)).getChild(new UiSelector().index(0));
                    checkAfter = appAfter.getContentDescription();
                }
            } catch (UiObjectNotFoundException e){
                e.printStackTrace();
            }
        } while (!checkAfter.equals(checkBefore));
        throw new UiObjectNotFoundException("Exception: 找不到 " + appName);
    }
    

    public  void goToAppsList() {
        device.pressHome();
        //scrollAndClickByDesc("所有應用程式");
        UiObject allApps = device.findObject(new UiSelector().description("所有應用程式"));
        try {
            allApps.click();
            device.waitForIdle();
        } catch (UiObjectNotFoundException e){
            Assert.assertTrue("Exception: 找不到所有應用程式 ",false);
        }
    }

    public  void goToAppLockSetting() {
        //goToAppsList();
        //launchAppInHTCM8(Define.appLock);
        try {
            goToAppLockHome();
            UiObject mainAction = device.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/main_title_btn_right"));
            mainAction.click();
            device.waitForIdle();
            UiObject setting = device.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/applock_menu_item_settings"));
            setting.click();
            device.waitForIdle();
        } catch (UiObjectNotFoundException e) {
            Assert.assertTrue(e.toString(),false);
        }
    }


    public  void setLockAppFrequency(String frequency) throws UiObjectNotFoundException{
        String lockFrequency=null;
        switch (frequency){
            case "screenLock":
                lockFrequency="com.cleanmaster.applock:id/remember_me_session";
                break;
            case "3min":
                lockFrequency="com.cleanmaster.applock:id/remember_me_5min";
                break;
            case "everyTime":
                lockFrequency="com.cleanmaster.applock:id/remember_me_disable";
                break;
            default:
                Assert.assertTrue("frequency are: [ screenLock | 3min | everyTime ]",false);
                break;
        }
        goToAppLockSetting();
        try{
            UiObject lockSeting = device.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/setting_temp_unlock_layout"));
            lockSeting.click();
            device.waitForIdle();
            UiObject lockFrequencyObj = device.findObject(new UiSelector().resourceId(lockFrequency));
            lockFrequencyObj.click();
            device.waitForIdle();
        } catch (UiObjectNotFoundException e){
            Assert.assertTrue("沒有 "+frequency+" 這個選項",false);
        }
    }
    //輸錯多少次就拍照的switch case
    public  void setShootFrequency(int times) throws UiObjectNotFoundException{
        switch (times){
            case 1:
                SetAndClickFrequency(0); //Set 1次
                break;
            case 2:
                SetAndClickFrequency(1); //Set 2次
                break;
            case 3:
                SetAndClickFrequency(2); //Set 3次
                break;
            case 5:
                SetAndClickFrequency(3); //Set 5次
                break;
            default:
                Assert.assertTrue("Method_setShootFrequency: only 1 2 3 5 can use",false);
                break;
        }
    }
    //輸錯多少次就拍照的設定點擊
    public void SetAndClickFrequency(int idx) throws UiObjectNotFoundException{
        goToAppLockSetting();
        UiObject shootBadGuy = device.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/setting_intruder_selfie"));
        shootBadGuy.click();
        device.waitForIdle();
        UiObject shootTimes = device.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/setting_intruder_selfie_counter"));
        shootTimes.click();
        device.waitForIdle();
        if(idx!=1) {
            UiObject selectTimes = device.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/listView")).getChild(new UiSelector().index(idx));
            selectTimes.click();}
        else
        {
            device.click((int)Math.round(device.getDisplayWidth() * 0.5),(int)Math.round(device.getDisplayHeight()*0.52));
        }

        device.waitForIdle();
    }

    public  void scrollAndClickByDesc(String desc) throws UiObjectNotFoundException {
        boolean haveSetting = new UiScrollable(new UiSelector().scrollable(true)).scrollDescriptionIntoView(desc);
        if(haveSetting){
            UiObject setting = device.findObject(new UiSelector().description(desc));
            setting.click();
            device.waitForIdle();
        } else {

            throw new UiObjectNotFoundException("Exception: 找不到 " + desc);
        }
    }

    public  void scrollAndClickByText(String text) throws UiObjectNotFoundException {
        boolean haveSetting = new UiScrollable(new UiSelector().scrollable(true)).scrollTextIntoView(text);
        if(haveSetting){
            UiObject setting = device.findObject(new UiSelector().text(text));
            setting.click();
            device.waitForIdle();
        } else {
            throw new UiObjectNotFoundException("Exception: 找不到 " + text);
        }
    }

    public  void scrollAndClickByResourceId(String resourceID) throws UiObjectNotFoundException {
        boolean haveSetting = new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().resourceId(resourceID));
        if(haveSetting){
            UiObject setting = device.findObject(new UiSelector().resourceId(resourceID));
            setting.click();
            device.waitForIdle();
        } else {
            throw new UiObjectNotFoundException("Exception: 找不到 " + resourceID);
        }
    }

    //拍照截圖並儲存於/sdcard/Pictures/資料夾下
    public void DoScreenShot(String resource){
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("screencap -p /sdcard/Pictures/"+resource+".png");
    }

    public  UiObject scrollAndGetUiObjByResourceId(String resourceID) throws UiObjectNotFoundException {
        UiObject result;
        boolean haveData = new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().resourceId(resourceID));
        if(haveData){
            result = device.findObject(new UiSelector().resourceId(resourceID));
        } else {
            throw new UiObjectNotFoundException("Exception: 找不到 " + resourceID);
        }
        return result;
    }
    /**
     * 是否要解鎖
     * @param status  true 就是要解鎖
     * */
    public  void unLockAppLockBySwipe(boolean status) {
        if(status){
            device.swipe(getAppLockSwipePwd(), 50);
        }else {
            device.swipe(getAppLockWrongSwipePwd(),50);
        }
        device.waitForIdle();
    }

    public void unLockAppLockForAppsBySwipe(boolean status){
        if(status){
            device.swipe(getAppLockForAppsSwipePwd(),50);
        }else {
            device.swipe(getAppLockForAppsWrongSwipePwd(),50);
        }
        device.waitForIdle();
    }

    public void unLockAppLockByDigital(boolean status) throws UiObjectNotFoundException{
        if(status){
            // default pwd 88888
            for(int i=0;i<5;i++){
                UiObject pressKeyCode = device.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/applock_keypad_8_text"));
                pressKeyCode.click();
                device.waitForIdle();
            }
        }else {
            // other pwd 00000
            for(int i=0;i<5;i++){
                UiObject pressKeyCode = device.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/applock_keypad_0_text"));
                pressKeyCode.click();
                device.waitForIdle();
            }
        }
    }

    /**
     * 解鎖 AppLock
     * @param pwdType 搭配 getAppLockPwdType 來決定要用什麼 type 來解鎖
     * @param status  boolean, true 就是正確pwd
    * */
    public void unLockAppLock(String pwdType, boolean status)  throws UiObjectNotFoundException{
        if(pwdType.equals(Define.pwdType_Digital)){
            unLockAppLockByDigital(status);
        }
        if(pwdType.equals(Define.pwdType_Swipe)){
            unLockAppLockBySwipe(status);
        }
    }
    /**
     * APP上鎖頁面下解鎖 AppLock
     * @param pwdType 搭配 getAppLockPwdType 來決定要用什麼 type 來解鎖
     * @param status  boolean, true 就是正確pwd
     * */
    public void unLockAppLockForApps(String pwdType, boolean status)  throws UiObjectNotFoundException{
        if(pwdType.equals(Define.pwdType_Digital)){
            unLockAppLockByDigital(status);
        }
        if(pwdType.equals(Define.pwdType_Swipe)){
            unLockAppLockForAppsBySwipe(status);
        }
    }

    public  Point[] getAppLockSwipePwd() {
        //AppLock主畫面下的鎖
        Point point[]= new Point[3];
        point[0] = new Point((int)Math.round(device.getDisplayWidth() * 0.25),(int)Math.round(device.getDisplayHeight()*0.45));
        point[1] = new Point((int)Math.round(device.getDisplayWidth()*0.25),(int)Math.round(device.getDisplayHeight()*0.81));
        point[2] = new Point((int)Math.round(device.getDisplayWidth()*0.75),(int)Math.round(device.getDisplayHeight()*0.81));
        return point;
    }

    public  Point[] getAppLockWrongSwipePwd() {
        //AppLock主畫面下的鎖
        Point point[]= new Point[3];
        point[2] = new Point((int)Math.round(device.getDisplayWidth() * 0.25),(int)Math.round(device.getDisplayHeight()*0.45));
        point[1] = new Point((int)Math.round(device.getDisplayWidth()*0.25),(int)Math.round(device.getDisplayHeight()*0.81));
        point[0] = new Point((int)Math.round(device.getDisplayWidth()*0.75),(int)Math.round(device.getDisplayHeight()*0.81));
        return point;

    }

    public  Point[] getAppLockForAppsSwipePwd() {
        // APP 下的鎖畫面
        Point point[]= new Point[3];
        point[0] = new Point((int)Math.round(device.getDisplayWidth() * 0.25),(int)Math.round(device.getDisplayHeight()/2));
        point[1] = new Point((int)Math.round(device.getDisplayWidth()*0.25),(int)Math.round(device.getDisplayHeight()*0.83));
        point[2] = new Point((int)Math.round(device.getDisplayWidth()*0.75),(int)Math.round(device.getDisplayHeight()*0.83));
        return point;

    }

    public  Point[] getAppLockForAppsWrongSwipePwd() {
        // APP 下的鎖畫面
        Point point[]= new Point[3];
        point[2] = new Point((int)Math.round(device.getDisplayWidth() * 0.25),(int)Math.round(device.getDisplayHeight()/2));
        point[1] = new Point((int)Math.round(device.getDisplayWidth()*0.25),(int)Math.round(device.getDisplayHeight()*0.83));
        point[0] = new Point((int)Math.round(device.getDisplayWidth()*0.75),(int)Math.round(device.getDisplayHeight()*0.83));
        return point;

    }

    public String getCurrentTime(){
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Calendar calobj = Calendar.getInstance();
        return df.format(calobj.getTime());
    }

    public Map<String, String> getDeviceInfo() {
        Map deviceInfo = null;
        deviceInfo.put("Device Type", device.getProductName());
        deviceInfo.put("Height", Integer.toString(device.getDisplayHeight()));
        deviceInfo.put("Width", Integer.toString(device.getDisplayWidth()));
        return deviceInfo;
    }

//    public String[] getLockAppList() {
//        String[] result;
//        StringBuffer tmp = new StringBuffer();
//        try {
//            goToAppLockHome();
//            UiCollection test = new UiCollection(new UiSelector().resourceId("com.cleanmaster.applock:id/applock_app_list"));
//            test.is
//
//        } catch (UiObjectNotFoundException e) {
//            Assert.assertTrue(e.toString(),false);
//        }
//
//    }

    public void goToAppLockHome() throws UiObjectNotFoundException {
        //launchAppInHomeScreen(Define.appLock);
        //device.swipe(getAppLockSwipePwd(), 40);
        unLockAppLock(getAppLockPwdType(),true);
        device.waitForWindowUpdate("com.cleanmaster.applock",1000);
        UiObject leftBack = device.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/custom_title_btn_left"));
        UiObject leftTitle = device.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/custom_title_label"));
        String leftTitleStr = leftTitle.getText();
        while (!(leftTitleStr.equals("APP鎖"))){
            leftBack.click();
            device.waitForIdle();
            leftTitle = device.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/custom_title_label"));
            leftTitleStr = leftTitle.getText();
        }
    }
    /**
     * 在上鎖設定頁, 勾選解鎖全部 app
     * @param unlock true = 勾選
     * */
    public void setUnLockAllApp(Boolean unlock){
        UiObject unLockAllApp = device.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/setting_universal_lock_btn"));
        try{
            if(unlock = false){
                if(unLockAllApp.isSelected()){
                    unLockAllApp.click();
                }
            }else {
                if(!(unLockAllApp.isSelected())){
                    unLockAllApp.click();
                }
            }
        }catch (UiObjectNotFoundException e){
            Assert.assertTrue(e.toString(),false);
        }
    }
    /**
     * 確認是否有 view
     * @param resourceId = 要確認的 rid
     * @return true 有Obj
     * @return false  沒有 Obj
     * */
    public Boolean checkViewByResourceId(String resourceId){
        UiObject view = device.findObject(new UiSelector().resourceId(resourceId));
        if(view.exists()){
            return true;
        }else {
            return false;
        }
    }

    public void goToChangePwdPageFromSetting(){
        UiObject changePwd = device.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/setting_change_password"));
        try {
            changePwd.click();
            device.waitForIdle();
        } catch (UiObjectNotFoundException e) {
            Assert.assertTrue("似乎沒有在AppLock設定頁",false);
        }
    }

    public void changePwdInChangePage(String pwdType, boolean pwd){
        if(pwdType.equals(Define.pwdType_Digital)){
            UiObject switchPwdType = device.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/lockpattern_switch_method"));
            try {
                switchPwdType.click();
                device.waitForIdle();
                unLockAppLockByDigital(pwd);
                device.waitForIdle();
                unLockAppLockByDigital(pwd);
            } catch (UiObjectNotFoundException e) {
                Assert.assertTrue("切換成數字密碼失敗",false);
            }
        }
        if(pwdType.equals(Define.pwdType_Swipe)){
            unLockAppLockBySwipe(pwd);
            device.waitForIdle();
            unLockAppLockBySwipe(pwd);
        }
        device.waitForIdle();
    }

    public String getAppLockPwdType() {
        device.waitForIdle();
        launchAppInHomeScreen(Define.appLock);
        String digitalRId = "com.cleanmaster.applock:id/keypad";
        String swipeRId = "com.cleanmaster.applock:id/lockpattern_pattern_layout";
        if (checkViewByResourceId(digitalRId)) {
            pwdTypeNow = "digital";
            return Define.pwdType_Digital;
        } else if (checkViewByResourceId(swipeRId)) {
            pwdTypeNow = "swipe";
            return Define.pwdType_Swipe;
        } else {
            return "";
        }
    }

    //AppLock 跑OOBE流程
    public void OOBE_intial() throws InterruptedException {
        device.waitForIdle();
        launchAppInHomeScreen(Define.appLock);
        if (android.os.Build.VERSION.SDK_INT >= 23){
            sleep(1000);
            try {
                UiObject protectBTN = device.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/applock_lock_recommended_btn"));
                protectBTN.click(); //開啟保護
                sleep(1500);
                device.swipe(getAppLockSwipePwd(), 50);
                sleep(1000);
                device.swipe(getAppLockSwipePwd(), 50);
                sleep(1500);
                UiObject Access_permission = device.findObject(new UiSelector().resourceId("com.android.packageinstaller:id/permission_allow_button"));
                Access_permission.click(); //開啟聯絡人權限
                sleep(1000);
                UiObject secure_question = device.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/et_question"));
                if (secure_question.exists()){
                    UiObject completeBTN = device.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/btn_finish"));
                    completeBTN.click();
                    sleep(1500);
                    protectBTN.click();
                }
                else {
                    protectBTN.click(); //開啟完成
                    sleep(3500);
                }
                Access_permission.click(); //開啟電話權限
                sleep(2000);
                UiObject Tab_Advanced = device.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/applock_item_subname"));
                Tab_Advanced.click();
                sleep(1000);
                Access_permission.click(); //開啟相機權限
                device.waitForIdle();
                Access_permission.click(); //開啟儲存權限
                device.pressHome();
            } catch (UiObjectNotFoundException e) {
                DoScreenShot("AppLock初始流程失敗os6.0");
                Assert.assertTrue("OOBE initial失敗 for OS 6.0",false);
            }
        }
        else {
            sleep(1000);
            try{
                UiObject protectBTN = device.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/applock_lock_recommended_btn"));
                protectBTN.click(); //開啟保護
                sleep(1500);
                device.swipe(getAppLockSwipePwd(), 50);
                sleep(1000);
                device.swipe(getAppLockSwipePwd(), 50);
                sleep(2500);
                UiObject secure_question = device.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/et_question"));
                if (secure_question.exists()){
                    UiObject completeBTN = device.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/btn_finish"));
                    completeBTN.click();
                    sleep(1500);
                    protectBTN.click();
                    device.pressHome();
                }
                else {
                    protectBTN.click(); //開啟完成
                    sleep(3000);
                    device.pressHome();
                }
            } catch (UiObjectNotFoundException e) {
                DoScreenShot("AppLock初始流程失敗os5.0");
                Assert.assertTrue("OOBE initial失敗 for OS 5.0",false);
            }
        }
    }
}
