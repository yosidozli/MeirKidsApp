package com.yosidozli.meirkidsapp

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

/**
 * Created by yosid on 12/06/2017.
 */
class PreferencesUtils(private val context: Context) {
    //    private val gson: Gson
    private val userPref: SharedPreferences

    val userFromPreferences: User?
        get() {
            val usrStr = userPref.getString(userKey, "")

            return when (usrStr) {
                null -> null
                "" -> null
                else -> User.jsonAdapter.fromJson(usrStr)
            }
        }

    fun putUserInPreferences(user: User?) {
        //Log.d(TAG, "putUserInPreferences: "+user);
//        val editor = userPref.edit()
//        val json = gson.toJson(user)
//        //Log.d(TAG, "putUserInPreferences: "+json);
//        editor.putString(userKey, json)
//        editor.commit()
        with(userPref.edit()) {
            putString(userKey,
                    User.jsonAdapter.toJson(user))
            apply()
        }

    }

    companion object {
        private val userKey: String = "User"
        private const val TAG = "PREFERENCES_UTILS"
    }

    init {
//        gson = Gson()
        userPref = context.getSharedPreferences(context.getString(R.string.user_preferences), Context.MODE_PRIVATE)
        //Log.d(TAG, "PreferencesUtils: "+ (userPref == null));
//        userKey = context.getString(R.string.pref_key_user)
    }
}