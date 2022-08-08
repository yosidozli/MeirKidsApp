package com.yosidozli.meirkidsapp

import android.net.Uri
import android.util.Log
import com.squareup.moshi.Moshi
import kotlinx.coroutines.*
import okhttp3.*
import retrofit2.HttpException
import java.io.IOException
import java.io.InputStream
import java.lang.NullPointerException
import java.net.URL
import kotlin.coroutines.resumeWithException

private const val LESSONS_URL = "https://meirkids.co.il/temp/xmlLessonesKids/lessons.asp"
private const val SETS_URL = "https://meirkids.co.il/temp/xmlLessonesKids/sets.asp"
private const val USER_AUTH_URL = "http://194.88.111.167/ApiCheck/api/loginapi/isuservalid"
private const val TAG = "WebApi"

suspend fun fetchLessonsXml() = suspendCancellableCoroutine<InputStream> {
    val client = OkHttpClient()
    val request = Request.Builder().url(LESSONS_URL).build()
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            it.resumeWithException(e)
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                it.resumeWith(Result.success(response.body!!.byteStream()))
            } else {
                it.resumeWithException(RuntimeException(response.message))
            }
        }

    })
}

suspend fun fetchSetsXml() = suspendCancellableCoroutine<InputStream> {
    val client = OkHttpClient()
    val request = Request.Builder().url(SETS_URL).build()
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            it.resumeWithException(e)
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                it.resumeWith(Result.success(response.body!!.byteStream()))
            } else {
                it.resumeWithException(RuntimeException(response.message))
            }
        }
    })
}

suspend fun fetchUser(userName :String , password: String) = suspendCancellableCoroutine<User>{
    Log.d(TAG, "fetchUser:")
    val adapter = User.jsonAdapter

//    val adapter = Moshi.Builder().build().adapter(User::class.java)
    val client = OkHttpClient()
    val url = Uri.parse(USER_AUTH_URL).buildUpon().appendQueryParameter("userName",userName).appendQueryParameter("password",password).toString()
    Log.d(TAG, "fetchUser: $url")
    val request = Request.Builder().url(url).build()
    client.newCall(request).execute().use { response ->
        Log.d(TAG, "fetchUser: $response")
        if(!response.isSuccessful)
            it.resumeWithException(IOException("unexpected call $response"))

        if(response.body == null){
            it.resumeWithException(IOException("no body"))
        }
              response.body?.apply {
                  try {
                      val json = string().removeSurrounding("\"").replace("\\", "")
                              val user = adapter.fromJson(json)
                      user?.apply{
                          it.resumeWith(Result.success(this))
                      }
                      if (user == null){
                          it.resumeWithException(IOException("Failed to login"))
                      }
                  } catch (t: Throwable) {
                      it.resumeWithException(t)
                  }
              }
    }
}

suspend fun fetchLessons() : List<Lesson>  {
        return fetchLessonsXml().run {
            val lessons = XmlLessonParser().parse(this)
            lessons
        }
}

suspend fun fetchSets() : List<LessonSet>  {
    return fetchSetsXml().run {
        val sets = XmlLessonSetParser().parse(this)
        sets
    }
}