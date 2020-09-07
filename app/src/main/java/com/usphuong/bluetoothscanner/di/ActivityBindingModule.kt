package com.usphuong.bluetoothscanner.di

import com.usphuong.bluetoothscanner.feature.github.GithubActivity
import com.usphuong.bluetoothscanner.feature.history.HistoryActivity
import com.usphuong.bluetoothscanner.feature.scanBluetooth.HomeActivity
import com.usphuong.bluetoothscanner.feature.userInfo.UserInfoActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {
    @ContributesAndroidInjector
    abstract fun bindHomeActivity(): HomeActivity

    @ContributesAndroidInjector
    abstract fun bindHistoryActivity(): HistoryActivity

    @ContributesAndroidInjector
    abstract fun bindUserInfoActivity(): UserInfoActivity

    @ContributesAndroidInjector
    abstract fun bindGithubActivity(): GithubActivity
}
