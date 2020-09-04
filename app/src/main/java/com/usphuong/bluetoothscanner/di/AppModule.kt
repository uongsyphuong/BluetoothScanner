package com.usphuong.bluetoothscanner.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.usphuong.bluetoothscanner.viewModel.DeviceViewModel
import com.usphuong.bluetoothscanner.viewModel.UserViewModel
import com.usphuong.bluetoothscanner.viewModel.ViewModelFactory
import com.usphuong.bluetoothscanner.viewModel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(DeviceViewModel::class)
    abstract fun bindDeviceViewModel(deviceViewModel: DeviceViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UserViewModel::class)
    abstract fun bindUserViewModel(userViewModel: UserViewModel): ViewModel
}
