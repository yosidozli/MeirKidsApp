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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.google.firebase.messaging.FirebaseMessaging
import com.yosidozli.meirkidsapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        private const val START_URL = "https://meirkids.co.il/"
        private const val KEY_CURRENT_URL = "current_url"
        private const val PREFS_NAME = "meirkids_prefs"
        private const val KEY_ASKED_NOTIFICATION_PERMISSION = "asked_notification_permission"

        // Appended to the WebView's default user agent so the backend/CDN can
        // recognize and whitelist requests coming from this app (e.g. video access).
        private const val APP_USER_AGENT_TOKEN = "MeirKidsAndroidApp"
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

        setupEdgeToEdgeInsets()
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

    // Android 15+ (targetSdk 36) forces edge-to-edge, so content draws under the status
    // bar and navigation bar/gesture buttons unless padded for them here. This pads
    // content_container (which hosts both the WebView and the error view, and is never
    // GONE) rather than its children, since inset dispatch skips GONE views. The
    // fullscreen video container is a sibling and stays unpadded so it can go true
    // edge-to-edge.
    private fun setupEdgeToEdgeInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.contentContainer) { view, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            view.updatePadding(left = bars.left, top = bars.top, right = bars.right, bottom = bars.bottom)
            insets
        }

        // web_content_background is a dark purple, so the status/nav bar icons drawn
        // over it need to be light rather than the theme's default dark icons.
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = false
        insetsController.isAppearanceLightNavigationBars = false

        // Without this the system draws its own translucent scrim over the transparent
        // bars for contrast, which washes web_content_background out to gray/white.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        if (Build.VERSION.SDK_INT >= 35) {
            window.isStatusBarContrastEnforced = false
        }
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
            userAgentString = "$userAgentString $APP_USER_AGENT_TOKEN/${BuildConfig.VERSION_NAME}"
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
