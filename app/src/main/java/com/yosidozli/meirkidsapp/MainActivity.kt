package com.yosidozli.meirkidsapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.firebase.messaging.FirebaseMessaging
import com.yosidozli.meirkidsapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        private const val START_URL = "https://meirkids.co.il/"
        private const val KEY_CURRENT_URL = "current_url"
        private const val PREFS_NAME = "meirkids_prefs"
        private const val KEY_ASKED_NOTIFICATION_PERMISSION = "asked_notification_permission"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var chromeClient: MeirKidsChromeClient
    private var pageLoadFailed = false

    private val fileChooserLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            chromeClient.onFileChooserResult(result.resultCode, result.data)
        }

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* no-op either way */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWebView()
        setupBackNavigation()

        binding.errorView.errorLoadingButton.setOnClickListener { retry() }

        val restoreUrl = savedInstanceState?.getString(KEY_CURRENT_URL)
        binding.webView.loadUrl(restoreUrl ?: START_URL)

        if (BuildConfig.DEBUG) {
            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                Log.i("MeirKidsFcm", "token: $token")
            }
        }
    }

    private fun maybeRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        if (prefs.getBoolean(KEY_ASKED_NOTIFICATION_PERMISSION, false)) return

        val granted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) return

        prefs.edit().putBoolean(KEY_ASKED_NOTIFICATION_PERMISSION, true).apply()
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun setupWebView() {
        val webView = binding.webView
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mediaPlaybackRequiresUserGesture = false
            useWideViewPort = true
            loadWithOverviewMode = true
            builtInZoomControls = true
            displayZoomControls = false
            setSupportZoom(true)
            javaScriptCanOpenWindowsAutomatically = true
            setSupportMultipleWindows(false)
            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
            cacheMode = WebSettings.LOAD_DEFAULT
        }

        CookieManager.getInstance().apply {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(webView, true)
        }

        webView.webViewClient = MeirKidsWebViewClient(
            onMainFrameError = {
                pageLoadFailed = true
                showErrorView()
            },
            onMainFrameLoadStarted = {
                pageLoadFailed = false
            },
            onPageVisible = {
                if (!pageLoadFailed) hideErrorView()
                maybeRequestNotificationPermission()
            }
        )

        chromeClient = MeirKidsChromeClient(
            activity = this,
            webView = webView,
            fullscreenContainer = binding.fullscreenContainer,
            launchFileChooserIntent = { intent -> fileChooserLauncher.launch(intent) }
        )
        webView.webChromeClient = chromeClient
    }

    private fun setupBackNavigation() {
        onBackPressedDispatcher.addCallback(this) {
            when {
                chromeClient.exitFullscreenIfShowing() -> Unit
                binding.errorView.root.isVisible -> finish()
                binding.webView.canGoBack() -> binding.webView.goBack()
                else -> {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }
    }

    private fun showErrorView() {
        binding.errorView.root.isVisible = true
        binding.webView.isVisible = false
    }

    private fun hideErrorView() {
        binding.errorView.root.isVisible = false
        binding.webView.isVisible = true
    }

    private fun retry() {
        if (!isOnline(this)) {
            Toast.makeText(this, R.string.error_still_offline, Toast.LENGTH_SHORT).show()
            return
        }
        hideErrorView()
        pageLoadFailed = false
        if (binding.webView.url == null) {
            binding.webView.loadUrl(START_URL)
        } else {
            binding.webView.reload()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_CURRENT_URL, binding.webView.url)
    }

    override fun onResume() {
        super.onResume()
        binding.webView.onResume()
        binding.webView.resumeTimers()
    }

    override fun onPause() {
        binding.webView.onPause()
        binding.webView.pauseTimers()
        CookieManager.getInstance().flush()
        super.onPause()
    }
}
