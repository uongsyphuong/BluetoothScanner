package com.usphuong.bluetoothscanner.di

import com.usphuong.bluetoothscanner.feature.github.GithubFragment
import com.usphuong.bluetoothscanner.feature.history.HistoryFragment
import com.usphuong.bluetoothscanner.feature.scanBluetooth.FilterDialogFragment
import com.usphuong.bluetoothscanner.feature.scanBluetooth.HomeFragment
import com.usphuong.bluetoothscanner.feature.userInfo.UserInfoFragment
import com.usphuong.bluetoothscanner.feature.webView.WebViewFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

    @ContributesAndroidInjector
    abstract fun bindHistoryActivity(): HistoryFragment

    @ContributesAndroidInjector
    abstract fun bindUserInfoFragment(): UserInfoFragment

    @ContributesAndroidInjector
    abstract fun bindGithubActivity(): GithubFragment

    @ContributesAndroidInjector
    abstract fun bindHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun bindFilterDialogFragment(): FilterDialogFragment

    @ContributesAndroidInjector
    abstract fun bindWebViewFragment(): WebViewFragment
}
