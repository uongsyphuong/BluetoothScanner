package com.usphuong.bluetoothscanner.feature.webView

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.usphuong.bluetoothscanner.Constants.EXTRA_URL
import com.usphuong.bluetoothscanner.R
import kotlinx.android.synthetic.main.activity_webview.webView

class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        supportActionBar?.title = getString(R.string.web_view)

        val url = intent.getStringExtra(EXTRA_URL)

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                url?.let { view?.loadUrl(it) }
                return true
            }
        }

        url?.let { webView.loadUrl(it) }
    }
}
