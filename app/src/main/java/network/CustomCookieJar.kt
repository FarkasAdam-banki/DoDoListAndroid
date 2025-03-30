package com.example.dodolist.network

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class CustomCookieJar : CookieJar {
    private val cookieStore: MutableMap<String, MutableList<Cookie>> = mutableMapOf()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieStore[url.host] = cookies.toMutableList()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookieStore[url.host] ?: mutableListOf()
    }

    fun getCookies(): List<Cookie> {
        return cookieStore.values.flatten()
    }

    fun getAuthToken(): String? {
        return getCookies().find { it.name == "auth_token" }?.value
    }
}
