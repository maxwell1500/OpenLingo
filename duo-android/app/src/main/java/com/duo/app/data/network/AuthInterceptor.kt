package com.duo.app.data.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Appends the Clerk session Bearer token from [AuthTokenHolder]
 * to all outgoing backend API calls.
 */
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val currentToken = AuthTokenHolder.token

        val request = if (!currentToken.isNullOrBlank()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $currentToken")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(request)
    }
}
