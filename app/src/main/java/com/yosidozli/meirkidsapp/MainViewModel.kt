package com.yosidozli.meirkidsapp

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository = UserRepository(PreferencesUtils(application))

    fun authenticate(userName: String, password: String){
        userRepository.authenticate(userName,password)
    }

    val user = userRepository.userLiveData


}
