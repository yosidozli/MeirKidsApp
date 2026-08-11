package com.yosidozli.meirkidsapp

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent

fun openExternalHttpUrl(context: Context, uri: Uri) {
    try {
        CustomTabsIntent.Builder().build().launchUrl(context, uri)
    } catch (e: ActivityNotFoundException) {
        openExternal(context, uri)
    }
}

fun openExternal(context: Context, uri: Uri) {
    try {
        val intent = if (uri.scheme == "intent") {
            Intent.parseUri(uri.toString(), Intent.URI_INTENT_SCHEME)
        } else {
            Intent(Intent.ACTION_VIEW, uri)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, R.string.error_cannot_open_link, Toast.LENGTH_SHORT).show()
    }
}
