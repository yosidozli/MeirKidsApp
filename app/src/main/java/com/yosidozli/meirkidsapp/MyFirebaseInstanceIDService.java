package com.yosidozli.meirkidsapp;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by yosid on 30/06/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private final static String TAG ="FbInstanceIdSer";
    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        //Log.d(TAG, "onTokenRefresh: "+refreshedToken);
        sendingRegistrationToServer(refreshedToken);
        super.onTokenRefresh();

    }

    private void sendingRegistrationToServer(String token){

    }
}
