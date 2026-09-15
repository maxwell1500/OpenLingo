package com.duo.app.data.network

import com.duo.app.data.network.models.MeResponse
import com.duo.app.data.network.models.SyncRequest
import com.duo.app.data.network.models.SyncResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET

/**
 * Retrofit interface for Duo backend REST API endpoints.
 */
interface DuoApi {

    /**
     * Authenticated endpoint returning current user profile verified against Clerk JWT.
     */
    @GET("api/v1/me")
    suspend fun getMe(): MeResponse

    /**
     * Syncs local mobile progress (hearts, points, completed challenges) to cloud Postgres.
     */
    @POST("api/v1/sync")
    suspend fun syncProgress(@Body request: SyncRequest): SyncResponse
}
