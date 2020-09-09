package com.usphuong.bluetoothscanner.feature.webView

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.usphuong.bluetoothscanner.Constants.EXTRA_URL
import com.usphuong.bluetoothscanner.R
import com.usphuong.bluetoothscanner.base.BaseFragment
import com.usphuong.bluetoothscanner.viewModel.UserViewModel
import kotlinx.android.synthetic.main.fragment_webview.webView

class WebViewFragment : BaseFragment() {

    private val userViewModel by activityViewModels<UserViewModel>()

    override val layoutId: Int
        get() = R.layout.fragment_webview

    override fun initObserver() {
    }

    override fun initView() {

        val url = userViewModel.userInfoLiveData.value?.website

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                url?.let { view?.loadUrl(it) }
                return true
            }
        }

        url?.let { webView.loadUrl(it) }

    }

}
