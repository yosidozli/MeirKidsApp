package com.yosidozli.meirkidsapp;

import android.app.Application;

import com.vimeo.networking.Configuration;

//import com.onesignal.OneSignal;

/**
 * Created by yosid on 30/06/2017.
 */

public class MeirKidsApplication extends Application {

    private static MeirKidsApplication singleton;

    public static MeirKidsApplication getInstance(){
        return singleton;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
      /*  OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();&*/
    }

}
