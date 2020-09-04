package com.usphuong.bluetoothscanner.data.remote

import com.usphuong.bluetoothscanner.data.model.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface ApiService {

    @Headers("Accept:*/*")
    @GET("6b4d76c6-59e1-462d-b0ec-a2034c899983/user-info")
    suspend fun getUserInfo(): Response<User>
}
