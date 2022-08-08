package com.yosidozli.meirkidsapp

import android.content.SharedPreferences
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserRepository(val pref: PreferencesUtils) {

    private val _userLiveData = MutableLiveData<ViewState>()
    val userLiveData: LiveData<ViewState> = _userLiveData

    fun authenticate(userName: String , password: String){
        GlobalScope.launch(Dispatchers.IO) {
            _userLiveData.postValue(ViewState.loading)
            val res = try {
                fetchUser(userName, password)

            } catch (t: Throwable) {
                t.message
            }
            Log.d("UserRepository", "authenticate: $res")
            when(res){
                is User -> {
                    _userLiveData.postValue(ViewState.Success(res))
                    pref.putUserInPreferences(res)
                }
                is Throwable -> _userLiveData.postValue(ViewState.Failure(res))
            }
        }
    }

}

