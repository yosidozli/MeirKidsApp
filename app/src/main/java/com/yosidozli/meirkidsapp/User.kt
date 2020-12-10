package com.yosidozli.meirkidsapp

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi

@JsonClass(generateAdapter = true)
data class User(
        @Json(name = "ID") val id : Int,
        @Json(name = "PersonID") val personId: Int,
        @Json(name = "FirstName") val firstName: String,
        @Json(name = "LastName") val lastName: String
        ){

        companion object{
        @JvmStatic
                val jsonAdapter  = Moshi.Builder().build().adapter(User::class.java)
        }
}
