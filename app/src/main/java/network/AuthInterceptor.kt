package com.example.dodolist.network

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val cookieJar: CustomCookieJar) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = cookieJar.getAuthToken()

        return if (token != null) {
            val newRequest = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(originalRequest)
        }
    }
}
