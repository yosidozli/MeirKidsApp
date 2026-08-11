package com.yosidozli.meirkidsapp

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Environment
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import java.io.File

class MeirKidsChromeClient(
    private val activity: Activity,
    private val webView: WebView,
    private val fullscreenContainer: FrameLayout,
    private val launchFileChooserIntent: (Intent) -> Unit,
) : WebChromeClient() {

    private var customView: android.view.View? = null
    private var customViewCallback: CustomViewCallback? = null
    private var pendingFileChooserCallback: ValueCallback<Array<Uri>>? = null
    private var pendingCameraCaptureUri: Uri? = null

    val isShowingCustomView: Boolean
        get() = customView != null

    override fun onShowCustomView(view: android.view.View, callback: CustomViewCallback) {
        if (customView != null) {
            callback.onCustomViewHidden()
            return
        }
        customView = view
        customViewCallback = callback
        fullscreenContainer.addView(view)
        fullscreenContainer.visibility = FrameLayout.VISIBLE
        webView.visibility = FrameLayout.INVISIBLE
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        hideSystemBars()
    }

    override fun onHideCustomView() {
        val view = customView ?: return
        fullscreenContainer.removeView(view)
        fullscreenContainer.visibility = FrameLayout.GONE
        webView.visibility = FrameLayout.VISIBLE
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        showSystemBars()
        customViewCallback?.onCustomViewHidden()
        customView = null
        customViewCallback = null
    }

    fun exitFullscreenIfShowing(): Boolean {
        if (customView == null) return false
        onHideCustomView()
        return true
    }

    private fun hideSystemBars() {
        val window = activity.window
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(androidx.core.view.WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    private fun showSystemBars() {
        val window = activity.window
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.show(androidx.core.view.WindowInsetsCompat.Type.systemBars())
        WindowCompat.setDecorFitsSystemWindows(window, true)
    }

    override fun onPermissionRequest(request: PermissionRequest) {
        request.deny()
    }

    override fun onShowFileChooser(
        webView: WebView,
        filePathCallback: ValueCallback<Array<Uri>>,
        fileChooserParams: FileChooserParams
    ): Boolean {
        pendingFileChooserCallback?.onReceiveValue(null)
        pendingFileChooserCallback = filePathCallback

        val contentIntent = fileChooserParams.createIntent()

        val cameraIntents = mutableListOf<Intent>()
        val cacheDir = File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "images")
        cacheDir.mkdirs()
        val photoFile = File(cacheDir, "capture_${System.currentTimeMillis()}.jpg")
        val photoUri = FileProvider.getUriForFile(
            activity, "${activity.packageName}.fileprovider", photoFile
        )
        pendingCameraCaptureUri = photoUri
        val captureIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri)
        }
        if (captureIntent.resolveActivity(activity.packageManager) != null) {
            cameraIntents.add(captureIntent)
        }

        val chooserIntent = Intent.createChooser(contentIntent, null).apply {
            putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toTypedArray())
        }
        launchFileChooserIntent(chooserIntent)
        return true
    }

    fun onFileChooserResult(resultCode: Int, data: Intent?) {
        val callback = pendingFileChooserCallback
        pendingFileChooserCallback = null
        if (callback == null) return

        if (resultCode != Activity.RESULT_OK) {
            callback.onReceiveValue(null)
            return
        }

        val results = FileChooserParams.parseResult(resultCode, data)
        if (results != null && results.isNotEmpty()) {
            callback.onReceiveValue(results)
        } else {
            val cameraUri = pendingCameraCaptureUri
            if (cameraUri != null) {
                callback.onReceiveValue(arrayOf(cameraUri))
            } else {
                callback.onReceiveValue(null)
            }
        }
        pendingCameraCaptureUri = null
    }
}

private typealias ValueCallback<T> = android.webkit.ValueCallback<T>
