package com.usphuong.bluetoothscanner.data.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.toDeferred
import com.usphuong.bluetoothscanner.FindQuery
import com.usphuong.bluetoothscanner.data.model.Resource
import java.io.IOException
import javax.inject.Inject

class GithubRepository @Inject constructor(private val apolloClient: ApolloClient) {
    suspend fun getRepo(owner: String, name: String): Resource<FindQuery.Data?> {
        val error = try {
            val deferred = apolloClient.query(FindQuery(owner, name)).toDeferred()
            val response = deferred.await()
            if (response.data != null) {
                return Resource.success(response.data)
            }
            "No data"
        } catch (e: IOException) {
            e.message ?: "Network call has failed"
        }
        return Resource.error(error)
    }
}
