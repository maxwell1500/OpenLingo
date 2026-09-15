package com.duo.app.data.network

/**
 * Thread-safe holder for the current Clerk JWT Bearer token,
 * injected into outbound Retrofit/OkHttp requests by [AuthInterceptor].
 */
object AuthTokenHolder {
    @Volatile
    var token: String? = null

    fun clear() {
        token = null
    }
}
