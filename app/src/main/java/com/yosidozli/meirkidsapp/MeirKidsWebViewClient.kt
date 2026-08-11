package com.yosidozli.meirkidsapp

import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient

private fun isInternalHost(host: String?): Boolean =
    host != null && (host == "meirkids.co.il" || host.endsWith(".meirkids.co.il"))

class MeirKidsWebViewClient(
    private val onMainFrameError: () -> Unit,
    private val onMainFrameLoadStarted: () -> Unit,
    private val onPageVisible: () -> Unit,
) : WebViewClient() {

    override fun onPageStarted(view: WebView, url: String?, favicon: android.graphics.Bitmap?) {
        onMainFrameLoadStarted()
    }

    override fun onPageCommitVisible(view: WebView, url: String?) {
        onPageVisible()
    }

    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        if (!request.isForMainFrame) return false

        val uri = request.url
        return when (uri.scheme) {
            "http", "https" ->
                if (isInternalHost(uri.host)) {
                    false
                } else {
                    openExternalHttpUrl(view.context, uri)
                    true
                }
            else -> {
                openExternal(view.context, uri)
                true
            }
        }
    }

    override fun onReceivedError(
        view: WebView,
        request: WebResourceRequest,
        error: WebResourceError
    ) {
        if (request.isForMainFrame) {
            onMainFrameError()
        }
    }

    override fun onReceivedHttpError(
        view: WebView,
        request: WebResourceRequest,
        errorResponse: WebResourceResponse
    ) {
        if (request.isForMainFrame) {
            onMainFrameError()
        }
    }
}
