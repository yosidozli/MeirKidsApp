package com.yosidozli.meirkidsapp;

import com.google.firebase.messaging.FirebaseMessagingService;


/**
 * Created by yosid on 30/06/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {
    private final static String TAG ="FbInstanceIdSer";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

    private void sendingRegistrationToServer(String token){

    }
}
