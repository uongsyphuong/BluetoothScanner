package com.usphuong.bluetoothscanner.di

import com.usphuong.bluetoothscanner.BluetoothScannerApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        RemoteModule::class,
        DbModule::class,
        ActivityBindingModule::class,
        ViewModelModule::class
    ]
)
interface AppComponent : AndroidInjector<BluetoothScannerApplication> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(app: BluetoothScannerApplication): Builder

        fun build(): AppComponent
    }
}
