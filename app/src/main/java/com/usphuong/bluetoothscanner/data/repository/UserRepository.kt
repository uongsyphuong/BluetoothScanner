package com.usphuong.bluetoothscanner.data.repository

import com.usphuong.bluetoothscanner.data.model.Resource
import com.usphuong.bluetoothscanner.data.model.User
import com.usphuong.bluetoothscanner.data.remote.ApiService
import java.io.IOException
import javax.inject.Inject

class UserRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getUserInfo(): Resource<User?> {
        val error = try {
            val response = apiService.getUserInfo()
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.username != null) return Resource.success(body)
                "No data"
            } else {
                "Network call has failed: ${response.message()} ${response.code()}"
            }
        } catch (e: IOException) {
            e.message ?: "Network call has failed"
        }
        return Resource.error(error)
    }
}
