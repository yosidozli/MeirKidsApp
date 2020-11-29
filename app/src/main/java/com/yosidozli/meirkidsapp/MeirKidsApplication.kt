package com.yosidozli.meirkidsapp

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

//import com.onesignal.OneSignal;
/**
 * Created by yosid on 30/06/2017.
 */
class MeirKidsApplication : Application() {

    val koinModule = module{
        single { MainViewModel() }
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MeirKidsApplication)
            modules(koinModule)
        }

        /*  OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();&*/
    }
}