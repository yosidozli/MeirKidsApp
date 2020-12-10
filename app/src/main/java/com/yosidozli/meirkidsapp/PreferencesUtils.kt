package com.yosidozli.meirkidsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.yosidozli.meirkidsapp.registration.User;

/**
 * Created by yosid on 12/06/2017.
 */

public class PreferencesUtils {
    private Gson gson;
    private SharedPreferences userPref;
    private Context context;
    private static String userKey;

    private static  final   String TAG = "PREFERENCES_UTILS";

    public PreferencesUtils( Context context) {
        this.context = context;
        this.gson = new Gson();
        this.userPref = context.getSharedPreferences(context.getString(R.string.user_preferences),Context.MODE_PRIVATE);
        //Log.d(TAG, "PreferencesUtils: "+ (userPref == null));
        userKey =context.getString(R.string.pref_key_user);
    }

    public User getUserFromPreferences( ){

        String usrStr = userPref.getString(userKey,"");
        User user = gson.fromJson(usrStr,User.class);
        return user;

    }

    public void putUserInPreferences(User user){
        //Log.d(TAG, "putUserInPreferences: "+user);
        SharedPreferences.Editor editor = userPref.edit();
        String json = gson.toJson(user);
        //Log.d(TAG, "putUserInPreferences: "+json);
        editor.putString(userKey,json);
        editor.commit();

    }


}
