package com.ciaorides.ciaorides.view.activities.menu

import android.net.http.SslError
import android.view.View
import android.webkit.*
import com.ciaorides.ciaorides.databinding.ActivityStaticPagesBinding
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.view.activities.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StaticPagesActivity : BaseActivity<ActivityStaticPagesBinding>() {
    override fun getViewBinding(): ActivityStaticPagesBinding =
        ActivityStaticPagesBinding.inflate(layoutInflater)

    override fun init() {
        updateToolBar(binding.toolbar.ivBadge)
        binding.progressLayout.root.visibility = View.VISIBLE
        binding.webView.webViewClient = WebViewClient()
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.setSupportZoom(true)
        binding.webView.loadUrl(intent.getStringExtra(Constants.DATA_VALUE).toString())
        binding.toolbar.tvHeader.text = intent.getStringExtra(Constants.TITLE).toString()
        binding.toolbar.ivMenu.setOnClickListener { onBackPressed() }
    }

    inner class WebViewClient : android.webkit.WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return false
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            binding.progressLayout.root.visibility = View.GONE
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            binding.progressLayout.root.visibility = View.GONE
            super.onReceivedError(view, request, error)
        }

        override fun onReceivedHttpError(
            view: WebView?,
            request: WebResourceRequest?,
            errorResponse: WebResourceResponse?
        ) {
            binding.progressLayout.root.visibility = View.GONE
            super.onReceivedHttpError(view, request, errorResponse)
        }

        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?
        ) {
            binding.progressLayout.root.visibility = View.GONE
            super.onReceivedSslError(view, handler, error)
        }
    }
}