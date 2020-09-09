package com.usphuong.bluetoothscanner.di

import com.apollographql.apollo.ApolloClient
import com.usphuong.bluetoothscanner.data.local.DeviceDao
import com.usphuong.bluetoothscanner.data.remote.ApiService
import com.usphuong.bluetoothscanner.data.repository.DeviceRepository
import com.usphuong.bluetoothscanner.data.repository.GithubRepository
import com.usphuong.bluetoothscanner.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object RepositoryModule {
    @Singleton
    @Provides
    fun provideUserRepository(apiService: ApiService) = UserRepository(apiService)

    @Singleton
    @Provides
    fun provideDeviceRepository(deviceDao: DeviceDao) = DeviceRepository(deviceDao)

    @Singleton
    @Provides
    fun provideGithubRepository(apolloClient: ApolloClient) = GithubRepository(apolloClient)

}
