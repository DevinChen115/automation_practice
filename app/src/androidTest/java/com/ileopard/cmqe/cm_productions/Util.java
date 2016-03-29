package com.ileopard.cmqe.cm_productions;

import android.graphics.Point;
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

/**
 * Created by User2 on 2016/2/26.
 */
public class Util {
     UiDevice device;

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
        try {
            // 1. 設定頁
            goToAppsList();
            scrollAndClickByDesc("設定");
            // 2. 應用程式管理員
            scrollAndClickByText("應用程式管理員");
            // 3. CM AppLock
            scrollAndClickByText(Define.appLock);
            // 4. 清除資料
            UiObject clrBtn = scrollAndGetUiObjByResourceId("com.android.settings:id/left_button");
            if (clrBtn.isEnabled()) {
                clrBtn.click();
                device.waitForIdle();
                UiObject confirm = device.findObject(new UiSelector().resourceId("com.android.settings:id/button1"));
                confirm.click();
                device.waitForIdle();
            }
        } catch (UiObjectNotFoundException e) {
            Assert.assertTrue("Clear Cache "+e.toString(), false);
        }
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
                    Log.d("Devin", getCurrentTime());
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
            UiObject setting = device.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/menu_main_layout"));
            setting.click();
            device.waitForIdle();
        } catch (UiObjectNotFoundException e) {
            Assert.assertTrue(e.toString(),false);
        }
    }


    public  void setLockAppFrequency(String frequency) throws UiObjectNotFoundException {
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
        UiObject lockSeting = device.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/setting_temp_unlock_layout"));
        lockSeting.click();
        device.waitForIdle();
        UiObject lockFrequencyObj = device.findObject(new UiSelector().resourceId(lockFrequency));
        try{
            lockFrequencyObj.click();
            device.waitForIdle();
        } catch (UiObjectNotFoundException e){
            Assert.assertTrue("沒有 "+frequency+" 這個選項",false);
        }
    }

    public  void setShootFrequency(int times) throws UiObjectNotFoundException{
        int idx = 0;
        switch (times){
            case 1:
                idx = 0;
                break;
            case 2:
                idx = 1;
                break;
            case 3:
                idx = 2;
                break;
            case 5:
                idx = 3;
                break;
            default:
                Assert.assertTrue("Method_setShootFrequency: only 1 2 3 5 can use",false);
                break;
        }
        goToAppLockSetting();
        UiObject shootBadGuy = device.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/setting_intruder_selfie"));
        shootBadGuy.click();
        device.waitForIdle();
        UiObject shootTimes = device.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/setting_intruder_selfie_counter"));
        shootTimes.click();
        device.waitForIdle();
        UiObject selectTimes = device.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/listView")).getChild(new UiSelector().index(idx));
        selectTimes.click();
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

    public  void unLockBySwipe(boolean status) {
        if(status){
            device.swipe(getSwipePwd(), 50);
        }else {
            device.swipe(getWrongSwipePwd(),50);
        }
        device.waitForIdle();
    }

    public  Point[] getSwipePwd() {
        //AppLock主畫面下的鎖
        Point point[]= new Point[3];
        point[0] = new Point((int)Math.round(device.getDisplayWidth() * 0.25),(int)Math.round(device.getDisplayHeight()*0.45));
        point[1] = new Point((int)Math.round(device.getDisplayWidth()*0.25),(int)Math.round(device.getDisplayHeight()*0.81));
        point[2] = new Point((int)Math.round(device.getDisplayWidth()*0.75),(int)Math.round(device.getDisplayHeight()*0.81));
        return point;
    }

    public  Point[] getWrongSwipePwd() {
        //AppLock主畫面下的鎖
        Point point[]= new Point[3];
        point[2] = new Point((int)Math.round(device.getDisplayWidth() * 0.25),(int)Math.round(device.getDisplayHeight()*0.45));
        point[1] = new Point((int)Math.round(device.getDisplayWidth()*0.25),(int)Math.round(device.getDisplayHeight()*0.81));
        point[0] = new Point((int)Math.round(device.getDisplayWidth()*0.75),(int)Math.round(device.getDisplayHeight()*0.81));
        return point;

    }

    public  Point[] getSwipePwdV2() {
        // APP 下的鎖畫面
        Point point[]= new Point[3];
        point[0] = new Point((int)Math.round(device.getDisplayWidth() * 0.25),(int)Math.round(device.getDisplayHeight()/2));
        point[1] = new Point((int)Math.round(device.getDisplayWidth()*0.25),(int)Math.round(device.getDisplayHeight()*0.83));
        point[2] = new Point((int)Math.round(device.getDisplayWidth()*0.75),(int)Math.round(device.getDisplayHeight()*0.83));
        return point;

    }

    public  Point[] getWrongSwipePwdV2() {
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
        launchAppInHomeScreen(Define.appLock);
        device.swipe(getSwipePwd(), 40);
        device.waitForWindowUpdate("com.cleanmaster.applock",1000);
        UiObject leftBack = device.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/custom_title_btn_left"));
        UiObject leftTitle = device.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/custom_title_label"));
        String leftTitleStr = leftTitle.getText();
        while (!(leftTitleStr.equals("APP鎖"))){
            Log.d("Devin", leftTitle.getText());
            leftBack.click();
            device.waitForIdle();
            leftTitle = device.findObject(new UiSelector().resourceId("com.cleanmaster.applock:id/custom_title_label"));
            leftTitleStr = leftTitle.getText();
        }
    }
    /**
     * 在上鎖設定頁, 勾選解鎖全部 app
     * //@param unlock true = 勾選
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

    public Boolean checkViewByResourceId(String resourceId){
        UiObject view = device.findObject(new UiSelector().resourceId(resourceId));
        if(view.exists()){
            return true;
        }else {
            return false;
        }
    }
}
